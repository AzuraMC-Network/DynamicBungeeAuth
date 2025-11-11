package net.uraharanz.plugins.dynamicbungeeauth.timers;

import java.util.concurrent.TimeUnit;

import net.uraharanz.plugins.dynamicbungeeauth.cache.server.ServerState;
import net.uraharanz.plugins.dynamicbungeeauth.main;

public class MaxLogin {
    private static main plugin;
    private static int max;
    private static int seconds;
    private static int currentCount;
    private static boolean enabled;
    private static int mode;
    private static int shield;

    public MaxLogin(main main2) {
        plugin = main2;
        currentCount = 0;
        max = main2.getConfigLoader().getIntegerCFG("Options.MaxLogin.Max");
        seconds = main2.getConfigLoader().getIntegerCFG("Options.MaxLogin.Seconds");
        enabled = main2.getConfigLoader().getBooleanCFG("Options.MaxLogin.Enabled");
        mode = main2.getConfigLoader().getIntegerCFG("Options.MaxLogin.Mode");
        shield = main2.getConfigLoader().getIntegerCFG("Options.MaxLogin.ShieldDuration");
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
        if (currentCount > max && currentCount != 0) {
            if (mode == 2) {
                if (ServerState.getState() == ServerState.NORMAL) {
                    MaxLogin.startProtection();
                    MaxLogin.removeProtection(shield);
                    return true;
                }
                return true;
            }
            return true;
        }
        return false;
    }

    private static void startProtection() {
        plugin.getProxy().getScheduler().runAsync(main.plugin, () -> ServerState.setState(ServerState.ATTACK));
    }

    private static void removeProtection(int n) {
        plugin.getProxy().getScheduler().schedule(main.plugin, () -> ServerState.setState(ServerState.NORMAL), n, TimeUnit.SECONDS);
    }
}
