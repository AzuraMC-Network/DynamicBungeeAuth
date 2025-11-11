package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.methods.PlayersMethods;

public class ServerConnectedListener
implements Listener {
    private final DBABungeePlugin plugin;

    public ServerConnectedListener(DBABungeePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onConnectedListened(ServerSwitchEvent serverSwitchEvent) {
        if (serverSwitchEvent.getPlayer() != null) {
            PlayersMethods.sendVerifyMSG(serverSwitchEvent.getPlayer());
        }
    }
}
