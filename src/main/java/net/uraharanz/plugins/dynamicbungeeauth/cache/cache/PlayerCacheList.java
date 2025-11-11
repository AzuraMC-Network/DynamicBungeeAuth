package net.uraharanz.plugins.dynamicbungeeauth.cache.cache;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.main;

public class PlayerCacheList {
    private main plugin;
    private ArrayList<PlayerCache> player;
    private int Time;

    public PlayerCacheList(main main2) {
        this.plugin = main2;
        this.player = new ArrayList<>();
        this.Time = main2.getConfigLoader().getIntegerCFG("Timers.CleanRequest");
    }

    public void addCache(PlayerCache playerCache) {
        if (this.searchCache(playerCache.getName()) == null) {
            this.player.add(playerCache);
        }
    }

    public PlayerCache searchCache(String string) {
        if (string != null) {
            for (PlayerCache playerCache : this.player) {
                if (!playerCache.getName().equals(string)) continue;
                return playerCache;
            }
        }
        return null;
    }

    public boolean removeCache(String string) {
        PlayerCache playerCache = this.searchCache(string);
        if (playerCache != null) {
            this.player.remove(playerCache);
            return true;
        }
        return false;
    }

    public void cleanCached() {
        this.plugin.getProxy().getScheduler().schedule(main.plugin, () -> {
            ArrayList<PlayerCache> arrayList = new ArrayList<>();
            for (PlayerCache playerCache : this.player) {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerCache.getName());
                if (proxiedPlayer != null) continue;
                arrayList.add(playerCache);
            }
            this.player.removeAll(arrayList);
        }, 1L, this.Time, TimeUnit.MINUTES);
    }
}
