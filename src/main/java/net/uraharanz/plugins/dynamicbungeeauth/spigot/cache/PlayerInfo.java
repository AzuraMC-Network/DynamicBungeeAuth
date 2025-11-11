package net.uraharanz.plugins.dynamicbungeeauth.spigot.cache;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerInfo {
    private String name;
    private String uuid;

    public PlayerInfo(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

}
