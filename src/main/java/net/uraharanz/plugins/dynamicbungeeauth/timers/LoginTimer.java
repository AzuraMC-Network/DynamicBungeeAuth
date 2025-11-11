package net.uraharanz.plugins.dynamicbungeeauth.timers;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackSQL;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;
import net.uraharanz.plugins.dynamicbungeeauth.utils.mysql.SQL;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginTimer {
    private final DBABungeePlugin plugin;
    @Getter
    private final HashMap<String, ScheduledTask> timers;
    private final int time;

    public LoginTimer(DBABungeePlugin plugin) {
        this.plugin = plugin;
        this.timers = new HashMap<>();
        this.time = plugin.getConfigLoader().getIntegerCFG("Timers.LoginMax");
    }

    public void logTimer(final ProxiedPlayer proxiedPlayer) {
        this.timers.put(proxiedPlayer.getName(), this.plugin.getProxy().getScheduler().schedule(this.plugin, () -> {
            if (this.timers.containsKey(proxiedPlayer.getName())) {
                try {
                    SQL.getPlayerDataS(proxiedPlayer.getName(), "valid", new CallbackSQL<String>(){

                        @Override
                        public void done(String string) {
                            if (string != null) {
                                if (string.equals("0")) {
                                    LoginTimer.this.timers.get(proxiedPlayer.getName()).cancel();
                                    LoginTimer.this.timers.remove(proxiedPlayer.getName());
                                    SQL.setPlayerData(proxiedPlayer, "lwlogged", "0");
                                    proxiedPlayer.disconnect(MessageHandler.sendMSG(LoginTimer.this.plugin.getConfigLoader().getStringMSG("KickMessages.login")));
                                } else {
                                    LoginTimer.this.timers.get(proxiedPlayer.getName()).cancel();
                                    LoginTimer.this.timers.remove(proxiedPlayer.getName());
                                }
                            } else {
                                LoginTimer.this.timers.get(proxiedPlayer.getName()).cancel();
                                LoginTimer.this.timers.remove(proxiedPlayer.getName());
                                SQL.setPlayerData(proxiedPlayer, "lwlogged", "0");
                                proxiedPlayer.disconnect(MessageHandler.sendMSG(LoginTimer.this.plugin.getConfigLoader().getStringMSG("KickMessages.login")));
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
