package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
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

public class PreLogin
implements Listener {
    private final DBAPlugin plugin;
    private final int Method;
    private final int lengthC;
    private final boolean protectCrackedAccounts;
    private final String nameRegex;
    private final String messageMaxCracked;
    private final String maxLogin;
    private final int maxLoginMode;
    private final String nameCheck;
    private final boolean floodgateAuto;

    public PreLogin(DBAPlugin plugin) {
        this.plugin = plugin;
        this.Method = plugin.getConfigLoader().getIntegerCFG("WorkMethod.Value");
        this.lengthC = plugin.getConfigLoader().getIntegerCFG("Options.CaptchaLength");
        this.protectCrackedAccounts = plugin.getConfigLoader().getBooleanCFG("Options.ProtectRegisteredCrackedAccounts");
        this.nameRegex = plugin.getConfigLoader().getStringCFG("Options.NameRegex");
        this.messageMaxCracked = plugin.getConfigLoader().getStringMSG("KickMessages.MaxLoginCracked");
        this.maxLogin = plugin.getConfigLoader().getStringMSG("KickMessages.MaxLogin");
        this.maxLoginMode = plugin.getConfigLoader().getIntegerCFG("Options.MaxLogin.Mode");
        this.nameCheck = plugin.getConfigLoader().getStringMSG("KickMessages.NameCheck");
        this.floodgateAuto = plugin.getConfigLoader().getBooleanCFG("Options.Floodgate.AutoRegister");
    }

    /*
     * Enabled aggressive block sorting
     */
    @EventHandler
    public void onPreLogin(final PreLoginEvent preLoginEvent) {
        preLoginEvent.registerIntent(this.plugin);
        if (preLoginEvent.getConnection() == null) {
            preLoginEvent.setCancelled(true);
            preLoginEvent.completeIntent(this.plugin);
            return;
        }
        if (!preLoginEvent.getConnection().isConnected()) {
            preLoginEvent.setCancelled(true);
            preLoginEvent.completeIntent(this.plugin);
            return;
        }
        if (preLoginEvent.isCancelled()) {
            preLoginEvent.setCancelled(true);
            preLoginEvent.completeIntent(this.plugin);
            return;
        }
        final String string = preLoginEvent.getConnection().getName();
        if (string == null) {
            preLoginEvent.setCancelled(true);
            preLoginEvent.completeIntent(this.plugin);
            return;
        }
        if (preLoginEvent.getConnection().getUniqueId() != null) {
            if (this.plugin.getFloodgateApi() == null) return;
            if (this.plugin.getFloodgateApi().getPlayer(preLoginEvent.getConnection().getUniqueId()) == null) return;
            this.floodgateConnection(preLoginEvent.getConnection(), string, this.floodgateAuto);
            preLoginEvent.completeIntent(this.plugin);
            return;
        }
        if (string.length() >= 3 && string.length() <= 16 && string.matches(this.nameRegex) && !string.contains("$") && !string.contains(" ") && !string.contains("-")) {
            PlayerData playerData;
            this.plugin.getMaxLogin().incrementLogin();
            if (ServerState.getState() == ServerState.ATTACK) {
                playerData = this.plugin.getPlayerDataList().searchPlayer(string);
                if (playerData == null) {
                    preLoginEvent.setCancelReason(MessageHandler.sendMSG(this.maxLogin));
                    preLoginEvent.setCancelled(true);
                    preLoginEvent.completeIntent(this.plugin);
                    return;
                }
                if (!playerData.isPremium()) {
                    preLoginEvent.setCancelReason(MessageHandler.sendMSG(this.messageMaxCracked));
                    preLoginEvent.setCancelled(true);
                    preLoginEvent.completeIntent(this.plugin);
                    return;
                }
            }
            if (this.plugin.getMaxLogin().mustBlock()) {
                if (this.maxLoginMode == 2) {
                    if (ServerState.getState() == ServerState.ATTACK) {
                        playerData = this.plugin.getPlayerDataList().searchPlayer(string);
                        if (playerData == null) {
                            preLoginEvent.setCancelReason(MessageHandler.sendMSG(this.maxLogin));
                            preLoginEvent.setCancelled(true);
                            preLoginEvent.completeIntent(this.plugin);
                            return;
                        }
                        if (!playerData.isPremium()) {
                            preLoginEvent.setCancelReason(MessageHandler.sendMSG(this.messageMaxCracked));
                            preLoginEvent.setCancelled(true);
                            preLoginEvent.completeIntent(this.plugin);
                            return;
                        }
                    }
                } else {
                    playerData = this.plugin.getPlayerDataList().searchPlayer(string);
                    if (playerData == null) {
                        preLoginEvent.setCancelReason(MessageHandler.sendMSG(this.maxLogin));
                        preLoginEvent.setCancelled(true);
                        preLoginEvent.completeIntent(this.plugin);
                        return;
                    }
                    if (!playerData.isPremium()) {
                        preLoginEvent.setCancelReason(MessageHandler.sendMSG(this.messageMaxCracked));
                        preLoginEvent.setCancelled(true);
                        preLoginEvent.completeIntent(this.plugin);
                        return;
                    }
                }
            }
            if (this.Method == 1) {
                PlayersMethods.verifyNameCheck(string, new CallbackMET<Boolean>(){

                    @Override
                    public void done(Boolean bl) {
                        if (!bl) {
                            preLoginEvent.setCancelReason(MessageHandler.sendMSG(PreLogin.this.nameCheck));
                            preLoginEvent.setCancelled(true);
                            preLoginEvent.completeIntent(PreLogin.this.plugin);
                            return;
                        }
                        SQL.getPlayerDataS(string, "premium", new CallbackSQL<String>(){

                            @Override
                            public void done(String string) {
                                if (string == null) {
                                    PreLogin.this.plugin.getProfileGenerator().Generator(string, new CallbackAPI<UUID>(){

                                        @Override
                                        public void done(UUID uUID) {
                                            if (uUID != null) {
                                                PlayerCache playerCache = new PlayerCache(string, true, SaltGenerator.generateCaptcha(PreLogin.this.lengthC));
                                                PreLogin.this.plugin.getPlayerCacheList().addCache(playerCache);
                                                preLoginEvent.getConnection().setOnlineMode(true);
                                                preLoginEvent.completeIntent(PreLogin.this.plugin);
                                            } else {
                                                PreLogin.this.setOffline(preLoginEvent.getConnection(), string);
                                                preLoginEvent.completeIntent(PreLogin.this.plugin);
                                            }
                                        }

                                        @Override
                                        public void error(Exception exception) {
                                        }
                                    });
                                } else if (string.equals("1")) {
                                    PlayerCache playerCache = new PlayerCache(string, true, SaltGenerator.generateCaptcha(PreLogin.this.lengthC));
                                    PreLogin.this.plugin.getPlayerCacheList().addCache(playerCache);
                                    preLoginEvent.getConnection().setOnlineMode(true);
                                    preLoginEvent.completeIntent(PreLogin.this.plugin);
                                } else if (PreLogin.this.protectCrackedAccounts) {
                                    PreLogin.this.setOffline(preLoginEvent.getConnection(), string);
                                    preLoginEvent.completeIntent(PreLogin.this.plugin);
                                } else {
                                    PreLogin.this.plugin.getProfileGenerator().Generator(string, new CallbackAPI<UUID>(){

                                        @Override
                                        public void done(UUID uUID) {
                                            if (uUID != null) {
                                                PlayerCache playerCache = new PlayerCache(string, true, SaltGenerator.generateCaptcha(PreLogin.this.lengthC));
                                                PreLogin.this.plugin.getPlayerCacheList().addCache(playerCache);
                                                preLoginEvent.getConnection().setOnlineMode(true);
                                                preLoginEvent.completeIntent(PreLogin.this.plugin);
                                            } else {
                                                PreLogin.this.setOffline(preLoginEvent.getConnection(), string);
                                                preLoginEvent.completeIntent(PreLogin.this.plugin);
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
            }
            if (this.Method == 2) {
                PlayersMethods.verifyNameCheck(string, new CallbackMET<Boolean>(){

                    @Override
                    public void done(Boolean bl) {
                        if (!bl) {
                            preLoginEvent.setCancelReason(MessageHandler.sendMSG(PreLogin.this.nameCheck));
                            preLoginEvent.setCancelled(true);
                            preLoginEvent.completeIntent(PreLogin.this.plugin);
                            return;
                        }
                        SQL.getPlayerDataS(string, "premium", new CallbackSQL<String>(){

                            @Override
                            public void done(String string) {
                                if (string == null) {
                                    SQL.isName(string, new CallbackSQL<Boolean>(){

                                        @Override
                                        public void done(Boolean bl) {
                                            if (bl) {
                                                PlayerCache playerCache = new PlayerCache(string, true, SaltGenerator.generateCaptcha(PreLogin.this.lengthC));
                                                PreLogin.this.plugin.getPlayerCacheList().addCache(playerCache);
                                                preLoginEvent.getConnection().setOnlineMode(true);
                                                preLoginEvent.completeIntent(PreLogin.this.plugin);
                                            } else {
                                                PreLogin.this.setOffline(preLoginEvent.getConnection(), string);
                                                preLoginEvent.completeIntent(PreLogin.this.plugin);
                                            }
                                        }

                                        @Override
                                        public void error(Exception exception) {
                                        }
                                    });
                                } else if (string.equals("1")) {
                                    PlayerCache playerCache = new PlayerCache(string, true, SaltGenerator.generateCaptcha(PreLogin.this.lengthC));
                                    PreLogin.this.plugin.getPlayerCacheList().addCache(playerCache);
                                    preLoginEvent.getConnection().setOnlineMode(true);
                                    preLoginEvent.completeIntent(PreLogin.this.plugin);
                                } else {
                                    PreLogin.this.setOffline(preLoginEvent.getConnection(), string);
                                    preLoginEvent.completeIntent(PreLogin.this.plugin);
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
                return;
            }
            if (this.Method != 3) return;
            PlayersMethods.verifyNameCheck(string, new CallbackMET<Boolean>(){

                @Override
                public void done(Boolean bl) {
                    if (!bl) {
                        preLoginEvent.setCancelReason(MessageHandler.sendMSG(PreLogin.this.nameCheck));
                        preLoginEvent.setCancelled(true);
                        preLoginEvent.completeIntent(PreLogin.this.plugin);
                        return;
                    }
                    PreLogin.this.setOffline(preLoginEvent.getConnection(), string);
                    preLoginEvent.completeIntent(PreLogin.this.plugin);
                }

                @Override
                public void error(Exception exception) {
                }
            });
            return;
        }
        preLoginEvent.setCancelled(true);
        preLoginEvent.completeIntent(this.plugin);
    }

    private void setOffline(PendingConnection pendingConnection, String string) {
        pendingConnection.setOnlineMode(false);
        PlayerCache playerCache = new PlayerCache(string, false, SaltGenerator.generateCaptcha(this.lengthC));
        this.plugin.getPlayerCacheList().addCache(playerCache);
    }

    private void floodgateConnection(PendingConnection pendingConnection, String string, Boolean bl) {
        pendingConnection.setOnlineMode(false);
        PlayerCache playerCache = new PlayerCache(string, bl, SaltGenerator.generateCaptcha(this.lengthC));
        this.plugin.getPlayerCacheList().addCache(playerCache);
    }
}
