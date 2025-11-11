//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.uraharanz.plugins.dynamicbungeeauth.cache.spam;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.cache.PlayerCache;
import net.uraharanz.plugins.dynamicbungeeauth.utils.messages.MessageHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SpamPlayerList {
    private final DBABungeePlugin plugin;
    private final ConcurrentHashMap<String, SpamPlayer> player;
    private final int seconds;
    private final boolean enabled;
    private final boolean sendTitles;
    private final boolean sendMessage;
    private final boolean captchaEnabled;
    private final String titleRegisterTop;
    private final String titleRegisterBottom;
    private final int titleRegisterFadeIn;
    private final int titleRegisterStay;
    private final int titleRegisterFadeOut;
    private final List<String> messageRegisters;
    private final String titleLoginTop;
    private final String titleLoginBottom;
    private final int titleLoginFadeIn;
    private final int titleLoginStay;
    private final int titleLoginFadeOut;
    private final List<String> messageLogins;

    public SpamPlayerList(DBABungeePlugin var1) {
        this.plugin = var1;
        this.enabled = var1.getConfigLoader().getBooleanCFG("Options.SpamMessages.Enabled");
        this.seconds = var1.getConfigLoader().getIntegerCFG("Options.SpamMessages.Seconds");
        this.sendTitles = var1.getConfigLoader().getBooleanCFG("Options.SpamMessages.SendTitles");
        this.sendMessage = var1.getConfigLoader().getBooleanCFG("Options.SpamMessages.SendMessages");
        this.captchaEnabled = var1.getConfigLoader().getBooleanCFG("Options.Captcha");
        this.titleRegisterTop = var1.getConfigLoader().getStringMSG("Titles.register.top");
        this.titleRegisterBottom = var1.getConfigLoader().getStringMSG("Titles.register.bottom");
        this.titleRegisterFadeIn = var1.getConfigLoader().getIntegerMSG("Titles.register.options.fadein");
        this.titleRegisterStay = var1.getConfigLoader().getIntegerMSG("Titles.register.options.stay");
        this.titleRegisterFadeOut = var1.getConfigLoader().getIntegerMSG("Titles.register.options.fadeout");
        this.messageRegisters = var1.getConfigLoader().getStringListMSG("AutoMessages.register");
        this.titleLoginTop = var1.getConfigLoader().getStringMSG("Titles.login.top");
        this.titleLoginBottom = var1.getConfigLoader().getStringMSG("Titles.login.bottom");
        this.titleLoginFadeIn = var1.getConfigLoader().getIntegerMSG("Titles.login.options.fadein");
        this.titleLoginStay = var1.getConfigLoader().getIntegerMSG("Titles.login.options.stay");
        this.titleLoginFadeOut = var1.getConfigLoader().getIntegerMSG("Titles.login.options.fadeout");
        this.messageLogins = var1.getConfigLoader().getStringListMSG("AutoMessages.login");
        this.player = new ConcurrentHashMap<>();
        if (this.enabled) {
            this.taskSpamMessages();
        }

    }

    public void addPlayer(SpamPlayer var1) {
        this.player.putIfAbsent(var1.getName(), var1);
    }

    public SpamPlayer searchPlayer(String var1) {
        return this.player.get(var1);
    }

    public void removePlayer(String var1) {
        SpamPlayer var2 = this.player.get(var1);
        if (var2 != null) {
            this.player.remove(var1);
        }

    }

    public void taskSpamMessages() {
        this.plugin.getProxy().getScheduler().schedule(this.plugin, () -> {
            for(Map.Entry<String, SpamPlayer> entry : this.player.entrySet()) {
                SpamPlayer spamPlayer = entry.getValue();
                if (!spamPlayer.isValid()) {
                    ProxiedPlayer player = this.plugin.getProxy().getPlayer(spamPlayer.getName());
                    if (spamPlayer.getStatus().equalsIgnoreCase("REGISTER")) {
                        if (player == null) {
                            this.removePlayer(spamPlayer.getName());
                        } else {
                            if (this.sendTitles) {
                                Title title = ProxyServer.getInstance().createTitle();
                                title.title(MessageHandler.createColoredMessage(this.titleRegisterTop));
                                if (this.captchaEnabled) {
                                    PlayerCache playerCache = DBABungeePlugin.plugin.getPlayerCacheList().searchCache(player.getName());
                                    title.subTitle(MessageHandler.createColoredMessage(this.titleRegisterBottom.replaceAll("%captcha%", playerCache.getCaptcha())));
                                } else {
                                    title.subTitle(MessageHandler.createColoredMessage(this.titleRegisterBottom));
                                }

                                title.fadeIn(this.titleRegisterFadeIn);
                                title.stay(this.titleRegisterStay);
                                title.fadeOut(this.titleRegisterFadeOut);
                                title.send(player);
                            }

                            if (this.sendMessage) {
                                for(String message : this.messageRegisters) {
                                    if (this.messageRegisters.indexOf(message) != 0 && this.messageRegisters.indexOf(message) != this.messageRegisters.size() - 1) {
                                        if (this.captchaEnabled) {
                                            PlayerCache playerCache = DBABungeePlugin.plugin.getPlayerCacheList().searchCache(spamPlayer.getName());
                                            MessageHandler.sendCenteredMessage(player, message.replaceAll("&", "§").replaceAll("%captcha%", playerCache.getCaptcha()));
                                        } else {
                                            MessageHandler.sendCenteredMessage(player, message.replaceAll("&", "§"));
                                        }
                                    } else if (this.captchaEnabled) {
                                        PlayerCache playerCache = DBABungeePlugin.plugin.getPlayerCacheList().searchCache(spamPlayer.getName());
                                        player.sendMessage(MessageHandler.createColoredMessage(message.replaceAll("%captcha%", playerCache.getCaptcha())));
                                    } else {
                                        player.sendMessage(MessageHandler.createColoredMessage(message));
                                    }
                                }
                            }
                        }
                    }

                    if (spamPlayer.getStatus().equalsIgnoreCase("LOGIN")) {
                        if (player != null) {
                            if (this.sendTitles) {
                                Title title = ProxyServer.getInstance().createTitle();
                                title.title(MessageHandler.createColoredMessage(this.titleLoginTop));
                                title.subTitle(MessageHandler.createColoredMessage(this.titleLoginBottom));
                                title.fadeIn(this.titleLoginFadeIn);
                                title.stay(this.titleLoginStay);
                                title.fadeOut(this.titleLoginFadeOut);
                                title.send(player);
                            }

                            if (this.sendMessage) {
                                for(String message : this.messageLogins) {
                                    if (this.messageLogins.indexOf(message) != 0 && this.messageLogins.indexOf(message) != this.messageLogins.size() - 1) {
                                        MessageHandler.sendCenteredMessage(player, message.replaceAll("&", "§"));
                                    } else {
                                        player.sendMessage(MessageHandler.createColoredMessage(message));
                                    }
                                }
                            }
                        } else {
                            this.removePlayer(spamPlayer.getName());
                        }
                    }
                }
            }

        }, 1L, this.seconds, TimeUnit.SECONDS);
    }
}
