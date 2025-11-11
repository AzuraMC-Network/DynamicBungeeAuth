package net.uraharanz.plugins.dynamicbungeeauth.spigot.listeners;

import net.uraharanz.plugins.dynamicbungeeauth.spigot.DBABukkitPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.cache.PlayerInfo;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovement
implements Listener {
    private final DBABukkitPlugin plugin;
    private final boolean SetupMode = Config.get("ConfigS.yml").getBoolean("SetupMode");
    private final boolean BlockMovement = Config.get("ConfigS.yml").getBoolean("PlayerOptions.BlockMovement");

    public PlayerMovement(DBABukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        if (!this.SetupMode && this.BlockMovement) {
            Player player = playerMoveEvent.getPlayer();
            PlayerInfo playerInfo = this.plugin.getPlayerInfoList().searchPlayer(player.getName());
            if (playerInfo == null && (playerMoveEvent.getFrom().getX() != playerMoveEvent.getTo().getX() || playerMoveEvent.getFrom().getZ() != playerMoveEvent.getTo().getZ())) {
                Location location = playerMoveEvent.getFrom();
                playerMoveEvent.getPlayer().teleport(location.setDirection(playerMoveEvent.getTo().getDirection()));
            }
        }
    }
}
