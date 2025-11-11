package net.uraharanz.plugins.dynamicbungeeauth.utils.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;

import java.sql.*;

/**
 * @author an5w1r@163.com
 */
public class PoolManager {

    private static HikariDataSource hikari;
    private static String address;
    private static String database;
    private static String port;
    private static String username;
    private static String password;
    private static String timezone;
    private static Boolean sslEnabled;
    private static int poolSize;
    private static int connectionTimeout;

    /**
     * connect database and init connection pool
     *
     * @param mode 1 = Spigot, other = Bungee
     */
    public static void connectDB(int mode) {
        loadConfiguration(mode);
        initializeConnectionPool();
    }

    private static void loadConfiguration(int mode) {
        if (mode == 1) {
            loadSpigotConfiguration();
        } else {
            loadBungeeConfiguration();
        }

        if (poolSize == 0) {
            poolSize = 10;
        }
    }

    private static void loadSpigotConfiguration() {
        address = Config.get("ConfigS.yml").getString("MySQL.ip");
        database = Config.get("ConfigS.yml").getString("MySQL.database");
        port = Config.get("ConfigS.yml").getString("MySQL.port");
        username = Config.get("ConfigS.yml").getString("MySQL.user");
        password = Config.get("ConfigS.yml").getString("MySQL.password");
        sslEnabled = Config.get("ConfigS.yml").getBoolean("MySQL.ssl");
        poolSize = Config.get("ConfigS.yml").getInt("MySQL.connections");
        connectionTimeout = Config.get("ConfigS.yml").getInt("MySQL.timeout");

        if (Config.get("ConfigS.yml").get("MySQL.extra") != null) {
            timezone = Config.get("ConfigS.yml").getString("MySQL.extra");
        }
    }

    private static void loadBungeeConfiguration() {
        address = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("MySQL.ip");
        database = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("MySQL.database");
        port = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("MySQL.port");
        username = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("MySQL.user");
        password = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("MySQL.password");
        sslEnabled = DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("MySQL.ssl");
        poolSize = DBABungeePlugin.plugin.getConfigLoader().getIntegerCFG("MySQL.connections");
        connectionTimeout = DBABungeePlugin.plugin.getConfigLoader().getIntegerCFG("MySQL.timeout");

        if (DBABungeePlugin.plugin.getFiles().getCFG().get("MySQL.extra") != null) {
            timezone = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("MySQL.extra");
        }
    }

    private static void initializeConnectionPool() {
        HikariConfig config = new HikariConfig();

        String jdbcUrl = buildJdbcUrl();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(poolSize);
        config.setConnectionTimeout(connectionTimeout);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        hikari = new HikariDataSource(config);
    }

    private static String buildJdbcUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("jdbc:mysql://")
                .append(address)
                .append(":")
                .append(port)
                .append("/")
                .append(database)
                .append("?useSSL=")
                .append(sslEnabled);

        if (timezone != null && !timezone.isEmpty()) {
            urlBuilder.append("&").append(timezone);
        }

        return urlBuilder.toString();
    }

    public static <T> T execute(ConnectionCallback<T> callback) {
        try (Connection connection = hikari.getConnection()) {
            return callback.doInConnection(connection);
        } catch (SQLException e) {
            throw new IllegalStateException("Error during database execution.", e);
        }
    }

    public static void closeConnection() {
        try {
            if (hikari != null && !hikari.isClosed()) {
                hikari.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createPlayerTable() {
        execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS playerdata(" +
                        "uuid VARCHAR(36) PRIMARY KEY NOT NULL, " +
                        "name VARCHAR(30) NOT NULL, " +
                        "email VARCHAR(80), " +
                        "reg_ip VARCHAR(20) NOT NULL, " +
                        "log_ip VARCHAR(20) NOT NULL, " +
                        "password VARCHAR(256) NOT NULL, " +
                        "salt VARCHAR(10) NOT NULL, " +
                        "firstjoin TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "lastjoin TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "premium INT(1) NOT NULL, " +
                        "valid INT(1) NOT NULL, " +
                        "server VARCHAR(40), " +
                        "lwlogged INT(1) NOT NULL" +
                        ") DEFAULT CHARSET=utf8;";
                stmt.execute(sql);
            }
            return null;
        });
    }

    public static void createIPTable() {
        execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS playerip(" +
                        "ip VARCHAR(20) PRIMARY KEY NOT NULL, " +
                        "accounts INT(2) NOT NULL, " +
                        "playing INT(2) NOT NULL, " +
                        "max_accounts INT(2) NOT NULL, " +
                        "max_playing INT(2) NOT NULL" +
                        ") DEFAULT CHARSET=utf8;";
                stmt.execute(sql);
            }
            return null;
        });
    }

    public static void createNamesTable() {
        execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS playernames(" +
                        "name VARCHAR(30) NOT NULL" +
                        ") DEFAULT CHARSET=utf8;";
                stmt.executeUpdate(sql);
            }
            return null;
        });
    }

    public static void alterTable() {
        execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                String checkSql = "SELECT * FROM information_schema.COLUMNS " +
                        "WHERE TABLE_NAME = 'playerdata' AND COLUMN_NAME = 'lwlogged'";
                ResultSet rs = stmt.executeQuery(checkSql);

                if (!rs.next()) {
                    String alterSql = "ALTER TABLE playerdata ADD COLUMN lwlogged INT(1) DEFAULT 0";
                    stmt.executeUpdate(alterSql);
                }
                rs.close();
            }
            return null;
        });
    }

    public static void resetValues() {
        execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("UPDATE playerdata SET valid = 0");
                stmt.executeUpdate("UPDATE playerdata SET lwlogged = 0");
                stmt.executeUpdate("UPDATE playerip SET playing = 0");
            }
            return null;
        });
    }

    public static void resetNames() {
        execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DELETE FROM playernames");
            }
            return null;
        });
    }

    public static void createIndex() {
        execute(connection -> {
            boolean hasNameIndex = false;
            boolean hasUuidIndex = false;
            boolean hasIpIndex = false;

            try (PreparedStatement stmt = connection.prepareStatement("SHOW INDEXES FROM playerdata")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String columnName = rs.getString("Column_name");
                    if (columnName.equalsIgnoreCase("name")) {
                        hasNameIndex = true;
                    }
                    if (columnName.equalsIgnoreCase("uuid")) {
                        hasUuidIndex = true;
                    }
                }
                rs.close();
            }

            try (PreparedStatement stmt = connection.prepareStatement("SHOW INDEXES FROM playerip")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String columnName = rs.getString("Column_name");
                    if (columnName.equalsIgnoreCase("ip")) {
                        hasIpIndex = true;
                    }
                }
                rs.close();
            }

            try (Statement stmt = connection.createStatement()) {
                if (!hasNameIndex) {
                    stmt.executeUpdate("CREATE INDEX name ON playerdata (name)");
                }
                if (!hasUuidIndex) {
                    stmt.executeUpdate("CREATE INDEX uuid ON playerdata (uuid)");
                }
                if (!hasIpIndex) {
                    stmt.executeUpdate("CREATE INDEX ip ON playerip (ip)");
                }
            }

            return null;
        });
    }

    public static void fetchData() {
        int maxAccounts = DBABungeePlugin.plugin.getConfigLoader().getIntegerCFG("Options.IPChecker.MaxAccountsDefault");
        int maxPlaying = DBABungeePlugin.plugin.getConfigLoader().getIntegerCFG("Options.IPChecker.MaxPlayingDefault");

        execute(connection -> {
            String sql = "INSERT INTO playerip " +
                    "SELECT reg_ip AS ip, COUNT(reg_ip) AS accounts, 0 AS playing, " +
                    "? AS max_accounts, ? AS max_playing " +
                    "FROM playerdata GROUP BY reg_ip";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, maxAccounts);
                pstmt.setInt(2, maxPlaying);
                pstmt.executeUpdate();
            }
            return null;
        });
    }

    @FunctionalInterface
    public interface ConnectionCallback<T> {
        T doInConnection(Connection connection) throws SQLException;
    }
}
