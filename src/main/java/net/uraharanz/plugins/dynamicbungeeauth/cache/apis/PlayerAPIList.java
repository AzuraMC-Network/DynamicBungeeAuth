package net.uraharanz.plugins.dynamicbungeeauth.cache.apis;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerAPIList {
    private final DBABungeePlugin plugin;
    private final List<PlayerAPI> requests;
    private final int cleanIntervalMinutes;

    public PlayerAPIList(DBABungeePlugin plugin) {
        this.plugin = plugin;
        this.requests = new ArrayList<>();
        this.cleanIntervalMinutes = plugin.getConfigLoader().getIntegerCFG("Timers.CleanRequest");
    }

    public void addRequest(PlayerAPI playerAPI) {
        if (this.searchRequest(playerAPI.getName()) == null) {
            this.requests.add(playerAPI);
        }
    }

    public PlayerAPI searchRequest(String playerName) {
        if (playerName != null) {
            for (PlayerAPI playerAPI : this.requests) {
                if (playerAPI.getName().equals(playerName)) {
                    return playerAPI;
                }
            }
        }
        return null;
    }

    public boolean removeRequest(String playerName) {
        PlayerAPI playerAPI = this.searchRequest(playerName);
        if (playerAPI != null) {
            this.requests.remove(playerAPI);
            return true;
        }
        return false;
    }

    public void cleanRequest() {
        this.plugin.getProxy().getScheduler().schedule(DBABungeePlugin.plugin, () -> {
            List<PlayerAPI> expiredRequests = new ArrayList<>();
            for (PlayerAPI playerAPI : this.requests) {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerAPI.getName());
                if (proxiedPlayer == null) {
                    expiredRequests.add(playerAPI);
                }
            }
            this.requests.removeAll(expiredRequests);
        }, 1L, this.cleanIntervalMinutes, TimeUnit.MINUTES);
    }
}
