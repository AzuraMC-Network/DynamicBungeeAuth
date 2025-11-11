package net.uraharanz.plugins.dynamicbungeeauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.password.HashMethods;

public class ChangeCMD
extends Command {
    private main plugin;
    private int minPass;
    private int maxPass;

    public ChangeCMD(main main2) {
        super("changepassword", "auth.changepassword", "cpw");
        this.plugin = main2;
        this.maxPass = main2.getConfigLoader().getIntegerCFG("Options.MaxPasswordLength");
        this.minPass = main2.getConfigLoader().getIntegerCFG("Options.MinPasswordLength");
    }

    public void execute(CommandSender commandSender, final String[] stringArray) {
        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            if (commandSender instanceof ProxiedPlayer) {
                final ProxiedPlayer proxiedPlayer = (ProxiedPlayer)commandSender;
                if (stringArray.length < 2) {
                    proxiedPlayer.sendMessage(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("Commands.changepassword.wrong")));
                    return;
                }
                SQL.isPlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                    @Override
                    public void done(Boolean bl) {
                        if (!bl) {
                            proxiedPlayer.sendMessage(MessageHandler.sendMSG(ChangeCMD.this.plugin.getConfigLoader().getStringMSG("Commands.changepassword.not")));
                            return;
                        }
                        if (stringArray[0].equals(stringArray[1])) {
                            proxiedPlayer.sendMessage(MessageHandler.sendMSG(ChangeCMD.this.plugin.getConfigLoader().getStringMSG("Commands.changepassword.same")));
                            return;
                        }
                        SQL.getPlayerDataS(proxiedPlayer, "password", new CallbackSQL<String>(){

                            @Override
                            public void done(final String string) {
                                SQL.getPlayerDataS(proxiedPlayer, "salt", new CallbackSQL<String>(){

                                    @Override
                                    public void done(String string4) {
                                        String string2 = HashMethods.HashPassword(proxiedPlayer, stringArray[0], string4);
                                        if (string.equals(string2)) {
                                            String string3 = HashMethods.HashPassword(proxiedPlayer, stringArray[1], string4);
                                            SQL.setPlayerData(proxiedPlayer, "password", string3);
                                            proxiedPlayer.sendMessage(MessageHandler.sendMSG(ChangeCMD.this.plugin.getConfigLoader().getStringMSG("Commands.changepassword.successful")));
                                            return;
                                        }
                                        proxiedPlayer.sendMessage(MessageHandler.sendMSG(ChangeCMD.this.plugin.getConfigLoader().getStringMSG("Commands.changepassword.incorrect")));
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
        });
    }
}
