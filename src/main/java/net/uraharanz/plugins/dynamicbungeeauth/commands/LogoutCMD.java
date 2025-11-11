package net.uraharanz.plugins.dynamicbungeeauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

/**
 * @author an5w1r@163.com
 */
public class LogoutCMD extends Command {
    private DBAPlugin plugin;

    public LogoutCMD(DBAPlugin plugin) {
        super("logout");
        this.plugin = plugin;
    }

    public void execute(CommandSender commandSender, String[] stringArray) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
            proxiedPlayer.disconnect(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("KickMessages.Logout")));
            this.plugin.getPlayerDataList().removePlayer(proxiedPlayer.getName());
            this.plugin.getPlayerCacheList().removeCache(proxiedPlayer.getName());
            SQL.setPlayerData(proxiedPlayer, "lwlogged", "0");
            SQL.setPlayerData(proxiedPlayer, "valid", "0");
        }
    }
}
