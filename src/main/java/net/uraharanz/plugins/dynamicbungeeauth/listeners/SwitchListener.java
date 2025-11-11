package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.fix.Fix;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.methods.ServerMethods;

public class SwitchListener
implements Listener {
    private final DBABungeePlugin plugin;
    private final boolean OldVersions;

    public SwitchListener(DBABungeePlugin plugin) {
        this.plugin = plugin;
        this.OldVersions = plugin.getConfigLoader().getBooleanCFG("Options.OldVersion");
    }

    @EventHandler(priority=64)
    public void onSwitch(ServerConnectEvent serverConnectEvent) {
        ServerInfo serverInfo = serverConnectEvent.getTarget();
        if (this.OldVersions) {
            Fix fix = this.plugin.getFixList().searchPlayer(serverConnectEvent.getPlayer().getName());
            if (fix == null) {
                Fix fix2 = new Fix(serverConnectEvent.getPlayer().getName(), serverConnectEvent.getPlayer().getUniqueId().toString());
                ServerInfo serverInfo2 = ServerMethods.getAuth();
                if (serverInfo2 == null) {
                    serverInfo2 = ServerMethods.getLobby();
                    if (serverInfo2 != null) {
                        serverConnectEvent.setTarget(serverInfo2);
                    } else {
                        serverConnectEvent.setTarget(serverInfo);
                    }
                } else {
                    serverConnectEvent.setTarget(serverInfo);
                }
                this.plugin.getFixList().addPlayer(fix2);
            } else {
                PlayerData playerData = this.plugin.getPlayerDataList().searchPlayer(serverConnectEvent.getPlayer().getName());
                if (playerData == null) {
                    serverConnectEvent.setCancelled(true);
                } else if (!playerData.isValid()) {
                    serverConnectEvent.setCancelled(true);
                }
            }
        } else {
            PlayerData playerData = this.plugin.getPlayerDataList().searchPlayer(serverConnectEvent.getPlayer().getName());
            if (serverConnectEvent.getReason() == ServerConnectEvent.Reason.JOIN_PROXY || serverConnectEvent.getPlayer().getServer() == null) {
                if (playerData == null) {
                    ServerInfo serverInfo3 = ServerMethods.getAuth();
                    if (serverInfo3 == null) {
                        serverInfo3 = ServerMethods.getLobby();
                        if (serverInfo3 != null) {
                            serverConnectEvent.setTarget(serverInfo3);
                        } else {
                            serverConnectEvent.setTarget(serverInfo);
                        }
                    } else {
                        serverConnectEvent.setTarget(serverInfo);
                    }
                } else if (playerData.isPremium()) {
                    ServerInfo serverInfo4 = ServerMethods.getLobby();
                    if (serverInfo4 != null) {
                        serverConnectEvent.setTarget(serverInfo4);
                    } else {
                        serverInfo4 = ServerMethods.getAuth();
                        if (serverInfo4 != null) {
                            serverConnectEvent.setTarget(serverInfo4);
                        } else {
                            serverConnectEvent.setTarget(serverInfo);
                        }
                    }
                } else {
                    ServerInfo serverInfo5 = ServerMethods.getAuth();
                    if (serverInfo5 == null) {
                        serverInfo5 = ServerMethods.getLobby();
                        if (serverInfo5 != null) {
                            serverConnectEvent.setTarget(serverInfo5);
                        } else {
                            serverConnectEvent.setTarget(serverInfo);
                        }
                    } else {
                        serverConnectEvent.setTarget(serverInfo);
                    }
                }
            } else {
                PlayerData playerData2 = this.plugin.getPlayerDataList().searchPlayer(serverConnectEvent.getPlayer().getName());
                if (playerData2 == null) {
                    serverConnectEvent.setCancelled(true);
                } else if (!playerData2.isValid()) {
                    serverConnectEvent.setCancelled(true);
                } else {
                    serverConnectEvent.setTarget(serverInfo);
                }
            }
        }
    }
}
