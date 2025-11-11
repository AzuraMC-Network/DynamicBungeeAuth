package net.uraharanz.plugins.dynamicbungeeauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.cache.PlayerCache;
import net.uraharanz.plugins.dynamicbungeeauth.methods.PlayersMethods;
import net.uraharanz.plugins.dynamicbungeeauth.methods.ServerMethods;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.password.HashMethods;

/**
 * @author an5w1r@163.com
 */
public class LoginCMD extends Command {
    private final DBAPlugin plugin;
    private final boolean kickOnWrongPassword;

    public LoginCMD(DBAPlugin plugin) {
        super("login", null, "log", "l");
        this.plugin = plugin;
        this.kickOnWrongPassword = plugin.getConfigLoader().getBooleanCFG("Login.WrongKick");
    }

    @Override
    public void execute(CommandSender sender, final String[] args) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            if (!(sender instanceof ProxiedPlayer)) {
                return;
            }

            final ProxiedPlayer player = (ProxiedPlayer) sender;
            processLogin(player, args);
        });
    }

    private void processLogin(final ProxiedPlayer player, final String[] args) {
        SQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (!isRegistered) {
                    handleUnregisteredPlayer(player);
                    return;
                }

                checkPlayerValidStatus(player, args);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleUnregisteredPlayer(ProxiedPlayer player) {
        PlayerCache cache = plugin.getPlayerCacheList().searchCache(player.getName());
        String message = plugin.getConfigLoader()
                .getStringMSG("Commands.login.register_first")
                .replaceAll("%captcha%", cache.getCaptcha());
        player.sendMessage(MessageHandler.sendMSG(message));
    }

    private void checkPlayerValidStatus(final ProxiedPlayer player, final String[] args) {
        SQL.getPlayerDataS(player, "valid", new CallbackSQL<String>() {
            @Override
            public void done(String validStatus) {
                if (validStatus.equals("1")) {
                    sendMessage(player, "Commands.login.already");
                    return;
                }

                validateLoginCommand(player, args);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void validateLoginCommand(final ProxiedPlayer player, final String[] args) {
        if (args.length != 1) {
            sendMessage(player, "Commands.login.wrong");
            return;
        }

        String password = args[0];
        verifyPassword(player, password);
    }

    private void verifyPassword(final ProxiedPlayer player, final String password) {
        HashMethods.MashMatch(player, password, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isPasswordCorrect) {
                if (!isPasswordCorrect) {
                    handleWrongPassword(player);
                    return;
                }

                checkIPPlayingLimit(player);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleWrongPassword(ProxiedPlayer player) {
        if (kickOnWrongPassword) {
            String kickMessage = plugin.getConfigLoader().getStringMSG("Commands.login.wrong_kick");
            player.disconnect(MessageHandler.sendMSG(kickMessage));
        } else {
            sendMessage(player, "Commands.login.wrong_pass");
        }
    }

    private void checkIPPlayingLimit(final ProxiedPlayer player) {
        PlayersMethods.verifyIPPlaying(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isWithinLimit) {
                if (isWithinLimit) {
                    performSuccessfulLogin(player);
                } else {
                    kickPlayerForIPLimit(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void kickPlayerForIPLimit(ProxiedPlayer player) {
        String kickMessage = plugin.getConfigLoader().getStringMSG("KickMessages.MaxPlayingIP");
        player.disconnect(MessageHandler.sendMSG(kickMessage));
    }

    private void performSuccessfulLogin(ProxiedPlayer player) {
        sendMessage(player, "Commands.login.correct");

        // clean up temp data
        plugin.getSpamPlayerList().removePlayer(player.getName());
        plugin.getLoginTimer().getTimers().remove(player.getName());

        // update player state
        SQL.setPlayerData(player, "valid", "1");
        SQL.setPlayerData(player, "log_ip", player.getAddress().getAddress().getHostAddress());
        SQL.setPlayerData(player, "lwlogged", "1");

        // clean title & cache
        PlayersMethods.cleanAndShowSuccessfulTitle(player);
        PlayersMethods.setValidCache(player);

        // send to lobby
        ServerMethods.sendLobbyServer(player);

        PlayersMethods.updatePlaying(player.getName(), true);
    }

    private void sendMessage(ProxiedPlayer player, String messageKey) {
        String message = plugin.getConfigLoader().getStringMSG(messageKey);
        player.sendMessage(MessageHandler.sendMSG(message));
    }
}
