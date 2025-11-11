package net.uraharanz.plugins.dynamicbungeeauth.spigot.listeners;

import net.uraharanz.plugins.dynamicbungeeauth.spigot.DBABukkitPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerEvents
implements Listener {
    private final DBABukkitPlugin plugin;
    private final boolean interact = Config.get("ConfigS.yml").getBoolean("PlayerOptions.BlockInteract");
    private final boolean drop = Config.get("ConfigS.yml").getBoolean("PlayerOptions.BlockDrop");
    private final boolean pickup = Config.get("ConfigS.yml").getBoolean("PlayerOptions.BlockPickup");
    private final boolean damage = Config.get("ConfigS.yml").getBoolean("PlayerOptions.BlockDamage");
    private final boolean place = Config.get("ConfigS.yml").getBoolean("PlayerOptions.BlockPlace");
    private final boolean breakb = Config.get("ConfigS.yml").getBoolean("PlayerOptions.BlockBreak");
    private final boolean itemheld = Config.get("ConfigS.yml").getBoolean("PlayerOptions.BlockItemHeld");
    private final boolean inventoryopen = Config.get("ConfigS.yml").getBoolean("PlayerOptions.BlockInventoryOpen");
    private final boolean falldamage = Config.get("ConfigS.yml").getBoolean("PlayerOptions.BlockFallDamage");

    public PlayerEvents(DBABukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent playerInteractEvent) {
        if (this.interact) {
            Player player = playerInteractEvent.getPlayer();
            if (this.plugin.getPlayerInfoList().searchPlayer(player.getName()) == null) {
                playerInteractEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent playerDropItemEvent) {
        if (this.drop) {
            Player player = playerDropItemEvent.getPlayer();
            if (this.plugin.getPlayerInfoList().searchPlayer(player.getName()) == null) {
                playerDropItemEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent playerPickupItemEvent) {
        if (this.pickup) {
            Player player = playerPickupItemEvent.getPlayer();
            if (this.plugin.getPlayerInfoList().searchPlayer(player.getName()) == null) {
                playerPickupItemEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent entityDamageEvent) {
        if (this.damage && entityDamageEvent.getEntity() instanceof Player) {
            Player player = (Player)entityDamageEvent.getEntity();
            if (this.plugin.getPlayerInfoList().searchPlayer(player.getName()) == null) {
                entityDamageEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        if (this.place) {
            Player player = blockPlaceEvent.getPlayer();
            if (this.plugin.getPlayerInfoList().searchPlayer(player.getName()) == null) {
                blockPlaceEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        if (this.breakb) {
            Player player = blockBreakEvent.getPlayer();
            if (this.plugin.getPlayerInfoList().searchPlayer(player.getName()) == null) {
                blockBreakEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent playerItemHeldEvent) {
        if (this.itemheld) {
            Player player = playerItemHeldEvent.getPlayer();
            if (this.plugin.getPlayerInfoList().searchPlayer(player.getName()) == null) {
                playerItemHeldEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent inventoryOpenEvent) {
        if (this.inventoryopen) {
            Player player = (Player)inventoryOpenEvent.getPlayer();
            if (this.plugin.getPlayerInfoList().searchPlayer(player.getName()) == null) {
                inventoryOpenEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent entityDamageEvent) {
        if (this.falldamage && entityDamageEvent.getEntity() instanceof Player && entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.FALL) {
            entityDamageEvent.setCancelled(true);
        }
    }
}
