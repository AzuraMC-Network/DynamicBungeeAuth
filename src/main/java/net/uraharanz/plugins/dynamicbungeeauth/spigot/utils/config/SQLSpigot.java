package net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config;

import net.uraharanz.plugins.dynamicbungeeauth.spigot.DBABukkitPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.PoolManager;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.Statement;

public class SQLSpigot {
    public static void getPlayerDataSpigot(final String string, final String string2, final CallbackSQL<String> callbackSQL) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(DBABukkitPlugin.plugin, () -> PoolManager.execute(connection -> {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM playerdata WHERE name = '" + string + "';");
            if (resultSet.next()) {
                callbackSQL.done(resultSet.getString(string2));
            } else {
                callbackSQL.done("null");
            }
            return null;
        }));
    }
}
