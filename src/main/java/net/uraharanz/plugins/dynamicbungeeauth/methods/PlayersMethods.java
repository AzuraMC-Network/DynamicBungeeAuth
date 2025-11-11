package net.uraharanz.plugins.dynamicbungeeauth.methods;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.cache.cache.PlayerCache;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.cache.spam.SpamPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.utils.apis.UpdateChecker;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackAPI;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackMET;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.random.UserID;

public class PlayersMethods {
    public static void CleanTitles(ProxiedPlayer proxiedPlayer) {
        if (main.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Successful")) {
            Title title = ProxyServer.getInstance().createTitle();
            title.reset();
            title.clear();
            title.send(proxiedPlayer);
            title.title(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.successful.top")));
            title.subTitle(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.successful.bottom")));
            title.fadeIn(main.plugin.getConfigLoader().getIntegerMSG("Titles.successful.options.fadein"));
            title.stay(main.plugin.getConfigLoader().getIntegerMSG("Titles.successful.options.stay"));
            title.fadeOut(main.plugin.getConfigLoader().getIntegerMSG("Titles.successful.options.fadeout"));
            title.send(proxiedPlayer);
        }
        if (proxiedPlayer != null) {
            PlayersMethods.sendInfo(proxiedPlayer.getName());
        }
    }

    public static void pMessage(ProxiedPlayer proxiedPlayer, int n) {
        block18: {
            if (proxiedPlayer == null) break block18;
            boolean bl = main.plugin.getConfigLoader().getBooleanCFG("Options.EnableDelay");
            int n2 = main.plugin.getConfigLoader().getIntegerCFG("Options.DelaySeconds");
            if (bl) {
                main.plugin.getProxy().getScheduler().schedule(main.plugin, () -> {
                    if (n == 1) {
                        List<String> arrayList = main.plugin.getConfigLoader().getStringListMSG("AutoMessages.auto");
                        for (String string : arrayList) {
                            if (arrayList.indexOf(string) == 0 || arrayList.indexOf(string) == arrayList.size() - 1) {
                                proxiedPlayer.sendMessage(MessageHandler.sendMSG(string));
                                continue;
                            }
                            MessageHandler.sendCenteredMessage(proxiedPlayer, string.replaceAll("&", "§"));
                        }
                    } else if (n == 2) {
                        SpamPlayer spamPlayer = new SpamPlayer(proxiedPlayer.getName(), "LOGIN", false);
                        main.plugin.getSpamPlayerList().addPlayer(spamPlayer);
                        List<String> arrayList = main.plugin.getConfigLoader().getStringListMSG("AutoMessages.login");
                        for (String string : arrayList) {
                            if (arrayList.indexOf(string) == 0 || arrayList.indexOf(string) == arrayList.size() - 1) {
                                proxiedPlayer.sendMessage(MessageHandler.sendMSG(string));
                                continue;
                            }
                            MessageHandler.sendCenteredMessage(proxiedPlayer, string.replaceAll("&", "§"));
                        }
                    } else if (n == 10) {
                        List<String> arrayList = main.plugin.getConfigLoader().getStringListMSG("AutoMessages.session");
                        for (String string : arrayList) {
                            if (arrayList.indexOf(string) == 0 || arrayList.indexOf(string) == arrayList.size() - 1) {
                                proxiedPlayer.sendMessage(MessageHandler.sendMSG(string));
                                continue;
                            }
                            MessageHandler.sendCenteredMessage(proxiedPlayer, string.replaceAll("&", "§"));
                        }
                    } else {
                        SpamPlayer spamPlayer = new SpamPlayer(proxiedPlayer.getName(), "REGISTER", false);
                        main.plugin.getSpamPlayerList().addPlayer(spamPlayer);
                        List<String> arrayList = main.plugin.getConfigLoader().getStringListMSG("AutoMessages.register");
                        for (String string : arrayList) {
                            PlayerCache playerCache;
                            boolean bl2;
                            if (arrayList.indexOf(string) == 0 || arrayList.indexOf(string) == arrayList.size() - 1) {
                                bl2 = main.plugin.getConfigLoader().getBooleanCFG("Options.Captcha");
                                if (bl2) {
                                    playerCache = main.plugin.getPlayerCacheList().searchCache(proxiedPlayer.getName());
                                    proxiedPlayer.sendMessage(MessageHandler.sendMSG(string.replaceAll("%captcha%", playerCache.getCaptcha())));
                                    continue;
                                }
                                proxiedPlayer.sendMessage(MessageHandler.sendMSG(string));
                                continue;
                            }
                            bl2 = main.plugin.getConfigLoader().getBooleanCFG("Options.Captcha");
                            if (bl2) {
                                playerCache = main.plugin.getPlayerCacheList().searchCache(proxiedPlayer.getName());
                                MessageHandler.sendCenteredMessage(proxiedPlayer, string.replaceAll("&", "§").replaceAll("%captcha%", playerCache.getCaptcha()));
                                continue;
                            }
                            MessageHandler.sendCenteredMessage(proxiedPlayer, string.replaceAll("&", "§"));
                        }
                    }
                }, n2, TimeUnit.SECONDS);
            } else if (n == 1) {
                List<String> arrayList = main.plugin.getConfigLoader().getStringListMSG("AutoMessages.auto");
                for (String string : arrayList) {
                    if (arrayList.indexOf(string) == 0 || arrayList.indexOf(string) == arrayList.size() - 1) {
                        proxiedPlayer.sendMessage(MessageHandler.sendMSG(string));
                        continue;
                    }
                    MessageHandler.sendCenteredMessage(proxiedPlayer, string.replaceAll("&", "§"));
                }
            } else if (n == 2) {
                SpamPlayer spamPlayer = new SpamPlayer(proxiedPlayer.getName(), "LOGIN", false);
                main.plugin.getSpamPlayerList().addPlayer(spamPlayer);
                List<String> arrayList = main.plugin.getConfigLoader().getStringListMSG("AutoMessages.login");
                for (String string : arrayList) {
                    if (arrayList.indexOf(string) == 0 || arrayList.indexOf(string) == arrayList.size() - 1) {
                        proxiedPlayer.sendMessage(MessageHandler.sendMSG(string));
                        continue;
                    }
                    MessageHandler.sendCenteredMessage(proxiedPlayer, string.replaceAll("&", "§"));
                }
            } else if (n == 10) {
                List<String> arrayList = main.plugin.getConfigLoader().getStringListMSG("AutoMessages.session");
                for (String string : arrayList) {
                    if (arrayList.indexOf(string) == 0 || arrayList.indexOf(string) == arrayList.size() - 1) {
                        proxiedPlayer.sendMessage(MessageHandler.sendMSG(string));
                        continue;
                    }
                    MessageHandler.sendCenteredMessage(proxiedPlayer, string.replaceAll("&", "§"));
                }
            } else {
                SpamPlayer spamPlayer = new SpamPlayer(proxiedPlayer.getName(), "REGISTER", false);
                main.plugin.getSpamPlayerList().addPlayer(spamPlayer);
                List<String> arrayList = main.plugin.getConfigLoader().getStringListMSG("AutoMessages.register");
                for (String string : arrayList) {
                    PlayerCache playerCache;
                    boolean bl2;
                    if (arrayList.indexOf(string) == 0 || arrayList.indexOf(string) == arrayList.size() - 1) {
                        bl2 = main.plugin.getConfigLoader().getBooleanCFG("Options.Captcha");
                        if (bl2) {
                            playerCache = main.plugin.getPlayerCacheList().searchCache(proxiedPlayer.getName());
                            proxiedPlayer.sendMessage(MessageHandler.sendMSG(string.replaceAll("%captcha%", playerCache.getCaptcha())));
                            continue;
                        }
                        proxiedPlayer.sendMessage(MessageHandler.sendMSG(string));
                        continue;
                    }
                    bl2 = main.plugin.getConfigLoader().getBooleanCFG("Options.Captcha");
                    if (bl2) {
                        playerCache = main.plugin.getPlayerCacheList().searchCache(proxiedPlayer.getName());
                        MessageHandler.sendCenteredMessage(proxiedPlayer, string.replaceAll("&", "§").replaceAll("%captcha%", playerCache.getCaptcha()));
                        continue;
                    }
                    MessageHandler.sendCenteredMessage(proxiedPlayer, string.replaceAll("&", "§"));
                }
            }
        }
    }

    public static void pTitles(ProxiedPlayer proxiedPlayer, int n) {
        boolean bl = main.plugin.getConfigLoader().getBooleanCFG("Options.EnableDelay");
        int n2 = main.plugin.getConfigLoader().getIntegerCFG("Options.DelaySeconds");
        if (bl) {
            main.plugin.getProxy().getScheduler().schedule(main.plugin, () -> {
                Title title = ProxyServer.getInstance().createTitle();
                if (n == 1 && main.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Auto")) {
                    title.title(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.auto.top")));
                    title.subTitle(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.auto.bottom")));
                    title.fadeIn(main.plugin.getConfigLoader().getIntegerMSG("Titles.auto.options.fadein"));
                    title.stay(main.plugin.getConfigLoader().getIntegerMSG("Titles.auto.options.stay"));
                    title.fadeOut(main.plugin.getConfigLoader().getIntegerMSG("Titles.auto.options.fadeout"));
                    title.send(proxiedPlayer);
                }
                if (n == 2 && main.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Login")) {
                    title.title(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.login.top")));
                    title.subTitle(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.login.bottom")));
                    title.fadeIn(main.plugin.getConfigLoader().getIntegerMSG("Titles.login.options.fadein"));
                    title.stay(main.plugin.getConfigLoader().getIntegerMSG("Titles.login.options.stay"));
                    title.fadeOut(main.plugin.getConfigLoader().getIntegerMSG("Titles.login.options.fadeout"));
                    title.send(proxiedPlayer);
                }
                if (n == 3 && main.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Register")) {
                    title.title(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.register.top")));
                    boolean bl2 = main.plugin.getConfigLoader().getBooleanCFG("Options.Captcha");
                    if (bl2) {
                        PlayerCache playerCache = main.plugin.getPlayerCacheList().searchCache(proxiedPlayer.getName());
                        title.subTitle(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.register.bottom").replaceAll("%captcha%", playerCache.getCaptcha())));
                    } else {
                        title.subTitle(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.register.bottom")));
                    }
                    title.fadeIn(main.plugin.getConfigLoader().getIntegerMSG("Titles.register.options.fadein"));
                    title.stay(main.plugin.getConfigLoader().getIntegerMSG("Titles.register.options.stay"));
                    title.fadeOut(main.plugin.getConfigLoader().getIntegerMSG("Titles.register.options.fadeout"));
                    title.send(proxiedPlayer);
                }
                if (n == 10 && main.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Session")) {
                    title.title(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.session.top")));
                    title.subTitle(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.session.bottom")));
                    title.fadeIn(main.plugin.getConfigLoader().getIntegerMSG("Titles.session.options.fadein"));
                    title.stay(main.plugin.getConfigLoader().getIntegerMSG("Titles.session.options.stay"));
                    title.fadeOut(main.plugin.getConfigLoader().getIntegerMSG("Titles.session.options.fadeout"));
                    title.send(proxiedPlayer);
                }
            }, n2, TimeUnit.SECONDS);
        } else {
            Title title = ProxyServer.getInstance().createTitle();
            if (n == 1 && main.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Auto")) {
                title.title(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.auto.top")));
                title.subTitle(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.auto.bottom")));
                title.fadeIn(main.plugin.getConfigLoader().getIntegerMSG("Titles.auto.options.fadein"));
                title.stay(main.plugin.getConfigLoader().getIntegerMSG("Titles.auto.options.stay"));
                title.fadeOut(main.plugin.getConfigLoader().getIntegerMSG("Titles.auto.options.fadeout"));
                title.send(proxiedPlayer);
            }
            if (n == 2 && main.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Login")) {
                title.title(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.login.top")));
                title.subTitle(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.login.bottom")));
                title.fadeIn(main.plugin.getConfigLoader().getIntegerMSG("Titles.login.options.fadein"));
                title.stay(main.plugin.getConfigLoader().getIntegerMSG("Titles.login.options.stay"));
                title.fadeOut(main.plugin.getConfigLoader().getIntegerMSG("Titles.login.options.fadeout"));
                title.send(proxiedPlayer);
            }
            if (n == 3 && main.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Register")) {
                title.title(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.register.top")));
                boolean bl2 = main.plugin.getConfigLoader().getBooleanCFG("Options.Captcha");
                if (bl2) {
                    PlayerCache playerCache = main.plugin.getPlayerCacheList().searchCache(proxiedPlayer.getName());
                    title.subTitle(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.register.bottom").replaceAll("%captcha%", playerCache.getCaptcha())));
                } else {
                    title.subTitle(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.register.bottom")));
                }
                title.fadeIn(main.plugin.getConfigLoader().getIntegerMSG("Titles.register.options.fadein"));
                title.stay(main.plugin.getConfigLoader().getIntegerMSG("Titles.register.options.stay"));
                title.fadeOut(main.plugin.getConfigLoader().getIntegerMSG("Titles.register.options.fadout"));
                title.send(proxiedPlayer);
            }
            if (n == 10 && main.plugin.getConfigLoader().getBooleanCFG("Options.Titles.Session")) {
                title.title(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.session.top")));
                title.subTitle(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Titles.session.bottom")));
                title.fadeIn(main.plugin.getConfigLoader().getIntegerMSG("Titles.session.options.fadein"));
                title.stay(main.plugin.getConfigLoader().getIntegerMSG("Titles.session.options.stay"));
                title.fadeOut(main.plugin.getConfigLoader().getIntegerMSG("Titles.session.options.fadeout"));
                title.send(proxiedPlayer);
            }
        }
    }

    public static void setValidCache(ProxiedPlayer proxiedPlayer) {
        PlayerData playerData = main.plugin.getPlayerDataList().searchPlayer(proxiedPlayer.getName());
        if (playerData != null) {
            playerData.setValid(true);
            PlayersMethods.sendVerifyMSG(proxiedPlayer);
        }
    }

    public static void asyncGet(ProxiedPlayer proxiedPlayer, final CallbackSQL<Boolean> callbackSQL) {
        SQL.getPlayer(proxiedPlayer, new CallbackSQL<Boolean>(){

            @Override
            public void done(Boolean bl) {
                if (bl) {
                    callbackSQL.done(true);
                }
            }

            @Override
            public void error(Exception exception) {
            }
        });
    }

    public static void setPlayerValidPremium(final ProxiedPlayer proxiedPlayer, final boolean bl) {
        ProxyServer.getInstance().getScheduler().schedule(main.plugin, () -> SQL.isPlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

            @Override
            public void done(Boolean bl2) {
                if (bl2) {
                    PlayersMethods.verifyIPPlaying(proxiedPlayer, new CallbackSQL<Boolean>(){

                        @Override
                        public void done(Boolean bl) {
                            if (bl) {
                                SQL.setPlayerDataAsync(proxiedPlayer, "valid", "1", new CallbackSQL<Boolean>(){

                                    @Override
                                    public void done(Boolean bl) {
                                        if (bl) {
                                            PlayersMethods.pMessage(proxiedPlayer, 1);
                                            PlayersMethods.pTitles(proxiedPlayer, 1);
                                            SQL.getPlayer(proxiedPlayer, new CallbackSQL<Boolean>(){

                                                @Override
                                                public void done(Boolean bl) {
                                                    if (bl) {
                                                        ServerMethods.sendLobbyServer(proxiedPlayer);
                                                        SQL.setPlayerData(proxiedPlayer, "log_ip", proxiedPlayer.getAddress().getAddress().getHostAddress());
                                                        SQL.setPlayerData(proxiedPlayer, "lwlogged", "1");
                                                        SQL.mathIPTable(proxiedPlayer, "+", "playing", 1);
                                                        PlayersMethods.updatePlaying(proxiedPlayer.getName(), true);
                                                        PlayersMethods.sendVerifyMSG(proxiedPlayer);
                                                    }
                                                }

                                                @Override
                                                public void error(Exception exception) {
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void error(Exception exception) {
                                    }
                                });
                            } else {
                                proxiedPlayer.disconnect(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("KickMessages.MaxPlayingIP")));
                            }
                        }

                        @Override
                        public void error(Exception exception) {
                        }
                    });
                } else {
                    PlayersMethods.verifyIPRegister(proxiedPlayer, new CallbackSQL<Boolean>(){

                        @Override
                        public void done(Boolean bl) {
                            if (bl) {
                                PlayersMethods.verifyIPPlaying(proxiedPlayer, new CallbackSQL<Boolean>(){

                                    @Override
                                    public void done(Boolean bl) {
                                        if (bl) {
                                            SQL.PlayerSQL(proxiedPlayer, 1, 1, false, new CallbackSQL<Boolean>(){

                                                @Override
                                                public void done(Boolean bl) {
                                                    SQL.setPlayerDataAsync(proxiedPlayer, "valid", "1", new CallbackSQL<Boolean>(){

                                                        @Override
                                                        public void done(Boolean bl) {
                                                            if (bl) {
                                                                PlayersMethods.pMessage(proxiedPlayer, 1);
                                                                PlayersMethods.pTitles(proxiedPlayer, 1);
                                                                SQL.getPlayer(proxiedPlayer, new CallbackSQL<Boolean>(){

                                                                    @Override
                                                                    public void done(Boolean bl) {
                                                                        if (bl) {
                                                                            ServerMethods.sendLobbyServer(proxiedPlayer);
                                                                            SQL.setPlayerData(proxiedPlayer, "log_ip", proxiedPlayer.getAddress().getAddress().getHostAddress());
                                                                            SQL.setPlayerData(proxiedPlayer, "lwlogged", "1");
                                                                            SQL.mathIPTable(proxiedPlayer, "+", "playing", 1);
                                                                            PlayersMethods.updatePlaying(proxiedPlayer.getName(), true);
                                                                            PlayersMethods.sendVerifyMSG(proxiedPlayer);
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void error(Exception exception) {
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void error(Exception exception) {
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void error(Exception exception) {
                                                }
                                            });
                                        } else {
                                            proxiedPlayer.disconnect(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("KickMessages.MaxPlayingIP")));
                                        }
                                    }

                                    @Override
                                    public void error(Exception exception) {
                                    }
                                });
                            } else {
                                proxiedPlayer.disconnect(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("KickMessages.MaxAccountsIP")));
                            }
                        }

                        @Override
                        public void error(Exception exception) {
                        }
                    });
                }
            }

            @Override
            public void error(Exception exception) {
            }
        }), 2L, TimeUnit.SECONDS);
    }

    public static void setPlayerValidSession(final ProxiedPlayer proxiedPlayer) {
        ProxyServer.getInstance().getScheduler().schedule(main.plugin, () -> SQL.setPlayerDataAsync(proxiedPlayer, "valid", "1", new CallbackSQL<Boolean>(){

            @Override
            public void done(Boolean bl) {
                if (bl) {
                    PlayersMethods.pMessage(proxiedPlayer, 10);
                    PlayersMethods.pTitles(proxiedPlayer, 10);
                    SQL.getPlayer(proxiedPlayer, new CallbackSQL<Boolean>(){

                        @Override
                        public void done(Boolean bl) {
                            if (bl) {
                                ServerMethods.sendLobbyServer(proxiedPlayer);
                                SQL.setPlayerData(proxiedPlayer, "log_ip", proxiedPlayer.getAddress().getAddress().getHostAddress());
                                SQL.setPlayerData(proxiedPlayer, "lwlogged", "1");
                                SQL.mathIPTable(proxiedPlayer, "+", "playing", 1);
                                PlayersMethods.updatePlaying(proxiedPlayer.getName(), true);
                                main.plugin.getProxy().getScheduler().schedule(main.plugin, () -> PlayersMethods.sendVerifyMSG(proxiedPlayer), 1L, TimeUnit.SECONDS);
                            }
                        }

                        @Override
                        public void error(Exception exception) {
                        }
                    });
                }
            }

            @Override
            public void error(Exception exception) {
            }
        }), 2L, TimeUnit.SECONDS);
    }

    public static void removePlayerDB(String string, String string2, CallbackAPI<Boolean> callbackAPI) {
        if (main.plugin.getProxy().getPlayer(string) != null) {
            ProxiedPlayer proxiedPlayer = main.plugin.getProxy().getPlayer(string);
            SQL.RemovePlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                @Override
                public void done(Boolean bl) {
                }

                @Override
                public void error(Exception exception) {
                }
            });
            proxiedPlayer.disconnect(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("KickMessages.unregisterkick").replaceAll("%admin%", string2)));
            PlayersMethods.playerRemoveCache(proxiedPlayer);
            ProxyServer.getInstance().getLogger().info(main.plugin.getConfigLoader().getStringMSG("Commands.unregister.console").replaceAll("%player%", string).replaceAll("%admin%", string2));
            SQL.mathIPTable(proxiedPlayer, "-", "playing", 1);
            SQL.mathIPTable(proxiedPlayer, "-", "accounts", 1);
            callbackAPI.done(true);
        } else {
            SQL.getPlayerDataS(string, "log_ip", new CallbackSQL<String>(){

                @Override
                public void done(String string) {
                    if (string != null) {
                        SQL.mathIPTable(string, "-", "playing", 1);
                        SQL.mathIPTable(string, "-", "accounts", 1);
                    }
                }

                @Override
                public void error(Exception exception) {
                }
            });
            SQL.RemovePlayerDBS(string, new CallbackSQL<Boolean>(){

                @Override
                public void done(Boolean bl) {
                }

                @Override
                public void error(Exception exception) {
                }
            });
            PlayersMethods.playerRemoveCache(string);
            ProxyServer.getInstance().getLogger().info(main.plugin.getConfigLoader().getStringMSG("Commands.unregister.console").replaceAll("%player%", string).replaceAll("%admin%", string2));
            callbackAPI.done(true);
        }
    }

    public static void playerRemoveCache(ProxiedPlayer proxiedPlayer) {
        main.plugin.getPlayerAPIList().removeRequest(proxiedPlayer.getName());
        main.plugin.getPlayerCacheList().removeCache(proxiedPlayer.getName());
        main.plugin.getPlayerDataList().removePlayer(proxiedPlayer.getName());
        SQL.setPlayerData(proxiedPlayer, "valid", "0");
        SQL.setPlayerData(proxiedPlayer, "lwlogged", "0");
    }

    public static void playerRemoveCache(String string) {
        main.plugin.getPlayerAPIList().removeRequest(string);
        main.plugin.getPlayerCacheList().removeCache(string);
        main.plugin.getPlayerDataList().removePlayer(string);
        SQL.setPlayerDataS(string, "valid", "0");
        SQL.setPlayerDataS(string, "lwlogged", "0");
    }

    public static void verifySession(final ProxiedPlayer proxiedPlayer, final CallbackSQL<Boolean> callbackSQL) {
        PlayersMethods.verifyIPPlaying(proxiedPlayer, new CallbackSQL<Boolean>(){

            @Override
            public void done(Boolean bl) {
                if (bl) {
                    SQL.getPlayerDataString(proxiedPlayer.getName(), "lwlogged", new CallbackSQL<String>(){

                        @Override
                        public void done(String string) {
                            if (string.equals("1")) {
                                SQL.getPlayerDataS(proxiedPlayer, "log_ip", new CallbackSQL<String>(){

                                    @Override
                                    public void done(String string) {
                                        if (string.equals(proxiedPlayer.getAddress().getAddress().getHostAddress())) {
                                            SQL.getPlayerDataS(proxiedPlayer, "lastjoin", new CallbackSQL<String>(){

                                                @Override
                                                public void done(String string) {
                                                    Timestamp timestamp = Timestamp.valueOf(string);
                                                    Date date = new Date();
                                                    Timestamp timestamp2 = new Timestamp(date.getTime());
                                                    long l = timestamp2.getTime() - timestamp.getTime();
                                                    if (l / 1000L <= (long) main.plugin.getConfigLoader().getIntegerCFG("Options.Sessions.MaxTimeToApply"))
                                                        callbackSQL.done(true);
                                                    else {
                                                        callbackSQL.done(false);
                                                    }
                                                }

                                                @Override
                                                public void error(Exception exception) {
                                                }
                                            });
                                        } else {
                                            callbackSQL.done(false);
                                        }
                                    }

                                    @Override
                                    public void error(Exception exception) {
                                    }
                                });
                            } else {
                                callbackSQL.done(false);
                            }
                        }

                        @Override
                        public void error(Exception exception) {
                        }
                    });
                } else {
                    callbackSQL.done(false);
                }
            }

            @Override
            public void error(Exception exception) {
            }
        });
    }

    public static void verifyNameCheck(final String string, final CallbackMET<Boolean> callbackMET) {
        if (main.plugin.getConfigLoader().getBooleanCFG("Options.NameCheck")) {
            SQL.getPlayerDataS(string, "name", new CallbackSQL<String>(){

                @Override
                public void done(String string2) {
                    if (string2 != null) {
                        if (string2.equals(string)) {
                            callbackMET.done(true);
                        } else {
                            callbackMET.done(false);
                        }
                    } else {
                        callbackMET.done(true);
                    }
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else {
            callbackMET.done(true);
        }
    }

    public static void verifyIPRegister(final ProxiedPlayer proxiedPlayer, final CallbackSQL<Boolean> callbackSQL) {
        if (!main.plugin.getConfigLoader().getBooleanCFG("Options.IPChecker.DisableIPRegisterLimit")) {
            final String string = proxiedPlayer.getAddress().getAddress().getHostAddress();
            SQL.getIPTable(string, "max_accounts", new CallbackSQL<String>(){

                @Override
                public void done(final String string2) {
                    if (string2 != null) {
                        SQL.getIPTable(string, "accounts", new CallbackSQL<String>(){

                            @Override
                            public void done(String string) {
                                int n = Integer.parseInt(string2);
                                int n2 = Integer.parseInt(string);
                                if (n > n2) {
                                    callbackSQL.done(true);
                                    SQL.mathIPTable(proxiedPlayer, "+", "accounts", 1);
                                } else {
                                    callbackSQL.done(false);
                                }
                            }

                            @Override
                            public void error(Exception exception) {
                            }
                        });
                    } else {
                        main.plugin.getProxy().getScheduler().schedule(main.plugin, () -> {
                            SQL.registerIP(proxiedPlayer, 0);
                            callbackSQL.done(true);
                        }, 1L, TimeUnit.SECONDS);
                    }
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else {
            callbackSQL.done(true);
        }
    }

    public static void verifyIPPlaying(final ProxiedPlayer proxiedPlayer, final CallbackSQL<Boolean> callbackSQL) {
        if (!main.plugin.getConfigLoader().getBooleanCFG("Options.IPChecker.DisableIPPlayingLimit")) {
            final String string = proxiedPlayer.getAddress().getAddress().getHostAddress();
            SQL.getIPTable(string, "max_playing", new CallbackSQL<String>(){

                @Override
                public void done(final String string2) {
                    if (string2 != null) {
                        SQL.getIPTable(string, "playing", new CallbackSQL<String>(){

                            @Override
                            public void done(String string) {
                                int n = Integer.parseInt(string2);
                                int n2 = Integer.parseInt(string);
                                if (n >= n2) {
                                    callbackSQL.done(true);
                                } else {
                                    callbackSQL.done(false);
                                }
                            }

                            @Override
                            public void error(Exception exception) {
                            }
                        });
                    } else {
                        main.plugin.getProxy().getScheduler().schedule(main.plugin, () -> {
                            SQL.registerIP(proxiedPlayer, 0);
                            callbackSQL.done(true);
                        }, 1L, TimeUnit.SECONDS);
                    }
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else {
            callbackSQL.done(true);
        }
    }

    public static void sendToOtherProxy(String string, String string2, CallbackMET<Boolean> callbackMET) {
        if (main.plugin.getConfigLoader().getBooleanCFG("Options.RedisBungee")) {
            String string3 = PlayersMethods.getCurrentProxy(string);
            String string4 = PlayersMethods.getCurrentProxy(string2);
            if (string3 != null && string4 != null) {
                if (string3.equals(string4)) {
                    callbackMET.done(true);
                } else {
                    callbackMET.done(false);
                }
            } else {
                callbackMET.done(true);
            }
        } else {
            callbackMET.done(true);
        }
    }

    private static String getCurrentProxy(String string) {
        UUID uUID = RedisBungee.getApi().getUuidFromName(string);
        return RedisBungee.getApi().getProxy(uUID);
    }

    public static void sendCommandProxy(String string, String string2) {
        String string3 = PlayersMethods.getCurrentProxy(string2);
        if (string3 != null) {
            RedisBungee.getApi().sendProxyCommand(string3, string);
        }
    }

    public static void sendKickProxy(String string) {
        String string2 = PlayersMethods.getCurrentProxy(string);
        if (string2 != null) {
            RedisBungee.getApi().sendProxyCommand(string2, "authadmin kick " + string);
        }
    }

    public static void sendInfo(String string) {
        ProxiedPlayer proxiedPlayer = main.plugin.getProxy().getPlayer(string);
        if (proxiedPlayer != null) {
            if (string.equals("UraharaNZ") || string.equals("HolaSoyTuTia")) {
                UpdateChecker.CheckUpdates(proxiedPlayer);
                proxiedPlayer.sendMessage(MessageHandler.sendMSG("§a§lDBA §8| §aBuyer: §a" + UserID.getUserSpigot("1061623")));
                proxiedPlayer.sendMessage(MessageHandler.sendMSG("§a§lDBA §8| §aID: §a1061623"));
            } else if (proxiedPlayer.hasPermission("auth.admin")) {
                UpdateChecker.CheckUpdates(proxiedPlayer);
            }
        }
    }

    public static void updatePlaying(String string, boolean bl) {
        PlayerData playerData = main.plugin.getPlayerDataList().searchPlayer(string);
        if (playerData != null) {
            playerData.setPlaying(bl);
            main.plugin.getPlayerDataList().modifyPlayer(playerData);
        }
    }

    private static boolean isVerifiedPlayer(ProxiedPlayer proxiedPlayer) {
        PlayerData playerData = main.plugin.getPlayerDataList().searchPlayer(proxiedPlayer.getName());
        if (playerData != null) {
            return playerData.isValid();
        }
        return false;
    }

    public static void sendVerifyMSG(ProxiedPlayer proxiedPlayer) {
        block6: {
            block8: {
                block7: {
                    if (!PlayersMethods.isVerifiedPlayer(proxiedPlayer)) break block6;
                    main.plugin.getProxy();
                    if (ProxyServer.getInstance().getPlayers() == null) break block7;
                    main.plugin.getProxy();
                    if (!ProxyServer.getInstance().getPlayers().isEmpty()) break block8;
                }
                return;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeUTF("dba:verifyplayer");
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            if (proxiedPlayer.getServer() != null) {
                proxiedPlayer.getServer().sendData("dba:" + main.plugin.getConfigLoader().getStringCFG("PluginChannel.verify"), byteArrayOutputStream.toByteArray());
                PlayersMethods.sendDelayedMSG(proxiedPlayer);
            }
        }
    }

    public static void sendDelayedMSG(ProxiedPlayer proxiedPlayer) {
        ProxyServer.getInstance().getScheduler().schedule(main.plugin, () -> {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeUTF("dba:verifyplayer");
                if (proxiedPlayer != null && proxiedPlayer.getServer() != null) {
                    proxiedPlayer.getServer().sendData("dba:" + main.plugin.getConfigLoader().getStringCFG("PluginChannel.verify"), byteArrayOutputStream.toByteArray());
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }, 1L, TimeUnit.SECONDS);
    }
}
