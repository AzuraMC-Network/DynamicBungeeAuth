package net.uraharanz.plugins.dynamicbungeeauth.cache.fix;

import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;

import java.util.HashMap;
import java.util.Map;

public class FixList {
    private final DBABungeePlugin plugin;
    private final Map<String, Fix> fixes;

    public FixList(DBABungeePlugin plugin) {
        this.plugin = plugin;
        this.fixes = new HashMap<>();
    }

    public boolean addPlayer(Fix fix) {
        this.fixes.putIfAbsent(fix.getName(), fix);
        return false;
    }

    public Fix searchPlayer(String playerName) {
        if (playerName != null) {
            return this.fixes.get(playerName);
        }
        return null;
    }

    public boolean removePlayer(String playerName) {
        Fix fix = this.searchPlayer(playerName);
        if (fix != null) {
            this.fixes.remove(playerName);
            return true;
        }
        return false;
    }
}
