package net.uraharanz.plugins.dynamicbungeeauth.methods;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

import java.util.List;

/**
 * @author an5w1r@163.com
 */
public class ServerMethods {

    private static final List<String> authList = DBABungeePlugin.plugin.getConfigLoader().getStringListCFG("Servers.Auth");
    private static final String authError = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("Servers.AuthError");
    private static final List<String> lobbyList = DBABungeePlugin.plugin.getConfigLoader().getStringListCFG("Servers.Lobby");
    private static final String lobbyError = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("Servers.LobbyError");
    private static final boolean connectLastServer = DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Options.ConnectUserLastServer");

    public static ServerInfo getAuth() {
        return getServerWithLoadBalance(authList, authError, "Auth");
    }

    public static ServerInfo getLobby() {
        return getServerWithLoadBalance(lobbyList, lobbyError, "Lobby");
    }

    private static ServerInfo getServerWithLoadBalance(List<String> serverList, String fallbackServerName, String serverType) {
        if (serverList.isEmpty()) {
            return getFallbackServer(fallbackServerName);
        }

        ServerInfo leastPopulatedServer = null;

        for (String serverName : serverList) {
            ServerInfo server = DBABungeePlugin.plugin.getProxy().getServers().get(serverName);

            if (server == null) {
                logServerConfigError(serverName);
                return null;
            }

            if (leastPopulatedServer == null || server.getPlayers().size() < leastPopulatedServer.getPlayers().size()) {
                leastPopulatedServer = server;
            }
        }

        return leastPopulatedServer;
    }

    private static ServerInfo getFallbackServer(String fallbackServerName) {
        if (fallbackServerName == null || fallbackServerName.isEmpty()) {
            return null;
        }
        return DBABungeePlugin.plugin.getProxy().getServerInfo(fallbackServerName);
    }

    private static void logServerConfigError(String serverName) {
        ProxyServer.getInstance().getLogger().warning(String.format(
                "§a§lDBA §8| §cIf you are seeing this message it is because your configuration in " +
                        "the Lobby/Auth section is wrong because server: %s do not exist in the Bungee configuration.", serverName));
        ProxyServer.getInstance().getLogger().warning("§a§lDBA §8| §cCheck this image for reference: https://gyazo.com/b09c11a43d47f4011536bcb1a1d1e787");
    }

    public static void sendLobbyServer(ProxiedPlayer player) {
        if (connectLastServer) {
            connectToLastServer(player);
        } else {
            connectToDefaultLobby(player);
        }
    }

    private static void connectToLastServer(ProxiedPlayer player) {
        SQL.getPlayerDataS(player, "server", new CallbackSQL<String>() {
            @Override
            public void done(String lastServerName) {
                if (lastServerName == null || lastServerName.isEmpty()) {
                    return;
                }

                if (isAuthServer(lastServerName)) {
                    connectToDefaultLobby(player);
                    return;
                }

                ServerInfo lastServer = DBABungeePlugin.plugin.getProxy().getServerInfo(lastServerName);
                if (lastServer != null) {
                    player.connect(lastServer);
                } else {
                    connectToDefaultLobby(player);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void connectToDefaultLobby(ProxiedPlayer player) {
        ServerInfo lobbyServer = getLobby();
        if (lobbyServer != null) {
            connectToLobby(player, lobbyServer);
        }
    }

    private static boolean isAuthServer(String serverName) {
        return authList.contains(serverName) || authError.equals(serverName);
    }

    public static void connectToLobby(ProxiedPlayer player, ServerInfo lobbyServer) {
        if (player.getServer() == null) {
            return;
        }

        String currentServerName = player.getServer().getInfo().getName();

        if (isInLobby(currentServerName, lobbyServer.getName())) {
            return;
        }

        player.connect(lobbyServer);
    }

    private static boolean isInLobby(String currentServerName, String targetLobbyName) {
        if (!lobbyList.isEmpty()) {
            return lobbyList.contains(currentServerName);
        }
        return currentServerName.equals(targetLobbyName);
    }

    public static void swapLobby(ProxiedPlayer player, ServerInfo lobbyServer) {
        if (player.getServer() == null) {
            return;
        }

        String currentServerName = player.getServer().getInfo().getName();
        if (!currentServerName.equals(lobbyServer.getName())) {
            player.connect(lobbyServer);
        }
    }
}
