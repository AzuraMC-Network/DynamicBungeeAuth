package net.uraharanz.plugins.dynamicbungeeauth.utils.messages;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.ChatColor;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MessageHandler
{
    private static final int CENTER_PX = 154;
    private static final int MAX_PX = 250;

    public static void sendCenteredMessage(final ProxiedPlayer proxiedPlayer, String str) {
        if (!main.plugin.getConfigLoader().getBooleanCFG("Options.UseCenteredMessages")) {
            proxiedPlayer.sendMessage(sendMSG(str));
        }
        else {
            str = ChatColor.translateAlternateColorCodes('&', str);
            int n = 0;
            int n2 = 0;
            boolean b = false;
            int n3 = 0;
            int n4 = 0;
            String string = null;
            String string2 = "";
            for (final char c : str.toCharArray()) {
                Label_0254: {
                    if (c == '§') {
                        n2 = 1;
                    }
                    else {
                        if (n2 != 0) {
                            n2 = 0;
                            string2 = "§" + c;
                            if (c == 'l' || c == 'L') {
                                b = true;
                                break Label_0254;
                            }
                            b = false;
                        }
                        else if (c == ' ') {
                            n4 = n3;
                        }
                        else {
                            final FontInfo defaultFontInfo = FontInfo.getDefaultFontInfo(c);
                            n += (b ? defaultFontInfo.getBoldLength() : defaultFontInfo.getLength());
                            ++n;
                        }
                        if (n >= 250) {
                            string = string2 + str.substring(n4 + 1);
                            str = str.substring(0, n4 + 1);
                            break;
                        }
                        ++n3;
                    }
                }
            }
            final int n5 = 154 - n / 2;
            final int n6 = FontInfo.SPACE.getLength() + 1;
            int j = 0;
            final StringBuilder sb = new StringBuilder();
            while (j < n5) {
                sb.append(" ");
                j += n6;
            }
            proxiedPlayer.sendMessage(sendMSG(sb + str));
            if (string != null) {
                sendCenteredMessage(proxiedPlayer, string);
            }
        }
    }

    public static BaseComponent sendMSG(final String s) {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', s));
    }
}
