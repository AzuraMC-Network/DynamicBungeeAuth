package net.uraharanz.plugins.dynamicbungeeauth.commands;

import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.cache.PlayerCache;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackAPI;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

public class PremiumCMD
extends Command {
    private main plugin;
    private int Method;
    private int MethodType;

    public PremiumCMD(main main2) {
        super("premium", "auth.premium");
        this.plugin = main2;
        this.Method = main2.getConfigLoader().getIntegerCFG("WorkMethod.Value");
        this.MethodType = main2.getConfigLoader().getIntegerCFG("Commands.PremiumMode");
    }

    public void execute(final CommandSender commandSender, String[] stringArray) {
        if (this.plugin.getConfigLoader().getBooleanCFG("Commands.PremiumCMD")) {
            if (this.Method == 3) {
                commandSender.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.premium.wrong_method")));
                return;
            }
        } else {
            commandSender.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.premium.deactivated")));
            return;
        }
        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            if (!(commandSender instanceof ProxiedPlayer)) {
                commandSender.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.premium.only_player")));
                return;
            }
            final ProxiedPlayer proxiedPlayer = (ProxiedPlayer)commandSender;
            this.plugin.getProfileGenerator().Generator(commandSender.getName(), new CallbackAPI<UUID>(){

                @Override
                public void done(UUID uUID) {
                    if (uUID == null) {
                        proxiedPlayer.sendMessage(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium.not_mojang")));
                        return;
                    }
                    SQL.isPlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                        @Override
                        public void done(Boolean bl) {
                            if (bl) {
                                if (PremiumCMD.this.MethodType != 1) {
                                    SQL.getPlayerDataS(proxiedPlayer, "valid", new CallbackSQL<String>(){

                                        @Override
                                        public void done(String string) {
                                            if (string.equals("1")) {
                                                SQL.getPlayerDataS(proxiedPlayer, "premium", new CallbackSQL<String>(){

                                                    @Override
                                                    public void done(String string) {
                                                        if (string.equalsIgnoreCase("1")) {
                                                            proxiedPlayer.sendMessage(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium.already_premium")));
                                                            return;
                                                        }
                                                        SQL.RemovePlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                                                            @Override
                                                            public void done(Boolean bl) {
                                                                if (!bl) {
                                                                    commandSender.sendMessage(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium.error")));
                                                                    return;
                                                                }
                                                                SQL.isName(proxiedPlayer.getName(), new CallbackSQL<Boolean>(){

                                                                    @Override
                                                                    public void done(Boolean bl) {
                                                                        if (bl) {
                                                                            commandSender.sendMessage(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium.on_list")));
                                                                            return;
                                                                        }
                                                                        SQL.addName(proxiedPlayer.getName());
                                                                        PremiumCMD.this.plugin.getPlayerDataList().removePlayer(proxiedPlayer.getName());
                                                                        PremiumCMD.this.plugin.getPlayerCacheList().removeCache(proxiedPlayer.getName());
                                                                        commandSender.sendMessage(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium.successful")));
                                                                        proxiedPlayer.disconnect(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("KickMessages.PremiumKick")));
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

                                                    @Override
                                                    public void error(Exception exception) {
                                                    }
                                                });
                                            } else {
                                                proxiedPlayer.sendMessage(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium.require_login")));
                                            }
                                        }

                                        @Override
                                        public void error(Exception exception) {
                                        }
                                    });
                                } else {
                                    SQL.getPlayerDataS(proxiedPlayer, "premium", new CallbackSQL<String>(){

                                        @Override
                                        public void done(String string) {
                                            if (string.equalsIgnoreCase("1")) {
                                                proxiedPlayer.sendMessage(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium.already_premium")));
                                                return;
                                            }
                                            SQL.RemovePlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                                                @Override
                                                public void done(Boolean bl) {
                                                    if (!bl) {
                                                        commandSender.sendMessage(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium.error")));
                                                        return;
                                                    }
                                                    SQL.isName(proxiedPlayer.getName(), new CallbackSQL<Boolean>(){

                                                        @Override
                                                        public void done(Boolean bl) {
                                                            if (bl) {
                                                                commandSender.sendMessage(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium.on_list")));
                                                                return;
                                                            }
                                                            SQL.addName(proxiedPlayer.getName());
                                                            PremiumCMD.this.plugin.getPlayerDataList().removePlayer(proxiedPlayer.getName());
                                                            PremiumCMD.this.plugin.getPlayerCacheList().removeCache(proxiedPlayer.getName());
                                                            commandSender.sendMessage(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium.successful")));
                                                            proxiedPlayer.disconnect(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("KickMessages.PremiumKick")));
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

                                        @Override
                                        public void error(Exception exception) {
                                        }
                                    });
                                }
                            } else {
                                PlayerCache playerCache = main.plugin.getPlayerCacheList().searchCache(proxiedPlayer.getName());
                                commandSender.sendMessage(MessageHandler.sendMSG(PremiumCMD.this.plugin.getConfigLoader().getStringMSG("Commands.premium.not_registered").replaceAll("%captcha%", playerCache.getCaptcha())));
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
        });
    }
}
