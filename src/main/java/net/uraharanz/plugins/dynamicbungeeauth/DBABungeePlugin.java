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
import net.uraharanz.plugins.dynamicbungeeauth.listeners.loader.LoadListeners;
import net.uraharanz.plugins.dynamicbungeeauth.loader.ConfigLoader;
import net.uraharanz.plugins.dynamicbungeeauth.timers.LoginTimer;
import net.uraharanz.plugins.dynamicbungeeauth.timers.MaxLogin;
import net.uraharanz.plugins.dynamicbungeeauth.timers.RegisterTimer;
import net.uraharanz.plugins.dynamicbungeeauth.utils.apis.ProfileGenerator;
import net.uraharanz.plugins.dynamicbungeeauth.utils.configs.Files;
import net.uraharanz.plugins.dynamicbungeeauth.utils.importers.AuthmeImporter;
import net.uraharanz.plugins.dynamicbungeeauth.utils.importers.SQLImp;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.PoolManager;
import net.uraharanz.plugins.dynamicbungeeauth.utils.smtp.EmailSystem;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.HashMap;

@Getter
@Setter
public class DBABungeePlugin
extends Plugin {
    public static DBABungeePlugin plugin;
    private Files files;
    private LoadListeners listeners;
    private PlayerAPIList playerAPIList;
    private PlayerDataList playerDataList;
    private PlayerCacheList playerCacheList;
    private ProfileGenerator profileGenerator;
    private FixList fixList;
    private RegisterTimer registerTimer;
    private LoginTimer loginTimer;
    private MaxLogin maxLogin;
    private SpamPlayerList spamPlayerList;
    private EmailSystem emailSystem;
    private ConfigLoader configLoader;
    private FloodgateApi floodgateApi;
    public static HashMap<String, Boolean> serverLobby;
    public static HashMap<String, Boolean> serverAuth;

    public void onEnable() {
        plugin = this;
        serverLobby = new HashMap<>();
        serverAuth = new HashMap<>();
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eGenerating and loading Config Files.");
        this.setFiles(new Files(this));
        this.files.createConfigs();
        this.files.createMessages();
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eConfig Files Loaded.");
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eConnecting to the DB Server.");
        this.setConfigLoader(new ConfigLoader(plugin));
        PoolManager.connectDB(2);
        PoolManager.createPlayerTable();
        PoolManager.createIPTable();
        PoolManager.createNamesTable();
        PoolManager.alterTable();
        PoolManager.resetValues();
        PoolManager.createIndex();
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eLoading Cache.");
        this.setPlayerAPIList(new PlayerAPIList(this));
        this.getPlayerAPIList().cleanRequest();
        this.setPlayerDataList(new PlayerDataList(this));
        this.getPlayerDataList().cleanData();
        this.setPlayerCacheList(new PlayerCacheList(this));
        this.getPlayerCacheList().cleanCached();
        this.setFixList(new FixList(this));
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eCache Loaded.");
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eLoading Listeners.");
        this.setListeners(new LoadListeners(this));
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eListeners Loaded.");
        this.setProfileGenerator(new ProfileGenerator(this));
        this.Commands();
        this.setRegisterTimer(new RegisterTimer(this));
        this.setLoginTimer(new LoginTimer(this));
        this.setMaxLogin(new MaxLogin(this));
        this.getMaxLogin().resetCountTimer();
        this.setSpamPlayerList(new SpamPlayerList(this));
        ServerState.state = ServerState.NORMAL;
        ServerState.setState(ServerState.NORMAL);
        if (plugin.getConfigLoader().getBooleanCFG("Importers.Enabled")) {
            SQLImp.connectDB();
            AuthmeImporter.importDB();
        }
        this.getProxy().registerChannel("dba:" + this.getConfigLoader().getStringCFG("PluginChannel.verify"));
        this.getProxy().registerChannel("dba:" + this.getConfigLoader().getStringCFG("PluginChannel.premium"));
        this.getProxy().registerChannel("dba:" + this.getConfigLoader().getStringCFG("PluginChannel.cracked"));
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eChannels Registered make sure to SYNC your config section DBA.PluginChannel with DBA config on spigot!");
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §eChecking for §aFloodGateAPI");
        if (ProxyServer.getInstance().getPluginManager().getPlugin("floodgate") != null) {
            this.setFloodgateApi(FloodgateApi.getInstance());
            if (this.getFloodgateApi() != null) {
                ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §aFloodGateAPI §eLoaded correctly");
            }
        } else {
            ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §aFloodGateAPI §cnot found");
            this.setFloodgateApi(null);
        }
    }

    public void onDisable() {
        ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §cShutting Down DB Connections!");
        PoolManager.closeConnection();
    }

    private void Commands() {
        plugin.getProxy().getPluginManager().registerCommand(this, new PremiumCMD(this));
        plugin.getProxy().getPluginManager().registerCommand(this, new CrackedCMD(this));
        plugin.getProxy().getPluginManager().registerCommand(this, new RegisterCMD(this));
        plugin.getProxy().getPluginManager().registerCommand(this, new LoginCMD(this));
        plugin.getProxy().getPluginManager().registerCommand(this, new AdminCMD(this));
        plugin.getProxy().getPluginManager().registerCommand(this, new ChangeCMD(this));
        plugin.getProxy().getPluginManager().registerCommand(this, new LogoutCMD(this));
        if (plugin.getConfigLoader().getBooleanCFG("Options.LobbyCommands")) {
            if (plugin.getConfigLoader().getBooleanCFG("Options.LobbyCommandsSpawn")) {
                plugin.getProxy().getPluginManager().registerCommand(this, new LobbyCMD(this, true));
            } else {
                plugin.getProxy().getPluginManager().registerCommand(this, new LobbyCMD(this));
            }
        }
    }

}
