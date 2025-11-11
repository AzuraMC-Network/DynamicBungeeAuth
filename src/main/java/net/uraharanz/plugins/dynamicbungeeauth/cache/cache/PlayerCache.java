package net.uraharanz.plugins.dynamicbungeeauth.cache.cache;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerCache {
    private String name;
    private boolean autologin;
    private String captcha;

    public PlayerCache(String string, boolean bl, String string2) {
        this.name = string;
        this.autologin = bl;
        this.captcha = string2;
    }

}
