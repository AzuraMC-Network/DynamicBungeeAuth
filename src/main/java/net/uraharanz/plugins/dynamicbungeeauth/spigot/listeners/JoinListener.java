package net.uraharanz.plugins.dynamicbungeeauth.spigot.listeners;

import net.uraharanz.plugins.dynamicbungeeauth.spigot.main;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JoinListener
implements Listener {
    private final main plugin;
    private final boolean SetupMode = Config.get("ConfigS.yml").getBoolean("SetupMode");
    private final boolean LocationD = Config.get("ConfigS.yml").getBoolean("Location.Disable");
    private final boolean JoinMD = Config.get("ConfigS.yml").getBoolean("Options.DisableJoinMSG");
    private final boolean LeaveMD = Config.get("ConfigS.yml").getBoolean("Options.DisableLeaveMSG");
    private final boolean Blind = Config.get("ConfigS.yml").getBoolean("Options.BlindnessD");
    private static final boolean Inventory = Config.get("ConfigS.yml").getBoolean("Options.EnableInventory");

    public JoinListener(main main2) {
        this.plugin = main2;
    }

    @EventHandler
    public void AsyncPreLogin(AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent) {
        this.plugin.getPlayerInfoList().removePlayer(asyncPlayerPreLoginEvent.getName());
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        if (this.SetupMode) {
            Bukkit.getConsoleSender().sendMessage("§c========================================================");
            Bukkit.getConsoleSender().sendMessage("§aSETUP MODE OF DBA IS ENABLED MAKE SURE TO CONFIGURE THE PLUGIN AND DISABLE SETUPMODE!!!");
            Bukkit.getConsoleSender().sendMessage("§c========================================================");
        } else if (this.plugin.getPlayerInfoList().searchPlayer(player.getName()) == null) {
            Location location;
            if (!this.LocationD && !Config.get("ConfigS.yml").getString("Location.Cords.world").equalsIgnoreCase("")) {
                location = new Location(Bukkit.getWorld(Config.get("ConfigS.yml").getString("Location.Cords.world")), Config.get("ConfigS.yml").getDouble("Location.Cords.x") + 0.5, Config.get("ConfigS.yml").getDouble("Location.Cords.y"), Config.get("ConfigS.yml").getDouble("Location.Cords.z") + 0.5, (float)Config.get("ConfigS.yml").getInt("Location.Cords.yaw"), (float)Config.get("ConfigS.yml").getInt("Location.Cords.pitch"));
                player.teleport(location);
            }
            if (this.JoinMD) {
                playerJoinEvent.setJoinMessage(null);
            }
            if (!this.Blind) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
            }
            if (Inventory && player.getInventory().firstEmpty() != 0) {
                ItemStack[] itemStackArray2 = player.getInventory().getContents();
                ItemStack[] itemStackArray = player.getInventory().getArmorContents();
                PluginChannelListener.items.put(player.getName(), itemStackArray2);
                PluginChannelListener.armor.put(player.getName(), itemStackArray);
                player.getInventory().clear();
                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);
            }
        }
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        this.plugin.getPlayerInfoList().removePlayer(player.getName());
        if (this.LeaveMD) {
            playerQuitEvent.setQuitMessage(null);
        }
    }
}
