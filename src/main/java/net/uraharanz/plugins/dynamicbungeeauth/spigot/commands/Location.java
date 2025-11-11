package net.uraharanz.plugins.dynamicbungeeauth.spigot.commands;

import net.uraharanz.plugins.dynamicbungeeauth.spigot.DBABukkitPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Location
implements CommandExecutor {
    private final DBABukkitPlugin plugin;

    public Location(DBABukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (string.equalsIgnoreCase("setlocation")) {
            if (commandSender instanceof Player) {
                if (commandSender.hasPermission("dba.setlocation")) {
                    Config.get("ConfigS.yml").set("Location.Cords.world", ((Player)commandSender).getLocation().getWorld().getName());
                    Config.get("ConfigS.yml").set("Location.Cords.yaw", ((Player) commandSender).getLocation().getYaw());
                    Config.get("ConfigS.yml").set("Location.Cords.pitch", ((Player) commandSender).getLocation().getPitch());
                    Config.get("ConfigS.yml").set("Location.Cords.x", ((Player)commandSender).getLocation().getBlockX());
                    Config.get("ConfigS.yml").set("Location.Cords.y", ((Player)commandSender).getLocation().getBlockY());
                    Config.get("ConfigS.yml").set("Location.Cords.z", ((Player)commandSender).getLocation().getBlockZ());
                    Config.save("ConfigS.yml");
                    commandSender.sendMessage("§a§lDBA §8| §aThe spawn Location has ben saved correctly :D!");
                } else {
                    commandSender.sendMessage("§a§lDBA §8| §cYou need perms to do that!");
                }
            } else {
                commandSender.sendMessage("§a§lDBA §8| §cThis command is only for players...");
            }
        }
        return true;
    }
}
