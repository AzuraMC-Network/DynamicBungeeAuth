package net.uraharanz.plugins.dynamicbungeeauth.cache.player;

import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PlayerDataList {
    public DBAPlugin plugin;
    private final Map<String, PlayerData> players;
    private final int cleanIntervalMinutes;

    public PlayerDataList(DBAPlugin plugin) {
        this.plugin = plugin;
        this.players = new HashMap<>();
        this.cleanIntervalMinutes = plugin.getConfigLoader().getIntegerCFG("Timers.CleanRequest");
    }

    public boolean addPlayer(PlayerData playerData) {
        PlayerData existingPlayer = this.players.get(playerData.getName());
        if (existingPlayer == null) {
            this.players.put(playerData.getName(), playerData);
            return true;
        }
        return false;
    }

    public PlayerData searchPlayer(String playerName) {
        if (playerName != null) {
            return this.players.get(playerName);
        }
        return null;
    }

    public boolean modifyPlayer(PlayerData playerData) {
        PlayerData existingPlayer = this.searchPlayer(playerData.getName());
        if (existingPlayer != null) {
            this.players.replace(playerData.getName(), existingPlayer, playerData);
            return true;
        }
        return false;
    }

    public boolean removePlayer(String playerName) {
        PlayerData playerData = this.searchPlayer(playerName);
        if (playerData != null) {
            this.players.remove(playerName);
            return true;
        }
        return false;
    }

    public void cleanData() {
        this.plugin.getProxy().getScheduler().schedule(DBAPlugin.plugin, () -> {
            Set<String> playersToRemove = new HashSet<>();
            for (Map.Entry<String, PlayerData> entry : this.players.entrySet()) {
                if (!entry.getValue().isPlaying()) {
                    playersToRemove.add(entry.getKey());
                }
            }
            this.players.keySet().removeAll(playersToRemove);
        }, 1L, this.cleanIntervalMinutes, TimeUnit.MINUTES);
    }
}
