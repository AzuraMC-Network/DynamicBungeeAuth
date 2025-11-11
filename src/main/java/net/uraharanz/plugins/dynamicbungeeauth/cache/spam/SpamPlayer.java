package net.uraharanz.plugins.dynamicbungeeauth.cache.spam;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpamPlayer {
    private String name;
    private String status;
    private boolean valid;

    public SpamPlayer(String name, String status, boolean valid) {
        this.name = name;
        this.status = status;
        this.valid = valid;
    }
}
