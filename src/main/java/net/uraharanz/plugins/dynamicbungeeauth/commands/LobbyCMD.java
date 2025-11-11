package net.uraharanz.plugins.dynamicbungeeauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.methods.ServerMethods;

public class LobbyCMD
extends Command {
    private DBAPlugin plugin;

    public LobbyCMD(DBAPlugin plugin) {
        super("lobby", "auth.lobby", "lobby", "hub");
        this.plugin = plugin;
    }

    public LobbyCMD(DBAPlugin plugin, Boolean bl) {
        super("lobby", "auth.lobby", "lobby", "hub", "spawn");
        this.plugin = plugin;
    }

    public void execute(CommandSender commandSender, String[] stringArray) {
        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            if (commandSender instanceof ProxiedPlayer) {
                ProxiedPlayer proxiedPlayer = (ProxiedPlayer)commandSender;
                ServerInfo serverInfo = ServerMethods.getLobby();
                if (serverInfo != null) {
                    ServerMethods.swapLobby(proxiedPlayer, serverInfo);
                }
            } else {
                ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §cThis command is only for users.");
            }
        });
    }
}
