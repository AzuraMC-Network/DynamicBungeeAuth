package net.uraharanz.plugins.dynamicbungeeauth.spigot.listeners;

import net.uraharanz.plugins.dynamicbungeeauth.spigot.cache.PlayerInfo;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.main;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldEvents
implements Listener {
    private main plugin;
    private boolean DisableFood;

    public WorldEvents(main main2) {
        this.plugin = main2;
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
