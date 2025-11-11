package net.uraharanz.plugins.dynamicbungeeauth.cache.spam;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpamPlayer {
    private String name;
    private String status;
    private boolean valid;

    public SpamPlayer(String string, String string2, boolean bl) {
        this.name = string;
        this.status = string2;
        this.valid = bl;
    }

}
