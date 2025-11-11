package net.uraharanz.plugins.dynamicbungeeauth.spigot.listeners;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.main;
import net.uraharanz.plugins.dynamicbungeeauth.spigot.utils.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PluginChannelListener
implements PluginMessageListener {
    private static final String KickMSG = Config.get("ConfigS.yml").getString("Messages.MaxTimeKick").replaceAll("&", "\u00a7");
    private static final int MaxTime = Config.get("ConfigS.yml").getInt("Options.MaxTime");
    private static final boolean AntiStuck = Config.get("ConfigS.yml").getBoolean("Options.AntiStuck.Enable");
    private static final int AntiDelay = Config.get("ConfigS.yml").getInt("Options.AntiStuck.Delay");
    private static final String AntiStuckS = Config.get("ConfigS.yml").getString("Options.AntiStuck.Server");
    private static final boolean Inventory = Config.get("ConfigS.yml").getBoolean("Options.EnableInventory");
    static HashMap<String, ItemStack[]> items = new HashMap<>();
    static HashMap<String, ItemStack[]> armor = new HashMap<>();

    public void onPluginMessageReceived(String string, Player player, byte[] byArray) {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(byArray));
        try {
            String string2 = dataInputStream.readUTF();
            if (string2.equalsIgnoreCase("dba:verifyplayer")) {
                main.plugin.getPlayerInfoList().addPlayer(player);
                Bukkit.getServer().getScheduler().runTask(main.plugin, () -> {
                    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                        if (!potionEffect.getType().equals(PotionEffectType.BLINDNESS)) continue;
                        player.removePotionEffect(potionEffect.getType());
                    }
                });
                if (AntiStuck && player.isOnline()) {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main.plugin, () -> {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                        try {
                            dataOutputStream.writeUTF("Connect");
                            dataOutputStream.writeUTF(AntiStuckS);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        player.sendPluginMessage(main.plugin, "BungeeCord", byteArrayOutputStream.toByteArray());
                    }, AntiDelay * 20L);
                }
                if (Inventory) {
                    ItemStack[] itemStackArray = items.get(player.getName());
                    ItemStack[] itemStackArray2 = armor.get(player.getName());
                    if (itemStackArray != null) {
                        player.getInventory().setContents(itemStackArray);
                    }
                    if (itemStackArray2 != null) {
                        player.getInventory().setArmorContents(itemStackArray2);
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
