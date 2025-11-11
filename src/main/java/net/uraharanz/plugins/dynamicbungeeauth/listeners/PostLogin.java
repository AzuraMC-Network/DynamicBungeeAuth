package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.cache.cache.PlayerCache;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.methods.PlayersMethods;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

public class PostLogin
implements Listener {
    private main plugin;
    private int Method;

    public PostLogin(main main2) {
        this.plugin = main2;
        this.Method = main2.getConfigLoader().getIntegerCFG("WorkMethod.Value");
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent postLoginEvent) {
        if (postLoginEvent.getPlayer() != null && postLoginEvent.getPlayer().isConnected()) {
            final ProxiedPlayer proxiedPlayer = postLoginEvent.getPlayer();
            PlayerData playerData = this.plugin.getPlayerDataList().searchPlayer(proxiedPlayer.getName());
            PlayerCache playerCache = this.plugin.getPlayerCacheList().searchCache(proxiedPlayer.getName());
            if (playerData != null && playerCache != null) {
                if (!playerData.isValid()) {
                    if (playerCache.isAutologin()) {
                        PlayersMethods.setPlayerValidPremium(proxiedPlayer, true);
                    } else {
                        PlayersMethods.verifySession(proxiedPlayer, new CallbackSQL<Boolean>(){

                            @Override
                            public void done(Boolean bl) {
                                if (bl) {
                                    PlayersMethods.setPlayerValidSession(proxiedPlayer);
                                } else {
                                    SQL.setPlayerData(proxiedPlayer, "lwlogged", "0");
                                    PostLogin.this.plugin.getLoginTimer().logTimer(proxiedPlayer);
                                    PlayersMethods.pMessage(proxiedPlayer, 2);
                                    PlayersMethods.pTitles(proxiedPlayer, 2);
                                }
                            }

                            @Override
                            public void error(Exception exception) {
                            }
                        });
                    }
                }
            } else if (this.Method == 1) {
                SQL.isPlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                    @Override
                    public void done(Boolean bl) {
                        if (bl) {
                            SQL.getPlayer(proxiedPlayer, new CallbackSQL<Boolean>(){

                                @Override
                                public void done(Boolean bl) {
                                    if (bl) {
                                        PlayerData playerData = PostLogin.this.plugin.getPlayerDataList().searchPlayer(proxiedPlayer.getName());
                                        if (playerData != null) {
                                            if (!playerData.isValid() && playerData.isPremium()) {
                                                PlayersMethods.setPlayerValidPremium(proxiedPlayer, true);
                                            } else if (playerData.isValid() && playerData.isPremium()) {
                                                PlayersMethods.setPlayerValidPremium(proxiedPlayer, true);
                                            } else {
                                                PlayersMethods.verifySession(proxiedPlayer, new CallbackSQL<Boolean>(){

                                                    @Override
                                                    public void done(Boolean bl) {
                                                        if (bl) {
                                                            PlayersMethods.setPlayerValidSession(proxiedPlayer);
                                                        } else {
                                                            SQL.setPlayerData(proxiedPlayer, "lwlogged", "0");
                                                            PostLogin.this.plugin.getLoginTimer().logTimer(proxiedPlayer);
                                                            PlayersMethods.pMessage(proxiedPlayer, 2);
                                                            PlayersMethods.pTitles(proxiedPlayer, 2);
                                                        }
                                                    }

                                                    @Override
                                                    public void error(Exception exception) {
                                                    }
                                                });
                                            }
                                        } else {
                                            PlayersMethods.setPlayerValidPremium(proxiedPlayer, true);
                                        }
                                    }
                                }

                                @Override
                                public void error(Exception exception) {
                                }
                            });
                        } else {
                            SQL.isName(proxiedPlayer.getName(), new CallbackSQL<Boolean>(){

                                @Override
                                public void done(Boolean bl) {
                                    if (bl) {
                                        PlayersMethods.setPlayerValidPremium(proxiedPlayer, true);
                                        SQL.removeName(proxiedPlayer.getName());
                                    } else {
                                        PlayerCache playerCache = PostLogin.this.plugin.getPlayerCacheList().searchCache(proxiedPlayer.getName());
                                        if (playerCache != null) {
                                            if (playerCache.isAutologin()) {
                                                PlayersMethods.setPlayerValidPremium(proxiedPlayer, true);
                                            } else {
                                                PostLogin.this.plugin.getRegisterTimer().regTimer(proxiedPlayer);
                                                PlayersMethods.pMessage(proxiedPlayer, 3);
                                                PlayersMethods.pTitles(proxiedPlayer, 3);
                                            }
                                        } else {
                                            PostLogin.this.plugin.getRegisterTimer().regTimer(proxiedPlayer);
                                            PlayersMethods.pMessage(proxiedPlayer, 3);
                                            PlayersMethods.pTitles(proxiedPlayer, 3);
                                        }
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
            } else if (this.Method == 2) {
                SQL.isPlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                    @Override
                    public void done(Boolean bl) {
                        if (!bl) {
                            SQL.isName(proxiedPlayer.getName(), new CallbackSQL<Boolean>(){

                                @Override
                                public void done(Boolean bl) {
                                    if (bl) {
                                        PlayersMethods.setPlayerValidPremium(proxiedPlayer, true);
                                        SQL.removeName(proxiedPlayer.getName());
                                    } else {
                                        PostLogin.this.plugin.getRegisterTimer().regTimer(proxiedPlayer);
                                        PlayersMethods.pMessage(proxiedPlayer, 3);
                                        PlayersMethods.pTitles(proxiedPlayer, 3);
                                    }
                                }

                                @Override
                                public void error(Exception exception) {
                                    exception.printStackTrace();
                                }
                            });
                        } else {
                            SQL.getPlayer(proxiedPlayer, new CallbackSQL<Boolean>(){

                                @Override
                                public void done(Boolean bl) {
                                    if (bl) {
                                        PlayerData playerData = PostLogin.this.plugin.getPlayerDataList().searchPlayer(proxiedPlayer.getName());
                                        if (playerData != null) {
                                            if (!playerData.isValid() && playerData.isPremium()) {
                                                PlayersMethods.setPlayerValidPremium(proxiedPlayer, true);
                                            } else {
                                                PlayersMethods.verifySession(proxiedPlayer, new CallbackSQL<Boolean>(){

                                                    @Override
                                                    public void done(Boolean bl) {
                                                        if (bl) {
                                                            PlayersMethods.setPlayerValidSession(proxiedPlayer);
                                                        } else {
                                                            SQL.setPlayerData(proxiedPlayer, "lwlogged", "0");
                                                            PostLogin.this.plugin.getLoginTimer().logTimer(proxiedPlayer);
                                                            PlayersMethods.pMessage(proxiedPlayer, 2);
                                                            PlayersMethods.pTitles(proxiedPlayer, 2);
                                                        }
                                                    }

                                                    @Override
                                                    public void error(Exception exception) {
                                                        exception.printStackTrace();
                                                    }
                                                });
                                            }
                                        } else {
                                            PlayersMethods.setPlayerValidPremium(proxiedPlayer, true);
                                        }
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
                SQL.isPlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                    @Override
                    public void done(Boolean bl) {
                        if (!bl) {
                            PostLogin.this.plugin.getRegisterTimer().regTimer(proxiedPlayer);
                            PlayersMethods.pMessage(proxiedPlayer, 3);
                            PlayersMethods.pTitles(proxiedPlayer, 3);
                        } else {
                            SQL.getPlayer(proxiedPlayer, new CallbackSQL<Boolean>(){

                                @Override
                                public void done(Boolean bl) {
                                    PlayersMethods.verifySession(proxiedPlayer, new CallbackSQL<Boolean>(){

                                        @Override
                                        public void done(Boolean bl) {
                                            if (bl) {
                                                PlayersMethods.setPlayerValidSession(proxiedPlayer);
                                            } else {
                                                SQL.setPlayerData(proxiedPlayer, "lwlogged", "0");
                                                PostLogin.this.plugin.getLoginTimer().logTimer(proxiedPlayer);
                                                PlayersMethods.pMessage(proxiedPlayer, 2);
                                                PlayersMethods.pTitles(proxiedPlayer, 2);
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
                        }
                    }

                    @Override
                    public void error(Exception exception) {
                    }
                });
            }
        }
    }
}
