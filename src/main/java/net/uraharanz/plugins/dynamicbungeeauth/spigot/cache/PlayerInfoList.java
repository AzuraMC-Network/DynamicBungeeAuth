package net.uraharanz.plugins.dynamicbungeeauth.spigot.cache;

import net.uraharanz.plugins.dynamicbungeeauth.spigot.DBABukkitPlugin;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerInfoList {
    private DBABukkitPlugin plugin;
    private ArrayList<PlayerInfo> player;

    public PlayerInfoList(DBABukkitPlugin plugin) {
        this.plugin = plugin;
        this.player = new ArrayList<>();
    }

    public boolean addPlayer(Player player) {
        String string = player.getName();
        String string2 = player.getUniqueId().toString();
        PlayerInfo playerInfo = new PlayerInfo(string, string2);
        if (this.searchPlayer(string) == null) {
            this.player.add(playerInfo);
            return true;
        }
        return false;
    }

    public PlayerInfo searchPlayer(String playerName) {
        for (PlayerInfo playerInfo : this.player) {
            if (playerInfo.getName() == null || !playerInfo.getName().equals(playerName)) continue;
            return playerInfo;
        }
        return null;
    }

    public boolean removePlayer(String playerName) {
        PlayerInfo playerInfo = this.searchPlayer(playerName);
        if (playerInfo != null) {
            this.player.remove(playerInfo);
        }
        return false;
    }
}
