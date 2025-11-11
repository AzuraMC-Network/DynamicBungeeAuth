package net.uraharanz.plugins.dynamicbungeeauth.timers;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.uraharanz.plugins.dynamicbungeeauth.DBAPlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RegisterTimer {
    private final DBAPlugin plugin;
    @Getter
    private final HashMap<String, ScheduledTask> timers;
    private final int time;

    public RegisterTimer(DBAPlugin plugin) {
        this.plugin = plugin;
        this.timers = new HashMap<>();
        this.time = plugin.getConfigLoader().getIntegerCFG("Timers.RegisterMax");
    }

    public void regTimer(final ProxiedPlayer proxiedPlayer) {
        this.timers.put(proxiedPlayer.getName(), this.plugin.getProxy().getScheduler().schedule(this.plugin, () -> {
            if (this.timers.containsKey(proxiedPlayer.getName())) {
                try {
                    SQL.getPlayerDataS(proxiedPlayer.getName(), "valid", new CallbackSQL<String>(){

                        @Override
                        public void done(String string) {
                            if (string != null) {
                                if (string.equals("0")) {
                                    RegisterTimer.this.timers.get(proxiedPlayer.getName()).cancel();
                                    RegisterTimer.this.timers.remove(proxiedPlayer.getName());
                                    proxiedPlayer.disconnect(MessageHandler.sendMSG(RegisterTimer.this.plugin.getConfigLoader().getStringMSG("KickMessages.register")));
                                } else {
                                    RegisterTimer.this.timers.get(proxiedPlayer.getName()).cancel();
                                    RegisterTimer.this.timers.remove(proxiedPlayer.getName());
                                }
                            } else {
                                RegisterTimer.this.timers.get(proxiedPlayer.getName()).cancel();
                                RegisterTimer.this.timers.remove(proxiedPlayer.getName());
                                proxiedPlayer.disconnect(MessageHandler.sendMSG(RegisterTimer.this.plugin.getConfigLoader().getStringMSG("KickMessages.register")));
                            }
                        }

                        @Override
                        public void error(Exception exception) {
                        }
                    });
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }, this.time, TimeUnit.SECONDS));
    }

}
