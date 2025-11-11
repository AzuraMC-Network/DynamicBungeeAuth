package net.uraharanz.plugins.dynamicbungeeauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.methods.ServerMethods;

/**
 * @author an5w1r@163.com
 */
public class LobbyCMD extends Command {
    private final DBABungeePlugin plugin;

    public LobbyCMD(DBABungeePlugin plugin) {
        super("lobby", "auth.lobby", "hub");
        this.plugin = plugin;
    }

    public LobbyCMD(DBABungeePlugin plugin, Boolean includeSpawnAlias) {
        super("lobby", "auth.lobby", "hub", "spawn");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            ProxyServer.getInstance().getLogger().info("§a§lDBA §8| §cThis command is only for users.");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            ServerInfo lobby = ServerMethods.getLobby();
            if (lobby != null) {
                ServerMethods.swapLobby(player, lobby);
            }
        });
    }
}
