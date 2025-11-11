package net.uraharanz.plugins.dynamicbungeeauth.utils.uuid;

import com.google.common.base.Charsets;
import java.util.UUID;

public class UUIDUtils {
    public static String generateOfflineUUID(String string) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(Charsets.UTF_8)).toString();
    }
}
