package net.uraharanz.plugins.dynamicbungeeauth.commands;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.methods.PlayersMethods;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.password.HashMethods;

public class CrackedCMD
extends Command {
    private main plugin;

    public CrackedCMD(main main2) {
        super("cracked", "auth.cracked", "crack");
        this.plugin = main2;
    }

    public void execute(final CommandSender commandSender, final String[] stringArray) {
        if (this.plugin.getConfigLoader().getBooleanCFG("Commands.CrackedCMD") && commandSender instanceof ProxiedPlayer) {
            this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
                final ProxiedPlayer proxiedPlayer = (ProxiedPlayer)commandSender;
                if (stringArray.length == 1) {
                    SQL.isPlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                        @Override
                        public void done(Boolean bl) {
                            if (!bl) {
                                commandSender.sendMessage(MessageHandler.sendMSG(CrackedCMD.this.plugin.getConfigLoader().getStringMSG("Commands.cracked.exception")));
                                return;
                            }
                            SQL.getPlayerDataS(proxiedPlayer, "premium", new CallbackSQL<String>(){

                                @Override
                                public void done(String string) {
                                    if (string.equals("0")) {
                                        commandSender.sendMessage(MessageHandler.sendMSG(CrackedCMD.this.plugin.getConfigLoader().getStringMSG("Commands.cracked.already")));
                                    } else {
                                        PlayersMethods.playerRemoveCache(proxiedPlayer);
                                        proxiedPlayer.disconnect(MessageHandler.sendMSG(CrackedCMD.this.plugin.getConfigLoader().getStringMSG("Commands.cracked.successful")));
                                        SQL.deletePlayerData(proxiedPlayer.getName());
                                        SQL.PlayerSQL(proxiedPlayer, 0, 0, true, new CallbackSQL<Boolean>(){

                                            @Override
                                            public void done(Boolean bl) {
                                                ProxyServer.getInstance().getScheduler().schedule(CrackedCMD.this.plugin, () -> SQL.getPlayerDataS(proxiedPlayer.getName(), "salt", new CallbackSQL<String>(){

                                                    @Override
                                                    public void done(String string) {
                                                        if (string != null || string.equalsIgnoreCase("null")) {
                                                            SQL.setPlayerDataS(proxiedPlayer.getName(), "password", HashMethods.HashPassword(proxiedPlayer, stringArray[0], string));
                                                        }
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
                                    }
                                }

                                @Override
                                public void error(Exception exception) {
                                }
                            });
                        }

                        @Override
                        public void error(Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                } else {
                    proxiedPlayer.sendMessage(MessageHandler.sendMSG("Please use the command correctly /cracked password"));
                }
            });
        }
    }
}
