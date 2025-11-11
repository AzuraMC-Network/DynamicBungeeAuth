package net.uraharanz.plugins.dynamicbungeeauth.utils.apis;

import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.apis.PlayerAPI;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * @author an5w1r@163.com
 */
public class ProfileGenerator {

    private static final String NULL_UUID = "null";

    private final DBABungeePlugin plugin;
    private final List<APIProvider> apiProviders;
    private final AtomicInteger currentApiIndex;

    public ProfileGenerator(DBABungeePlugin plugin) {
        this.plugin = plugin;
        this.apiProviders = initializeAPIProviders();
        this.currentApiIndex = new AtomicInteger(0);
    }

    private List<APIProvider> initializeAPIProviders() {
        List<APIProvider> providers = new ArrayList<>();

        if (plugin.getConfigLoader().getBooleanCFG("APIS.Enable.Mojang.Enable")) {
            int fallback = plugin.getConfigLoader().getIntegerCFG("APIS.Enable.Mojang.Fallback");
            providers.add(new APIProvider("Mojang", ProfileAPIS::mojang, fallback));
        }

        if (plugin.getConfigLoader().getBooleanCFG("APIS.Enable.CloudProtected.Enable")) {
            int fallback = plugin.getConfigLoader().getIntegerCFG("APIS.Enable.CloudProtected.Fallback");
            providers.add(new APIProvider("CloudProtected", ProfileAPIS::cloudProtected, fallback));
        }

        if (plugin.getConfigLoader().getBooleanCFG("APIS.Enable.MineTools.Enable")) {
            int fallback = plugin.getConfigLoader().getIntegerCFG("APIS.Enable.MineTools.Fallback");
            providers.add(new APIProvider("MineTools", ProfileAPIS::mineTools, fallback));
        }

        if (plugin.getConfigLoader().getBooleanCFG("APIS.Enable.BauxiteAPI.Enable")) {
            int fallback = plugin.getConfigLoader().getIntegerCFG("APIS.Enable.BauxiteAPI.Fallback");
            providers.add(new APIProvider("BauxiteAPI", ProfileAPIS::bauxiteAPI, fallback));
        }

        return providers;
    }

    public void generator(String playerName, CallbackAPI<UUID> callback) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            try {
                PlayerAPI cachedAPI = plugin.getPlayerAPIList().searchRequest(playerName);
                if (cachedAPI != null) {
                    handleCachedResult(cachedAPI, callback);
                    return;
                }

                queryNextAPI(playerName, callback);

            } catch (Exception e) {
                e.printStackTrace();
                callback.done(null);
            }
        });
    }

    private void handleCachedResult(PlayerAPI cachedAPI, CallbackAPI<UUID> callback) {
        if (NULL_UUID.equals(cachedAPI.getUuid())) {
            callback.done(null);
        } else {
            callback.done(formatUUID(cachedAPI.getUuid()));
        }
    }

    private void queryNextAPI(String playerName, CallbackAPI<UUID> callback) {
        if (apiProviders.isEmpty()) {
            cacheAndReturn(playerName, NULL_UUID, callback);
            return;
        }

        int index = currentApiIndex.getAndUpdate(i -> (i + 1) % apiProviders.size());
        APIProvider provider = apiProviders.get(index);

        provider.query(playerName, new CallbackAPI<String>() {
            @Override
            public void done(String uuidString) {
                if (uuidString != null && !NULL_UUID.equals(uuidString)) {
                    cacheAndReturn(playerName, uuidString, callback);
                } else {
                    queryFallbackAPI(playerName, provider.getFallbackIndex(), callback);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
                queryFallbackAPI(playerName, provider.getFallbackIndex(), callback);
            }
        });
    }

    private void queryFallbackAPI(String playerName, int fallbackIndex, CallbackAPI<UUID> callback) {
        if (fallbackIndex <= 0 || fallbackIndex > 4) {
            cacheAndReturn(playerName, NULL_UUID, callback);
            return;
        }

        BiConsumer<String, CallbackAPI<String>> fallbackAPI = getFallbackAPI(fallbackIndex);
        if (fallbackAPI == null) {
            cacheAndReturn(playerName, NULL_UUID, callback);
            return;
        }

        fallbackAPI.accept(playerName, new CallbackAPI<String>() {
            @Override
            public void done(String uuidString) {
                if (uuidString != null && !NULL_UUID.equals(uuidString)) {
                    cacheAndReturn(playerName, uuidString, callback);
                } else {
                    cacheAndReturn(playerName, NULL_UUID, callback);
                }
            }

            @Override
            public void error(Exception e) {
                cacheAndReturn(playerName, NULL_UUID, callback);
            }
        });
    }

    private BiConsumer<String, CallbackAPI<String>> getFallbackAPI(int index) {
        switch (index) {
            case 1:
                return ProfileAPIS::mojang;
            case 2:
                return ProfileAPIS::cloudProtected;
            case 3:
                return ProfileAPIS::mineTools;
            case 4:
                return ProfileAPIS::bauxiteAPI;
            default:
                return null;
        }
    }

    private void cacheAndReturn(String playerName, String uuidString, CallbackAPI<UUID> callback) {
        PlayerAPI playerAPI = new PlayerAPI(playerName, uuidString);
        plugin.getPlayerAPIList().addRequest(playerAPI);

        if (NULL_UUID.equals(uuidString)) {
            callback.done(null);
        } else {
            callback.done(formatUUID(uuidString));
        }
    }

    private UUID formatUUID(String uuidString) {
        if (uuidString == null || uuidString.length() < 32) {
            return null;
        }

        try {
            String cleaned = uuidString.replace("\"", "");

            if (cleaned.contains("-")) {
                return UUID.fromString(cleaned);
            }

            String formatted = String.format("%s-%s-%s-%s-%s",
                    cleaned.substring(0, 8),
                    cleaned.substring(8, 12),
                    cleaned.substring(12, 16),
                    cleaned.substring(16, 20),
                    cleaned.substring(20, 32)
            );

            return UUID.fromString(formatted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class APIProvider {
        private final BiConsumer<String, CallbackAPI<String>> queryMethod;
        private final int fallbackIndex;

        APIProvider(String name, BiConsumer<String, CallbackAPI<String>> queryMethod, int fallbackIndex) {
            this.queryMethod = queryMethod;
            this.fallbackIndex = fallbackIndex;
        }

        void query(String playerName, CallbackAPI<String> callback) {
            queryMethod.accept(playerName, callback);
        }

        int getFallbackIndex() {
            return fallbackIndex;
        }
    }
}
