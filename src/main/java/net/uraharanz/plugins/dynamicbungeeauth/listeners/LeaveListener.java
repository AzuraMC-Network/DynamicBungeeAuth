package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.fix.Fix;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author an5w1r@163.com
 */
public class LeaveListener implements Listener {
    private final DBABungeePlugin plugin;

    public LeaveListener(DBABungeePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = 64)
    public void onDisconnect(PlayerDisconnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        handlePlayerData(player);
        updateDatabaseOnLeave(player);
        cancelScheduledTasks(player);
        cleanupFixList(player);
    }

    private void handlePlayerData(ProxiedPlayer player) {
        PlayerData playerData = plugin.getPlayerDataList().searchPlayer(player.getName());

        if (playerData == null) {
            return;
        }

        playerData.setValid(false);
        playerData.setPlaying(false);

        saveCurrentServer(player);
    }

    private void saveCurrentServer(ProxiedPlayer player) {
        if (player.getServer() != null) {
            String serverName = player.getServer().getInfo().getName();
            SQL.setPlayerData(player, "server", serverName);
        }
    }

    private void updateDatabaseOnLeave(final ProxiedPlayer player) {
        SQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (isRegistered) {
                    updateLastJoinTime(player);
                    setPlayerOffline(player);
                    decrementIPPlayingCount(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateLastJoinTime(final ProxiedPlayer player) {
        SQL.getPlayerDataS(player, "valid", new CallbackSQL<String>() {
            @Override
            public void done(String validStatus) {
                if (validStatus.equals("1")) {
                    Timestamp currentTime = new Timestamp(new Date().getTime());
                    SQL.setPlayerData(player, "lastjoin", currentTime.toString());
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setPlayerOffline(ProxiedPlayer player) {
        SQL.setPlayerData(player, "valid", "0");
    }

    private void decrementIPPlayingCount(ProxiedPlayer player) {
        SQL.mathIPTable(player, "-", "playing", 1);
    }

    private void cancelScheduledTasks(ProxiedPlayer player) {
        String playerName = player.getName();

        cancelRegisterTimer(playerName);
        cancelLoginTimer(playerName);
    }

    private void cancelRegisterTimer(String playerName) {
        ScheduledTask registerTask = plugin.getRegisterTimer().getTimers().get(playerName);

        if (registerTask != null) {
            registerTask.cancel();
            plugin.getRegisterTimer().getTimers().remove(playerName);
        }
    }

    private void cancelLoginTimer(String playerName) {
        ScheduledTask loginTask = plugin.getLoginTimer().getTimers().get(playerName);

        if (loginTask != null) {
            loginTask.cancel();
            plugin.getLoginTimer().getTimers().remove(playerName);
        }
    }

    private void cleanupFixList(ProxiedPlayer player) {
        Fix fix = plugin.getFixList().searchPlayer(player.getName());

        if (fix != null) {
            plugin.getFixList().removePlayer(player.getName());
        }
    }
}
