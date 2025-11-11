package net.uraharanz.plugins.dynamicbungeeauth.spigot;

import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.SQLSpigot;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;

public class DynamicAPISpigot {
    public static void getUUIDSQL(String string, final CallbackSQL<String> callbackSQL) {
        SQLSpigot.getPlayerDataSpigot(string, "uuid", new CallbackSQL<String>(){

            @Override
            public void done(String string) {
                callbackSQL.done(string);
            }

            @Override
            public void error(Exception exception) {
            }
        });
    }

    public static void getValueSQL(String string, String string2, final CallbackSQL<String> callbackSQL) {
        SQLSpigot.getPlayerDataSpigot(string, string2, new CallbackSQL<String>(){

            @Override
            public void done(String string) {
                callbackSQL.done(string);
            }

            @Override
            public void error(Exception exception) {
            }
        });
    }
}
