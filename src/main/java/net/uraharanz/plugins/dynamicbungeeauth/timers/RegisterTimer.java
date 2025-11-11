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
public class RegisterTimer {

    private final DBABungeePlugin plugin;

    @Getter
    private final HashMap<String, ScheduledTask> timers;

    private final int maxRegisterTimeSeconds;

    public RegisterTimer(DBABungeePlugin plugin) {
        this.plugin = plugin;
        this.timers = new HashMap<>();
        this.maxRegisterTimeSeconds = plugin.getConfigLoader().getIntegerCFG("Timers.RegisterMax");
    }

    public void regTimer(ProxiedPlayer player) {
        String playerName = player.getName();

        ScheduledTask task = plugin.getProxy().getScheduler().schedule(
                plugin,
                () -> handleRegisterTimeout(player),
                maxRegisterTimeSeconds,
                TimeUnit.SECONDS
        );

        timers.put(playerName, task);
    }

    private void handleRegisterTimeout(ProxiedPlayer player) {
        String playerName = player.getName();

        if (!timers.containsKey(playerName)) {
            return;
        }

        try {
            SQL.getPlayerDataS(playerName, "valid", new CallbackSQL<String>() {
                @Override
                public void done(String validStatus) {
                    boolean isPlayerRegistered = validStatus != null && !validStatus.equals("0");

                    if (isPlayerRegistered) {
                        cancelTimer(playerName);
                    } else {
                        kickPlayerForTimeout(player, playerName);
                    }
                }

                @Override
                public void error(Exception exception) {
                    cancelTimer(playerName);
                    exception.printStackTrace();
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
            cancelTimer(playerName);
        }
    }

    private void kickPlayerForTimeout(ProxiedPlayer player, String playerName) {
        cancelTimer(playerName);

        String kickMessage = plugin.getConfigLoader().getStringMSG("KickMessages.register");
        player.disconnect(MessageHandler.createColoredMessage(kickMessage));
    }

    private void cancelTimer(String playerName) {
        ScheduledTask task = timers.remove(playerName);
        if (task != null) {
            task.cancel();
        }
    }
}
