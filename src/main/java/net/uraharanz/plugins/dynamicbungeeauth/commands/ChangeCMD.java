package net.uraharanz.plugins.dynamicbungeeauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.password.HashMethods;

/**
 * @author an5w1r@163.com
 */
public class ChangeCMD extends Command {
    private final DBABungeePlugin plugin;
    private final int minPasswordLength;
    private final int maxPasswordLength;

    public ChangeCMD(DBABungeePlugin plugin) {
        super("changepassword", "auth.changepassword", "cpw");
        this.plugin = plugin;
        this.maxPasswordLength = plugin.getConfigLoader().getIntegerCFG("Options.MaxPasswordLength");
        this.minPasswordLength = plugin.getConfigLoader().getIntegerCFG("Options.MinPasswordLength");
    }

    @Override
    public void execute(CommandSender sender, final String[] args) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            if (!(sender instanceof ProxiedPlayer)) {
                return;
            }

            final ProxiedPlayer player = (ProxiedPlayer) sender;
            processPasswordChange(player, args);
        });
    }

    private void processPasswordChange(final ProxiedPlayer player, final String[] args) {
        if (!validateCommandFormat(player, args)) {
            return;
        }

        checkPlayerRegistration(player, args);
    }

    private boolean validateCommandFormat(ProxiedPlayer player, String[] args) {
        if (args.length < 2) {
            sendMessage(player, "Commands.changepassword.wrong");
            return false;
        }
        return true;
    }

    private void checkPlayerRegistration(final ProxiedPlayer player, final String[] args) {
        SQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (!isRegistered) {
                    sendMessage(player, "Commands.changepassword.not");
                    return;
                }

                validatePasswordChange(player, args);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void validatePasswordChange(final ProxiedPlayer player, final String[] args) {
        String oldPassword = args[0];
        String newPassword = args[1];

        if (oldPassword.equals(newPassword)) {
            sendMessage(player, "Commands.changepassword.same");
            return;
        }

        if (!validateNewPasswordLength(player, newPassword)) {
            return;
        }

        verifyCurrentPassword(player, oldPassword, newPassword);
    }

    private boolean validateNewPasswordLength(ProxiedPlayer player, String newPassword) {
        if (newPassword.length() < minPasswordLength) {
            sendMessage(player, "Commands.changepassword.too_short");
            return false;
        }

        if (newPassword.length() > maxPasswordLength) {
            sendMessage(player, "Commands.changepassword.too_long");
            return false;
        }

        return true;
    }

    private void verifyCurrentPassword(final ProxiedPlayer player,
                                       final String oldPassword,
                                       final String newPassword) {
        SQL.getPlayerDataS(player, "password", new CallbackSQL<String>() {
            @Override
            public void done(final String storedPassword) {
                getSaltAndVerify(player, oldPassword, newPassword, storedPassword);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void getSaltAndVerify(final ProxiedPlayer player,
                                  final String oldPassword,
                                  final String newPassword,
                                  final String storedPassword) {
        SQL.getPlayerDataS(player, "salt", new CallbackSQL<String>() {
            @Override
            public void done(String salt) {
                String hashedOldPassword = HashMethods.HashPassword(player, oldPassword, salt);

                if (storedPassword.equals(hashedOldPassword)) {
                    updatePassword(player, newPassword, salt);
                } else {
                    sendMessage(player, "Commands.changepassword.incorrect");
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updatePassword(ProxiedPlayer player, String newPassword, String salt) {
        String hashedNewPassword = HashMethods.HashPassword(player, newPassword, salt);
        SQL.setPlayerData(player, "password", hashedNewPassword);
        sendMessage(player, "Commands.changepassword.successful");
    }

    private void sendMessage(ProxiedPlayer player, String messageKey) {
        String message = plugin.getConfigLoader().getStringMSG(messageKey);
        player.sendMessage(MessageHandler.sendMSG(message));
    }
}
