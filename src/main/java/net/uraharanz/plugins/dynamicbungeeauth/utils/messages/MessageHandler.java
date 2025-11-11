package net.uraharanz.plugins.dynamicbungeeauth.utils.messages;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;

/**
 * @author an5w1r@163.com
 */
public class MessageHandler {

    private static final int CENTER_PIXEL = 154;

    private static final int MAX_LINE_WIDTH = 250;

    private static final char COLOR_CODE_PREFIX = '§';

    public static void sendCenteredMessage(ProxiedPlayer player, String message) {
        if (!DBABungeePlugin.plugin.getConfigLoader().getBooleanCFG("Options.UseCenteredMessages")) {
            player.sendMessage(createColoredMessage(message));
            return;
        }

        String coloredMessage = ChatColor.translateAlternateColorCodes('&', message);

        MessageMetrics metrics = calculateMessageMetrics(coloredMessage);

        String centeredMessage = addCenterPadding(metrics.processedMessage, metrics.totalWidth);
        player.sendMessage(createColoredMessage(centeredMessage));

        if (metrics.remainingText != null) {
            sendCenteredMessage(player, metrics.remainingText);
        }
    }

    public static BaseComponent createColoredMessage(String message) {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
    }

    private static MessageMetrics calculateMessageMetrics(String message) {
        int totalWidth = 0;
        int charIndex = 0;
        int lastSpaceIndex = 0;
        boolean isBold = false;
        boolean isNextColorCode = false;
        String lastColorCode = "";
        String remainingText = null;

        char[] chars = message.toCharArray();

        for (char currentChar : chars) {
            if (currentChar == COLOR_CODE_PREFIX) {
                isNextColorCode = true;
                continue;
            }

            if (isNextColorCode) {
                isNextColorCode = false;
                lastColorCode = COLOR_CODE_PREFIX + String.valueOf(currentChar);

                isBold = currentChar == 'l' || currentChar == 'L';

                charIndex++;
                continue;
            }

            if (currentChar == ' ') {
                lastSpaceIndex = charIndex;
            } else {
                int charWidth = isBold ? FontInfo.getBoldLength(currentChar) : FontInfo.getLength(currentChar);
                totalWidth += charWidth + 1;
            }

            if (totalWidth >= MAX_LINE_WIDTH) {
                remainingText = lastColorCode + message.substring(lastSpaceIndex + 1);
                message = message.substring(0, lastSpaceIndex + 1);
                break;
            }

            charIndex++;
        }

        return new MessageMetrics(message, totalWidth, remainingText);
    }

    private static String addCenterPadding(String message, int messageWidth) {
        int leftOffset = CENTER_PIXEL - messageWidth / 2;
        int spaceWidth = FontInfo.getSpaceWidth() + 1;
        int spaceCount = leftOffset / spaceWidth;

        StringBuilder padding = new StringBuilder(spaceCount);
        for (int i = 0; i < spaceCount; i++) {
            padding.append(' ');
        }

        return padding + message;
    }

    private static class MessageMetrics {
        final String processedMessage;
        final int totalWidth;
        final String remainingText;

        MessageMetrics(String processedMessage, int totalWidth, String remainingText) {
            this.processedMessage = processedMessage;
            this.totalWidth = totalWidth;
            this.remainingText = remainingText;
        }
    }
}
