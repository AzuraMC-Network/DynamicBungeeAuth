package net.uraharanz.plugins.dynamicbungeeauth.cache.cache;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerCacheList {
    private final DBABungeePlugin plugin;
    private final List<PlayerCache> caches;
    private final int cleanIntervalMinutes;

    public PlayerCacheList(DBABungeePlugin plugin) {
        this.plugin = plugin;
        this.caches = new ArrayList<>();
        this.cleanIntervalMinutes = plugin.getConfigLoader().getIntegerCFG("Timers.CleanRequest");
    }

    public void addCache(PlayerCache playerCache) {
        if (this.searchCache(playerCache.getName()) == null) {
            this.caches.add(playerCache);
        }
    }

    public PlayerCache searchCache(String playerName) {
        if (playerName != null) {
            for (PlayerCache playerCache : this.caches) {
                if (playerCache.getName().equals(playerName)) {
                    return playerCache;
                }
            }
        }
        return null;
    }

    public boolean removeCache(String playerName) {
        PlayerCache playerCache = this.searchCache(playerName);
        if (playerCache != null) {
            this.caches.remove(playerCache);
            return true;
        }
        return false;
    }

    public void cleanCached() {
        this.plugin.getProxy().getScheduler().schedule(DBABungeePlugin.plugin, () -> {
            List<PlayerCache> expiredCaches = new ArrayList<>();
            for (PlayerCache playerCache : this.caches) {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerCache.getName());
                if (proxiedPlayer == null) {
                    expiredCaches.add(playerCache);
                }
            }
            this.caches.removeAll(expiredCaches);
        }, 1L, this.cleanIntervalMinutes, TimeUnit.MINUTES);
    }
}
