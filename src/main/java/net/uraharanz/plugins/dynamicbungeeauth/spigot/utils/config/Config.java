package net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    private static final Plugin PLUGIN = JavaPlugin.getProvidingPlugin(Config.class);
    private static final Map<String, FileConfiguration> configs = new HashMap<>();

    public static boolean isFileLoaded(String string) {
        return configs.containsKey(string);
    }

    public static void load(String string) {
        File file = new File(PLUGIN.getDataFolder(), string);
        if (!file.exists()) {
            try {
                PLUGIN.saveResource(string, false);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (!Config.isFileLoaded(string)) {
            configs.put(string, YamlConfiguration.loadConfiguration(file));
        }
    }

    public static FileConfiguration get(String string) {
        if (Config.isFileLoaded(string)) {
            return configs.get(string);
        }
        return null;
    }

    public static boolean update(String string, String string2, Object object) {
        if (Config.isFileLoaded(string) && !configs.get(string).contains(string2)) {
            configs.get(string).set(string2, object);
            return true;
        }
        return false;
    }

    public static void set(String string, String string2, Object object) {
        if (Config.isFileLoaded(string)) {
            configs.get(string).set(string2, object);
        }
    }

    public void addComment(String string, String string2, String ... stringArray) {
        if (Config.isFileLoaded(string)) {
            for (String string3 : stringArray) {
                if (configs.get(string).contains(string2)) continue;
                configs.get(string).set("_COMMENT_" + stringArray.length, " " + string3);
            }
        }
    }

    public static void remove(String string, String string2) {
        if (Config.isFileLoaded(string)) {
            configs.get(string).set(string2, null);
        }
    }

    public static boolean contains(String string, String string2) {
        if (Config.isFileLoaded(string)) {
            return configs.get(string).contains(string2);
        }
        return false;
    }

    public static void reload(String string) {
        File file = new File(PLUGIN.getDataFolder(), string);
        if (Config.isFileLoaded(string)) {
            try {
                configs.get(string).load(file);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void save(String string) {
        File file = new File(PLUGIN.getDataFolder(), string);
        if (Config.isFileLoaded(string)) {
            try {
                configs.get(string).save(file);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
