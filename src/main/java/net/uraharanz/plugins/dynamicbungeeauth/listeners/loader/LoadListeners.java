package net.uraharanz.plugins.dynamicbungeeauth.listeners.loader;

import net.uraharanz.plugins.dynamicbungeeauth.listeners.ChatListener;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.LeaveListener;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.Login;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.PluginChanelListenerB;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.PostLogin;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.PreLogin;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.ServerConnectedListener;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.SwitchListener;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;

public class LoadListeners {

    public LoadListeners(DBAPlugin plugin) {
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
