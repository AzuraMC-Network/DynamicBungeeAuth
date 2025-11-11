package net.uraharanz.plugins.dynamicbungeeauth.commands;

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

import java.util.concurrent.TimeUnit;

/**
 * @author an5w1r@163.com
 */
public class CrackedCMD extends Command {
    private final main plugin;

    public CrackedCMD(main plugin) {
        super("cracked", "auth.cracked", "crack");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (!isCommandEnabled()) {
            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            processCrackedConversion(player, args);
        });
    }

    private boolean isCommandEnabled() {
        return plugin.getConfigLoader().getBooleanCFG("Commands.CrackedCMD");
    }

    private void processCrackedConversion(final ProxiedPlayer player, final String[] args) {
        if (!validateCommandFormat(player, args)) {
            return;
        }

        checkPlayerRegistration(player, args);
    }

    private boolean validateCommandFormat(ProxiedPlayer player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(MessageHandler.sendMSG(
                    "Please use the command correctly /cracked password"
            ));
            return false;
        }
        return true;
    }

    private void checkPlayerRegistration(final ProxiedPlayer player, final String[] args) {
        SQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (!isRegistered) {
                    sendMessage(player, "Commands.cracked.exception");
                    return;
                }

                checkPremiumStatus(player, args);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void checkPremiumStatus(final ProxiedPlayer player, final String[] args) {
        SQL.getPlayerDataS(player, "premium", new CallbackSQL<String>() {
            @Override
            public void done(String premiumStatus) {
                if (premiumStatus.equals("0")) {
                    sendMessage(player, "Commands.cracked.already");
                } else {
                    performCrackedConversion(player, args[0]);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void performCrackedConversion(final ProxiedPlayer player, final String password) {
        PlayersMethods.playerRemoveCache(player);

        String successMessage = plugin.getConfigLoader().getStringMSG("Commands.cracked.successful");
        player.disconnect(MessageHandler.sendMSG(successMessage));

        SQL.deletePlayerData(player.getName());

        createCrackedAccount(player, password);
    }

    private void createCrackedAccount(final ProxiedPlayer player, final String password) {
        SQL.PlayerSQL(player, 0, 0, true, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    schedulePasswordSetting(player, password);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void schedulePasswordSetting(final ProxiedPlayer player, final String password) {
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            setPasswordForCrackedAccount(player, password);
        }, 1L, TimeUnit.SECONDS);
    }

    private void setPasswordForCrackedAccount(final ProxiedPlayer player, final String password) {
        SQL.getPlayerDataS(player.getName(), "salt", new CallbackSQL<String>() {
            @Override
            public void done(String salt) {
                if (isValidSalt(salt)) {
                    String hashedPassword = HashMethods.HashPassword(player, password, salt);
                    SQL.setPlayerDataS(player.getName(), "password", hashedPassword);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean isValidSalt(String salt) {
        return salt != null && !salt.equalsIgnoreCase("null");
    }

    private void sendMessage(ProxiedPlayer player, String messageKey) {
        String message = plugin.getConfigLoader().getStringMSG(messageKey);
        player.sendMessage(MessageHandler.sendMSG(message));
    }
}
