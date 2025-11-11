package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.fix.Fix;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.methods.ServerMethods;

/**
 * @author an5w1r@163.com
 */
public class SwitchListener implements Listener {

    private final DBABungeePlugin plugin;
    private final boolean isOldVersionMode;

    public SwitchListener(DBABungeePlugin plugin) {
        this.plugin = plugin;
        this.isOldVersionMode = plugin.getConfigLoader().getBooleanCFG("Options.OldVersion");
    }

    @EventHandler(priority = 64)
    public void onSwitch(ServerConnectEvent event) {
        if (isOldVersionMode) {
            handleOldVersionMode(event);
        } else {
            handleNormalMode(event);
        }
    }

    private void handleOldVersionMode(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String playerName = player.getName();

        Fix fix = plugin.getFixList().searchPlayer(playerName);

        // first connection, create Fix record
        if (fix == null) {
            Fix newFix = new Fix(playerName, player.getUniqueId().toString());
            plugin.getFixList().addPlayer(newFix);

            ServerInfo targetServer = selectAuthOrLobbyServer(event.getTarget());
            event.setTarget(targetServer);
            return;
        }

        PlayerData playerData = plugin.getPlayerDataList().searchPlayer(playerName);
        if (!isPlayerValid(playerData)) {
            event.setCancelled(true);
        }
    }

    private void handleNormalMode(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerConnectEvent.Reason reason = event.getReason();

        if (isFirstJoin(reason, player)) {
            handleFirstJoin(event);
        } else {
            handleServerSwitch(event);
        }
    }

    private boolean isFirstJoin(ServerConnectEvent.Reason reason, ProxiedPlayer player) {
        return reason == ServerConnectEvent.Reason.JOIN_PROXY || player.getServer() == null;
    }

    private void handleFirstJoin(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataList().searchPlayer(player.getName());

        ServerInfo targetServer;

        if (playerData == null) {
            // new player -> auth/lobby
            targetServer = selectAuthOrLobbyServer(event.getTarget());
        } else if (playerData.isPremium()) {
            // premium player -> (lobby -> auth)
            targetServer = selectLobbyOrAuthServer(event.getTarget());
        } else {
            // cracked player -> (auth -> lobby)
            targetServer = selectAuthOrLobbyServer(event.getTarget());
        }

        event.setTarget(targetServer);
    }

    private void handleServerSwitch(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataList().searchPlayer(player.getName());

        if (isPlayerValid(playerData)) {
            event.setTarget(event.getTarget());
        } else {
            event.setCancelled(true);
        }
    }

    private boolean isPlayerValid(PlayerData playerData) {
        return playerData != null && playerData.isValid();
    }

    private ServerInfo selectAuthOrLobbyServer(ServerInfo originalTarget) {
        ServerInfo authServer = ServerMethods.getAuth();
        if (authServer != null) {
            return authServer;
        }

        ServerInfo lobbyServer = ServerMethods.getLobby();
        if (lobbyServer != null) {
            return lobbyServer;
        }

        return originalTarget;
    }

    private ServerInfo selectLobbyOrAuthServer(ServerInfo originalTarget) {
        ServerInfo lobbyServer = ServerMethods.getLobby();
        if (lobbyServer != null) {
            return lobbyServer;
        }

        ServerInfo authServer = ServerMethods.getAuth();
        if (authServer != null) {
            return authServer;
        }

        return originalTarget;
    }
}
