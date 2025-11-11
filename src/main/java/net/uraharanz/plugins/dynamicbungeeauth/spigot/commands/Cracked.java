package net.uraharanz.plugins.dynamicbungeeauth.spigot.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.main;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Cracked
implements CommandExecutor {
    private final main plugin;

    public Cracked(main main2) {
        this.plugin = main2;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (string.equalsIgnoreCase("crackedspigot") && commandSender instanceof Player && commandSender.hasPermission("dba.cracked")) {
            Player player = (Player)commandSender;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeUTF("dba:cracked");
                dataOutputStream.writeUTF(commandSender.getName());
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            player.sendPluginMessage(this.plugin, "dba:" + Config.get("ConfigS.yml").getString("PluginChannel.cracked"), byteArrayOutputStream.toByteArray());
        }
        return true;
    }
}
