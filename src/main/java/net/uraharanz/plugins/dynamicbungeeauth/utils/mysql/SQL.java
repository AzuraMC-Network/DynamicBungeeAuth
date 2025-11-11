package net.uraharanz.plugins.dynamicbungeeauth.utils.mysql;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.random.SaltGenerator;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @author an5w1r@163.com
 */
public class SQL {

    private static final String PLAYERDATA_TABLE = "playerdata";
    private static final String PLAYERIP_TABLE = "playerip";
    private static final String PLAYERNAMES_TABLE = "playernames";

    public static UUID generateOfflineUUID(String playerName) {
        Objects.requireNonNull(playerName);
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * register new player or update exist player
     */
    public static void PlayerSQL(ProxiedPlayer player, int premium, int valid, boolean useOfflineUUID, CallbackSQL<Boolean> callback) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            String playerName = player.getName();
            String uuid = useOfflineUUID ? generateOfflineUUID(playerName).toString() : player.getUniqueId().toString();
            String ipAddress = player.getAddress().getAddress().getHostAddress();

            PoolManager.execute(connection -> {
                String checkSql = "SELECT name FROM " + PLAYERDATA_TABLE + " WHERE uuid = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                    checkStmt.setString(1, uuid);
                    ResultSet rs = checkStmt.executeQuery();

                    if (!rs.next()) {
                        insertNewPlayer(connection, uuid, playerName, ipAddress, premium, valid);
                    }

                    rs.close();
                }

                callback.done(true);
                return null;
            });
        });
    }

    public static void PlayerIMPORTER(String playerName, String uuid, String password, String lastIP, String regIP, int premium, int valid) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String checkSql = "SELECT * FROM " + PLAYERDATA_TABLE + " WHERE uuid = ?";
                    try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                        checkStmt.setString(1, uuid);
                        ResultSet rs = checkStmt.executeQuery();

                        if (!rs.next()) {
                            String insertSql = "INSERT INTO " + PLAYERDATA_TABLE +
                                    " (uuid, name, email, reg_ip, log_ip, password, salt, firstjoin, lastjoin, premium, valid, server, lwlogged) " +
                                    "VALUES (?, ?, 'null', ?, ?, ?, 'null', ?, ?, ?, ?, 'null', '0')";

                            Timestamp now = new Timestamp(new Date().getTime());

                            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                                insertStmt.setString(1, uuid);
                                insertStmt.setString(2, playerName);
                                insertStmt.setString(3, regIP);
                                insertStmt.setString(4, lastIP);
                                insertStmt.setString(5, password);
                                insertStmt.setTimestamp(6, now);
                                insertStmt.setTimestamp(7, now);
                                insertStmt.setInt(8, premium);
                                insertStmt.setInt(9, valid);
                                insertStmt.executeUpdate();
                            }
                        }

                        rs.close();
                    }
                    return null;
                })
        );
    }

    private static void insertNewPlayer(java.sql.Connection connection,
                                        String uuid, String playerName,
                                        String ipAddress,
                                        int premium,
                                        int valid) throws java.sql.SQLException {
        String insertSql = "INSERT INTO " + PLAYERDATA_TABLE +
                " (uuid, name, email, reg_ip, log_ip, password, salt, firstjoin, lastjoin, premium, valid, server, lwlogged) " +
                "VALUES (?, ?, 'null', ?, ?, 'null', ?, ?, ?, ?, ?, 'null', '0')";

        Timestamp now = new Timestamp(new Date().getTime());

        try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
            stmt.setString(1, uuid);
            stmt.setString(2, playerName);
            stmt.setString(3, ipAddress);
            stmt.setString(4, ipAddress);
            stmt.setString(5, SaltGenerator.generateString());
            stmt.setTimestamp(6, now);
            stmt.setTimestamp(7, now);
            stmt.setInt(8, premium);
            stmt.setInt(9, valid);
            stmt.execute();
        }
    }

    public static void deletePlayerData(String playerName) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
            PoolManager.execute(connection -> {
                String sql = "DELETE FROM " + PLAYERDATA_TABLE + " WHERE name = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, playerName);
                    stmt.execute();
                }
                return true;
            })
        );
    }

    public static void getPlayer(ProxiedPlayer player, CallbackSQL<Boolean> callback) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            String uuid = player.getUniqueId().toString();

            PoolManager.execute(connection -> {
                String sql = "SELECT * FROM " + PLAYERDATA_TABLE + " WHERE uuid = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, uuid);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        if (!rs.getString("name").equals(player.getName())) {
                            updatePlayerName(connection, player.getName(), uuid);
                        }

                        PlayerData playerData = createPlayerDataFromResultSet(rs, true);
                        updatePlayerDataCache(playerData);
                    } else {
                        PlayerData emptyData = createEmptyPlayerData(player);
                        updatePlayerDataCache(emptyData);
                    }

                    rs.close();
                    callback.done(true);
                }
                return null;
            });
        });
    }

    private static void updatePlayerName(java.sql.Connection connection, String newName, String uuid) throws SQLException {
        String sql = "UPDATE " + PLAYERDATA_TABLE + " SET name = ? WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setString(2, uuid);
            stmt.execute();
        }
    }

    private static PlayerData createPlayerDataFromResultSet(ResultSet rs, boolean isOnline) throws SQLException {
        return new PlayerData(
                rs.getString("uuid"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("reg_ip"),
                rs.getString("log_ip"),
                rs.getString("password"),
                rs.getString("salt"),
                rs.getDate("firstjoin"),
                rs.getDate("lastjoin"),
                rs.getBoolean("premium"),
                rs.getBoolean("valid"),
                rs.getString("server"),
                rs.getBoolean("lwlogged"),
                isOnline
        );
    }

    private static PlayerData createEmptyPlayerData(ProxiedPlayer player) {
        return new PlayerData(
                player.getUniqueId().toString(),
                player.getName(),
                "null", "null", "null", "null", "null",
                null, null,
                false, false, "null", false, false
        );
    }

    private static void updatePlayerDataCache(PlayerData playerData) {
        if (DBABungeePlugin.plugin.getPlayerDataList().searchPlayer(playerData.getName()) == null) {
            DBABungeePlugin.plugin.getPlayerDataList().addPlayer(playerData);
        } else {
            DBABungeePlugin.plugin.getPlayerDataList().modifyPlayer(playerData);
        }
    }

    public static void isPlayerDB(ProxiedPlayer player, CallbackSQL<Boolean> callback) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            if (player == null) {
                callback.done(false);
                return;
            }

            String uuid = player.getUniqueId().toString();

            PoolManager.execute(connection -> {
                String sql = "SELECT name FROM " + PLAYERDATA_TABLE + " WHERE uuid = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, uuid);
                    ResultSet rs = stmt.executeQuery();

                    boolean exists = rs.next();
                    rs.close();

                    callback.done(exists);
                }
                return null;
            });
        });
    }

    public static void isPlayerDB(String playerName, CallbackSQL<Boolean> callback) {
        long startTime = System.currentTimeMillis();

        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            if (playerName == null) {
                callback.done(false);
                return;
            }

            PoolManager.execute(connection -> {
                String sql = "SELECT * FROM " + PLAYERDATA_TABLE + " WHERE name = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, playerName);
                    ResultSet rs = stmt.executeQuery();

                    boolean exists = rs.next();
                    rs.close();

                    callback.done(exists);
                }
                return null;
            });

            long executionTime = System.currentTimeMillis() - startTime;
            ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eISPLAYERDB : §c" + executionTime + "ms");
        });
    }

    public static void setPlayerData(ProxiedPlayer player, String fieldName, String value) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            String uuid = player.getUniqueId().toString();

            PoolManager.execute(connection -> {
                String sql = "UPDATE " + PLAYERDATA_TABLE + " SET `" + fieldName + "` = ? WHERE uuid = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, value);
                    stmt.setString(2, uuid);
                    stmt.executeUpdate();
                }
                return null;
            });
        });
    }

    public static void setPlayerDataS(String playerName, String fieldName, String value) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String sql = "UPDATE " + PLAYERDATA_TABLE + " SET `" + fieldName + "` = ? WHERE name = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, value);
                        stmt.setString(2, playerName);
                        stmt.executeUpdate();
                    }
                    return null;
                })
        );
    }

    public static void setPlayerDataAsync(ProxiedPlayer player, String fieldName, String value, CallbackSQL<Boolean> callback) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            String uuid = player.getUniqueId().toString();

            PoolManager.execute(connection -> {
                String sql = "UPDATE " + PLAYERDATA_TABLE + " SET `" + fieldName + "` = ? WHERE uuid = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, value);
                    stmt.setString(2, uuid);
                    stmt.executeUpdate();
                    callback.done(true);
                }
                return null;
            });
        });
    }

    public static void getPlayerDataS(ProxiedPlayer player, String fieldName, CallbackSQL<String> callback) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            String uuid = player.getUniqueId().toString();

            PoolManager.execute(connection -> {
                String sql = "SELECT `" + fieldName + "` AS result FROM " + PLAYERDATA_TABLE + " WHERE uuid = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, uuid);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        callback.done(rs.getString("result"));
                    } else {
                        callback.done(null);
                    }

                    rs.close();
                }
                return null;
            });
        });
    }

    public static void getPlayerDataS(String playerName, String fieldName, CallbackSQL<String> callback) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String sql = "SELECT `" + fieldName + "` AS result FROM " + PLAYERDATA_TABLE + " WHERE name = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, playerName);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            callback.done(rs.getString("result"));
                        } else {
                            callback.done(null);
                        }

                        rs.close();
                    }
                    return null;
                })
        );
    }

    public static void getPlayerDataAPI(String playerName, String fieldName, CallbackSQL<String> callback) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String sql = "SELECT `" + fieldName + "` AS result FROM " + PLAYERDATA_TABLE + " WHERE name = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, playerName);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            callback.done(rs.getString("result"));
                        } else {
                            callback.done("null");
                        }

                        rs.close();
                    }
                    return null;
                })
        );
    }

    public static void getPlayerDataString(String playerName, String fieldName, CallbackSQL<String> callback) {
        getPlayerDataS(playerName, fieldName, callback);
    }

    public static void RemovePlayerDB(ProxiedPlayer player, CallbackSQL<Boolean> callback) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String sql = "DELETE FROM " + PLAYERDATA_TABLE + " WHERE name = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, player.getName());
                        stmt.executeUpdate();
                        callback.done(true);
                    }
                    return null;
                })
        );
    }

    public static void RemovePlayerDBS(String playerName, CallbackSQL<Boolean> callback) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String sql = "DELETE FROM " + PLAYERDATA_TABLE + " WHERE name = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, playerName);
                        stmt.executeUpdate();
                        callback.done(true);
                    }
                    return null;
                })
        );
    }

    public static void addName(String playerName) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String sql = "INSERT INTO " + PLAYERNAMES_TABLE + " (name) VALUES (?)";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, playerName);
                        stmt.executeUpdate();
                    }
                    return null;
                })
        );
    }

    public static void removeName(String playerName) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String sql = "DELETE FROM " + PLAYERNAMES_TABLE + " WHERE name = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, playerName);
                        stmt.executeUpdate();
                    }
                    return null;
                })
        );
    }

    public static void isName(String playerName, CallbackSQL<Boolean> callback) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String sql = "SELECT * FROM " + PLAYERNAMES_TABLE + " WHERE name = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, playerName);
                        ResultSet rs = stmt.executeQuery();

                        boolean exists = rs.next();
                        rs.close();

                        callback.done(exists);
                    }
                    return null;
                })
        );
    }

    public static void getIPTable(String ipAddress, String fieldName, CallbackSQL<String> callback) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String sql = "SELECT `" + fieldName + "` AS result FROM " + PLAYERIP_TABLE + " WHERE ip = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, ipAddress);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            callback.done(rs.getString("result"));
                        } else {
                            callback.done(null);
                        }

                        rs.close();
                    }
                    return null;
                })
        );
    }

    public static void mathIPTable(String ipAddress, String operation, String fieldName, int amount) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            getIPTable(ipAddress, fieldName, new CallbackSQL<String>() {
                @Override
                public void done(String currentValue) {
                    if (currentValue == null) {
                        return;
                    }

                    int current = Integer.parseInt(currentValue);
                    int newValue;

                    if ("+".equals(operation)) {
                        newValue = current + amount;
                    } else if ("-".equals(operation) && current > 0) {
                        newValue = current - amount;
                    } else {
                        return;
                    }

                    PoolManager.execute(connection -> {
                        String sql = "UPDATE " + PLAYERIP_TABLE + " SET `" + fieldName + "` = ? WHERE ip = ?";
                        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                            stmt.setInt(1, newValue);
                            stmt.setString(2, ipAddress);
                            stmt.executeUpdate();
                        }
                        return null;
                    });
                }

                @Override
                public void error(Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public static void mathIPTable(ProxiedPlayer player, String operation, String fieldName, int amount) {
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        mathIPTable(ipAddress, operation, fieldName, amount);
    }

    public static void registerIP(ProxiedPlayer player, int playing) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String ipAddress = player.getAddress().getAddress().getHostAddress();

                    String checkSql = "SELECT * FROM " + PLAYERIP_TABLE + " WHERE ip = ?";
                    try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                        checkStmt.setString(1, ipAddress);
                        ResultSet rs = checkStmt.executeQuery();

                        if (!rs.next()) {
                            int maxAccounts = DBABungeePlugin.plugin.getConfigLoader().getIntegerCFG("Options.IPChecker.MaxAccountsDefault");
                            int maxPlaying = DBABungeePlugin.plugin.getConfigLoader().getIntegerCFG("Options.IPChecker.MaxPlayingDefault");

                            String insertSql = "INSERT INTO " + PLAYERIP_TABLE +
                                    " (ip, accounts, playing, max_accounts, max_playing) VALUES (?, 1, ?, ?, ?)";

                            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                                insertStmt.setString(1, ipAddress);
                                insertStmt.setInt(2, playing);
                                insertStmt.setInt(3, maxAccounts);
                                insertStmt.setInt(4, maxPlaying);
                                insertStmt.executeUpdate();
                            }
                        } else {
                            mathIPTable(ipAddress, "+", "accounts", 1);
                        }

                        rs.close();
                    }
                    return null;
                })
        );
    }

    public static void deleteIP(String ipAddress) {
        ProxyServer.getInstance().getScheduler().runAsync(DBABungeePlugin.plugin, () ->
                PoolManager.execute(connection -> {
                    String sql = "DELETE FROM " + PLAYERIP_TABLE + " WHERE ip = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, ipAddress);
                        stmt.execute();
                    }
                return null;
                })
        );
    }
}
