package net.uraharanz.plugins.dynamicbungeeauth.commands;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.cache.cache.PlayerCache;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.methods.PlayersMethods;
import net.uraharanz.plugins.dynamicbungeeauth.methods.ServerMethods;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.password.HashMethods;

public class RegisterCMD
extends Command {
    private main plugin;
    private int minPass;
    private int maxPass;
    private boolean Captcha;

    public RegisterCMD(main main2) {
        super("register", null, "reg");
        this.plugin = main2;
        this.maxPass = main2.getConfigLoader().getIntegerCFG("Options.MaxPasswordLength");
        this.minPass = main2.getConfigLoader().getIntegerCFG("Options.MinPasswordLength");
        this.Captcha = main.plugin.getConfigLoader().getBooleanCFG("Options.Captcha");
    }

    public void execute(CommandSender commandSender, final String[] stringArray) {
        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            if (commandSender instanceof ProxiedPlayer) {
                final ProxiedPlayer proxiedPlayer = (ProxiedPlayer)commandSender;
                SQL.isPlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                    /*
                     * Enabled aggressive block sorting
                     */
                    @Override
                    public void done(Boolean bl) {
                        if (bl) {
                            proxiedPlayer.sendMessage(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("Commands.register.exist")));
                            return;
                        }
                        if (RegisterCMD.this.Captcha) {
                            PlayerCache playerCache = main.plugin.getPlayerCacheList().searchCache(proxiedPlayer.getName());
                            if (stringArray.length < 2) {
                                proxiedPlayer.sendMessage(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("Commands.register.wrong").replaceAll("%captcha%", playerCache.getCaptcha())));
                                return;
                            }
                            if (stringArray.length != 3) {
                                proxiedPlayer.sendMessage(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("Commands.register.non_captcha").replaceAll("%captcha%", playerCache.getCaptcha())));
                                return;
                            }
                            if (!stringArray[2].equals(playerCache.getCaptcha())) {
                                proxiedPlayer.sendMessage(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("Commands.register.wrong_captcha").replaceAll("%captcha%", playerCache.getCaptcha())));
                                return;
                            }
                        } else if (stringArray.length < 2) {
                            proxiedPlayer.sendMessage(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("Commands.register.wrong")));
                            return;
                        }
                        if (!stringArray[0].equals(stringArray[1])) {
                            proxiedPlayer.sendMessage(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("Commands.register.wrong")));
                            return;
                        }
                        boolean bl2 = true;
                        for (String string : RegisterCMD.this.plugin.getConfigLoader().getStringListCFG("BannedPasswords")) {
                            if (!stringArray[0].equals(string)) continue;
                            proxiedPlayer.sendMessage(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("Commands.register.bannedpw")));
                            bl2 = false;
                            break;
                        }
                        if (stringArray[0].length() < RegisterCMD.this.minPass) {
                            proxiedPlayer.sendMessage(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("Commands.register.to_short")));
                            bl2 = false;
                        }
                        if (stringArray[0].length() > RegisterCMD.this.maxPass) {
                            proxiedPlayer.sendMessage(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("Commands.register.to_long")));
                            return;
                        }
                        if (!bl2) {
                            return;
                        }
                        PlayersMethods.verifyIPRegister(proxiedPlayer, new CallbackSQL<Boolean>(){

                            @Override
                            public void done(Boolean bl) {
                                if (bl) {
                                    proxiedPlayer.sendMessage(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("Commands.register.registering")));
                                    SQL.PlayerSQL(proxiedPlayer, 0, 1, false, new CallbackSQL<Boolean>(){

                                        @Override
                                        public void done(Boolean bl) {
                                            RegisterCMD.this.plugin.getProxy().getScheduler().schedule(RegisterCMD.this.plugin, () -> SQL.getPlayerDataS(proxiedPlayer, "salt", new CallbackSQL<String>(){

                                                @Override
                                                public void done(String string) {
                                                    String string2 = HashMethods.HashPassword(proxiedPlayer, stringArray[0], string);
                                                    SQL.setPlayerData(proxiedPlayer, "password", string2);
                                                    proxiedPlayer.sendMessage(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("Commands.register.success")));
                                                    RegisterCMD.this.plugin.getSpamPlayerList().removePlayer(proxiedPlayer.getName());
                                                    PlayersMethods.CleanTitles(proxiedPlayer);
                                                    PlayersMethods.asyncGet(proxiedPlayer, new CallbackSQL<Boolean>(){

                                                        @Override
                                                        public void done(Boolean bl1) {
                                                            if (bl1) {
                                                                PlayersMethods.setValidCache(proxiedPlayer);
                                                                ServerMethods.sendLobbyServer(proxiedPlayer);
                                                                SQL.setPlayerData(proxiedPlayer, "lwlogged", "1");
                                                                PlayersMethods.updatePlaying(proxiedPlayer.getName(), true);
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
                                            }), 1L, TimeUnit.SECONDS);
                                        }

                                        @Override
                                        public void error(Exception exception) {
                                        }
                                    });
                                    return;
                                }
                                proxiedPlayer.disconnect(MessageHandler.sendMSG(RegisterCMD.this.plugin.getConfigLoader().getStringMSG("KickMessages.MaxAccountsIP")));
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
        });
    }
}
