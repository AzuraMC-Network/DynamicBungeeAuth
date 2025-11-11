package net.uraharanz.plugins.dynamicbungeeauth.commands;

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

public class LoginCMD
extends Command {
    private main plugin;
    private boolean kick;

    public LoginCMD(main main2) {
        super("login", null, "log", "l");
        this.plugin = main2;
        this.kick = main2.getConfigLoader().getBooleanCFG("Login.WrongKick");
    }

    public void execute(CommandSender commandSender, final String[] stringArray) {
        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            if (commandSender instanceof ProxiedPlayer) {
                final ProxiedPlayer proxiedPlayer = (ProxiedPlayer)commandSender;
                SQL.isPlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                    @Override
                    public void done(Boolean bl) {
                        if (bl) {
                            SQL.getPlayerDataS(proxiedPlayer, "valid", new CallbackSQL<String>(){

                                @Override
                                public void done(String string) {
                                    if (!string.equals("1")) {
                                        if (stringArray.length == 1) {
                                            HashMethods.MashMatch(proxiedPlayer, stringArray[0], new CallbackSQL<Boolean>(){

                                                @Override
                                                public void done(Boolean bl) {
                                                    if (!bl) {
                                                        if (LoginCMD.this.kick) {
                                                            proxiedPlayer.disconnect(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Commands.login.wrong_kick")));
                                                            return;
                                                        }
                                                        proxiedPlayer.sendMessage(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Commands.login.wrong_pass")));
                                                        return;
                                                    }
                                                    PlayersMethods.verifyIPPlaying(proxiedPlayer, new CallbackSQL<Boolean>(){

                                                        @Override
                                                        public void done(Boolean bl) {
                                                            if (bl) {
                                                                proxiedPlayer.sendMessage(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Commands.login.correct")));
                                                                LoginCMD.this.plugin.getSpamPlayerList().removePlayer(proxiedPlayer.getName());
                                                                LoginCMD.this.plugin.getLoginTimer().getTimers().remove(proxiedPlayer.getName());
                                                                SQL.setPlayerData(proxiedPlayer, "valid", "1");
                                                                PlayersMethods.CleanTitles(proxiedPlayer);
                                                                PlayersMethods.setValidCache(proxiedPlayer);
                                                                ServerMethods.sendLobbyServer(proxiedPlayer);
                                                                SQL.setPlayerData(proxiedPlayer, "log_ip", proxiedPlayer.getAddress().getAddress().getHostAddress());
                                                                SQL.setPlayerData(proxiedPlayer, "lwlogged", "1");
                                                                PlayersMethods.updatePlaying(proxiedPlayer.getName(), true);
                                                            } else {
                                                                proxiedPlayer.disconnect(MessageHandler.sendMSG(LoginCMD.this.plugin.getConfigLoader().getStringMSG("KickMessages.MaxPlayingIP")));
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
                                        proxiedPlayer.sendMessage(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Commands.login.wrong")));
                                        return;
                                    }
                                    proxiedPlayer.sendMessage(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Commands.login.already")));
                                }

                                @Override
                                public void error(Exception exception) {
                                }
                            });
                            return;
                        }
                        PlayerCache playerCache = main.plugin.getPlayerCacheList().searchCache(proxiedPlayer.getName());
                        proxiedPlayer.sendMessage(MessageHandler.sendMSG(main.plugin.getConfigLoader().getStringMSG("Commands.login.register_first").replaceAll("%captcha%", playerCache.getCaptcha())));
                    }

                    @Override
                    public void error(Exception exception) {
                    }
                });
            }
        });
    }
}
