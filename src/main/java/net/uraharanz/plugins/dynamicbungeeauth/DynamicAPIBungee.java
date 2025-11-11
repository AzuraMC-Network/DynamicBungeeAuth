package net.uraharanz.plugins.dynamicbungeeauth;

import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

public class DynamicAPIBungee {
    public static void getUUIDSQL(String string, final CallbackSQL<String> callbackSQL) {
        SQL.getPlayerDataAPI(string, "uuid", new CallbackSQL<String>(){

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
        SQL.getPlayerDataAPI(string, string2, new CallbackSQL<String>(){

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
