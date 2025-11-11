package net.uraharanz.plugins.dynamicbungeeauth.listeners.loader;

import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.*;

public class LoadListeners {

    public LoadListeners(DBABungeePlugin plugin) {
        plugin.getProxy().getPluginManager().registerListener(plugin, new PostLogin(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new PreLogin(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new LeaveListener(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new ChatListener(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new SwitchListener(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new ServerConnectedListener(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new PluginChanelListenerB(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new Login(plugin));
    }
}
