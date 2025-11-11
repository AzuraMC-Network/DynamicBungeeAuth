package net.uraharanz.plugins.dynamicbungeeauth.utils.configs;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.uraharanz.plugins.dynamicbungeeauth.main;

public class Files {
    private final main plugin;
    private Configuration config;
    private ConfigurationProvider configp;
    private File file;

    public Files(main main2) {
        this.plugin = main2;
    }

    public void createConfigs() {
        block20: {
            if (!this.plugin.getDataFolder().exists()) {
                this.plugin.getDataFolder().mkdir();
            }
            this.file = new File(this.plugin.getDataFolder(), "Config.yml");
            this.configp = ConfigurationProvider.getProvider(YamlConfiguration.class);
            if (!this.file.exists()) {
                try {
                    this.file.createNewFile();
                    try (InputStream inputStream = this.plugin.getResourceAsStream("Config.yml")){
                        FileOutputStream fileOutputStream = new FileOutputStream(this.file);
                        try {
                            ByteStreams.copy((InputStream)inputStream, (OutputStream)fileOutputStream);
                            this.config = this.configp.load(this.file);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        break block20;
                    }
                }
                catch (IOException iOException) {
                    throw new RuntimeException("Unable to create config file", iOException);
                }
            }
            try {
                this.config = this.configp.load(this.file);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    public void createMessages() {
        block20: {
            if (!this.plugin.getDataFolder().exists()) {
                this.plugin.getDataFolder().mkdir();
            }
            this.file = new File(this.plugin.getDataFolder(), "Messages.yml");
            this.configp = ConfigurationProvider.getProvider(YamlConfiguration.class);
            if (!this.file.exists()) {
                try {
                    this.file.createNewFile();
                    try (InputStream inputStream = this.plugin.getResourceAsStream("Messages.yml")){
                        FileOutputStream fileOutputStream = new FileOutputStream(this.file);
                        try {
                            ByteStreams.copy((InputStream)inputStream, (OutputStream)fileOutputStream);
                            this.config = this.configp.load(this.file);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        break block20;
                    }
                }
                catch (IOException iOException) {
                    throw new RuntimeException("Unable to create config file", iOException);
                }
            }
            try {
                this.config = this.configp.load(this.file);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    public Configuration getCFG() {
        File file = new File(this.plugin.getDataFolder(), "Config.yml");
        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        return this.config;
    }

    public Configuration getMSG() {
        File file = new File(this.plugin.getDataFolder(), "Messages.yml");
        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        return this.config;
    }
}
