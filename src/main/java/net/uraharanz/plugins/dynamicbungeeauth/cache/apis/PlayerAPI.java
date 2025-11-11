package net.uraharanz.plugins.dynamicbungeeauth.cache.apis;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerAPI {
    private String name;
    private String uuid;

    public PlayerAPI(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

}
