package net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config;

import java.sql.ResultSet;
import java.sql.Statement;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.main;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.PoolManager;
import org.bukkit.Bukkit;

public class SQLSpigot {
    public static void getPlayerDataSpigot(final String string, final String string2, final CallbackSQL<String> callbackSQL) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(main.plugin, () -> PoolManager.execute(connection -> {
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
