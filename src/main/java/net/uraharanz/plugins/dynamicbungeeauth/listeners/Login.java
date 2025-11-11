package net.uraharanz.plugins.dynamicbungeeauth.listeners;

import java.lang.reflect.Field;
import java.util.UUID;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;
import org.geysermc.floodgate.api.FloodgateApi;

public class Login
implements Listener {
    private main plugin;
    private boolean offlineUUID;
    private FloodgateApi floodgateApi;

    public Login(main main2) {
        this.plugin = main2;
        this.offlineUUID = main2.getConfigLoader().getBooleanCFG("Options.OfflineUUID");
    }

    @EventHandler(priority=-64)
    public void onLogin(LoginEvent loginEvent) {
        if (loginEvent.isCancelled()) {
            return;
        }
        if (this.offlineUUID) {
            if (this.plugin.getFloodgateApi() != null && this.plugin.getFloodgateApi().getPlayer(loginEvent.getConnection().getUniqueId()) != null) {
                return;
            }
            PendingConnection pendingConnection = loginEvent.getConnection();
            InitialHandler initialHandler = (InitialHandler)pendingConnection;
            String string = pendingConnection.getName();
            try {
                Field field = InitialHandler.class.getDeclaredField("uniqueId");
                field.setAccessible(true);
                field.set(pendingConnection, Login.generateOfflineUUID(string));
            }
            catch (IllegalAccessException | NoSuchFieldException reflectiveOperationException) {
                reflectiveOperationException.printStackTrace();
            }
        }
    }

    public static UUID generateOfflineUUID(String string) {
        return SQL.generateOfflineUUID(string);
    }
}
