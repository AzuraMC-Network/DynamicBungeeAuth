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

public class ProfileAPIS {
    public static void Mojang(String string, CallbackAPI<String> callbackAPI) {
        DBABungeePlugin.plugin.getProxy().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            try {
                long l = System.currentTimeMillis();
                String string2 = "https://api.mojang.com/users/profiles/minecraft/" + string;
                URL uRL = new URL(string2);
                HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                httpURLConnection.connect();
                int n = httpURLConnection.getResponseCode();
                if (DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Debug.apis")) {
                    ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG MOJANG FOR" + string + " : §c" + n);
                }
                if (n == 200) {
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(new InputStreamReader(httpURLConnection.getInputStream()));
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    callbackAPI.done(jsonObject.get("id").getAsString());
                    httpURLConnection.disconnect();
                    ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG MOJANG FOR" + string + " : §c" + jsonObject.get("id").getAsString());
                    long l2 = System.currentTimeMillis();
                    if (DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Debug.apis")) {
                        long l3 = l2 - l;
                        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG MOJANG FOR " + string + " : §c" + l3 + "ms");
                    }
                } else {
                    callbackAPI.done("null");
                    httpURLConnection.disconnect();
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public static void CloudProtected(String string, CallbackAPI<String> callbackAPI) {
        DBABungeePlugin.plugin.getProxy().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            try {
                long l = System.currentTimeMillis();
                String string2 = "https://mcapi.cloudprotected.net/uuid/" + string;
                URL uRL = new URL(string2);
                HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                httpURLConnection.connect();
                int n = httpURLConnection.getResponseCode();
                if (DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Debug.apis")) {
                    ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG CLOUDPROTECTED FOR " + string + " : §c" + n);
                }
                if (n == 200) {
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(new InputStreamReader(httpURLConnection.getInputStream()));
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                    for (JsonElement jsonElement2 : jsonArray) {
                        JsonObject jsonObject2 = (JsonObject)jsonElement2;
                        if (jsonObject2.get("uuid").isJsonNull()) {
                            callbackAPI.done("null");
                            httpURLConnection.disconnect();
                            continue;
                        }
                        callbackAPI.done(jsonObject2.get("uuid").getAsString());
                        httpURLConnection.disconnect();
                        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG CLOUD FOR " + string + "  §c" + jsonObject2.get("uuid").getAsString());
                    }
                    long l2 = System.currentTimeMillis();
                    if (DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Debug.apis")) {
                        long l3 = l2 - l;
                        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG CLOUDPROTECTED FOR " + string + " : §c" + l3 + "ms");
                    }
                } else {
                    callbackAPI.done("null");
                    httpURLConnection.disconnect();
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public static void MineTools(String string, CallbackAPI<String> callbackAPI) {
        DBABungeePlugin.plugin.getProxy().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            try {
                long l = System.currentTimeMillis();
                String string2 = "https://api.minetools.eu/uuid/" + string;
                URL uRL = new URL(string2);
                HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                httpURLConnection.connect();
                int n = httpURLConnection.getResponseCode();
                if (DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Debug.apis")) {
                    ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG MINETOOLS FOR " + string + " : §c" + n);
                }
                if (n == 200) {
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(new InputStreamReader(httpURLConnection.getInputStream()));
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.get("id") != null) {
                        callbackAPI.done(jsonObject.get("id").toString());
                        httpURLConnection.disconnect();
                        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG MINETOOLS FOR " + string + " : §c" + jsonObject.get("id").toString());
                    } else {
                        callbackAPI.done("null");
                        httpURLConnection.disconnect();
                    }
                    long l2 = System.currentTimeMillis();
                    if (DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Debug.apis")) {
                        long l3 = l2 - l;
                        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG MINETOOLS FOR " + string + " : §c" + l3 + "ms");
                    }
                } else {
                    httpURLConnection.disconnect();
                    callbackAPI.done("null");
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public static void BauxiteAPI(String string, CallbackAPI<String> callbackAPI) {
        DBABungeePlugin.plugin.getProxy().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            try {
                long l = System.currentTimeMillis();
                String string2 = "https://api.bauxitenetwork.net/getuuid/" + string;
                URL uRL = new URL(string2);
                HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                httpURLConnection.connect();
                int n = httpURLConnection.getResponseCode();
                if (DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Debug.apis")) {
                    ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG BAUXITEAPI: §c" + n);
                }
                if (n == 200) {
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(new InputStreamReader(httpURLConnection.getInputStream()));
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.get("id") != null) {
                        callbackAPI.done(jsonObject.get("id").getAsString());
                        httpURLConnection.disconnect();
                        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG BAUXITE: §c" + jsonObject.get("id").getAsString());
                    } else {
                        callbackAPI.done("null");
                        httpURLConnection.disconnect();
                    }
                    long l2 = System.currentTimeMillis();
                    if (DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Debug.apis")) {
                        long l3 = l2 - l;
                        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eAPI DEBUG BAUXITEAPI: §c" + l3 + "ms");
                    }
                } else {
                    callbackAPI.done("null");
                    httpURLConnection.disconnect();
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
