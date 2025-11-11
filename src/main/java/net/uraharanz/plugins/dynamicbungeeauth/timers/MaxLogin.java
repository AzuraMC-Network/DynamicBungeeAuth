package net.uraharanz.plugins.dynamicbungeeauth.timers;

import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.server.ServerState;

import java.util.concurrent.TimeUnit;

public class MaxLogin {
    private static DBABungeePlugin plugin;
    private static int max;
    private static int seconds;
    private static int currentCount;
    private static boolean enabled;
    private static int mode;
    private static int shield;

    public MaxLogin(DBABungeePlugin plugin) {
        MaxLogin.plugin = plugin;
        currentCount = 0;
        max = plugin.getConfigLoader().getIntegerCFG("Options.MaxLogin.Max");
        seconds = plugin.getConfigLoader().getIntegerCFG("Options.MaxLogin.Seconds");
        enabled = plugin.getConfigLoader().getBooleanCFG("Options.MaxLogin.Enabled");
        mode = plugin.getConfigLoader().getIntegerCFG("Options.MaxLogin.Mode");
        shield = plugin.getConfigLoader().getIntegerCFG("Options.MaxLogin.ShieldDuration");
    }

    public void resetCountTimer() {
        plugin.getProxy().getScheduler().schedule(plugin, () -> currentCount = 0, 1L, seconds, TimeUnit.SECONDS);
    }

    public void incrementLogin() {
        if (enabled) {
            ++currentCount;
        }
    }

    public static void resetCount() {
        if (enabled) {
            currentCount = 0;
        }
    }

    public boolean mustBlock() {
        boolean shouldBlock = currentCount > max && currentCount != 0;

        if (shouldBlock && mode == 2 && ServerState.getState() == ServerState.NORMAL) {
            MaxLogin.startProtection();
            MaxLogin.removeProtection(shield);
        }

        return shouldBlock;
    }

    private static void startProtection() {
        plugin.getProxy().getScheduler().runAsync(DBABungeePlugin.plugin, () -> ServerState.setState(ServerState.ATTACK));
    }

    private static void removeProtection(int n) {
        plugin.getProxy().getScheduler().schedule(DBABungeePlugin.plugin, () -> ServerState.setState(ServerState.NORMAL), n, TimeUnit.SECONDS);
    }
}
