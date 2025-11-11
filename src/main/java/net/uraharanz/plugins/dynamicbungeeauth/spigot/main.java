package net.uraharanz.plugins.dynamicbungeeauth.spigot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.cache.PlayerInfoList;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.commands.Cracked;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.commands.Location;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.commands.Premium;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.listeners.JoinListener;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.listeners.PlayerEvents;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.listeners.PlayerMovement;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.listeners.PluginChannelListener;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.listeners.WorldEvents;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.PoolManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

@Setter
@Getter
public class main
extends JavaPlugin {
    public static final String USER = "%%__USER__%%";
    public static main plugin;
    private PlayerInfoList playerInfoList;
    public static PluginMessageListener pml;
    public HashMap<String, Integer> taskID;

    public void onEnable() {
        main.loadConfig0();
        plugin = this;
        this.taskID = new HashMap<>();
        Config.save("ConfigS.yml");
        Config.load("ConfigS.yml");
        this.setPlayerInfoList(new PlayerInfoList(this));
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMovement(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new WorldEvents(this), this);
        this.getCommand("setlocation").setExecutor(new Location(this));
        this.getCommand("premiumspigot").setExecutor(new Premium(this));
        this.getCommand("crackedspigot").setExecutor(new Cracked(this));
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "dba:" + Config.get("ConfigS.yml").getString("PluginChannel.premium"));
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "dba:" + Config.get("ConfigS.yml").getString("PluginChannel.cracked"));
        pml = new PluginChannelListener();
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "dba:" + Config.get("ConfigS.yml").getString("PluginChannel.verify"), pml);
        if (Config.get("ConfigS.yml").getBoolean("MySQL.UseSQL")) {
            PoolManager.connectDB(1);
        }
    }

    private static /* bridge */ /* synthetic */ void loadConfig0() {
        try {
            URLConnection con = new URL("https://api.spigotmc.org/legacy/premium.php?user_id=1061623&resource_id=27480&nonce=-2059244194").openConnection();
            con.setConnectTimeout(1000);
            con.setReadTimeout(1000);
            ((HttpURLConnection)con).setInstanceFollowRedirects(true);
            String response = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if ("false".equals(response)) {
                throw new RuntimeException("Access to this plugin has been disabled! Please contact the author!");
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}
