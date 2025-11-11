package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

/**
 * @author an5w1r@163.com
 */
public class Login implements Listener {
    private final DBAPlugin plugin;
    private final boolean offlineUUID;

    public Login(DBAPlugin plugin) {
        this.plugin = plugin;
        this.offlineUUID = plugin.getConfigLoader().getBooleanCFG("Options.OfflineUUID");
    }

    public static UUID generateOfflineUUID(String username) {
        return SQL.generateOfflineUUID(username);
    }

    @EventHandler(priority=-64)
    public void onLogin(LoginEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (this.offlineUUID) {
            final FloodgateApi floodgate = this.plugin.getFloodgateApi();
            if (floodgate != null && floodgate.getPlayer(event.getConnection().getUniqueId()) != null) {
                return;
            }
            final PendingConnection connection = event.getConnection();
            final String username = connection.getName();
            // original code used reflection, but I don't know why, I am not sure use connection.setUniqueId() hasn't issue.
            connection.setUniqueId(Login.generateOfflineUUID(username));
        }
    }
}
