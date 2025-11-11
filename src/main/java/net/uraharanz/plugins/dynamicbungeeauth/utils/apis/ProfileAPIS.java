package net.uraharanz.plugins.dynamicbungeeauth.utils.apis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ProxyServer;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackAPI;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author an5w1r@163.com
 */
public class ProfileAPIS {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
    private static final String NULL_RESULT = "null";
    private static final int HTTP_OK = 200;

    public static void mojang(String playerName, CallbackAPI<String> callback) {
        String apiUrl = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
        queryUUID(playerName, apiUrl, "MOJANG", callback, ProfileAPIS::parseMojangResponse);
    }

    public static void cloudProtected(String playerName, CallbackAPI<String> callback) {
        String apiUrl = "https://mcapi.cloudprotected.net/uuid/" + playerName;
        queryUUID(playerName, apiUrl, "CLOUDPROTECTED", callback, ProfileAPIS::parseCloudProtectedResponse);
    }

    public static void mineTools(String playerName, CallbackAPI<String> callback) {
        String apiUrl = "https://api.minetools.eu/uuid/" + playerName;
        queryUUID(playerName, apiUrl, "MINETOOLS", callback, ProfileAPIS::parseMineToolsResponse);
    }

    public static void bauxiteAPI(String playerName, CallbackAPI<String> callback) {
        String apiUrl = "https://api.bauxitenetwork.net/getuuid/" + playerName;
        queryUUID(playerName, apiUrl, "BAUXITEAPI", callback, ProfileAPIS::parseBauxiteResponse);
    }

    private static void queryUUID(
            String playerName,
            String apiUrl,
            String apiName,
            CallbackAPI<String> callback,
            ResponseParser responseParser
    ) {
        DBABungeePlugin.plugin.getProxy().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            long startTime = System.currentTimeMillis();

            try {
                HttpURLConnection connection = createConnection(apiUrl);
                int responseCode = connection.getResponseCode();

                logDebug(apiName, playerName, "Response Code: " + responseCode);

                if (responseCode == HTTP_OK) {
                    String uuid = responseParser.parse(connection, playerName);
                    callback.done(uuid != null ? uuid : NULL_RESULT);

                    if (uuid != null && !uuid.equals(NULL_RESULT)) {
                        logDebug(apiName, playerName, "UUID: " + uuid);
                    }
                } else {
                    callback.done(NULL_RESULT);
                }

                connection.disconnect();
                logExecutionTime(apiName, playerName, startTime);

            } catch (Exception e) {
                e.printStackTrace();
                callback.done(NULL_RESULT);
            }
        });
    }

    private static HttpURLConnection createConnection(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.connect();
        return connection;
    }

    private static String parseMojangResponse(HttpURLConnection connection, String playerName) throws Exception {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(new InputStreamReader(connection.getInputStream()));
        JsonObject json = element.getAsJsonObject();

        if (json.has("id")) {
            return json.get("id").getAsString();
        }
        return null;
    }

    private static String parseCloudProtectedResponse(HttpURLConnection connection, String playerName) throws Exception {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(new InputStreamReader(connection.getInputStream()));
        JsonObject json = element.getAsJsonObject();
        JsonArray results = json.getAsJsonArray("result");

        if (results != null && results.size() > 0) {
            JsonObject result = results.get(0).getAsJsonObject();
            if (result.has("uuid") && !result.get("uuid").isJsonNull()) {
                return result.get("uuid").getAsString();
            }
        }
        return null;
    }

    private static String parseMineToolsResponse(HttpURLConnection connection, String playerName) throws Exception {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(new InputStreamReader(connection.getInputStream()));
        JsonObject json = element.getAsJsonObject();

        if (json.has("id") && !json.get("id").isJsonNull()) {
            return json.get("id").getAsString().replace("\"", "");
        }
        return null;
    }

    private static String parseBauxiteResponse(HttpURLConnection connection, String playerName) throws Exception {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(new InputStreamReader(connection.getInputStream()));
        JsonObject json = element.getAsJsonObject();

        if (json.has("id") && !json.get("id").isJsonNull()) {
            return json.get("id").getAsString();
        }
        return null;
    }

    private static void logDebug(String apiName, String playerName, String message) {
        if (DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Debug.apis")) {
            ProxyServer.getInstance().getLogger().info(
                    String.format("§a§lDBA §8| §eAPI DEBUG %s FOR %s : §c%s", apiName, playerName, message)
            );
        }
    }

    private static void logExecutionTime(String apiName, String playerName, long startTime) {
        if (DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Debug.apis")) {
            long executionTime = System.currentTimeMillis() - startTime;
            ProxyServer.getInstance().getLogger().info(
                    String.format("§a§lDBA §8| §eAPI DEBUG %s FOR %s : §c%dms", apiName, playerName, executionTime)
            );
        }
    }

    @FunctionalInterface
    private interface ResponseParser {
        String parse(HttpURLConnection connection, String playerName) throws Exception;
    }
}
