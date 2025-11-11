package net.uraharanz.plugins.dynamicbungeeauth.listeners.loader;

import net.uraharanz.plugins.dynamicbungeeauth.listeners.ChatListener;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.LeaveListener;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.Login;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.PluginChanelListenerB;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.PostLogin;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.PreLogin;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.ServerConnectedListener;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.SwitchListener;
import net.uraharanz.plugins.dynamicbungeeauth.main;

public class LoadListeners {

    public LoadListeners(main main2) {
        main2.getProxy().getPluginManager().registerListener(main2, new PostLogin(main2));
        main2.getProxy().getPluginManager().registerListener(main2, new PreLogin(main2));
        main2.getProxy().getPluginManager().registerListener(main2, new LeaveListener(main2));
        main2.getProxy().getPluginManager().registerListener(main2, new ChatListener(main2));
        main2.getProxy().getPluginManager().registerListener(main2, new SwitchListener(main2));
        main2.getProxy().getPluginManager().registerListener(main2, new ServerConnectedListener(main2));
        main2.getProxy().getPluginManager().registerListener(main2, new PluginChanelListenerB(main2));
        main2.getProxy().getPluginManager().registerListener(main2, new Login(main2));
    }
}
