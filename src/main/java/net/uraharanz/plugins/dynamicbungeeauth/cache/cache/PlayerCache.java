package net.uraharanz.plugins.dynamicbungeeauth.cache.cache;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerCache {
    private String name;
    private boolean autologin;
    private String captcha;

    public PlayerCache(String name, boolean autoLogin, String captcha) {
        this.name = name;
        this.autologin = autoLogin;
        this.captcha = captcha;
    }
}
