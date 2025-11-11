package net.uraharanz.plugins.dynamicbungeeauth.utils.importers;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.PoolManager;

public class SQLImp {
    private static HikariDataSource hikari;
    private static String address;
    private static String database;
    private static String port;
    private static String username;
    private static String password;
    private static boolean ssl;

    public static void connectDB() {
        address = main.plugin.getConfigLoader().getStringCFG("Importers.SQL.ip");
        database = main.plugin.getConfigLoader().getStringCFG("Importers.SQL.database");
        port = main.plugin.getConfigLoader().getStringCFG("Importers.SQL.port");
        username = main.plugin.getConfigLoader().getStringCFG("Importers.SQL.user");
        password = main.plugin.getConfigLoader().getStringCFG("Importers.SQL.password");
        ssl = main.plugin.getConfigLoader().getBooleanCFG("Importers.SSL");
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
