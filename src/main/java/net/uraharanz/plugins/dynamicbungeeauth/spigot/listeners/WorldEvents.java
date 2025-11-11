package net.uraharanz.plugins.dynamicbungeeauth.spigot.listeners;

import net.uraharanz.plugins.dynamicbungeeauth.spigot.DBABukkitPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.cache.PlayerInfo;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldEvents
implements Listener {
    private DBABukkitPlugin plugin;
    private boolean DisableFood;

    public WorldEvents(DBABukkitPlugin plugin) {
        this.plugin = plugin;
        this.DisableFood = Config.get("ConfigS.yml").getBoolean("WorldOptions.DisableFood");
    }

    @EventHandler
    public void DisableRain(WeatherChangeEvent weatherChangeEvent) {
        weatherChangeEvent.setCancelled(Config.get("ConfigS.yml").getBoolean("WorldOptions.DisableRain"));
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent foodLevelChangeEvent) {
        if (this.DisableFood && foodLevelChangeEvent.getEntity() instanceof Player) {
            Player player = (Player)foodLevelChangeEvent.getEntity();
            PlayerInfo playerInfo = this.plugin.getPlayerInfoList().searchPlayer(player.getName());
            if (playerInfo == null) {
                foodLevelChangeEvent.setCancelled(true);
            }
        }
    }
}
