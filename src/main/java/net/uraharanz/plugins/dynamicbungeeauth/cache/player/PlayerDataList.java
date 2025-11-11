package net.uraharanz.plugins.dynamicbungeeauth.cache.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.uraharanz.plugins.dynamicbungeeauth.main;

public class PlayerDataList {
    public main plugin;
    private HashMap<String, PlayerData> player;
    private int Time;

    public PlayerDataList(main main2) {
        this.plugin = main2;
        this.player = new HashMap<>();
        this.Time = main2.getConfigLoader().getIntegerCFG("Timers.CleanRequest");
    }

    public boolean addPlayer(PlayerData playerData) {
        PlayerData playerData2 = this.player.get(playerData.getName());
        if (playerData2 == null) {
            this.player.put(playerData.getName(), playerData);
            return true;
        }
        return false;
    }

    public PlayerData searchPlayer(String string) {
        PlayerData playerData;
        if (string != null && (playerData = this.player.get(string)) != null) {
            return playerData;
        }
        return null;
    }

    public boolean modifyPlayer(PlayerData playerData) {
        PlayerData playerData2 = this.searchPlayer(playerData.getName());
        if (playerData2 != null) {
            this.player.replace(playerData.getName(), playerData2, playerData);
            return true;
        }
        return false;
    }

    public boolean removePlayer(String string) {
        PlayerData playerData = this.searchPlayer(string);
        if (playerData != null) {
            this.player.remove(string);
            return true;
        }
        return false;
    }

    public void cleanData() {
        this.plugin.getProxy().getScheduler().schedule(main.plugin, () -> {
            HashSet<String> hashSet = new HashSet<>();
            for (Map.Entry<String, PlayerData> entry : this.player.entrySet()) {
                if (entry.getValue().isPlaying()) continue;
                hashSet.add(entry.getKey());
            }
            this.player.keySet().removeAll(hashSet);
        }, 1L, this.Time, TimeUnit.MINUTES);
    }
}
