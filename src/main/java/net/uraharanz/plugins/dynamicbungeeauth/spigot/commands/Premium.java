package net.uraharanz.plugins.dynamicbungeeauth.spigot.commands;

import net.uraharanz.plugins.dynamicbungeeauth.spigot.DBABukkitPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Premium
implements CommandExecutor {
    private final DBABukkitPlugin plugin;

    public Premium(DBABukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (string.equalsIgnoreCase("premiumspigot") && commandSender instanceof Player && commandSender.hasPermission("dba.premium")) {
            Player player = (Player)commandSender;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeUTF("dba:premium");
                dataOutputStream.writeUTF(commandSender.getName());
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            player.sendPluginMessage(this.plugin, "dba:" + Config.get("ConfigS.yml").getString("PluginChannel.premium"), byteArrayOutputStream.toByteArray());
        }
        return true;
    }
}
