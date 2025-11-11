package net.uraharanz.plugins.dynamicbungeeauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.cache.server.ServerState;
import net.uraharanz.plugins.dynamicbungeeauth.methods.PlayersMethods;
import net.uraharanz.plugins.dynamicbungeeauth.methods.ServerMethods;
import net.uraharanz.plugins.dynamicbungeeauth.timers.MaxLogin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackAPI;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackMET;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.PoolManager;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

public class AdminCMD extends Command {
    private main plugin;

    public AdminCMD(main var1) {
        super("authadmin", "auth.admin", new String[0]);
        this.plugin = var1;
    }

    public void execute(final CommandSender var1, final String[] var2) {
        if (var2.length == 0) {
            for(String var17 : main.plugin.getConfigLoader().getStringListMSG("HelpADM")) {
                var1.sendMessage(MessageHandler.sendMSG(var17));
            }

        } else {
            if (var2[0].equalsIgnoreCase("unregister")) {
                if (var2.length < 2) {
                    var1.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.unregister.correct")));
                    return;
                }

                final String var3 = var2[1];
                PlayersMethods.removePlayerDB(var3, var1.getName(), new CallbackAPI<Boolean>() {
                    public void done(Boolean var1x) {
                        if (var1x) {
                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.unregister.success")));
                            if (ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null) {
                                PlayersMethods.sendKickProxy(var3);
                                var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.redis")));
                            } else {
                                var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.rediserror")));
                            }
                        }

                    }

                    public void error(Exception var1x) {
                    }
                });
            }

            if (var2[0].equalsIgnoreCase("disableshield")) {
                if (this.plugin.getConfigLoader().getIntegerCFG("Options.MaxLogin.Mode") == 2) {
                    var1.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.disableshield.success")));
                    MaxLogin.resetCount();
                    ServerState.setState(ServerState.NORMAL);
                } else {
                    var1.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.disableshield.wrong")));
                }
            } else {
                if (var2[0].equalsIgnoreCase("clearcache")) {
                    if (var2.length < 2) {
                        var1.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.cacheclear.correct")));
                    } else {
                        final String var6 = var2[1];
                        PlayersMethods.sendToOtherProxy(var1.getName(), var6, new CallbackMET<Boolean>() {
                            public void done(Boolean var1x) {
                                if (var1x) {
                                    ProxiedPlayer var2x = AdminCMD.this.plugin.getProxy().getPlayer(var6);
                                    if (var2x != null) {
                                        if (var2x.isConnected()) {
                                            var2x.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.cacheclear.player_msg")));
                                            PlayersMethods.playerRemoveCache(var2x);
                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.cacheclear.success").replaceAll("%player%", var2[1])));
                                            var2x.disconnect(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("KickMessages.ClearCache")));
                                        }
                                    } else {
                                        PlayersMethods.playerRemoveCache(var6);
                                        var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.cacheclear.success").replaceAll("%player%", var2[1])));
                                    }
                                } else if (ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null) {
                                    PlayersMethods.sendCommandProxy("authadmin clearcache " + var6, var6);
                                    var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.redis")));
                                } else {
                                    var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.rediserror")));
                                }

                            }

                            public void error(Exception var1x) {
                            }
                        });
                    }
                }

                if (var2[0].equalsIgnoreCase("clearnames")) {
                    PoolManager.resetNames();
                    var1.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.clearnames.success")));
                }

                if (var2[0].equalsIgnoreCase("premium")) {
                    if (var2.length < 2) {
                        var1.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.premium_adm.wrong")));
                    } else {
                        final String var7 = var2[1];
                        PlayersMethods.sendToOtherProxy(var1.getName(), var7, new CallbackMET<Boolean>() {
                            public void done(Boolean var1x) {
                                if (var1x) {
                                    SQL.isPlayerDB(var7, new CallbackSQL<Boolean>() {
                                        public void done(Boolean var1x) {
                                            if (var1x) {
                                                SQL.getPlayerDataS(var7, "premium", new CallbackSQL<String>() {
                                                    public void done(String var1x) {
                                                        if (var1x.equalsIgnoreCase("1")) {
                                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium_adm.already")));
                                                        } else {
                                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium_adm.success")));
                                                            SQL.setPlayerDataS(var7, "premium", "1");
                                                            PlayersMethods.playerRemoveCache(var7);
                                                            ProxiedPlayer var2 = AdminCMD.this.plugin.getProxy().getPlayer(var7);
                                                            if (var2 != null) {
                                                                var2.disconnect(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("KickMessages.PremiumKick")));
                                                            }
                                                        }

                                                    }

                                                    public void error(Exception var1x) {
                                                    }
                                                });
                                            } else {
                                                var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium_adm.not_registered")));
                                            }

                                        }

                                        public void error(Exception var1x) {
                                        }
                                    });
                                } else if (ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null) {
                                    PlayersMethods.sendCommandProxy("authadmin premium " + var7, var7);
                                    var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.redis")));
                                } else {
                                    var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.rediserror")));
                                }

                            }

                            public void error(Exception var1x) {
                            }
                        });
                    }
                }

                if (var2[0].equalsIgnoreCase("cracked")) {
                    if (var2.length < 2) {
                        var1.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.cracked_adm.wrong")));
                    } else {
                        final String var8 = var2[1];
                        PlayersMethods.sendToOtherProxy(var1.getName(), var8, new CallbackMET<Boolean>() {
                            public void done(Boolean var1x) {
                                if (var1x) {
                                    SQL.isPlayerDB(var8, new CallbackSQL<Boolean>() {
                                        public void done(Boolean var1x) {
                                            if (var1x) {
                                                SQL.getPlayerDataS(var8, "premium", new CallbackSQL<String>() {
                                                    public void done(String var1x) {
                                                        if (var1x.equalsIgnoreCase("0")) {
                                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.cracked_adm.already")));
                                                        } else {
                                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.cracked_adm.success")));
                                                            SQL.setPlayerDataS(var8, "premium", "0");
                                                            PlayersMethods.playerRemoveCache(var8);
                                                            ProxiedPlayer var2 = AdminCMD.this.plugin.getProxy().getPlayer(var8);
                                                            if (var2 != null) {
                                                                var2.disconnect(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("KickMessages.PremiumKick")));
                                                            }
                                                        }

                                                    }

                                                    public void error(Exception var1x) {
                                                    }
                                                });
                                            } else {
                                                var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.cracked_adm.not_registered")));
                                            }

                                        }

                                        public void error(Exception var1x) {
                                        }
                                    });
                                } else if (ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null) {
                                    PlayersMethods.sendCommandProxy("authadmin cracked " + var8, var8);
                                    var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.redis")));
                                } else {
                                    var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.rediserror")));
                                }

                            }

                            public void error(Exception var1x) {
                            }
                        });
                    }
                }

                if (var2[0].equalsIgnoreCase("fetchdata")) {
                    PoolManager.fetchData();
                    var1.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.fetchdata")));
                }

                if (var2[0].equalsIgnoreCase("playerip")) {
                    if (var2.length <= 3) {
                        for(String var5 : main.plugin.getConfigLoader().getStringListMSG("HelpPlayerIP")) {
                            var1.sendMessage(MessageHandler.sendMSG(var5));
                        }
                    } else {
                        if (var2[1].equalsIgnoreCase("accounts")) {
                            SQL.getPlayerDataS(var2[3], "log_ip", new CallbackSQL<String>() {
                                public void done(String var1x) {
                                    if (var1x != null) {
                                        if (var2[2].equals("add")) {
                                            SQL.mathIPTable(var1x, "+", "accounts", 1);
                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.addaccount").replaceAll("%player%", var2[3])));
                                        } else if (var2[2].equals("minus")) {
                                            SQL.mathIPTable(var1x, "-", "accounts", 1);
                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.minusaccount").replaceAll("%player%", var2[3])));
                                        } else {
                                            for(String var4 : main.plugin.getConfigLoader().getStringListMSG("HelpPlayerIP")) {
                                                var1.sendMessage(MessageHandler.sendMSG(var4));
                                            }
                                        }
                                    } else {
                                        var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.ipnotfound")));
                                    }

                                }

                                public void error(Exception var1x) {
                                }
                            });
                            return;
                        }

                        if (var2[1].equalsIgnoreCase("max_accounts")) {
                            SQL.getPlayerDataS(var2[3], "log_ip", new CallbackSQL<String>() {
                                public void done(String var1x) {
                                    if (var1x != null) {
                                        if (var2[2].equals("add")) {
                                            SQL.mathIPTable(var1x, "+", "max_accounts", 1);
                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.addaccount").replaceAll("%player%", var2[3])));
                                        } else if (var2[2].equals("minus")) {
                                            SQL.mathIPTable(var1x, "-", "max_accounts", 1);
                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.minusaccount").replaceAll("%player%", var2[3])));
                                        } else {
                                            for(String var4 : main.plugin.getConfigLoader().getStringListMSG("HelpPlayerIP")) {
                                                var1.sendMessage(MessageHandler.sendMSG(var4));
                                            }
                                        }
                                    } else {
                                        var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.ipnotfound")));
                                    }

                                }

                                public void error(Exception var1x) {
                                }
                            });
                        } else if (var2[1].equalsIgnoreCase("playing")) {
                            SQL.getPlayerDataS(var2[3], "log_ip", new CallbackSQL<String>() {
                                public void done(String var1x) {
                                    if (var1x != null) {
                                        if (var2[2].equals("add")) {
                                            SQL.mathIPTable(var1x, "+", "playing", 1);
                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.addplaying").replaceAll("%player%", var2[3])));
                                        } else if (var2[2].equals("minus")) {
                                            SQL.mathIPTable(var1x, "-", "playing", 1);
                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.minusplaying").replaceAll("%player%", var2[3])));
                                        } else {
                                            for(String var4 : main.plugin.getConfigLoader().getStringListMSG("HelpPlayerIP")) {
                                                var1.sendMessage(MessageHandler.sendMSG(var4));
                                            }
                                        }
                                    } else {
                                        var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.ipnotfound")));
                                    }

                                }

                                public void error(Exception var1x) {
                                }
                            });
                        } else if (var2[1].equalsIgnoreCase("max_playing")) {
                            SQL.getPlayerDataS(var2[3], "log_ip", new CallbackSQL<String>() {
                                public void done(String var1x) {
                                    if (var1x != null) {
                                        if (var2[2].equals("add")) {
                                            SQL.mathIPTable(var1x, "+", "max_playing", 1);
                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.addplaying").replaceAll("%player%", var2[3])));
                                        } else if (var2[2].equals("minus")) {
                                            SQL.mathIPTable(var1x, "-", "max_playing", 1);
                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.minusplaying").replaceAll("%player%", var2[3])));
                                        } else {
                                            for(String var4 : main.plugin.getConfigLoader().getStringListMSG("HelpPlayerIP")) {
                                                var1.sendMessage(MessageHandler.sendMSG(var4));
                                            }
                                        }
                                    } else {
                                        var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.ipnotfound")));
                                    }

                                }

                                public void error(Exception var1x) {
                                }
                            });
                        } else if (var2[1].equalsIgnoreCase("delete")) {
                            SQL.getPlayerDataS(var2[2], "log_ip", new CallbackSQL<String>() {
                                public void done(String var1x) {
                                    if (var1x != null) {
                                        SQL.deleteIP(var1x);
                                        var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.delete").replaceAll("%player%", var2[2])));
                                    } else {
                                        var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.playerip.ipnotfound")));
                                    }

                                }

                                public void error(Exception var1x) {
                                }
                            });
                        } else {
                            for(String var16 : main.plugin.getConfigLoader().getStringListMSG("HelpPlayerIP")) {
                                var1.sendMessage(MessageHandler.sendMSG(var16));
                            }
                        }
                    }
                }

                if (var2[0].equalsIgnoreCase("kick")) {
                    if (var2.length < 2) {
                        var1.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.cacheclear.correct")));
                    } else {
                        ProxiedPlayer var11 = ProxyServer.getInstance().getPlayer(var2[1]);
                        if (var11 != null) {
                            var11.disconnect(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("KickMessages.unregisterkick").replaceAll("%admin%", var1.getName())));
                        }
                    }
                }

                if (var2[0].equalsIgnoreCase("forcelogin")) {
                    final String var12 = var2[1];
                    PlayersMethods.sendToOtherProxy(var1.getName(), var12, new CallbackMET<Boolean>() {
                        public void done(Boolean var1x) {
                            if (var1x) {
                                SQL.isPlayerDB(var12, new CallbackSQL<Boolean>() {
                                    public void done(Boolean var1x) {
                                        if (var1x) {
                                            ProxiedPlayer var2 = AdminCMD.this.plugin.getProxy().getPlayer(var12);
                                            if (var2 != null) {
                                                var2.sendMessage(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Commands.forcelogin.success").replaceAll("%staff_name%", var1.getName())));
                                                AdminCMD.this.plugin.getLoginTimer().getTimers().remove(var2.getName());
                                                SQL.setPlayerData(var2, "valid", "1");
                                                PlayersMethods.CleanTitles(var2);
                                                PlayersMethods.setValidCache(var2);
                                                ServerMethods.sendLobbyServer(var2);
                                                SQL.setPlayerData(var2, "log_ip", var2.getAddress().getAddress().getHostAddress());
                                                SQL.setPlayerData(var2, "lwlogged", "1");
                                                PlayersMethods.updatePlaying(var2.getName(), true);
                                            } else {
                                                var1.sendMessage(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Commands.forcelogin.offline").replaceAll("%player%", var12)));
                                            }
                                        } else {
                                            var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.forcelogin.not_registered")));
                                        }

                                    }

                                    public void error(Exception var1x) {
                                    }
                                });
                            } else if (ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null) {
                                PlayersMethods.sendCommandProxy("authadmin premium " + var12, var12);
                                var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.redis")));
                            } else {
                                var1.sendMessage(MessageHandler.sendMSG(AdminCMD.this.plugin.getConfigLoader().getStringMSG("Commands.rediserror")));
                            }

                        }

                        public void error(Exception var1x) {
                        }
                    });
                }

            }
        }
    }
}
