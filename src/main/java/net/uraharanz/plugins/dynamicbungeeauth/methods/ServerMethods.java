package net.uraharanz.plugins.dynamicbungeeauth.methods;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

import java.util.List;

public class ServerMethods {
    private static final List<String> authList = DBABungeePlugin.plugin.getConfigLoader().getStringListCFG("Servers.Auth");
    private static final String authError = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("Servers.AuthError");
    private static final List<String> lobbyList = DBABungeePlugin.plugin.getConfigLoader().getStringListCFG("Servers.Lobby");
    private static final String lobbyError = DBABungeePlugin.plugin.getConfigLoader().getStringCFG("Servers.LobbyError");
    private static final boolean connectLastServer = DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Options.ConnectUserLastServer");

    public static ServerInfo getAuth() {
        if (!authList.isEmpty()) {
            ServerInfo serverInfo = null;
            for (String string : authList) {
                ServerInfo serverInfo2 = DBABungeePlugin.plugin.getProxy().getServers().get(string);
                if (serverInfo2 == null) {
                    ProxyServer.getInstance().getLogger().warning("§a§lDBA §8| §cIf you are seeing this message it is because your configuration in the Lobby/Auth section is wrong because server: " + string + " do not exist in the Bungee configuration.");
                    ProxyServer.getInstance().getLogger().warning("§a§lDBA §8| §cCheck this image for reference: https://gyazo.com/b09c11a43d47f4011536bcb1a1d1e787");
                    return null;
                }
                if (serverInfo == null) {
                    serverInfo = serverInfo2;
                }
                if (serverInfo2.getPlayers().size() >= serverInfo.getPlayers().size()) continue;
                serverInfo = serverInfo2;
            }
            return serverInfo;
        }
        if (!authError.isEmpty()) {
            return DBABungeePlugin.plugin.getProxy().getServerInfo(authError);
        }
        return null;
    }

    public static ServerInfo getLobby() {
        if (!lobbyList.isEmpty()) {
            ServerInfo serverInfo = null;
            for (String string : lobbyList) {
                ServerInfo serverInfo2 = DBABungeePlugin.plugin.getProxy().getServers().get(string);
                if (serverInfo2 == null) {
                    ProxyServer.getInstance().getLogger().warning("§a§lDBA §8| §cIf you are seeing this message it is because your configuration in the Lobby/Auth section is wrong because server: " + string + " do not exist in the Bungee configuration.");
                    ProxyServer.getInstance().getLogger().warning("§a§lDBA §8| §cCheck this image for reference: https://gyazo.com/b09c11a43d47f4011536bcb1a1d1e787");
                    return null;
                }
                if (serverInfo == null) {
                    serverInfo = serverInfo2;
                }
                if (serverInfo2.getPlayers().size() >= serverInfo.getPlayers().size()) continue;
                serverInfo = serverInfo2;
            }
            return serverInfo;
        }
        if (!lobbyError.isEmpty()) {
            return DBABungeePlugin.plugin.getProxy().getServerInfo(lobbyError);
        }
        return null;
    }

    public static void sendLobbyServer(final ProxiedPlayer proxiedPlayer) {
        if (connectLastServer) {
            SQL.getPlayerDataS(proxiedPlayer, "server", new CallbackSQL<String>(){

                @Override
                public void done(String string) {
                    if (string != null && !string.isEmpty()) {
                        ServerInfo serverInfo = DBABungeePlugin.plugin.getProxy().getServerInfo(string);
                        if (authList.contains(string) || authError.equals(string)) {
                            ServerInfo serverInfo2 = ServerMethods.getLobby();
                            if (serverInfo2 != null) {
                                ServerMethods.connectToLobby(proxiedPlayer, serverInfo2);
                            }
                        } else if (serverInfo != null) {
                            proxiedPlayer.connect(serverInfo);
                        } else {
                            ServerInfo serverInfo3 = ServerMethods.getLobby();
                            if (serverInfo3 != null) {
                                ServerMethods.connectToLobby(proxiedPlayer, serverInfo3);
                            }
                        }
                    }
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else {
            ServerInfo serverInfo = ServerMethods.getLobby();
            if (serverInfo != null) {
                ServerMethods.connectToLobby(proxiedPlayer, serverInfo);
            }
        }
    }

    public static void connectToLobby(ProxiedPlayer proxiedPlayer, ServerInfo serverInfo) {
        if (proxiedPlayer.getServer() != null) {
            if (!lobbyList.isEmpty()) {
                if (!lobbyList.contains(proxiedPlayer.getServer().getInfo().getName())) {
                    proxiedPlayer.connect(serverInfo);
                }
            } else if (!proxiedPlayer.getServer().getInfo().getName().equals(serverInfo.getName())) {
                proxiedPlayer.connect(serverInfo);
            }
        }
    }

    public static void swapLobby(ProxiedPlayer proxiedPlayer, ServerInfo serverInfo) {
        if (!proxiedPlayer.getServer().getInfo().getName().equals(serverInfo.getName())) {
            proxiedPlayer.connect(serverInfo);
        }
    }
}
