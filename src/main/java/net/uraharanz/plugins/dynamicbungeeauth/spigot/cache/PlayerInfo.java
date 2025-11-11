package net.uraharanz.plugins.dynamicbungeeauth.spigot.cache;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerInfo {
    private String name;
    private String uuid;

    public PlayerInfo(String string, String string2) {
        this.name = string;
        this.uuid = string2;
    }

}
