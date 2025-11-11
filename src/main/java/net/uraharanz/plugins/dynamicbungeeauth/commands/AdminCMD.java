package net.uraharanz.plugins.dynamicbungeeauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
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

/**
 * Command: /authadmin
 *
 * @author an5w1r@163.com
 */
public class AdminCMD extends Command {
    private final DBAPlugin plugin;

    public AdminCMD(DBAPlugin plugin) {
        super("authadmin", "auth.admin");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            showHelpMessage(sender);
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "unregister":
                handleUnregister(sender, args);
                break;
            case "disableshield":
                handleDisableShield(sender);
                break;
            case "clearcache":
                handleClearCache(sender, args);
                break;
            case "clearnames":
                handleClearNames(sender);
                break;
            case "premium":
                handlePremium(sender, args);
                break;
            case "cracked":
                handleCracked(sender, args);
                break;
            case "fetchdata":
                handleFetchData(sender);
                break;
            case "playerip":
                handlePlayerIP(sender, args);
                break;
            case "kick":
                handleKick(sender, args);
                break;
            case "forcelogin":
                handleForceLogin(sender, args);
                break;
            default:
                showHelpMessage(sender);
                break;
        }
    }

    private void showHelpMessage(CommandSender sender) {
        for (String line : plugin.getConfigLoader().getStringListMSG("HelpADM")) {
            sender.sendMessage(MessageHandler.sendMSG(line));
        }
    }

    private void sendMessage(CommandSender sender, String key) {
        sender.sendMessage(MessageHandler.sendMSG(plugin.getConfigLoader().getStringMSG(key)));
    }

    private void sendMessage(CommandSender sender, String key, String placeholder, String value) {
        String message = plugin.getConfigLoader().getStringMSG(key).replaceAll(placeholder, value);
        sender.sendMessage(MessageHandler.sendMSG(message));
    }

    private boolean isRedisBungeeAvailable() {
        return ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null;
    }

    private void handleRedisCommand(CommandSender sender, String command, String targetPlayer) {
        if (isRedisBungeeAvailable()) {
            PlayersMethods.sendCommandProxy(command, targetPlayer);
            sendMessage(sender, "Commands.redis");
        } else {
            sendMessage(sender, "Commands.rediserror");
        }
    }

    private void handleUnregister(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "Commands.unregister.correct");
            return;
        }

        final String targetPlayer = args[1];

        PlayersMethods.removePlayerDB(targetPlayer, sender.getName(), new CallbackAPI<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    sendMessage(sender, "Commands.unregister.success");

                    if (isRedisBungeeAvailable()) {
                        PlayersMethods.sendKickProxy(targetPlayer);
                        sendMessage(sender, "Commands.redis");
                    } else {
                        sendMessage(sender, "Commands.rediserror");
                    }
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleDisableShield(CommandSender sender) {
        int maxLoginMode = plugin.getConfigLoader().getIntegerCFG("Options.MaxLogin.Mode");

        if (maxLoginMode == 2) {
            sendMessage(sender, "Commands.disableshield.success");
            MaxLogin.resetCount();
            ServerState.setState(ServerState.NORMAL);
        } else {
            sendMessage(sender, "Commands.disableshield.wrong");
        }
    }

    private void handleClearCache(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "Commands.cacheclear.correct");
            return;
        }

        final String targetPlayer = args[1];

        PlayersMethods.sendToOtherProxy(sender.getName(), targetPlayer, new CallbackMET<Boolean>() {
            @Override
            public void done(Boolean isOnThisProxy) {
                if (isOnThisProxy) {
                    clearPlayerCache(sender, targetPlayer);
                } else {
                    handleRedisCommand(sender, "authadmin clearcache " + targetPlayer, targetPlayer);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void clearPlayerCache(CommandSender sender, String targetPlayer) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(targetPlayer);

        if (player != null && player.isConnected()) {
            sendMessage(player, "Commands.cacheclear.player_msg");
            PlayersMethods.playerRemoveCache(player);
            sendMessage(sender, "Commands.cacheclear.success", "%player%", targetPlayer);
            player.disconnect(MessageHandler.sendMSG(
                    plugin.getConfigLoader().getStringMSG("KickMessages.ClearCache")
            ));
        } else {
            PlayersMethods.playerRemoveCache(targetPlayer);
            sendMessage(sender, "Commands.cacheclear.success", "%player%", targetPlayer);
        }
    }

    private void handleClearNames(CommandSender sender) {
        PoolManager.resetNames();
        sendMessage(sender, "Commands.clearnames.success");
    }

    private void handlePremium(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "Commands.premium_adm.wrong");
            return;
        }

        final String targetPlayer = args[1];

        PlayersMethods.sendToOtherProxy(sender.getName(), targetPlayer, new CallbackMET<Boolean>() {
            @Override
            public void done(Boolean isOnThisProxy) {
                if (isOnThisProxy) {
                    setPremiumStatus(sender, targetPlayer, true);
                } else {
                    handleRedisCommand(sender, "authadmin premium " + targetPlayer, targetPlayer);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setPremiumStatus(final CommandSender sender, final String targetPlayer, final boolean isPremium) {
        SQL.isPlayerDB(targetPlayer, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (!isRegistered) {
                    sendMessage(sender, "Commands.premium_adm.not_registered");
                    return;
                }

                SQL.getPlayerDataS(targetPlayer, "premium", new CallbackSQL<String>() {
                    @Override
                    public void done(String currentStatus) {
                        String expectedStatus = isPremium ? "1" : "0";
                        String alreadyKey = isPremium ? "Commands.premium_adm.already" : "Commands.cracked_adm.already";
                        String successKey = isPremium ? "Commands.premium_adm.success" : "Commands.cracked_adm.success";

                        if (currentStatus.equalsIgnoreCase(expectedStatus)) {
                            sendMessage(sender, alreadyKey);
                        } else {
                            sendMessage(sender, successKey);
                            SQL.setPlayerDataS(targetPlayer, "premium", expectedStatus);
                            PlayersMethods.playerRemoveCache(targetPlayer);

                            ProxiedPlayer player = plugin.getProxy().getPlayer(targetPlayer);
                            if (player != null) {
                                player.disconnect(MessageHandler.sendMSG(
                                        plugin.getConfigLoader().getStringMSG("KickMessages.PremiumKick")
                                ));
                            }
                        }
                    }

                    @Override
                    public void error(Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleCracked(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "Commands.cracked_adm.wrong");
            return;
        }

        final String targetPlayer = args[1];

        PlayersMethods.sendToOtherProxy(sender.getName(), targetPlayer, new CallbackMET<Boolean>() {
            @Override
            public void done(Boolean isOnThisProxy) {
                if (isOnThisProxy) {
                    setPremiumStatus(sender, targetPlayer, false);
                } else {
                    handleRedisCommand(sender, "authadmin cracked " + targetPlayer, targetPlayer);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleFetchData(CommandSender sender) {
        PoolManager.fetchData();
        sendMessage(sender, "Commands.fetchdata");
    }

    /**
     * this is a sub command "/authadmin playerip"
     */
    private void handlePlayerIP(final CommandSender sender, final String[] args) {
        if (args.length <= 3) {
            showPlayerIPHelp(sender);
            return;
        }

        String operation = args[1].toLowerCase();

        switch (operation) {
            case "accounts":
                handleIPOperation(sender, args, "accounts", 3);
                break;
            case "max_accounts":
                handleIPOperation(sender, args, "max_accounts", 3);
                break;
            case "playing":
                handleIPOperation(sender, args, "playing", 3);
                break;
            case "max_playing":
                handleIPOperation(sender, args, "max_playing", 3);
                break;
            case "delete":
                handleIPDelete(sender, args);
                break;
            default:
                showPlayerIPHelp(sender);
                break;
        }
    }

    private void showPlayerIPHelp(CommandSender sender) {
        for (String line : plugin.getConfigLoader().getStringListMSG("HelpPlayerIP")) {
            sender.sendMessage(MessageHandler.sendMSG(line));
        }
    }

    private void handleIPOperation(final CommandSender sender, final String[] args, final String column, final int playerIndex) {
        final String action = args[2];
        final String targetPlayer = args[playerIndex];

        SQL.getPlayerDataS(targetPlayer, "log_ip", new CallbackSQL<String>() {
            @Override
            public void done(String ipAddress) {
                if (ipAddress == null) {
                    sendMessage(sender, "Commands.playerip.ipnotfound");
                    return;
                }

                if (action.equals("add")) {
                    SQL.mathIPTable(ipAddress, "+", column, 1);
                    String messageKey = column.contains("playing") ?
                            "Commands.playerip.addplaying" : "Commands.playerip.addaccount";
                    sendMessage(sender, messageKey, "%player%", targetPlayer);
                } else if (action.equals("minus")) {
                    SQL.mathIPTable(ipAddress, "-", column, 1);
                    String messageKey = column.contains("playing") ?
                            "Commands.playerip.minusplaying" : "Commands.playerip.minusaccount";
                    sendMessage(sender, messageKey, "%player%", targetPlayer);
                } else {
                    showPlayerIPHelp(sender);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleIPDelete(final CommandSender sender, final String[] args) {
        final String targetPlayer = args[2];

        SQL.getPlayerDataS(targetPlayer, "log_ip", new CallbackSQL<String>() {
            @Override
            public void done(String ipAddress) {
                if (ipAddress != null) {
                    SQL.deleteIP(ipAddress);
                    sendMessage(sender, "Commands.playerip.delete", "%player%", targetPlayer);
                } else {
                    sendMessage(sender, "Commands.playerip.ipnotfound");
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleKick(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "Commands.cacheclear.correct");
            return;
        }

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[1]);
        if (player != null) {
            String kickMessage = plugin.getConfigLoader()
                    .getStringMSG("KickMessages.unregisterkick")
                    .replaceAll("%admin%", sender.getName());
            player.disconnect(MessageHandler.sendMSG(kickMessage));
        }
    }

    private void handleForceLogin(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            return;
        }

        final String targetPlayer = args[1];

        PlayersMethods.sendToOtherProxy(sender.getName(), targetPlayer, new CallbackMET<Boolean>() {
            @Override
            public void done(Boolean isOnThisProxy) {
                if (isOnThisProxy) {
                    performForceLogin(sender, targetPlayer);
                } else {
                    handleRedisCommand(sender, "authadmin premium " + targetPlayer, targetPlayer);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void performForceLogin(final CommandSender sender, final String targetPlayer) {
        SQL.isPlayerDB(targetPlayer, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (!isRegistered) {
                    sendMessage(sender, "Commands.forcelogin.not_registered");
                    return;
                }

                ProxiedPlayer player = plugin.getProxy().getPlayer(targetPlayer);
                if (player == null) {
                    sendMessage(sender, "Commands.forcelogin.offline", "%player%", targetPlayer);
                    return;
                }

                String message = plugin.getConfigLoader()
                        .getStringMSG("Commands.forcelogin.success")
                        .replaceAll("%staff_name%", sender.getName());
                player.sendMessage(MessageHandler.sendMSG(message));

                plugin.getLoginTimer().getTimers().remove(player.getName());
                SQL.setPlayerData(player, "valid", "1");
                PlayersMethods.cleanAndShowSuccessfulTitle(player);
                PlayersMethods.setValidCache(player);
                ServerMethods.sendLobbyServer(player);
                SQL.setPlayerData(player, "log_ip", player.getAddress().getAddress().getHostAddress());
                SQL.setPlayerData(player, "lwlogged", "1");
                PlayersMethods.updatePlaying(player.getName(), true);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
