package net.uraharanz.plugins.dynamicbungeeauth;

import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

/**
 * @author an5w1r@163.com
 */
public class DynamicAPIBungee {

    public static void getUUIDSQL(String playerName, CallbackSQL<String> callback) {
        SQL.getPlayerDataAPI(playerName, "uuid", new CallbackSQL<String>() {
            @Override
            public void done(String uuid) {
                callback.done(uuid);
            }

            @Override
            public void error(Exception e) {
                callback.error(e);
            }
        });
    }

    public static void getValueSQL(String playerName, String fieldName, CallbackSQL<String> callback) {
        SQL.getPlayerDataAPI(playerName, fieldName, new CallbackSQL<String>() {
            @Override
            public void done(String value) {
                callback.done(value);
            }

            @Override
            public void error(Exception e) {
                callback.error(e);
            }
        });
    }
}
