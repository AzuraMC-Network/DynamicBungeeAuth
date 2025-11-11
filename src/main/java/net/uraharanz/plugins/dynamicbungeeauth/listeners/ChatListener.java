package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.methods.PlayersMethods;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

public class ChatListener
implements Listener {
    private main plugin;
    private int Method;

    public ChatListener(main main2) {
        this.plugin = main2;
        this.Method = main2.getConfigLoader().getIntegerCFG("WorkMethod.Value");
    }

    @EventHandler(priority=64)
    public void onPlayerChat(ChatEvent chatEvent) {
        final ProxiedPlayer proxiedPlayer = (ProxiedPlayer)chatEvent.getSender();
        PlayerData playerData = this.plugin.getPlayerDataList().searchPlayer(proxiedPlayer.getName());
        String string = chatEvent.getMessage();
        String[] stringArray = string.split(" ");
        String string2 = stringArray[0];
        if (playerData != null) {
            if (!playerData.isValid() && !playerData.getPassword().equals("null")) {
                if (string2.equalsIgnoreCase("/register") || string2.equalsIgnoreCase("/login") || string2.equalsIgnoreCase("/l") || string2.equalsIgnoreCase("/reg") || string2.equalsIgnoreCase("/premium")) {
                    chatEvent.setCancelled(false);
                } else {
                    PlayersMethods.pMessage(proxiedPlayer, 2);
                    chatEvent.setCancelled(true);
                }
            }
        } else if (string2.equalsIgnoreCase("/register") || string2.equalsIgnoreCase("/login") || string2.equalsIgnoreCase("/l") || string2.equalsIgnoreCase("/reg") || string2.equalsIgnoreCase("/premium")) {
            chatEvent.setCancelled(false);
        } else {
            chatEvent.setCancelled(true);
            SQL.isPlayerDB(proxiedPlayer, new CallbackSQL<Boolean>(){

                @Override
                public void done(Boolean bl) {
                    if (bl) {
                        PlayersMethods.pMessage(proxiedPlayer, 2);
                    } else {
                        PlayersMethods.pMessage(proxiedPlayer, 3);
                    }
                }

                @Override
                public void error(Exception exception) {
                }
            });
        }
    }
}
