package net.uraharanz.plugins.dynamicbungeeauth.utils.importers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.PoolManager;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLImp {
    private static HikariDataSource hikari;
    private static String address;
    private static String database;
    private static String port;
    private static String username;
    private static String password;
    private static boolean ssl;

    public static void connectDB() {
        address = DBAPlugin.plugin.getConfigLoader().getStringCFG("Importers.SQL.ip");
        database = DBAPlugin.plugin.getConfigLoader().getStringCFG("Importers.SQL.database");
        port = DBAPlugin.plugin.getConfigLoader().getStringCFG("Importers.SQL.port");
        username = DBAPlugin.plugin.getConfigLoader().getStringCFG("Importers.SQL.user");
        password = DBAPlugin.plugin.getConfigLoader().getStringCFG("Importers.SQL.password");
        ssl = DBAPlugin.plugin.getConfigLoader().getBooleanCFG("Importers.SSL");
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + address + ":" + port + "/" + database + "?useSSL=" + ssl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(5);
        hikariConfig.setConnectionTimeout(25000L);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikari = new HikariDataSource(hikariConfig);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T> T execute(PoolManager.ConnectionCallback<T> connectionCallback) {
        try (Connection connection = hikari.getConnection()){
            T t = connectionCallback.doInConnection(connection);
            return t;
        }
        catch (SQLException sQLException) {
            throw new IllegalStateException("Error during execution.", sQLException);
        }
    }

    public static void closeConnection() {
        try {
            hikari.getConnection().close();
        }
        catch (SQLException sQLException) {
            sQLException.printStackTrace();
        }
    }

    public interface ConnectionCallback<T> {
        T doInConnection(Connection var1) throws SQLException;
    }
}
