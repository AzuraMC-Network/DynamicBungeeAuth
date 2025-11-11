package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.player.PlayerData;
import net.uraharanz.plugins.dynamicbungeeauth.methods.PlayersMethods;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author an5w1r@163.com
 */
public class ChatListener implements Listener {
    private static final Set<String> ALLOWED_COMMANDS = new HashSet<>(Arrays.asList(
            "/register", "/login", "/l", "/reg", "/premium"
    ));
    private final DBAPlugin plugin;
    private final int workMethod;

    public ChatListener(DBAPlugin plugin) {
        this.plugin = plugin;
        this.workMethod = plugin.getConfigLoader().getIntegerCFG("WorkMethod.Value");
    }

    @EventHandler(priority = 64)
    public void onPlayerChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();

        if (!message.startsWith("/")) {
            handleNonCommand(event, player);
            return;
        }

        handleCommand(event, player, message);
    }

    private void handleNonCommand(ChatEvent event, ProxiedPlayer player) {
        PlayerData playerData = plugin.getPlayerDataList().searchPlayer(player.getName());

        if (playerData == null) {
            event.setCancelled(true);
            checkPlayerAndShowMessage(player);
        } else if (!isPlayerAuthenticated(playerData)) {
            event.setCancelled(true);
            PlayersMethods.pMessage(player, 2);
        }
    }

    private void handleCommand(ChatEvent event, ProxiedPlayer player, String message) {
        String command = extractCommand(message);

        if (isAuthCommand(command)) {
            event.setCancelled(false);
            return;
        }

        PlayerData playerData = plugin.getPlayerDataList().searchPlayer(player.getName());

        if (playerData == null) {
            event.setCancelled(true);
            checkPlayerAndShowMessage(player);
        } else if (!isPlayerAuthenticated(playerData)) {
            event.setCancelled(true);
            PlayersMethods.pMessage(player, 2);
        }
    }

    private String extractCommand(String message) {
        String[] parts = message.split(" ");
        return parts.length > 0 ? parts[0] : message;
    }

    private boolean isAuthCommand(String command) {
        return ALLOWED_COMMANDS.stream()
                .anyMatch(allowed -> allowed.equalsIgnoreCase(command));
    }

    private boolean isPlayerAuthenticated(PlayerData playerData) {
        // is premium
        if (playerData.getPassword().equals("null")) {
            return true;
        }

        return playerData.isValid();
    }

    private void checkPlayerAndShowMessage(final ProxiedPlayer player) {
        SQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
            @Override
            public void done(Boolean isRegistered) {
                if (isRegistered) {
                    // registered but not login
                    PlayersMethods.pMessage(player, 2);
                } else {
                    // not registered
                    PlayersMethods.pMessage(player, 3);
                }
            }

            @Override
            public void error(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
