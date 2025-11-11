package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.main;

public class PluginChanelListenerB
implements Listener {
    private final main plugin;
    private final String premiumChannel;
    private final String crackedChannel;

    public PluginChanelListenerB(main main2) {
        this.plugin = main2;
        this.premiumChannel = main2.getConfigLoader().getStringCFG("PluginChannel.premium");
        this.crackedChannel = main2.getConfigLoader().getStringCFG("PluginChannel.cracked");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent pluginMessageEvent) {
        ProxiedPlayer proxiedPlayer;
        String string;
        String string2;
        DataInputStream dataInputStream;
        if (pluginMessageEvent.getTag().equals(this.premiumChannel)) {
            dataInputStream = new DataInputStream(new ByteArrayInputStream(pluginMessageEvent.getData()));
            try {
                string2 = dataInputStream.readUTF();
                string = dataInputStream.readUTF();
                proxiedPlayer = ProxyServer.getInstance().getPlayer(string);
                if (proxiedPlayer != null) {
                    BungeeCord.getInstance().getPluginManager().dispatchCommand(proxiedPlayer, "premium");
                }
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        if (pluginMessageEvent.getTag().equals(this.crackedChannel)) {
            dataInputStream = new DataInputStream(new ByteArrayInputStream(pluginMessageEvent.getData()));
            try {
                string2 = dataInputStream.readUTF();
                string = dataInputStream.readUTF();
                proxiedPlayer = ProxyServer.getInstance().getPlayer(string);
                if (proxiedPlayer != null) {
                    BungeeCord.getInstance().getPluginManager().dispatchCommand(proxiedPlayer, "cracked");
                }
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }
}
