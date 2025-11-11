package net.uraharanz.plugins.dynamicbungeeauth.cache.fix;

import java.util.HashMap;
import net.uraharanz.plugins.dynamicbungeeauth.cache.fix.Fix;
import net.uraharanz.plugins.dynamicbungeeauth.main;

public class FixList {
    private HashMap<String, Fix> player;
    private main plugin;

    public FixList(main main2) {
        this.plugin = main2;
        this.player = new HashMap<>();
    }

    public boolean addPlayer(Fix fix) {
        this.player.putIfAbsent(fix.getName(), fix);
        return false;
    }

    public Fix searchPlayer(String string) {
        Fix fix;
        if (string != null && (fix = this.player.get(string)) != null) {
            return fix;
        }
        return null;
    }

    public boolean removePlayer(String string) {
        Fix fix = this.searchPlayer(string);
        if (fix != null) {
            this.player.remove(string);
            return true;
        }
        return false;
    }
}
