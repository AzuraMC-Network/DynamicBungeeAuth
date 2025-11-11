package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.cache.PlayerCache;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.methods.PlayersMethods;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

/**
 * WorkMethod introduction from the Config.yml ->
 * - 1 = Normal Mode (Premium Accounts are Protected and Cracked User can't use premium names)
 * - 2 = Mixed Mode (Premium Accounts are required to register, but they can use the /premium command to get auto-logged in and protected just like in Normal Mode)
 * - 3 = Secure Mode (All accounts are required to use the register/login commands)
 *
 * @author an5w1r@163.com
 */
public class PostLogin implements Listener {
    private final DBAPlugin plugin;
    private final int workMethod;

    public PostLogin(DBAPlugin plugin) {
        this.plugin = plugin;
        this.workMethod = plugin.getConfigLoader().getIntegerCFG("WorkMethod.Value");
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        if (!isValidPlayer(event.getPlayer())) {
            return;
        }

        final ProxiedPlayer player = event.getPlayer();
        handlePlayerLogin(player);
    }

    private boolean isValidPlayer(ProxiedPlayer player) {
        return player != null && player.isConnected();
    }

    private void handlePlayerLogin(ProxiedPlayer player) {
        PlayerData playerData = plugin.getPlayerDataList().searchPlayer(player.getName());
        PlayerCache playerCache = plugin.getPlayerCacheList().searchCache(player.getName());

        if (playerData != null && playerCache != null) {
            handleCachedPlayer(player, playerData, playerCache);
            return;
        }

        handleNewPlayerByMode(player);
    }

    private void handleCachedPlayer(ProxiedPlayer player, PlayerData playerData, PlayerCache playerCache) {
        if (playerData.isValid()) {
            return;
        }

        if (playerCache.isAutologin()) {
            // premium player
            PlayersMethods.setPlayerValidPremium(player, true);
        } else {
            // session
            verifyPlayerSession(player);
        }
    }

    private void handleNewPlayerByMode(ProxiedPlayer player) {
        switch (workMethod) {
            case 1:
                handleMode1(player);
                break;
            case 2:
                handleMode2(player);
                break;
            default:
                handleMode3(player);
                break;
        }
    }

    private void handleMode1(final ProxiedPlayer player) {
        SQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (isRegistered) {
                    handleMode1RegisteredPlayer(player);
                } else {
                    handleMode1UnregisteredPlayer(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleMode1RegisteredPlayer(final ProxiedPlayer player) {
        SQL.getPlayer(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    processMode1PlayerData(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void processMode1PlayerData(ProxiedPlayer player) {
        PlayerData playerData = plugin.getPlayerDataList().searchPlayer(player.getName());

        if (playerData == null) {
            // data is not loaded, default considered as premium
            PlayersMethods.setPlayerValidPremium(player, true);
            return;
        }

        if (playerData.isPremium()) {
            // premium account
            PlayersMethods.setPlayerValidPremium(player, true);
        } else {
            // cracked account
            verifyPlayerSession(player);
        }
    }

    private void handleMode1UnregisteredPlayer(final ProxiedPlayer player) {
        SQL.isName(player.getName(), new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isOnPremiumList) {
                if (isOnPremiumList) {
                    PlayersMethods.setPlayerValidPremium(player, true);
                    SQL.removeName(player.getName());
                } else {
                    checkAutoLoginAndRegister(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void checkAutoLoginAndRegister(ProxiedPlayer player) {
        PlayerCache playerCache = plugin.getPlayerCacheList().searchCache(player.getName());

        if (playerCache != null && playerCache.isAutologin()) {
            PlayersMethods.setPlayerValidPremium(player, true);
        } else {
            requireRegistration(player);
        }
    }

    private void handleMode2(final ProxiedPlayer player) {
        SQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (isRegistered) {
                    handleMode2RegisteredPlayer(player);
                } else {
                    handleMode2UnregisteredPlayer(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleMode2RegisteredPlayer(final ProxiedPlayer player) {
        SQL.getPlayer(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    processMode2PlayerData(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void processMode2PlayerData(ProxiedPlayer player) {
        PlayerData playerData = plugin.getPlayerDataList().searchPlayer(player.getName());

        if (playerData == null) {
            PlayersMethods.setPlayerValidPremium(player, true);
            return;
        }

        if (!playerData.isValid() && playerData.isPremium()) {
            PlayersMethods.setPlayerValidPremium(player, true);
        } else {
            verifyPlayerSession(player);
        }
    }

    private void handleMode2UnregisteredPlayer(final ProxiedPlayer player) {
        SQL.isName(player.getName(), new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isOnPremiumList) {
                if (isOnPremiumList) {
                    PlayersMethods.setPlayerValidPremium(player, true);
                    SQL.removeName(player.getName());
                } else {
                    requireRegistration(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleMode3(final ProxiedPlayer player) {
        SQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (isRegistered) {
                    handleMode3RegisteredPlayer(player);
                } else {
                    requireRegistration(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleMode3RegisteredPlayer(final ProxiedPlayer player) {
        SQL.getPlayer(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    verifyPlayerSession(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void verifyPlayerSession(final ProxiedPlayer player) {
        PlayersMethods.verifySession(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isSessionValid) {
                if (isSessionValid) {
                    PlayersMethods.setPlayerValidSession(player);
                } else {
                    requireLogin(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void requireLogin(ProxiedPlayer player) {
        SQL.setPlayerData(player, "lwlogged", "0");
        plugin.getLoginTimer().logTimer(player);
        PlayersMethods.pMessage(player, 2);
        PlayersMethods.pTitles(player, 2);
    }

    private void requireRegistration(ProxiedPlayer player) {
        plugin.getRegisterTimer().regTimer(player);
        PlayersMethods.pMessage(player, 3);
        PlayersMethods.pTitles(player, 3);
    }
}
