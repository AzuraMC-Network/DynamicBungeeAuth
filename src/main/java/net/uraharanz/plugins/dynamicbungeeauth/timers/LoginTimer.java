package net.uraharanz.plugins.dynamicbungeeauth.timers;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author an5w1r@163.com
 */
public class LoginTimer {

    private final DBABungeePlugin plugin;

    @Getter
    private final HashMap<String, ScheduledTask> timers;

    private final int maxLoginTimeSeconds;

    public LoginTimer(DBABungeePlugin plugin) {
        this.plugin = plugin;
        this.timers = new HashMap<>();
        this.maxLoginTimeSeconds = plugin.getConfigLoader().getIntegerCFG("Timers.LoginMax");
    }

    public void logTimer(ProxiedPlayer player) {
        String playerName = player.getName();

        ScheduledTask task = plugin.getProxy().getScheduler().schedule(
                plugin,
                () -> handleLoginTimeout(player),
                maxLoginTimeSeconds,
                TimeUnit.SECONDS
        );

        timers.put(playerName, task);
    }

    private void handleLoginTimeout(ProxiedPlayer player) {
        String playerName = player.getName();

        if (!timers.containsKey(playerName)) {
            return;
        }

        try {
            SQL.getPlayerDataS(playerName, "valid", new CallbackSQL<String>() {
                @Override
                public void done(String validStatus) {
                    boolean isPlayerLoggedIn = validStatus != null && !validStatus.equals("0");

                    if (isPlayerLoggedIn) {
                        cancelTimer(playerName);
                    } else {
                        kickPlayerForTimeout(player, playerName);
                    }
                }

                @Override
                public void error(Exception e) {
                    cancelTimer(playerName);
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            cancelTimer(playerName);
        }
    }

    private void kickPlayerForTimeout(ProxiedPlayer player, String playerName) {
        cancelTimer(playerName);
        SQL.setPlayerData(player, "lwlogged", "0");

        String kickMessage = plugin.getConfigLoader().getStringMSG("KickMessages.login");
        player.disconnect(MessageHandler.createColoredMessage(kickMessage));
    }

    private void cancelTimer(String playerName) {
        ScheduledTask task = timers.remove(playerName);
        if (task != null) {
            task.cancel();
        }
    }

    public void cancelPlayerTimer(String playerName) {
        cancelTimer(playerName);
    }
}
