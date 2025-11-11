package net.uraharanz.plugins.dynamicbungeeauth.spigot.cache;

import java.util.ArrayList;

import net.uraharanz.plugins.dynamicbungeeauth.spigot.main;
import org.bukkit.entity.Player;

public class PlayerInfoList {
    private main plugin;
    private ArrayList<PlayerInfo> player;

    public PlayerInfoList(main main2) {
        this.plugin = main2;
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

    public PlayerInfo searchPlayer(String string) {
        for (PlayerInfo playerInfo : this.player) {
            if (playerInfo.getName() == null || !playerInfo.getName().equals(string)) continue;
            return playerInfo;
        }
        return null;
    }

    public boolean removePlayer(String string) {
        PlayerInfo playerInfo = this.searchPlayer(string);
        if (playerInfo != null) {
            this.player.remove(playerInfo);
        }
        return false;
    }
}
