package net.uraharanz.plugins.dynamicbungeeauth.utils.mysql;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.random.SaltGenerator;

public class SQL {
    public static UUID generateOfflineUUID(String string) {
        Objects.requireNonNull(string);
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(StandardCharsets.UTF_8));
    }

    public static void PlayerSQL(ProxiedPlayer proxiedPlayer, int n, int n2, boolean bl, CallbackSQL<Boolean> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> {
            String string = proxiedPlayer.getName();
            String string2 = bl ? SQL.generateOfflineUUID(string).toString() : proxiedPlayer.getUniqueId().toString();
            String string3 = proxiedPlayer.getAddress().getAddress().getHostAddress();
            PoolManager.execute(connection -> {
                String string4 = "SELECT name FROM playerdata WHERE uuid = ?";
                String string5 = "INSERT INTO playerdata(`uuid`, `name`, `email`, `reg_ip`, `log_ip`, `password`, `salt`, `firstjoin`, `lastjoin`, `premium`, `valid`, `server`, `lwlogged`) VALUES (?, ?, 'null', ?, ?, 'null', '" + SaltGenerator.generateString() + "', ?, ?, ?, ?, 'null', '0')";
                PreparedStatement preparedStatement = connection.prepareStatement(string4);
                preparedStatement.setString(1, string2);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) {
                    Date date = new Date();
                    Timestamp timestamp = new Timestamp(date.getTime());
                    preparedStatement = connection.prepareStatement(string5);
                    preparedStatement.setString(1, string2);
                    preparedStatement.setString(2, string);
                    preparedStatement.setString(3, string3);
                    preparedStatement.setString(4, string3);
                    preparedStatement.setTimestamp(5, timestamp);
                    preparedStatement.setTimestamp(6, timestamp);
                    preparedStatement.setInt(7, n);
                    preparedStatement.setInt(8, n2);
                    preparedStatement.execute();
                    callbackSQL.done(true);
                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                    return null;
                }
                callbackSQL.done(true);
                resultSet.close();
                preparedStatement.close();
                connection.close();
                return null;
            });
        });
    }

    public static void deletePlayerData(String string) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            String string2 = "DELETE FROM playerdata WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(string2);
            preparedStatement.setString(1, string);
            preparedStatement.execute();
            return true;
        }));
    }

    public static void PlayerIMPORTER(String string, String string2, String string3, String string4, String string5, int n, int n2) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM playerdata WHERE uuid = '" + string2 + "';");
            if (!resultSet.next()) {
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                statement.executeUpdate("INSERT INTO playerdata (`uuid`, `name`, `email`, `reg_ip`, `log_ip`, `password`, `salt`, `firstjoin`, `lastjoin`, `premium`, `valid`, `server`, `lwlogged`) VALUES('" + string2 + "', '" + string + "', 'null', '" + string5 + "', '" + string4 + "', '" + string3 + "', 'null', '" + timestamp + "', '" + timestamp + "', '" + n + "', '" + n2 + "', 'null', '0');");
                resultSet.close();
                statement.close();
                connection.close();
                return null;
            }
            resultSet.close();
            statement.close();
            connection.close();
            return null;
        }));
    }

    public static void getPlayer(ProxiedPlayer proxiedPlayer, CallbackSQL<Boolean> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> {
            String string = proxiedPlayer.getUniqueId().toString();
            PoolManager.execute(connection -> {
                String string2 = "SELECT * FROM playerdata WHERE uuid = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(string2);
                preparedStatement.setString(1, string);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Object object;
                    if (!resultSet.getString("name").equals(proxiedPlayer.getName())) {
                        object = "UPDATE playerdata SET name = ? WHERE uuid = ?";
                        preparedStatement = connection.prepareStatement((String)object);
                        preparedStatement.setString(1, proxiedPlayer.getName());
                        preparedStatement.setString(2, string);
                        preparedStatement.execute();
                        preparedStatement.close();
                    }
                    if (main.plugin.getPlayerDataList().searchPlayer(proxiedPlayer.getName()) == null) {
                        object = new PlayerData(resultSet.getString("uuid"), resultSet.getString("name"), resultSet.getString("email"), resultSet.getString("reg_ip"), resultSet.getString("log_ip"), resultSet.getString("password"), resultSet.getString("salt"), resultSet.getDate("firstjoin"), resultSet.getDate("lastjoin"), resultSet.getBoolean("premium"), resultSet.getBoolean("valid"), resultSet.getString("server"), resultSet.getBoolean("lwlogged"), true);
                        main.plugin.getPlayerDataList().addPlayer((PlayerData)object);
                        callbackSQL.done(true);
                        resultSet.close();
                        preparedStatement.close();
                        connection.close();
                    } else {
                        object = new PlayerData(resultSet.getString("uuid"), resultSet.getString("name"), resultSet.getString("email"), resultSet.getString("reg_ip"), resultSet.getString("log_ip"), resultSet.getString("password"), resultSet.getString("salt"), resultSet.getDate("firstjoin"), resultSet.getDate("lastjoin"), resultSet.getBoolean("premium"), resultSet.getBoolean("valid"), resultSet.getString("server"), resultSet.getBoolean("lwlogged"), true);
                        main.plugin.getPlayerDataList().modifyPlayer((PlayerData)object);
                        callbackSQL.done(true);
                        resultSet.close();
                        preparedStatement.close();
                        connection.close();
                    }
                } else if (main.plugin.getPlayerDataList().searchPlayer(proxiedPlayer.getName()) == null) {
                    PlayerData playerData = new PlayerData(proxiedPlayer.getUniqueId().toString(), proxiedPlayer.getName(), "null", "null", "null", "null", "null", null, null, false, false, "null", false, false);
                    main.plugin.getPlayerDataList().addPlayer(playerData);
                    callbackSQL.done(true);
                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                } else {
                    PlayerData playerData = new PlayerData(proxiedPlayer.getUniqueId().toString(), proxiedPlayer.getName(), "null", "null", "null", "null", "null", null, null, false, false, "null", false, false);
                    main.plugin.getPlayerDataList().modifyPlayer(playerData);
                    callbackSQL.done(true);
                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                }
                resultSet.close();
                preparedStatement.close();
                connection.close();
                return null;
            });
        });
    }

    public static void isPlayerDB(ProxiedPlayer proxiedPlayer, CallbackSQL<Boolean> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> {
            if (proxiedPlayer != null) {
                String string = proxiedPlayer.getUniqueId().toString();
                PoolManager.execute(connection -> {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT name FROM playerdata WHERE uuid = '" + string + "';");
                    if (resultSet.next()) {
                        callbackSQL.done(true);
                        resultSet.close();
                        statement.close();
                        connection.close();
                    } else {
                        callbackSQL.done(false);
                        resultSet.close();
                        statement.close();
                        connection.close();
                    }
                    resultSet.close();
                    statement.close();
                    connection.close();
                    return null;
                });
            } else {
                callbackSQL.done(false);
            }
        });
    }

    public static void isPlayerDB(String string, CallbackSQL<Boolean> callbackSQL) {
        long l = System.currentTimeMillis();
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> {
            if (string != null) {
                PoolManager.execute(connection -> {
                    String string2 = "SELECT * FROM playerdata WHERE name = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(string2);
                    preparedStatement.setString(1, string);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        callbackSQL.done(true);
                        resultSet.close();
                        preparedStatement.close();
                        connection.close();
                    } else {
                        callbackSQL.done(false);
                        resultSet.close();
                        preparedStatement.close();
                        connection.close();
                    }
                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                    return null;
                });
            } else {
                callbackSQL.done(false);
            }
        });
        long l2 = System.currentTimeMillis();
        long l3 = l2 - l;
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eISPLAYERDB : §c" + l3 + "ms");
    }

    public static void setPlayerData(ProxiedPlayer proxiedPlayer, String string, String string2) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> {
            String string3 = proxiedPlayer.getUniqueId().toString();
            PoolManager.execute(connection -> {
                String string4 = "UPDATE playerdata SET `" + string + "` = ? WHERE uuid = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(string4);
                preparedStatement.setString(1, string2);
                preparedStatement.setString(2, string3);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
                return null;
            });
        });
    }

    public static void setPlayerDataS(String string, String string2, String string3) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            String string4 = "UPDATE playerdata SET `" + string2 + "` = ? WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(string4);
            preparedStatement.setString(1, string3);
            preparedStatement.setString(2, string);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            return null;
        }));
    }

    public static void setPlayerDataAsync(ProxiedPlayer proxiedPlayer, String string, String string2, CallbackSQL<Boolean> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> {
            String string3 = proxiedPlayer.getUniqueId().toString();
            PoolManager.execute(connection -> {
                String string4 = "UPDATE playerdata SET `" + string + "` = ? WHERE uuid = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(string4);
                preparedStatement.setString(1, string2);
                preparedStatement.setString(2, string3);
                preparedStatement.executeUpdate();
                callbackSQL.done(true);
                preparedStatement.close();
                connection.close();
                return null;
            });
        });
    }

    public static void getPlayerDataS(ProxiedPlayer proxiedPlayer, String string, CallbackSQL<String> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> {
            String string2 = proxiedPlayer.getUniqueId().toString();
            PoolManager.execute(connection -> {
                String string3 = "SELECT `" + string + "` AS result FROM playerdata WHERE uuid = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(string3);
                preparedStatement.setString(1, string2);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    callbackSQL.done(resultSet.getString("result"));
                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                } else {
                    callbackSQL.done(null);
                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                }
                return null;
            });
        });
    }

    public static void getPlayerDataString(String string, String string2, CallbackSQL<String> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            String string3 = "SELECT `" + string2 + "` AS result FROM playerdata WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(string3);
            preparedStatement.setString(1, string);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                callbackSQL.done(resultSet.getString("result"));
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } else {
                callbackSQL.done(null);
                resultSet.close();
                preparedStatement.close();
                connection.close();
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return null;
        }));
    }

    public static void getPlayerDataS(String string, String string2, CallbackSQL<String> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            String string3 = "SELECT `" + string2 + "` AS result FROM playerdata WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(string3);
            preparedStatement.setString(1, string);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                callbackSQL.done(resultSet.getString("result"));
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } else {
                callbackSQL.done(null);
                resultSet.close();
                preparedStatement.close();
                connection.close();
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return null;
        }));
    }

    public static void getPlayerDataAPI(String string, String string2, CallbackSQL<String> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            String string3 = "SELECT `" + string2 + "` AS result FROM playerdata WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(string3);
            preparedStatement.setString(1, string);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                callbackSQL.done(resultSet.getString("result"));
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } else {
                callbackSQL.done("null");
                resultSet.close();
                preparedStatement.close();
                connection.close();
            }
            return null;
        }));
    }

    public static void RemovePlayerDB(ProxiedPlayer proxiedPlayer, CallbackSQL<Boolean> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            String string = "DELETE FROM playerdata WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(string);
            preparedStatement.setString(1, proxiedPlayer.getName());
            preparedStatement.executeUpdate();
            callbackSQL.done(true);
            preparedStatement.close();
            connection.close();
            return null;
        }));
    }

    public static void RemovePlayerDBS(String string, CallbackSQL<Boolean> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            String string2 = "DELETE FROM playerdata WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(string2);
            preparedStatement.setString(1, string);
            preparedStatement.executeUpdate();
            callbackSQL.done(true);
            preparedStatement.close();
            connection.close();
            return null;
        }));
    }

    public static void addName(String string) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            String string2 = "INSERT INTO playernames (`name`) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(string2);
            preparedStatement.setString(1, string);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            return null;
        }));
    }

    public static void removeName(String string) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            String string2 = "DELETE FROM playernames WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(string2);
            preparedStatement.setString(1, string);
            preparedStatement.close();
            connection.close();
            return null;
        }));
    }

    public static void isName(String string, CallbackSQL<Boolean> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            String string2 = "SELECT * FROM playernames WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(string2);
            preparedStatement.setString(1, string);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                callbackSQL.done(true);
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } else {
                callbackSQL.done(false);
                resultSet.close();
                preparedStatement.close();
                connection.close();
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return null;
        }));
    }

    public static void getIPTable(String string, String string2, CallbackSQL<String> callbackSQL) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            String string3 = "SELECT `" + string2 + "` AS result FROM playerip WHERE ip = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(string3);
            preparedStatement.setString(1, string);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                callbackSQL.done(resultSet.getString("result"));
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } else {
                callbackSQL.done(null);
                resultSet.close();
                preparedStatement.close();
                connection.close();
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return null;
        }));
    }

    public static void mathIPTable(final String string, String string2, final String string3, final int n) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> {
            if (string2.equals("+")) {
                SQL.getIPTable(string, string3, new CallbackSQL<String>(){

                    @Override
                    public void done(String string2) {
                        if (string2 != null) {
                            PoolManager.execute(connection -> {
                                Statement statement = connection.createStatement();
                                statement.executeUpdate("UPDATE playerip SET " + string3 + " = '" + (Integer.parseInt(string2) + n) + "' WHERE ip = '" + string + "'");
                                statement.close();
                                connection.close();
                                return null;
                            });
                        }
                    }

                    @Override
                    public void error(Exception exception) {
                    }
                });
            } else if (string2.equals("-")) {
                SQL.getIPTable(string, string3, new CallbackSQL<String>(){

                    @Override
                    public void done(String string2) {
                        if (string2 != null && !string2.equals("0")) {
                            PoolManager.execute(connection -> {
                                Statement statement = connection.createStatement();
                                statement.executeUpdate("UPDATE playerip SET " + string3 + " = '" + (Integer.parseInt(string2) - n) + "' WHERE ip = '" + string + "'");
                                statement.close();
                                connection.close();
                                return null;
                            });
                        }
                    }

                    @Override
                    public void error(Exception exception) {
                    }
                });
            }
        });
    }

    public static void mathIPTable(final ProxiedPlayer proxiedPlayer, final String string, final String string2, final int n) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> {
            final String string3 = proxiedPlayer.getAddress().getAddress().getHostAddress();
            if (string.equals("+")) {
                SQL.getIPTable(string3, string2, new CallbackSQL<String>(){

                    @Override
                    public void done(String string1) {
                        if (string1 != null) {
                            PoolManager.execute(connection -> {
                                Statement statement = connection.createStatement();
                                statement.executeUpdate("UPDATE playerip SET " + string2 + " = '" + (Integer.parseInt(string1) + n) + "' WHERE ip = '" + string3 + "'");
                                statement.close();
                                connection.close();
                                return null;
                            });
                        }
                    }

                    @Override
                    public void error(Exception exception) {
                    }
                });
            } else if (string.equals("-")) {
                SQL.getIPTable(string3, string2, new CallbackSQL<String>(){

                    @Override
                    public void done(String string1) {
                        if (string1 != null && !string1.equals("0")) {
                            PoolManager.execute(connection -> {
                                Statement statement = connection.createStatement();
                                statement.executeUpdate("UPDATE playerip SET " + string2 + " = '" + (Integer.parseInt(string1) - n) + "' WHERE ip = '" + string3 + "'");
                                statement.close();
                                connection.close();
                                return null;
                            });
                        }
                    }

                    @Override
                    public void error(Exception exception) {
                    }
                });
            }
        });
    }

    public static void registerIP(ProxiedPlayer proxiedPlayer, int n) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            Statement statement = connection.createStatement();
            String string = proxiedPlayer.getAddress().getAddress().getHostAddress();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM playerip WHERE ip = '" + string + "';");
            if (!resultSet.next()) {
                statement.executeUpdate("INSERT INTO playerip (`ip`, `accounts`, `playing`, `max_accounts`, `max_playing`) VALUES ('" + string + "', '1', '" + n + "', '" + main.plugin.getConfigLoader().getIntegerCFG("Options.IPChecker.MaxAccountsDefault") + "', '" + main.plugin.getConfigLoader().getIntegerCFG("Options.IPChecker.MaxPlayingDefault") + "');");
                resultSet.close();
                statement.close();
                connection.close();
                return null;
            }
            SQL.mathIPTable(string, "+", "accounts", 1);
            resultSet.close();
            statement.close();
            connection.close();
            resultSet.close();
            statement.close();
            connection.close();
            return false;
        }));
    }

    public static void deleteIP(String string) {
        ProxyServer.getInstance().getScheduler().runAsync(main.plugin, () -> PoolManager.execute(connection -> {
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM `playerip` WHERE `playerip`.`ip` = '" + string + "' ");
            statement.close();
            connection.close();
            return null;
        }));
    }
}
