package net.uraharanz.plugins.dynamicbungeeauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.cache.PlayerCache;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackAPI;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

import java.util.UUID;

/**
 * @author an5w1r@163.com
 */
public class PremiumCMD extends Command {
    private final DBABungeePlugin plugin;
    private final int workMethod;
    private final int premiumMode;

    public PremiumCMD(DBABungeePlugin plugin) {
        super("premium", "auth.premium");
        this.plugin = plugin;
        this.workMethod = plugin.getConfigLoader().getIntegerCFG("WorkMethod.Value");
        this.premiumMode = plugin.getConfigLoader().getIntegerCFG("Commands.PremiumMode");
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (!isCommandAvailable(sender)) {
            return;
        }

        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            processPremiumConversion(sender);
        });
    }

    private boolean isCommandAvailable(CommandSender sender) {
        if (!plugin.getConfigLoader().getBooleanCFG("Commands.PremiumCMD")) {
            sendMessage(sender, "Commands.premium.deactivated");
            return false;
        }

        if (workMethod == 3) {
            sendMessage(sender, "Commands.premium.wrong_method");
            return false;
        }

        return true;
    }

    private void processPremiumConversion(final CommandSender sender) {
        if (!validatePlayerSender(sender)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;

        verifyMojangAccount(player);
    }

    private boolean validatePlayerSender(CommandSender sender) {
        if (!(sender instanceof ProxiedPlayer)) {
            sendMessage(sender, "Commands.premium.only_player");
            return false;
        }
        return true;
    }

    private void verifyMojangAccount(final ProxiedPlayer player) {
        plugin.getProfileGenerator().Generator(player.getName(), new CallbackAPI<UUID>() {
            @Override
            public void done(UUID uuid) {
                if (uuid == null) {
                    sendMessage(player, "Commands.premium.not_mojang");
                    return;
                }

                checkPlayerRegistration(player);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void checkPlayerRegistration(final ProxiedPlayer player) {
        SQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (!isRegistered) {
                    handleUnregisteredPlayer(player);
                    return;
                }

                processPremiumModeConversion(player);
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
                .getStringMSG("Commands.premium.not_registered")
                .replaceAll("%captcha%", cache.getCaptcha());
        player.sendMessage(MessageHandler.createColoredMessage(message));
    }

    private void processPremiumModeConversion(final ProxiedPlayer player) {
        if (premiumMode != 1) {
            checkLoginRequirement(player);
        } else {
            checkPremiumStatus(player);
        }
    }

    private void checkLoginRequirement(final ProxiedPlayer player) {
        SQL.getPlayerDataS(player, "valid", new CallbackSQL<String>() {
            @Override
            public void done(String validStatus) {
                if (!validStatus.equals("1")) {
                    sendMessage(player, "Commands.premium.require_login");
                    return;
                }

                checkPremiumStatus(player);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void checkPremiumStatus(final ProxiedPlayer player) {
        SQL.getPlayerDataS(player, "premium", new CallbackSQL<String>() {
            @Override
            public void done(String premiumStatus) {
                if (premiumStatus.equalsIgnoreCase("1")) {
                    sendMessage(player, "Commands.premium.already_premium");
                    return;
                }

                removePlayerFromDatabase(player);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void removePlayerFromDatabase(final ProxiedPlayer player) {
        SQL.RemovePlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (!success) {
                    sendMessage(player, "Commands.premium.error");
                    return;
                }

                checkNameList(player);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void checkNameList(final ProxiedPlayer player) {
        SQL.isName(player.getName(), new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isOnList) {
                if (isOnList) {
                    sendMessage(player, "Commands.premium.on_list");
                    return;
                }

                completePremiumConversion(player);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void completePremiumConversion(ProxiedPlayer player) {
        // add to premium list
        SQL.addName(player.getName());

        plugin.getPlayerDataList().removePlayer(player.getName());
        plugin.getPlayerCacheList().removeCache(player.getName());

        sendMessage(player, "Commands.premium.successful");

        String kickMessage = plugin.getConfigLoader().getStringMSG("KickMessages.PremiumKick");
        player.disconnect(MessageHandler.createColoredMessage(kickMessage));
    }

    private void sendMessage(CommandSender sender, String messageKey) {
        String message = plugin.getConfigLoader().getStringMSG(messageKey);
        sender.sendMessage(MessageHandler.createColoredMessage(message));
    }
}
