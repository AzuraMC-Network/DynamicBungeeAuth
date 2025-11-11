package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.fix.Fix;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

import java.sql.Timestamp;
import java.util.Date;

public class LeaveListener
implements Listener {
    private DBAPlugin plugin;

    public LeaveListener(DBAPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=64)
    public void onDisconnect(PlayerDisconnectEvent playerDisconnectEvent) {
        Fix fix;
        final ProxiedPlayer proxiedPlayer = playerDisconnectEvent.getPlayer();
        PlayerData playerData = this.plugin.getPlayerDataList().searchPlayer(proxiedPlayer.getName());
        if (playerData != null) {
            playerData.setValid(false);
            playerData.setPlaying(false);
            if (playerDisconnectEvent.getPlayer().getServer() != null) {
                SQL.setPlayerData(proxiedPlayer, "server", playerDisconnectEvent.getPlayer().getServer().getInfo().getName());
            }
            SQL.isPlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                @Override
                public void done(Boolean bl) {
                    if (bl) {
                        SQL.getPlayerDataS(proxiedPlayer, "valid", new CallbackSQL<String>(){

                            @Override
                            public void done(String string) {
                                if (string.equals("1")) {
                                    Date date = new Date();
                                    Timestamp timestamp = new Timestamp(date.getTime());
                                    SQL.setPlayerData(proxiedPlayer, "lastjoin", timestamp.toString());
                                }
                            }

                            @Override
                            public void error(Exception exception) {
                            }
                        });
                        SQL.setPlayerData(proxiedPlayer, "valid", "0");
                        SQL.mathIPTable(proxiedPlayer, "-", "playing", 1);
                    }
                }

                @Override
                public void error(Exception exception) {
                }
            });
        }
        ScheduledTask scheduledTask = this.plugin.getRegisterTimer().getTimers().get(proxiedPlayer.getName());
        ScheduledTask scheduledTask2 = this.plugin.getLoginTimer().getTimers().get(proxiedPlayer.getName());
        if (scheduledTask != null) {
            this.plugin.getRegisterTimer().getTimers().get(proxiedPlayer.getName()).cancel();
            this.plugin.getRegisterTimer().getTimers().remove(proxiedPlayer.getName());
        }
        if (scheduledTask2 != null) {
            this.plugin.getLoginTimer().getTimers().get(proxiedPlayer.getName()).cancel();
            this.plugin.getLoginTimer().getTimers().remove(proxiedPlayer.getName());
        }
        if ((fix = this.plugin.getFixList().searchPlayer(playerDisconnectEvent.getPlayer().getName())) != null) {
            this.plugin.getFixList().removePlayer(playerDisconnectEvent.getPlayer().getName());
        }
    }
}
