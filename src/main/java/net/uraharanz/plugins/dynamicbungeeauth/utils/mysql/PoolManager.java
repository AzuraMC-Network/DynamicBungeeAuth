package net.uraharanz.plugins.dynamicbungeeauth.utils.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;

import java.sql.*;

public class PoolManager {
    private static HikariDataSource hikari;
    private static String address;
    private static String database;
    private static String port;
    private static String username;
    private static String password;
    private static String timezone;
    private static Boolean ssl;
    private static int size;
    private static int timeout;

    public static void connectDB(int n) {
        if (n == 1) {
            address = Config.get("ConfigS.yml").getString("MySQL.ip");
            database = Config.get("ConfigS.yml").getString("MySQL.database");
            port = Config.get("ConfigS.yml").getString("MySQL.port");
            username = Config.get("ConfigS.yml").getString("MySQL.user");
            password = Config.get("ConfigS.yml").getString("MySQL.password");
            ssl = Config.get("ConfigS.yml").getBoolean("MySQL.ssl");
            size = Config.get("ConfigS.yml").getInt("MySQL.connections");
            timeout = Config.get("ConfigS.yml").getInt("MySQL.timeout");
            if (Config.get("ConfigS.yml").get("MySQL.extra") != null) {
                timezone = Config.get("ConfigS.yml").getString("MySQL.extra");
            }
        } else {
            address = DBAPlugin.plugin.getConfigLoader().getStringCFG("MySQL.ip");
            database = DBAPlugin.plugin.getConfigLoader().getStringCFG("MySQL.database");
            port = DBAPlugin.plugin.getConfigLoader().getStringCFG("MySQL.port");
            username = DBAPlugin.plugin.getConfigLoader().getStringCFG("MySQL.user");
            password = DBAPlugin.plugin.getConfigLoader().getStringCFG("MySQL.password");
            ssl = DBAPlugin.plugin.getConfigLoader().getBooleanCFG("MySQL.ssl");
            size = DBAPlugin.plugin.getConfigLoader().getIntegerCFG("MySQL.connections");
            timeout = DBAPlugin.plugin.getConfigLoader().getIntegerCFG("MySQL.timeout");
            if (DBAPlugin.plugin.getFiles().getCFG().get("MySQL.extra") != null) {
                timezone = DBAPlugin.plugin.getConfigLoader().getStringCFG("MySQL.extra");
            }
        }
        if (size == 0) {
            size = 10;
        }
        HikariConfig hikariConfig = new HikariConfig();
        if (timezone != null) {
            hikariConfig.setJdbcUrl("jdbc:mysql://" + address + ":" + port + "/" + database + "?useSSL=" + ssl + "&" + timezone);
        } else {
            hikariConfig.setJdbcUrl("jdbc:mysql://" + address + ":" + port + "/" + database + "?useSSL=" + ssl);
        }
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(size);
        hikariConfig.setConnectionTimeout(timeout);
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
    public static <T> T execute(ConnectionCallback<T> connectionCallback) {
        try (Connection connection = hikari.getConnection();){
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

    public static void createPlayerTable() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        PoolManager.execute(connection -> {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS playerdata(uuid VARCHAR(36) PRIMARY KEY  NOT NULL,name VARCHAR(30) NOT NULL,email VARCHAR(80),reg_ip VARCHAR(20) NOT NULL,log_ip VARCHAR(20) NOT NULL,password VARCHAR(256) NOT NULL,salt VARCHAR(10)  NOT NULL,firstjoin timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,lastjoin timestamp NOT NULL DEFAULT '" + timestamp + "',premium INT(1) NOT NULL,valid INT(1) NOT NULL,server VARCHAR(40),lwlogged INT(1) NOT NULL) DEFAULT CHARSET=utf8;");
            return null;
        });
    }

    public static void createIPTable() {
        PoolManager.execute(connection -> {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS playerip(ip varchar(20) PRIMARY KEY NOT NULL, accounts INT(2) NOT NULL ,playing INT(2) NOT NULL,max_accounts INT(2) NOT NULL,max_playing INT(2) NOT NULL ) DEFAULT CHARSET=utf8;");
            return null;
        });
    }

    public static void createNamesTable() {
        PoolManager.execute(connection -> {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS playernames(name VARCHAR(30) NOT NULL ) DEFAULT CHARSET=utf8;");
            return null;
        });
    }

    public static void alterTable() {
        PoolManager.execute(connection -> {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME = 'playerdata' AND COLUMN_NAME = 'lwlogged'");
            if (!resultSet.next()) {
                statement.executeUpdate("ALTER TABLE playerdata ADD COLUMN lwlogged INT(1) DEFAULT 0");
            }
            return null;
        });
    }

    public static void resetValues() {
        PoolManager.execute(connection -> {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE playerdata SET valid=0");
            statement.executeUpdate("UPDATE playerdata SET lwlogged=0");
            statement.executeUpdate("UPDATE playerip SET playing=0");
            return null;
        });
    }

    public static void resetNames() {
        PoolManager.execute(connection -> {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM playernames;");
            return null;
        });
    }

    public static void createIndex() {
        PoolManager.execute(connection -> {
            String string = "SHOW INDEXES FROM playerdata";
            String string2 = "SHOW INDEXES FROM playerip";
            PreparedStatement preparedStatement = connection.prepareStatement(string);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean bl = false;
            boolean bl2 = false;
            boolean bl3 = false;
            while (resultSet.next()) {
                if (resultSet.getString("Column_name").equalsIgnoreCase("name")) {
                    bl = true;
                }
                if (!resultSet.getString("Column_name").equalsIgnoreCase("uuid")) continue;
                bl2 = true;
            }
            preparedStatement = connection.prepareStatement(string2);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (!resultSet.getString("Column_name").equalsIgnoreCase("ip")) continue;
                bl3 = true;
            }
            String string3 = "CREATE INDEX name ON playerdata (name)";
            String string4 = "CREATE INDEX uuid ON playerdata (uuid)";
            String string5 = "CREATE INDEX ip ON playerip (ip)";
            if (!bl) {
                preparedStatement = connection.prepareStatement(string3);
                preparedStatement.executeUpdate();
            }
            if (!bl2) {
                preparedStatement = connection.prepareStatement(string4);
                preparedStatement.executeUpdate();
            }
            if (!bl3) {
                preparedStatement = connection.prepareStatement(string5);
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();
            connection.close();
            return null;
        });
    }

    public static void fetchData() {
        int n = DBAPlugin.plugin.getConfigLoader().getIntegerCFG("Options.IPChecker.MaxAccountsDefault");
        int n2 = DBAPlugin.plugin.getConfigLoader().getIntegerCFG("Options.IPChecker.MaxPlayingDefault");
        PoolManager.execute(connection -> {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO playerip SELECT reg_ip AS ip ,COUNT(reg_ip) AS accounts, 0 as playing, '" + n + "' as max_accounts, '" + n2 + "' as max_playing FROM playerdata GROUP BY reg_ip");
            return null;
        });
    }

    public static interface ConnectionCallback<T> {
        public T doInConnection(Connection var1) throws SQLException;
    }
}
