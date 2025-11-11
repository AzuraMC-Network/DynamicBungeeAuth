package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author an5w1r@163.com
 */
public class PluginChanelListenerB implements Listener {
    private final String premiumChannel;
    private final String crackedChannel;

    public PluginChanelListenerB(DBAPlugin plugin) {
        this.premiumChannel = plugin.getConfigLoader().getStringCFG("PluginChannel.premium");
        this.crackedChannel = plugin.getConfigLoader().getStringCFG("PluginChannel.cracked");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        handleChannelMessage(event, this.premiumChannel, "premium");
        handleChannelMessage(event, this.crackedChannel, "cracked");
    }

    private void handleChannelMessage(PluginMessageEvent event, String expectedChannel, String commandToDispatch) {
        if (!event.getTag().equals(expectedChannel)) {
            return;
        }
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            String targetPlayerName = in.readUTF();
            ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetPlayerName);
            if (targetPlayer != null) {
                ProxyServer.getInstance().getPluginManager().dispatchCommand(targetPlayer, commandToDispatch);
            }
        } catch (IOException ex) {
            ProxyServer.getInstance().getLogger().severe("Failed to read plugin message for channel: " + expectedChannel);
            ex.printStackTrace();
        }
    }
}
