package net.uraharanz.plugins.dynamicbungeeauth.cache.apis;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.main;

public class PlayerAPIList {
    private main plugin;
    private ArrayList<PlayerAPI> player;
    private int Time;

    public PlayerAPIList(main main2) {
        this.plugin = main2;
        this.player = new ArrayList<>();
        this.Time = main2.getConfigLoader().getIntegerCFG("Timers.CleanRequest");
    }

    public void addRequest(PlayerAPI playerAPI) {
        if (this.searchRequest(playerAPI.getName()) == null) {
            this.player.add(playerAPI);
        }
    }

    public PlayerAPI searchRequest(String string) {
        if (string != null) {
            for (PlayerAPI playerAPI : this.player) {
                if (!playerAPI.getName().equals(string)) continue;
                return playerAPI;
            }
        }
        return null;
    }

    public boolean removeRequest(String string) {
        PlayerAPI playerAPI = this.searchRequest(string);
        if (playerAPI != null) {
            this.player.remove(playerAPI);
            return true;
        }
        return false;
    }

    public void cleanRequest() {
        this.plugin.getProxy().getScheduler().schedule(main.plugin, () -> {
            ArrayList<PlayerAPI> arrayList = new ArrayList<>();
            for (PlayerAPI playerAPI : this.player) {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerAPI.getName());
                if (proxiedPlayer != null) continue;
                arrayList.add(playerAPI);
            }
            this.player.removeAll(arrayList);
        }, 1L, this.Time, TimeUnit.MINUTES);
    }
}
