package net.uraharanz.plugins.dynamicbungeeauth.methods;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.cache.PlayerCache;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.cache.spam.SpamPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackAPI;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackMET;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author an5w1r@163.com
 */
public class PlayersMethods {

    private static final int MSG_TYPE_AUTO = 1;
    private static final int MSG_TYPE_LOGIN = 2;
    private static final int MSG_TYPE_REGISTER = 3;
    private static final int MSG_TYPE_SESSION = 10;

    /**
     * clean and show successful title
     */
    public static void cleanAndShowSuccessfulTitle(ProxiedPlayer player) {
        if (!DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Successful")) {
            return;
        }

        Title title = ProxyServer.getInstance().createTitle();
        title.reset();
        title.clear();
        title.send(player);

        configureTitleFromConfig(title, "Titles.successful");
        title.send(player);
    }

    /**
     * show title by type
     *
     * @param player ProxiedPlayer
     * @param type   type (1=auto, 2=login, 3=register, 10=session)
     */
    public static void pTitles(ProxiedPlayer player, int type) {
        boolean enableDelay = DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.EnableDelay");
        int delaySeconds = DBAPlugin.plugin.getConfigLoader().getIntegerCFG("Options.DelaySeconds");

        if (enableDelay) {
            DBAPlugin.plugin.getProxy().getScheduler().schedule(
                    DBAPlugin.plugin,
                    () -> showTitleByType(player, type),
                    delaySeconds,
                    TimeUnit.SECONDS
            );
        } else {
            showTitleByType(player, type);
        }
    }

    private static void showTitleByType(ProxiedPlayer player, int type) {
        Title title = ProxyServer.getInstance().createTitle();

        switch (type) {
            case MSG_TYPE_AUTO:
                if (DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Auto")) {
                    configureTitleFromConfig(title, "Titles.auto");
                    title.send(player);
                }
                break;

            case MSG_TYPE_LOGIN:
                if (DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Login")) {
                    configureTitleFromConfig(title, "Titles.login");
                    title.send(player);
                }
                break;

            case MSG_TYPE_REGISTER:
                if (DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Register")) {
                    configureRegisterTitle(title, player);
                    title.send(player);
                }
                break;

            case MSG_TYPE_SESSION:
                if (DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Session")) {
                    configureTitleFromConfig(title, "Titles.session");
                    title.send(player);
                }
                break;
        }
    }

    private static void configureTitleFromConfig(Title title, String configPath) {
        String topMessage = DBAPlugin.plugin.getConfigLoader().getStringMSG(configPath + ".top");
        String bottomMessage = DBAPlugin.plugin.getConfigLoader().getStringMSG(configPath + ".bottom");
        int fadeIn = DBAPlugin.plugin.getConfigLoader().getIntegerMSG(configPath + ".options.fadein");
        int stay = DBAPlugin.plugin.getConfigLoader().getIntegerMSG(configPath + ".options.stay");
        int fadeOut = DBAPlugin.plugin.getConfigLoader().getIntegerMSG(configPath + ".options.fadeout");

        title.title(MessageHandler.sendMSG(topMessage));
        title.subTitle(MessageHandler.sendMSG(bottomMessage));
        title.fadeIn(fadeIn);
        title.stay(stay);
        title.fadeOut(fadeOut);
    }

    private static void configureRegisterTitle(Title title, ProxiedPlayer player) {
        String topMessage = DBAPlugin.plugin.getConfigLoader().getStringMSG("Titles.register.top");
        title.title(MessageHandler.sendMSG(topMessage));

        boolean captchaEnabled = DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.Captcha");
        String bottomMessage = DBAPlugin.plugin.getConfigLoader().getStringMSG("Titles.register.bottom");

        if (captchaEnabled) {
            PlayerCache cache = DBAPlugin.plugin.getPlayerCacheList().searchCache(player.getName());
            bottomMessage = bottomMessage.replaceAll("%captcha%", cache.getCaptcha());
        }

        title.subTitle(MessageHandler.sendMSG(bottomMessage));

        int fadeIn = DBAPlugin.plugin.getConfigLoader().getIntegerMSG("Titles.register.options.fadein");
        int stay = DBAPlugin.plugin.getConfigLoader().getIntegerMSG("Titles.register.options.stay");
        int fadeOut = DBAPlugin.plugin.getConfigLoader().getIntegerMSG("Titles.register.options.fadeout");

        title.fadeIn(fadeIn);
        title.stay(stay);
        title.fadeOut(fadeOut);
    }

    /**
     * send message by type
     *
     * @param player ProxiedPlayer
     * @param type   type (1=auto, 2=login, 3=register, 10=session)
     */
    public static void pMessage(ProxiedPlayer player, int type) {
        if (player == null) {
            return;
        }

        boolean enableDelay = DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.EnableDelay");
        int delaySeconds = DBAPlugin.plugin.getConfigLoader().getIntegerCFG("Options.DelaySeconds");

        if (enableDelay) {
            DBAPlugin.plugin.getProxy().getScheduler().schedule(
                    DBAPlugin.plugin,
                    () -> sendMessageByType(player, type),
                    delaySeconds,
                    TimeUnit.SECONDS
            );
        } else {
            sendMessageByType(player, type);
        }
    }

    private static void sendMessageByType(ProxiedPlayer player, int type) {
        switch (type) {
            case MSG_TYPE_AUTO:
                sendAutoMessage(player);
                break;
            case MSG_TYPE_LOGIN:
                sendLoginMessage(player);
                break;
            case MSG_TYPE_REGISTER:
                sendRegisterMessage(player);
                break;
            case MSG_TYPE_SESSION:
                sendSessionMessage(player);
                break;
        }
    }

    private static void sendAutoMessage(ProxiedPlayer player) {
        List<String> messages = DBAPlugin.plugin.getConfigLoader().getStringListMSG("AutoMessages.auto");
        sendFormattedMessages(player, messages, false);
    }

    private static void sendLoginMessage(ProxiedPlayer player) {
        addSpamPlayer(player, "LOGIN");
        List<String> messages = DBAPlugin.plugin.getConfigLoader().getStringListMSG("AutoMessages.login");
        sendFormattedMessages(player, messages, false);
    }

    private static void sendRegisterMessage(ProxiedPlayer player) {
        addSpamPlayer(player, "REGISTER");
        List<String> messages = DBAPlugin.plugin.getConfigLoader().getStringListMSG("AutoMessages.register");
        sendFormattedMessages(player, messages, true);
    }

    private static void sendSessionMessage(ProxiedPlayer player) {
        List<String> messages = DBAPlugin.plugin.getConfigLoader().getStringListMSG("AutoMessages.session");
        sendFormattedMessages(player, messages, false);
    }

    private static void sendFormattedMessages(ProxiedPlayer player, List<String> messages, boolean supportCaptcha) {
        boolean captchaEnabled = DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.Captcha");
        PlayerCache cache = supportCaptcha && captchaEnabled ?
                DBAPlugin.plugin.getPlayerCacheList().searchCache(player.getName()) : null;

        for (int i = 0; i < messages.size(); i++) {
            String message = messages.get(i);
            boolean isFirstOrLast = (i == 0 || i == messages.size() - 1);

            if (supportCaptcha && captchaEnabled && cache != null) {
                message = message.replaceAll("%captcha%", cache.getCaptcha());
            }

            if (isFirstOrLast) {
                player.sendMessage(MessageHandler.sendMSG(message));
            } else {
                MessageHandler.sendCenteredMessage(player, message.replaceAll("&", "§"));
            }
        }
    }

    private static void addSpamPlayer(ProxiedPlayer player, String type) {
        SpamPlayer spamPlayer = new SpamPlayer(player.getName(), type, false);
        DBAPlugin.plugin.getSpamPlayerList().addPlayer(spamPlayer);
    }

    public static void setValidCache(ProxiedPlayer player) {
        PlayerData playerData = DBAPlugin.plugin.getPlayerDataList().searchPlayer(player.getName());
        if (playerData != null) {
            playerData.setValid(true);
            sendVerifyMSG(player);
        }
    }

    public static void playerRemoveCache(ProxiedPlayer player) {
        removePlayerCacheData(player.getName());
        SQL.setPlayerData(player, "valid", "0");
        SQL.setPlayerData(player, "lwlogged", "0");
    }

    public static void playerRemoveCache(String playerName) {
        removePlayerCacheData(playerName);
        SQL.setPlayerDataS(playerName, "valid", "0");
        SQL.setPlayerDataS(playerName, "lwlogged", "0");
    }

    private static void removePlayerCacheData(String playerName) {
        DBAPlugin.plugin.getPlayerAPIList().removeRequest(playerName);
        DBAPlugin.plugin.getPlayerCacheList().removeCache(playerName);
        DBAPlugin.plugin.getPlayerDataList().removePlayer(playerName);
    }

    public static void updatePlaying(String playerName, boolean isPlaying) {
        PlayerData playerData = DBAPlugin.plugin.getPlayerDataList().searchPlayer(playerName);
        if (playerData != null) {
            playerData.setPlaying(isPlaying);
            DBAPlugin.plugin.getPlayerDataList().modifyPlayer(playerData);
        }
    }

    public static void asyncGet(ProxiedPlayer player, final CallbackSQL<Boolean> callback) {
        SQL.getPlayer(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    callback.done(true);
                }
            }

            @Override
            public void error(Exception e) {
                // 错误处理
            }
        });
    }

    public static void setPlayerValidPremium(final ProxiedPlayer player, final boolean isPremium) {
        ProxyServer.getInstance().getScheduler().schedule(
                DBAPlugin.plugin,
                () -> processPremiumValidation(player),
                2L,
                TimeUnit.SECONDS
        );
    }

    private static void processPremiumValidation(final ProxiedPlayer player) {
        SQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (isRegistered) {
                    handleRegisteredPremiumPlayer(player);
                } else {
                    handleUnregisteredPremiumPlayer(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void handleRegisteredPremiumPlayer(final ProxiedPlayer player) {
        verifyIPPlaying(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isWithinLimit) {
                if (isWithinLimit) {
                    setPlayerValid(player);
                } else {
                    kickForIPLimit(player, "KickMessages.MaxPlayingIP");
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void handleUnregisteredPremiumPlayer(final ProxiedPlayer player) {
        verifyIPRegister(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean canRegister) {
                if (canRegister) {
                    checkIPPlayingAndRegister(player);
                } else {
                    kickForIPLimit(player, "KickMessages.MaxAccountsIP");
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void checkIPPlayingAndRegister(final ProxiedPlayer player) {
        verifyIPPlaying(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isWithinLimit) {
                if (isWithinLimit) {
                    registerAndValidatePlayer(player);
                } else {
                    kickForIPLimit(player, "KickMessages.MaxPlayingIP");
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void registerAndValidatePlayer(final ProxiedPlayer player) {
        SQL.PlayerSQL(player, 1, 1, false, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    setPlayerValid(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void setPlayerValid(final ProxiedPlayer player) {
        SQL.setPlayerDataAsync(player, "valid", "1", new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    sendAutoMessages(player);
                    completePlayerLogin(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void sendAutoMessages(ProxiedPlayer player) {
        pMessage(player, MSG_TYPE_AUTO);
        pTitles(player, MSG_TYPE_AUTO);
    }

    private static void completePlayerLogin(final ProxiedPlayer player) {
        SQL.getPlayer(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    finalizeLogin(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void finalizeLogin(ProxiedPlayer player) {
        ServerMethods.sendLobbyServer(player);
        SQL.setPlayerData(player, "log_ip", player.getAddress().getAddress().getHostAddress());
        SQL.setPlayerData(player, "lwlogged", "1");
        SQL.mathIPTable(player, "+", "playing", 1);
        updatePlaying(player.getName(), true);
        sendVerifyMSG(player);
    }

    private static void kickForIPLimit(ProxiedPlayer player, String messageKey) {
        String kickMessage = DBAPlugin.plugin.getConfigLoader().getStringMSG(messageKey);
        player.disconnect(MessageHandler.sendMSG(kickMessage));
    }

    public static void setPlayerValidSession(final ProxiedPlayer player) {
        ProxyServer.getInstance().getScheduler().schedule(
                DBAPlugin.plugin,
                () -> processSessionValidation(player),
                2L,
                TimeUnit.SECONDS
        );
    }

    private static void processSessionValidation(final ProxiedPlayer player) {
        SQL.setPlayerDataAsync(player, "valid", "1", new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    sendSessionMessages(player);
                    completeSessionLogin(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void sendSessionMessages(ProxiedPlayer player) {
        pMessage(player, MSG_TYPE_SESSION);
        pTitles(player, MSG_TYPE_SESSION);
    }

    private static void completeSessionLogin(final ProxiedPlayer player) {
        SQL.getPlayer(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    ServerMethods.sendLobbyServer(player);
                    SQL.setPlayerData(player, "log_ip", player.getAddress().getAddress().getHostAddress());
                    SQL.setPlayerData(player, "lwlogged", "1");
                    SQL.mathIPTable(player, "+", "playing", 1);
                    updatePlaying(player.getName(), true);

                    DBAPlugin.plugin.getProxy().getScheduler().schedule(
                            DBAPlugin.plugin,
                            () -> sendVerifyMSG(player),
                            1L,
                            TimeUnit.SECONDS
                    );
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void verifySession(final ProxiedPlayer player, final CallbackSQL<Boolean> callback) {
        verifyIPPlaying(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isWithinLimit) {
                if (isWithinLimit) {
                    checkLastLogged(player, callback);
                } else {
                    callback.done(false);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void checkLastLogged(final ProxiedPlayer player, final CallbackSQL<Boolean> callback) {
        SQL.getPlayerDataString(player.getName(), "lwlogged", new CallbackSQL<String>() {
            @Override
            public void done(String lastLogged) {
                if (lastLogged.equals("1")) {
                    checkLoginIP(player, callback);
                } else {
                    callback.done(false);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void checkLoginIP(final ProxiedPlayer player, final CallbackSQL<Boolean> callback) {
        SQL.getPlayerDataS(player, "log_ip", new CallbackSQL<String>() {
            @Override
            public void done(String loggedIP) {
                String currentIP = player.getAddress().getAddress().getHostAddress();
                if (loggedIP.equals(currentIP)) {
                    checkSessionTimeout(player, callback);
                } else {
                    callback.done(false);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void checkSessionTimeout(final ProxiedPlayer player, final CallbackSQL<Boolean> callback) {
        SQL.getPlayerDataS(player, "lastjoin", new CallbackSQL<String>() {
            @Override
            public void done(String lastJoinStr) {
                Timestamp lastJoin = Timestamp.valueOf(lastJoinStr);
                Timestamp now = new Timestamp(new Date().getTime());
                long elapsedSeconds = (now.getTime() - lastJoin.getTime()) / 1000L;
                int maxSessionTime = DBAPlugin.plugin.getConfigLoader().getIntegerCFG("Options.Sessions.MaxTimeToApply");

                callback.done(elapsedSeconds <= maxSessionTime);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void verifyIPRegister(final ProxiedPlayer player, final CallbackSQL<Boolean> callback) {
        if (DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.IPChecker.DisableIPRegisterLimit")) {
            callback.done(true);
            return;
        }

        final String ipAddress = player.getAddress().getAddress().getHostAddress();
        checkIPRegisterLimit(player, ipAddress, callback);
    }

    private static void checkIPRegisterLimit(final ProxiedPlayer player, final String ipAddress, final CallbackSQL<Boolean> callback) {
        SQL.getIPTable(ipAddress, "max_accounts", new CallbackSQL<String>() {
            @Override
            public void done(final String maxAccounts) {
                if (maxAccounts != null) {
                    compareIPAccounts(player, ipAddress, maxAccounts, callback);
                } else {
                    registerNewIP(player, callback);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void compareIPAccounts(final ProxiedPlayer player, final String ipAddress, final String maxAccounts, final CallbackSQL<Boolean> callback) {
        SQL.getIPTable(ipAddress, "accounts", new CallbackSQL<String>() {
            @Override
            public void done(String currentAccounts) {
                int max = Integer.parseInt(maxAccounts);
                int current = Integer.parseInt(currentAccounts);

                if (max > current) {
                    SQL.mathIPTable(player, "+", "accounts", 1);
                    callback.done(true);
                } else {
                    callback.done(false);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void verifyIPPlaying(final ProxiedPlayer player, final CallbackSQL<Boolean> callback) {
        if (DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.IPChecker.DisableIPPlayingLimit")) {
            callback.done(true);
            return;
        }

        final String ipAddress = player.getAddress().getAddress().getHostAddress();
        checkIPPlayingLimit(player, ipAddress, callback);
    }

    private static void checkIPPlayingLimit(final ProxiedPlayer player, final String ipAddress, final CallbackSQL<Boolean> callback) {
        SQL.getIPTable(ipAddress, "max_playing", new CallbackSQL<String>() {
            @Override
            public void done(final String maxPlaying) {
                if (maxPlaying != null) {
                    compareIPPlaying(ipAddress, maxPlaying, callback);
                } else {
                    registerNewIP(player, callback);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void compareIPPlaying(final String ipAddress, final String maxPlaying, final CallbackSQL<Boolean> callback) {
        SQL.getIPTable(ipAddress, "playing", new CallbackSQL<String>() {
            @Override
            public void done(String currentPlaying) {
                int max = Integer.parseInt(maxPlaying);
                int current = Integer.parseInt(currentPlaying);
                callback.done(max >= current);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void registerNewIP(final ProxiedPlayer player, final CallbackSQL<Boolean> callback) {
        DBAPlugin.plugin.getProxy().getScheduler().schedule(
                DBAPlugin.plugin,
                () -> {
                    SQL.registerIP(player, 0);
                    callback.done(true);
                },
                1L,
                TimeUnit.SECONDS
        );
    }

    public static void verifyNameCheck(final String playerName, final CallbackMET<Boolean> callback) {
        if (!DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.NameCheck")) {
            callback.done(true);
            return;
        }

        SQL.getPlayerDataS(playerName, "name", new CallbackSQL<String>() {
            @Override
            public void done(String storedName) {
                if (storedName == null || storedName.equals(playerName)) {
                    callback.done(true);
                } else {
                    callback.done(false);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void removePlayerDB(String playerName, String adminName, CallbackAPI<Boolean> callback) {
        ProxiedPlayer player = DBAPlugin.plugin.getProxy().getPlayer(playerName);

        if (player != null) {
            removeOnlinePlayer(player, playerName, adminName, callback);
        } else {
            removeOfflinePlayer(playerName, adminName, callback);
        }
    }

    private static void removeOnlinePlayer(ProxiedPlayer player, String playerName, String adminName, CallbackAPI<Boolean> callback) {
        SQL.RemovePlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });

        String kickMessage = DBAPlugin.plugin.getConfigLoader()
                .getStringMSG("KickMessages.unregisterkick")
                .replaceAll("%admin%", adminName);
        player.disconnect(MessageHandler.sendMSG(kickMessage));

        playerRemoveCache(player);
        SQL.mathIPTable(player, "-", "playing", 1);
        SQL.mathIPTable(player, "-", "accounts", 1);

        logUnregister(playerName, adminName);
        callback.done(true);
    }

    private static void removeOfflinePlayer(String playerName, String adminName, CallbackAPI<Boolean> callback) {
        SQL.getPlayerDataS(playerName, "log_ip", new CallbackSQL<String>() {
            @Override
            public void done(String ipAddress) {
                if (ipAddress != null) {
                    SQL.mathIPTable(ipAddress, "-", "playing", 1);
                    SQL.mathIPTable(ipAddress, "-", "accounts", 1);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });

        SQL.RemovePlayerDBS(playerName, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });

        playerRemoveCache(playerName);

        logUnregister(playerName, adminName);
        callback.done(true);
    }

    private static void logUnregister(String playerName, String adminName) {
        String logMessage = DBAPlugin.plugin.getConfigLoader()
                .getStringMSG("Commands.unregister.console")
                .replaceAll("%player%", playerName)
                .replaceAll("%admin%", adminName);
        ProxyServer.getInstance().getLogger().info(logMessage);
    }

    public static void sendToOtherProxy(String senderName, String targetName, CallbackMET<Boolean> callback) {
        if (!DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Options.RedisBungee")) {
            callback.done(true);
            return;
        }

        String senderProxy = getCurrentProxy(senderName);
        String targetProxy = getCurrentProxy(targetName);

        if (senderProxy != null && targetProxy != null) {
            callback.done(senderProxy.equals(targetProxy));
        } else {
            callback.done(true);
        }
    }

    private static String getCurrentProxy(String playerName) {
        UUID uuid = RedisBungee.getApi().getUuidFromName(playerName);
        return RedisBungee.getApi().getProxy(uuid);
    }

    public static void sendCommandProxy(String command, String playerName) {
        String proxyServer = getCurrentProxy(playerName);
        if (proxyServer != null) {
            RedisBungee.getApi().sendProxyCommand(proxyServer, command);
        }
    }

    public static void sendKickProxy(String playerName) {
        String proxyServer = getCurrentProxy(playerName);
        if (proxyServer != null) {
            RedisBungee.getApi().sendProxyCommand(proxyServer, "authadmin kick " + playerName);
        }
    }

    private static boolean isVerifiedPlayer(ProxiedPlayer player) {
        PlayerData playerData = DBAPlugin.plugin.getPlayerDataList().searchPlayer(player.getName());
        return playerData != null && playerData.isValid();
    }

    public static void sendVerifyMSG(ProxiedPlayer player) {
        if (!isVerifiedPlayer(player)) {
            return;
        }

        if (ProxyServer.getInstance().getPlayers() == null ||
                ProxyServer.getInstance().getPlayers().isEmpty()) {
            return;
        }

        if (player.getServer() == null) {
            return;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(outputStream);

        try {
            dataStream.writeUTF("dba:verifyplayer");
            String channelName = "dba:" + DBAPlugin.plugin.getConfigLoader().getStringCFG("PluginChannel.verify");
            player.getServer().sendData(channelName, outputStream.toByteArray());
            sendDelayedMSG(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendDelayedMSG(ProxiedPlayer player) {
        ProxyServer.getInstance().getScheduler().schedule(
                DBAPlugin.plugin,
                () -> sendDelayedVerifyMessage(player),
                1L,
                TimeUnit.SECONDS
        );
    }

    private static void sendDelayedVerifyMessage(ProxiedPlayer player) {
        if (player == null || player.getServer() == null) {
            return;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(outputStream);

        try {
            dataStream.writeUTF("dba:verifyplayer");
            String channelName = "dba:" + DBAPlugin.plugin.getConfigLoader().getStringCFG("PluginChannel.verify");
            player.getServer().sendData(channelName, outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
