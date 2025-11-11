package net.uraharanz.plugins.dynamicbungeeauth.utils.password;

import com.google.common.hash.Hashing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;
import org.jasypt.util.password.StrongPasswordEncryptor;

import java.nio.charset.StandardCharsets;

public class HashMethods {
    public static String HashPassword(ProxiedPlayer proxiedPlayer, String string, String string2) {
        String string3 = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("Options.PasswordHash");
        if (string3.equalsIgnoreCase("SHA256")) {
            return HashMethods.SHA256H(string, string2);
        }
        if (string3.equalsIgnoreCase("SHA512")) {
            return HashMethods.SHA512H(string, string2);
        }
        if (string3.equalsIgnoreCase("SMD5")) {
            return HashMethods.SaltedMD5H(string, string2);
        }
        if (string3.equalsIgnoreCase("SHA512C")) {
            return HashMethods.SHA512C(string, string2);
        }
        if (string3.equalsIgnoreCase("OLDDEFAULT")) {
            return HashMethods.OldDefault(string);
        }
        if (string3.equalsIgnoreCase("AUTHME-SHA256")) {
            return HashMethods.SHA256H(string, string2);
        }
        if (string3.equalsIgnoreCase("SHA256SN")) {
            return HashMethods.SHA256SN(string);
        }
        return null;
    }

    public static void MashMatch(final ProxiedPlayer proxiedPlayer, final String string, final CallbackSQL<Boolean> callbackSQL) {
        String string2 = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("Options.PasswordHash");
        if (string2.equalsIgnoreCase("SHA256")) {
            SQL.getPlayerDataS(proxiedPlayer, "password", new CallbackSQL<String>(){

                @Override
                public void done(final String string2) {
                    SQL.getPlayerDataS(proxiedPlayer, "salt", new CallbackSQL<String>(){

                        @Override
                        public void done(String string) {
                            callbackSQL.done(string2.equals(HashMethods.SHA256H(string, string)));
                        }

                        @Override
                        public void error(Exception exception) {
                        }
                    });
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else if (string2.equalsIgnoreCase("SHA512")) {
            SQL.getPlayerDataS(proxiedPlayer, "password", new CallbackSQL<String>(){

                @Override
                public void done(final String string2) {
                    SQL.getPlayerDataS(proxiedPlayer, "salt", new CallbackSQL<String>(){

                        @Override
                        public void done(String string) {
                            callbackSQL.done(string2.equals(HashMethods.SHA512H(string, string)));
                        }

                        @Override
                        public void error(Exception exception) {
                        }
                    });
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else if (string2.equalsIgnoreCase("SMD5")) {
            SQL.getPlayerDataS(proxiedPlayer, "password", new CallbackSQL<String>(){

                @Override
                public void done(final String string2) {
                    SQL.getPlayerDataS(proxiedPlayer, "salt", new CallbackSQL<String>(){

                        @Override
                        public void done(String string) {
                            callbackSQL.done(string2.equals(HashMethods.SaltedMD5H(string, string)));
                        }

                        @Override
                        public void error(Exception exception) {
                        }
                    });
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else if (string2.equalsIgnoreCase("SHA512C")) {
            SQL.getPlayerDataS(proxiedPlayer, "password", new CallbackSQL<String>(){

                @Override
                public void done(final String string2) {
                    SQL.getPlayerDataS(proxiedPlayer, "salt", new CallbackSQL<String>(){

                        @Override
                        public void done(String string) {
                            if (!string.equals("null")) {
                                callbackSQL.done(string2.equals(HashMethods.SHA512C(string, string)));
                            } else {
                                callbackSQL.done(string2.equals(HashMethods.SHA512C(string, null)));
                            }
                        }

                        @Override
                        public void error(Exception exception) {
                        }
                    });
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else if (string2.equalsIgnoreCase("OLDDEFAULT")) {
            SQL.getPlayerDataS(proxiedPlayer, "password", new CallbackSQL<String>(){

                @Override
                public void done(String string2) {
                    StrongPasswordEncryptor strongPasswordEncryptor = new StrongPasswordEncryptor();
                    callbackSQL.done(strongPasswordEncryptor.checkPassword(string, string2));
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else if (string2.equalsIgnoreCase("AUTHME-SHA256")) {
            SQL.getPlayerDataS(proxiedPlayer, "password", new CallbackSQL<String>(){

                @Override
                public void done(final String string2) {
                    SQL.getPlayerDataS(proxiedPlayer, "salt", new CallbackSQL<String>(){

                        @Override
                        public void done(String string) {
                            if (string == null || string.equalsIgnoreCase("") || string.equalsIgnoreCase("null")) {
                                callbackSQL.done(AuthmeHashers.compareSHA256(string, string2));
                            } else {
                                callbackSQL.done(string2.equals(HashMethods.SHA256H(string, string)));
                            }
                        }

                        @Override
                        public void error(Exception exception) {
                        }
                    });
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else if (string2.equalsIgnoreCase("SHA256SN")) {
            SQL.getPlayerDataS(proxiedPlayer, "password", new CallbackSQL<String>(){

                @Override
                public void done(String string2) {
                    callbackSQL.done(string2.equals(HashMethods.SHA256SN(string)));
                }

                @Override
                public void error(Exception exception) {
                }
            });
        }
    }

    private static String SHA256SN(String string) {
        return Hashing.sha256().hashString(string, StandardCharsets.UTF_8).toString();
    }

    private static String SHA256H(String string, String string2) {
        String string3 = Hashing.sha256().hashString(string, StandardCharsets.UTF_8).toString();
        return Hashing.sha256().hashString(string3 + string2, StandardCharsets.UTF_8).toString();
    }

    private static String SHA512H(String string, String string2) {
        String string3 = Hashing.sha512().hashString(string, StandardCharsets.UTF_8).toString();
        return Hashing.sha512().hashString(string3 + string2, StandardCharsets.UTF_8).toString();
    }

    private static String SHA512C(String string, String string2) {
        if (string2 != null) {
            String string3 = Hashers.sha512c(string);
            return Hashers.sha512c(string3 + string2);
        }
        String string4 = Hashers.sha512c(string);
        return string4;
    }

    private static String SaltedMD5H(String string, String string2) {
        return Hashers.md5(Hashers.md5(string) + string2);
    }

    private static String OldDefault(String string) {
        return Hashers.olddefault(string);
    }
}
