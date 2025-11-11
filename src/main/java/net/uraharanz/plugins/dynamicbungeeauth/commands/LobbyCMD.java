package net.uraharanz.plugins.dynamicbungeeauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.methods.ServerMethods;

public class LobbyCMD
extends Command {
    private main plugin;

    public LobbyCMD(main main2) {
        super("lobby", "auth.lobby", "lobby", "hub");
        this.plugin = main2;
    }

    public LobbyCMD(main main2, Boolean bl) {
        super("lobby", "auth.lobby", "lobby", "hub", "spawn");
        this.plugin = main2;
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
