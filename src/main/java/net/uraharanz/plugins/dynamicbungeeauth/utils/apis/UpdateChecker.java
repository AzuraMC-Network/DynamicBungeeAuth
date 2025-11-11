package net.uraharanz.plugins.dynamicbungeeauth.utils.apis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.main;

public class UpdateChecker {
    public static void CheckUpdates() {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> {
            block5: {
                URL uRL;
                try {
                    uRL = new URL("https://api.spigotmc.org/legacy/update.php?resource=27480");
                    URLConnection uRLConnection;
                    try {
                        uRLConnection = uRL.openConnection();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream()));
                        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §aCURRENT VERSION: §a" + main.plugin.getDescription().getVersion());
                        if (bufferedReader.readLine().equals(main.plugin.getDescription().getVersion())) {
                            ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §aYou are running the latest version of the plugin ;)!");
                            break block5;
                        }
                        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §cPlease update your plugin ASAP Download it here.");
                        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §chttps://www.spigotmc.org/resources/dynamicbungeeauth-premium-command-semi-premium-system-sessions.27480/");
                    }
                    catch (IOException iOException) {
                        iOException.printStackTrace();
                    }
                }
                catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
            }
        });
    }

    public static void CheckUpdates(final ProxiedPlayer proxiedPlayer) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> {
            block5: {
                URL uRL;
                try {
                    uRL = new URL("https://api.spigotmc.org/legacy/update.php?resource=27480");
                    URLConnection uRLConnection;
                    try {
                        uRLConnection = uRL.openConnection();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream()));
                        proxiedPlayer.sendMessage("§a§lDBA §8| §aCURRENT VERSION: §a" + main.plugin.getDescription().getVersion());
                        if (bufferedReader.readLine().equals(main.plugin.getDescription().getVersion())) {
                            proxiedPlayer.sendMessage("§a§lDBA §8| §aYou are running the latest version of the plugin ;)!");
                            break block5;
                        }
                        proxiedPlayer.sendMessage("§a§lDBA §8| §cPlease update your plugin ASAP Download it here.");
                        proxiedPlayer.sendMessage("§a§lDBA §8| §chttps://www.spigotmc.org/resources/dynamicbungeeauth-premium-command-semi-premium-system-sessions.27480/");
                    }
                    catch (IOException iOException) {
                        iOException.printStackTrace();
                    }
                }
                catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
            }
        });
    }
}
