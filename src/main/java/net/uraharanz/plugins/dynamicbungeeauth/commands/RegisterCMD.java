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

import java.util.concurrent.TimeUnit;

/**
 * @author an5w1r@163.com
 */
public class RegisterCMD extends Command {
    private final DBAPlugin plugin;
    private final int minPasswordLength;
    private final int maxPasswordLength;
    private final boolean captchaEnabled;

    public RegisterCMD(DBAPlugin plugin) {
        super("register", null, "reg");
        this.plugin = plugin;
        this.maxPasswordLength = plugin.getConfigLoader().getIntegerCFG("Options.MaxPasswordLength");
        this.minPasswordLength = plugin.getConfigLoader().getIntegerCFG("Options.MinPasswordLength");
        this.captchaEnabled = plugin.getConfigLoader().getBooleanCFG("Options.Captcha");
    }

    @Override
    public void execute(CommandSender sender, final String[] args) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            if (!(sender instanceof ProxiedPlayer)) {
                return;
            }

            final ProxiedPlayer player = (ProxiedPlayer) sender;
            processRegistration(player, args);
        });
    }

    private void processRegistration(final ProxiedPlayer player, final String[] args) {
        SQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (isRegistered) {
                    sendMessage(player, "Commands.register.exist");
                    return;
                }

                validateRegistrationInput(player, args);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void validateRegistrationInput(ProxiedPlayer player, String[] args) {
        PlayerCache cache = plugin.getPlayerCacheList().searchCache(player.getName());

        if (!validateCommandFormat(player, args, cache)) {
            return;
        }

        if (captchaEnabled && !validateCaptcha(player, args, cache)) {
            return;
        }

        if (!validatePassword(player, args)) {
            return;
        }

        executeRegistration(player, args[0]);
    }

    private boolean validateCommandFormat(ProxiedPlayer player, String[] args, PlayerCache cache) {
        if (captchaEnabled) {
            if (args.length < 2) {
                sendMessageWithCaptcha(player, "Commands.register.wrong", cache);
                return false;
            }
            if (args.length != 3) {
                sendMessageWithCaptcha(player, "Commands.register.non_captcha", cache);
                return false;
            }
        } else {
            if (args.length < 2) {
                sendMessage(player, "Commands.register.wrong");
                return false;
            }
        }
        return true;
    }

    private boolean validateCaptcha(ProxiedPlayer player, String[] args, PlayerCache cache) {
        String inputCaptcha = args[2];
        String expectedCaptcha = cache.getCaptcha();

        if (!inputCaptcha.equals(expectedCaptcha)) {
            sendMessageWithCaptcha(player, "Commands.register.wrong_captcha", cache);
            return false;
        }
        return true;
    }

    private boolean validatePassword(ProxiedPlayer player, String[] args) {
        String password = args[0];
        String confirmPassword = args[1];

        if (!password.equals(confirmPassword)) {
            sendMessage(player, "Commands.register.wrong");
            return false;
        }

        if (password.length() < minPasswordLength) {
            sendMessage(player, "Commands.register.to_short");
            return false;
        }

        if (password.length() > maxPasswordLength) {
            sendMessage(player, "Commands.register.to_long");
            return false;
        }

        if (isBannedPassword(password)) {
            sendMessage(player, "Commands.register.bannedpw");
            return false;
        }

        return true;
    }

    private boolean isBannedPassword(String password) {
        for (String bannedPassword : plugin.getConfigLoader().getStringListCFG("BannedPasswords")) {
            if (password.equals(bannedPassword)) {
                return true;
            }
        }
        return false;
    }

    private void executeRegistration(final ProxiedPlayer player, final String password) {
        PlayersMethods.verifyIPRegister(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isWithinLimit) {
                if (!isWithinLimit) {
                    kickPlayerForIPLimit(player);
                    return;
                }

                performRegistration(player, password);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void kickPlayerForIPLimit(ProxiedPlayer player) {
        String kickMessage = plugin.getConfigLoader().getStringMSG("KickMessages.MaxAccountsIP");
        player.disconnect(MessageHandler.sendMSG(kickMessage));
    }

    private void performRegistration(final ProxiedPlayer player, final String password) {
        sendMessage(player, "Commands.register.registering");

        SQL.PlayerSQL(player, 0, 1, false, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    schedulePasswordHashing(player, password);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void schedulePasswordHashing(final ProxiedPlayer player, final String password) {
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            hashAndSavePassword(player, password);
        }, 1L, TimeUnit.SECONDS);
    }

    private void hashAndSavePassword(final ProxiedPlayer player, final String password) {
        SQL.getPlayerDataS(player, "salt", new CallbackSQL<String>() {
            @Override
            public void done(String salt) {
                String hashedPassword = HashMethods.HashPassword(player, password, salt);
                SQL.setPlayerData(player, "password", hashedPassword);

                completeRegistration(player);
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void completeRegistration(final ProxiedPlayer player) {
        sendMessage(player, "Commands.register.success");

        plugin.getSpamPlayerList().removePlayer(player.getName());
        PlayersMethods.CleanTitles(player);

        performAutoLogin(player);
    }

    private void performAutoLogin(final ProxiedPlayer player) {
        PlayersMethods.asyncGet(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean success) {
                if (success) {
                    PlayersMethods.setValidCache(player);

                    ServerMethods.sendLobbyServer(player);

                    SQL.setPlayerData(player, "lwlogged", "1");
                    PlayersMethods.updatePlaying(player.getName(), true);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void sendMessage(ProxiedPlayer player, String messageKey) {
        String message = plugin.getConfigLoader().getStringMSG(messageKey);
        player.sendMessage(MessageHandler.sendMSG(message));
    }

    private void sendMessageWithCaptcha(ProxiedPlayer player, String messageKey, PlayerCache cache) {
        String message = plugin.getConfigLoader()
                .getStringMSG(messageKey)
                .replaceAll("%captcha%", cache.getCaptcha());
        player.sendMessage(MessageHandler.sendMSG(message));
    }
}
