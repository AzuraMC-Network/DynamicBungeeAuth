package net.uraharanz.plugins.dynamicbungeeauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

public class LogoutCMD
extends Command {
    private main plugin;

    public LogoutCMD(main main2) {
        super("logout");
        this.plugin = main2;
    }

    public void execute(CommandSender commandSender, String[] stringArray) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer)commandSender;
            proxiedPlayer.disconnect(MessageHandler.sendMSG(this.plugin.getConfigLoader().getStringMSG("KickMessages.Logout")));
            this.plugin.getPlayerDataList().removePlayer(proxiedPlayer.getName());
            this.plugin.getPlayerCacheList().removeCache(proxiedPlayer.getName());
            SQL.setPlayerData(proxiedPlayer, "lwlogged", "0");
            SQL.setPlayerData(proxiedPlayer, "valid", "0");
        }
    }
}
