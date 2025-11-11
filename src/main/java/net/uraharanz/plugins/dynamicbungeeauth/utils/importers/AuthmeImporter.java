package net.uraharanz.plugins.dynamicbungeeauth.utils.importers;

import com.google.common.base.Charsets;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

public class AuthmeImporter {
    public static void importDB() {
        if (main.plugin.getConfigLoader().getBooleanCFG("Importers.Enabled")) {
            main.plugin.getProxy().getScheduler().runAsync(main.plugin, () -> SQLImp.execute(connection -> {
                Statement statement = connection.createStatement();
                String string = main.plugin.getConfigLoader().getStringCFG("Importers.SQL.table");
                String string2 = main.plugin.getConfigLoader().getStringCFG("Importers.SQL.structure.name");
                String string3 = main.plugin.getConfigLoader().getStringCFG("Importers.SQL.structure.password");
                String string4 = main.plugin.getConfigLoader().getStringCFG("Importers.SQL.structure.ip");
                String string5 = main.plugin.getConfigLoader().getStringCFG("Importers.SQL.structure.regip");
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + string + ";");
                while (resultSet.next()) {
                    SQL.PlayerIMPORTER(resultSet.getString(string2), UUID.nameUUIDFromBytes(("OfflinePlayer:" + resultSet.getString(string2)).getBytes(Charsets.UTF_8)).toString(), resultSet.getString(string3), resultSet.getString(string4), resultSet.getString(string5), 0, 0);
                    ProxyServer.getInstance().getLogger().info("§a§lUSER: §C§l" + resultSet.getString(string2) + " §a§lUUID: §C§l" + UUID.nameUUIDFromBytes(("OfflinePlayer:" + resultSet.getString(string2)).getBytes(Charsets.UTF_8)) + " §E§lWAS IMPORTED TO DBA TABLE.");
                }
                return null;
            }));
        }
    }

    public static void ImportSQLite() {
        try {
            File file = new File("SQL.jar");
            URLClassLoader uRLClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
            Method method = DriverManager.class.getDeclaredMethod("getConnection", String.class, Properties.class, Class.class);
            method.setAccessible(true);
            Connection connection = (Connection)method.invoke(null, "jdbc:sqlite:" + file.getPath(), new Properties(), Class.forName("org.sqlite.JDBC", true, uRLClassLoader));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
