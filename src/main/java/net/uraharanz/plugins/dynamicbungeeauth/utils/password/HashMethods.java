package net.uraharanz.plugins.dynamicbungeeauth.utils.password;

import com.google.common.hash.Hashing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

import java.nio.charset.StandardCharsets;

/**
 * @author an5w1r@163.com
 */
public class HashMethods {

    public static String hashPassword(ProxiedPlayer player, String password, String salt) {
        String hashType = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("Options.PasswordHash");
        HashAlgorithm algorithm = HashAlgorithm.fromConfig(hashType);

        if (algorithm == null) {
            return null;
        }

        switch (algorithm) {
            case SHA256:
            case AUTHME_SHA256:
                return sha256Hash(password, salt);
            case SHA512:
                return sha512Hash(password, salt);
            case SMD5:
                return saltedMD5Hash(password, salt);
            case SHA512C:
                return sha512cHash(password, salt);
            case OLDDEFAULT:
                return oldDefaultHash(password);
            case SHA256SN:
                return sha256NoSalt(password);
            default:
                return null;
        }
    }

    public static void matchPassword(ProxiedPlayer player, String password, CallbackSQL<Boolean> callback) {
        String hashType = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("Options.PasswordHash");
        HashAlgorithm algorithm = HashAlgorithm.fromConfig(hashType);

        if (algorithm == null) {
            callback.done(false);
            return;
        }

        switch (algorithm) {
            case SHA256:
                verifyWithSalt(player, password, callback, HashMethods::sha256Hash);
                break;
            case SHA512:
                verifyWithSalt(player, password, callback, HashMethods::sha512Hash);
                break;
            case SMD5:
                verifyWithSalt(player, password, callback, HashMethods::saltedMD5Hash);
                break;
            case SHA512C:
                verifyWithSalt(player, password, callback, HashMethods::sha512cHash);
                break;
            case OLDDEFAULT:
                verifyOldDefault(player, password, callback);
                break;
            case AUTHME_SHA256:
                verifyAuthMe(player, password, callback);
                break;
            case SHA256SN:
                verifyNoSalt(player, password, callback, HashMethods::sha256NoSalt);
                break;
        }
    }

    private static void verifyWithSalt(ProxiedPlayer player,
                                       String password,
                                       CallbackSQL<Boolean> callback,
                                       HashFunction hashFunction) {
        SQL.getPlayerDataS(player, "password", new CallbackSQL<String>() {
            @Override
            public void done(String storedHash) {
                SQL.getPlayerDataS(player, "salt", new CallbackSQL<String>() {
                    @Override
                    public void done(String salt) {
                        String computedHash = hashFunction.hash(password, salt);
                        callback.done(storedHash.equals(computedHash));
                    }

                    @Override
                    public void error(Exception e) {
                        e.printStackTrace();
                        callback.done(false);
                    }
                });
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
                callback.done(false);
            }
        });
    }

    private static void verifyNoSalt(ProxiedPlayer player,
                                     String password,
                                     CallbackSQL<Boolean> callback,
                                     NoSaltHashFunction hashFunction) {
        SQL.getPlayerDataS(player, "password", new CallbackSQL<String>() {
            @Override
            public void done(String storedHash) {
                String computedHash = hashFunction.hash(password);
                callback.done(storedHash.equals(computedHash));
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
                callback.done(false);
            }
        });
    }

    private static void verifyOldDefault(ProxiedPlayer player, String password, CallbackSQL<Boolean> callback) {
        SQL.getPlayerDataS(player, "password", new CallbackSQL<String>() {
            @Override
            public void done(String storedHash) {
                callback.done(Hashers.checkOldDefault(password, storedHash));
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
                callback.done(false);
            }
        });
    }

    private static void verifyAuthMe(ProxiedPlayer player, String password, CallbackSQL<Boolean> callback) {
        SQL.getPlayerDataS(player, "password", new CallbackSQL<String>() {
            @Override
            public void done(String storedHash) {
                SQL.getPlayerDataS(player, "salt", new CallbackSQL<String>() {
                    @Override
                    public void done(String salt) {
                        boolean isMatch;

                        if (salt == null || salt.isEmpty() || salt.equalsIgnoreCase("null")) {
                            // 使用 AuthMe 格式验证
                            isMatch = AuthmeHashers.compareSHA256(password, storedHash);
                        } else {
                            // 使用标准 SHA256 验证
                            String computedHash = sha256Hash(password, salt);
                            isMatch = storedHash.equals(computedHash);
                        }

                        callback.done(isMatch);
                    }

                    @Override
                    public void error(Exception e) {
                        e.printStackTrace();
                        callback.done(false);
                    }
                });
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
                callback.done(false);
            }
        });
    }

    private static String sha256Hash(String password, String salt) {
        String firstHash = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
        return Hashing.sha256().hashString(firstHash + salt, StandardCharsets.UTF_8).toString();
    }

    private static String sha512Hash(String password, String salt) {
        String firstHash = Hashing.sha512().hashString(password, StandardCharsets.UTF_8).toString();
        return Hashing.sha512().hashString(firstHash + salt, StandardCharsets.UTF_8).toString();
    }

    private static String sha512cHash(String password, String salt) {
        String firstHash = Hashers.sha512c(password);

        if (salt != null && !salt.isEmpty() && !salt.equalsIgnoreCase("null")) {
            return Hashers.sha512c(firstHash + salt);
        }

        return firstHash;
    }

    private static String saltedMD5Hash(String password, String salt) {
        return Hashers.md5(Hashers.md5(password) + salt);
    }

    private static String oldDefaultHash(String password) {
        return Hashers.oldDefault(password);
    }

    private static String sha256NoSalt(String password) {
        return Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    }

    private enum HashAlgorithm {
        SHA256("SHA256"),
        SHA512("SHA512"),
        SMD5("SMD5"),
        SHA512C("SHA512C"),
        OLDDEFAULT("OLDDEFAULT"),
        AUTHME_SHA256("AUTHME-SHA256"),
        SHA256SN("SHA256SN");

        private final String configName;

        HashAlgorithm(String configName) {
            this.configName = configName;
        }

        static HashAlgorithm fromConfig(String config) {
            for (HashAlgorithm algorithm : values()) {
                if (algorithm.configName.equalsIgnoreCase(config)) {
                    return algorithm;
                }
            }
            return null;
        }
    }

    @FunctionalInterface
    private interface HashFunction {
        String hash(String password, String salt);
    }

    @FunctionalInterface
    private interface NoSaltHashFunction {
        String hash(String password);
    }
}
