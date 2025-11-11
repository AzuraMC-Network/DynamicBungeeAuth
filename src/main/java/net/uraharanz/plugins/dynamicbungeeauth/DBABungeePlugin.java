package net.uraharanz.plugins.dynamicbungeeauth;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.apis.PlayerAPIList;
import net.uraharanz.plugins.dynamicbungeeauth.cache.cache.PlayerCacheList;
import net.uraharanz.plugins.dynamicbungeeauth.cache.fix.FixList;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerDataList;
import net.uraharanz.plugins.dynamicbungeeauth.cache.server.ServerState;
import net.uraharanz.plugins.dynamicbungeeauth.cache.spam.SpamPlayerList;
import net.uraharanz.plugins.dynamicbungeeauth.commands.*;
import net.uraharanz.plugins.dynamicbungeeauth.listeners.*;
import net.uraharanz.plugins.dynamicbungeeauth.loader.ConfigLoader;
import net.uraharanz.plugins.dynamicbungeeauth.timers.LoginTimer;
import net.uraharanz.plugins.dynamicbungeeauth.timers.MaxLogin;
import net.uraharanz.plugins.dynamicbungeeauth.timers.RegisterTimer;
import net.uraharanz.plugins.dynamicbungeeauth.utils.apis.ProfileGenerator;
import net.uraharanz.plugins.dynamicbungeeauth.utils.configs.Files;
import net.uraharanz.plugins.dynamicbungeeauth.utils.importers.AuthmeImporter;
import net.uraharanz.plugins.dynamicbungeeauth.utils.importers.SQLImp;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.PoolManager;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.HashMap;

/**
 * @author an5w1r@163.com
 */
@Getter
@Setter
public class DBABungeePlugin extends Plugin {

    public static DBABungeePlugin plugin;

    private Files files;
    private ConfigLoader configLoader;

    private PlayerAPIList playerAPIList;
    private PlayerDataList playerDataList;
    private PlayerCacheList playerCacheList;
    private SpamPlayerList spamPlayerList;
    private FixList fixList;

    private ProfileGenerator profileGenerator;

    private RegisterTimer registerTimer;
    private LoginTimer loginTimer;
    private MaxLogin maxLogin;

    private FloodgateApi floodgateApi;

    public static HashMap<String, Boolean> serverLobby;
    public static HashMap<String, Boolean> serverAuth;

    @Override
    public void onEnable() {
        plugin = this;
        initializeServerMaps();

        logInfo("Initializing plugin...");

        loadConfigurations();
        connectDatabase();
        initializeCaches();
        loadListeners();
        registerCommands();
        initializeTimers();
        initializeServerState();
        importDataIfEnabled();
        registerPluginChannels();
        initializeFloodGate();

        logInfo("Plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        logInfo("Shutting down...");
        PoolManager.closeConnection();
        logInfo("Database connections closed.");
    }

    private void initializeServerMaps() {
        serverLobby = new HashMap<>();
        serverAuth = new HashMap<>();
    }

    private void loadConfigurations() {
        logInfo("Loading configuration files...");

        setFiles(new Files(this));
        files.createConfigs();
        files.createMessages();
        setConfigLoader(new ConfigLoader(plugin));

        logInfo("Configuration files loaded.");
    }

    private void connectDatabase() {
        logInfo("Connecting to database...");

        PoolManager.connectDB(2);
        PoolManager.createPlayerTable();
        PoolManager.createIPTable();
        PoolManager.createNamesTable();
        PoolManager.alterTable();
        PoolManager.resetValues();
        PoolManager.createIndex();

        logInfo("Database connected and initialized.");
    }

    private void initializeCaches() {
        logInfo("Loading caches...");

        setPlayerAPIList(new PlayerAPIList(this));
        playerAPIList.cleanRequest();

        setPlayerDataList(new PlayerDataList(this));
        playerDataList.cleanData();

        setPlayerCacheList(new PlayerCacheList(this));
        playerCacheList.cleanCached();

        setFixList(new FixList(this));

        logInfo("Caches loaded.");
    }

    private void loadListeners() {
        logInfo("Loading listeners...");
        plugin.getProxy().getPluginManager().registerListener(plugin, new PostLogin(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new PreLogin(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new LeaveListener(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new ChatListener(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new SwitchListener(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new ServerConnectedListener(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new PluginChanelListenerB(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin, new Login(plugin));
        logInfo("Listeners loaded.");
    }

    private void registerCommands() {
        logInfo("Registering commands...");

        getProxy().getPluginManager().registerCommand(this, new PremiumCMD(this));
        getProxy().getPluginManager().registerCommand(this, new CrackedCMD(this));
        getProxy().getPluginManager().registerCommand(this, new RegisterCMD(this));
        getProxy().getPluginManager().registerCommand(this, new LoginCMD(this));
        getProxy().getPluginManager().registerCommand(this, new AdminCMD(this));
        getProxy().getPluginManager().registerCommand(this, new ChangeCMD(this));
        getProxy().getPluginManager().registerCommand(this, new LogoutCMD(this));

        registerLobbyCommand();

        logInfo("Commands registered.");
    }

    private void registerLobbyCommand() {
        if (!configLoader.getBooleanCFG("Options.LobbyCommands")) {
            return;
        }

        boolean isSpawnMode = configLoader.getBooleanCFG("Options.LobbyCommandsSpawn");
        LobbyCMD lobbyCommand = isSpawnMode ? new LobbyCMD(this, true) : new LobbyCMD(this);

        getProxy().getPluginManager().registerCommand(this, lobbyCommand);
    }

    private void initializeTimers() {
        logInfo("Initializing timers...");

        setProfileGenerator(new ProfileGenerator(this));
        setRegisterTimer(new RegisterTimer(this));
        setLoginTimer(new LoginTimer(this));
        setMaxLogin(new MaxLogin(this));
        maxLogin.resetCountTimer();
        setSpamPlayerList(new SpamPlayerList(this));

        logInfo("Timers initialized.");
    }

    private void initializeServerState() {
        ServerState.state = ServerState.NORMAL;
        ServerState.setState(ServerState.NORMAL);
    }

    private void importDataIfEnabled() {
        if (!configLoader.getBooleanCFG("Importers.Enabled")) {
            return;
        }

        logInfo("Importing data...");
        SQLImp.connectDB();
        AuthmeImporter.importDB();
        logInfo("Data imported.");
    }

    private void registerPluginChannels() {
        String verifyChannel = "dba:" + configLoader.getStringCFG("PluginChannel.verify");
        String premiumChannel = "dba:" + configLoader.getStringCFG("PluginChannel.premium");
        String crackedChannel = "dba:" + configLoader.getStringCFG("PluginChannel.cracked");

        getProxy().registerChannel(verifyChannel);
        getProxy().registerChannel(premiumChannel);
        getProxy().registerChannel(crackedChannel);

        logInfo("Plugin channels registered. Make sure to sync DBA.PluginChannel config section with Spigot!");
    }

    private void initializeFloodGate() {
        logInfo("Checking for FloodGateAPI...");

        if (ProxyServer.getInstance().getPluginManager().getPlugin("floodgate") == null) {
            logInfo("FloodGateAPI not found.");
            setFloodgateApi(null);
            return;
        }

        try {
            setFloodgateApi(FloodgateApi.getInstance());
            if (floodgateApi != null) {
                logInfo("FloodGateAPI loaded successfully!");
            }
        } catch (Exception e) {
            logWarning("Failed to load FloodGateAPI: " + e.getMessage());
            setFloodgateApi(null);
        }
    }

    private void logInfo(String message) {
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §e" + message);
    }

    private void logWarning(String message) {
        ProxyServer.getInstance().getLogger().warning("§a§lDBA §8| §c" + message);
    }
}
