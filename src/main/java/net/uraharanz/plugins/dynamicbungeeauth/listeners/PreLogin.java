package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.cache.PlayerCache;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.cache.server.ServerState;
import net.uraharanz.plugins.dynamicbungeeauth.methods.PlayersMethods;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackAPI;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackMET;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.random.SaltGenerator;

import java.util.UUID;

/**
 * @author an5w1r@163.com
 */
public class PreLogin implements Listener {
    private final DBABungeePlugin plugin;

    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 16;
    private final boolean protectCrackedAccounts;
    private final String nameRegex;
    private final int workMethod;
    private final int captchaLength;
    private final String maxCrackedMessage;
    private final int maxLoginMode;
    private final String maxLoginMessage;
    private final String nameCheckMessage;
    private final boolean floodgateAutoRegister;

    public PreLogin(DBABungeePlugin plugin) {
        this.plugin = plugin;
        this.workMethod = plugin.getConfigLoader().getIntegerCFG("WorkMethod.Value");
        this.captchaLength = plugin.getConfigLoader().getIntegerCFG("Options.CaptchaLength");
        this.protectCrackedAccounts = plugin.getConfigLoader().getBooleanCFG("Options.ProtectRegisteredCrackedAccounts");
        this.nameRegex = plugin.getConfigLoader().getStringCFG("Options.NameRegex");
        this.maxCrackedMessage = plugin.getConfigLoader().getStringMSG("KickMessages.MaxLoginCracked");
        this.maxLoginMessage = plugin.getConfigLoader().getStringMSG("KickMessages.MaxLogin");
        this.maxLoginMode = plugin.getConfigLoader().getIntegerCFG("Options.MaxLogin.Mode");
        this.nameCheckMessage = plugin.getConfigLoader().getStringMSG("KickMessages.NameCheck");
        this.floodgateAutoRegister = plugin.getConfigLoader().getBooleanCFG("Options.Floodgate.AutoRegister");
    }

    @EventHandler
    public void onPreLogin(final PreLoginEvent event) {
        event.registerIntent(plugin);

        if (!isValidConnection(event)) {
            cancelEvent(event, null);
            return;
        }

        final PendingConnection connection = event.getConnection();
        final String playerName = connection.getName();

        if (playerName == null) {
            cancelEvent(event, null);
            return;
        }

        if (isFloodgateConnection(connection)) {
            handleFloodgateConnection(event, connection, playerName);
            return;
        }

        if (!isValidPlayerName(playerName)) {
            cancelEvent(event, null);
            return;
        }

        plugin.getMaxLogin().incrementLogin();

        if (!checkLoginLimits(event, playerName)) {
            return;
        }

        handleByWorkMode(event, connection, playerName);
    }

    private boolean isValidConnection(PreLoginEvent event) {
        PendingConnection connection = event.getConnection();
        return connection != null &&
                connection.isConnected() &&
                !event.isCancelled();
    }

    private boolean isFloodgateConnection(PendingConnection connection) {
        if (connection.getUniqueId() == null) {
            return false;
        }

        if (plugin.getFloodgateApi() == null) {
            return false;
        }

        return plugin.getFloodgateApi().getPlayer(connection.getUniqueId()) != null;
    }

    private void handleFloodgateConnection(PreLoginEvent event, PendingConnection connection, String playerName) {
        createPlayerCache(connection, playerName, floodgateAutoRegister);
        event.completeIntent(plugin);
    }

    private boolean isValidPlayerName(String playerName) {
        return playerName.length() >= MIN_NAME_LENGTH &&
                playerName.length() <= MAX_NAME_LENGTH &&
                playerName.matches(nameRegex) &&
                !playerName.contains("$") &&
                !playerName.contains(" ") &&
                !playerName.contains("-");
    }

    private boolean checkLoginLimits(PreLoginEvent event, String playerName) {
        if (ServerState.getState() == ServerState.ATTACK) {
            return checkAttackMode(event, playerName);
        }

        if (plugin.getMaxLogin().mustBlock()) {
            return checkBlockMode(event, playerName);
        }

        return true;
    }

    private boolean checkAttackMode(PreLoginEvent event, String playerName) {
        PlayerData playerData = plugin.getPlayerDataList().searchPlayer(playerName);

        if (playerData == null) {
            cancelEvent(event, maxLoginMessage);
            return false;
        }

        if (!playerData.isPremium()) {
            cancelEvent(event, maxCrackedMessage);
            return false;
        }

        return true;
    }

    private boolean checkBlockMode(PreLoginEvent event, String playerName) {
        if (maxLoginMode == 2 && ServerState.getState() == ServerState.ATTACK) {
            return checkAttackMode(event, playerName);
        }

        PlayerData playerData = plugin.getPlayerDataList().searchPlayer(playerName);

        if (playerData == null) {
            cancelEvent(event, maxLoginMessage);
            return false;
        }

        if (!playerData.isPremium()) {
            cancelEvent(event, maxCrackedMessage);
            return false;
        }

        return true;
    }

    private void handleByWorkMode(PreLoginEvent event, PendingConnection connection, String playerName) {
        switch (workMethod) {
            case 1:
                handleMode1(event, connection, playerName);
                break;
            case 2:
                handleMode2(event, connection, playerName);
                break;
            case 3:
                handleMode3(event, connection, playerName);
                break;
        }
    }

    private void handleMode1(final PreLoginEvent event, final PendingConnection connection, final String playerName) {
        verifyPlayerName(event, playerName, () -> checkPremiumStatusMode1(event, connection, playerName));
    }

    private void checkPremiumStatusMode1(final PreLoginEvent event, final PendingConnection connection, final String playerName) {
        SQL.getPlayerDataS(playerName, "premium", new CallbackSQL<String>() {
            @Override
            public void done(String premiumStatus) {
                if (premiumStatus == null) {
                    checkMojangAndSetMode(event, connection, playerName);
                } else if (premiumStatus.equals("1")) {
                    setPremiumMode(event, connection, playerName);
                } else {
                    handleRegisteredCracked(event, connection, playerName);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleRegisteredCracked(final PreLoginEvent event, final PendingConnection connection, final String playerName) {
        if (protectCrackedAccounts) {
            // protect cracked account, so force set offline mode
            setOfflineMode(connection, playerName);
            event.completeIntent(plugin);
        } else {
            // no protect, check Mojang
            checkMojangAndSetMode(event, connection, playerName);
        }
    }

    private void handleMode2(final PreLoginEvent event, final PendingConnection connection, final String playerName) {
        verifyPlayerName(event, playerName, () -> {
            checkPremiumStatusMode2(event, connection, playerName);
        });
    }

    private void checkPremiumStatusMode2(final PreLoginEvent event, final PendingConnection connection, final String playerName) {
        SQL.getPlayerDataS(playerName, "premium", new CallbackSQL<String>() {
            @Override
            public void done(String premiumStatus) {
                if (premiumStatus == null) {
                    checkPremiumWhitelist(event, connection, playerName);
                } else if (premiumStatus.equals("1")) {
                    setPremiumMode(event, connection, playerName);
                } else {
                    setOfflineMode(connection, playerName);
                    event.completeIntent(plugin);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void checkPremiumWhitelist(final PreLoginEvent event, final PendingConnection connection, final String playerName) {
        SQL.isName(playerName, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isOnWhitelist) {
                if (isOnWhitelist) {
                    setPremiumMode(event, connection, playerName);
                } else {
                    setOfflineMode(connection, playerName);
                    event.completeIntent(plugin);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleMode3(final PreLoginEvent event, final PendingConnection connection, final String playerName) {
        verifyPlayerName(event, playerName, () -> {
            setOfflineMode(connection, playerName);
            event.completeIntent(plugin);
        });
    }

    private void verifyPlayerName(final PreLoginEvent event, final String playerName, final Runnable onSuccess) {
        PlayersMethods.verifyNameCheck(playerName, new CallbackMET<Boolean>() {
            @Override
            public void done(Boolean isValid) {
                if (isValid) {
                    onSuccess.run();
                } else {
                    cancelEvent(event, nameCheckMessage);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void checkMojangAndSetMode(final PreLoginEvent event, final PendingConnection connection, final String playerName) {
        plugin.getProfileGenerator().Generator(playerName, new CallbackAPI<UUID>() {
            @Override
            public void done(UUID uuid) {
                if (uuid != null) {
                    setPremiumMode(event, connection, playerName);
                } else {
                    setOfflineMode(connection, playerName);
                    event.completeIntent(plugin);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setPremiumMode(PreLoginEvent event, PendingConnection connection, String playerName) {
        connection.setOnlineMode(true);
        PlayerCache cache = new PlayerCache(
                playerName,
                true,
                SaltGenerator.generateCaptcha(captchaLength)
        );
        plugin.getPlayerCacheList().addCache(cache);
        event.completeIntent(plugin);
    }

    private void setOfflineMode(PendingConnection connection, String playerName) {
        connection.setOnlineMode(false);
        createPlayerCache(connection, playerName, false);
    }

    private void createPlayerCache(PendingConnection connection, String playerName, boolean autoLogin) {
        connection.setOnlineMode(false);
        PlayerCache cache = new PlayerCache(
                playerName,
                autoLogin,
                SaltGenerator.generateCaptcha(captchaLength)
        );
        plugin.getPlayerCacheList().addCache(cache);
    }

    private void cancelEvent(PreLoginEvent event, String message) {
        if (message != null) {
            event.setCancelReason(MessageHandler.createColoredMessage(message));
        }
        event.setCancelled(true);
        event.completeIntent(plugin);
    }
}
