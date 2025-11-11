package net.uraharanz.plugins.dynamicbungeeauth.utils.messages;

import java.util.HashMap;
import java.util.Map;

/**
 * @author an5w1r@163.com
 */
public class FontInfo {

    private static final Map<Character, Integer> CHAR_WIDTHS = new HashMap<>();
    private static final int DEFAULT_WIDTH = 4;
    private static final int SPACE_WIDTH = 3;

    static {
        // uppercase (5 pixels wide)
        for (char c = 'A'; c <= 'Z'; c++) {
            CHAR_WIDTHS.put(c, 5);
        }
        CHAR_WIDTHS.put('Ñ', 5);

        // lowercase (mostly 5 pixels wide, some are special)
        for (char c = 'a'; c <= 'z'; c++) {
            CHAR_WIDTHS.put(c, 5);
        }
        CHAR_WIDTHS.put('ñ', 5);
        CHAR_WIDTHS.put('f', 4);
        CHAR_WIDTHS.put('i', 1);
        CHAR_WIDTHS.put('k', 4);
        CHAR_WIDTHS.put('l', 1);
        CHAR_WIDTHS.put('t', 4);

        // numbers (5 pixels wide)
        for (char c = '0'; c <= '9'; c++) {
            CHAR_WIDTHS.put(c, 5);
        }

        // punctuation marks and special characters
        CHAR_WIDTHS.put('¡', 1);
        CHAR_WIDTHS.put('!', 1);
        CHAR_WIDTHS.put('@', 6);
        CHAR_WIDTHS.put('#', 5);
        CHAR_WIDTHS.put('$', 5);
        CHAR_WIDTHS.put('%', 5);
        CHAR_WIDTHS.put('^', 5);
        CHAR_WIDTHS.put('&', 5);
        CHAR_WIDTHS.put('*', 5);
        CHAR_WIDTHS.put('(', 4);
        CHAR_WIDTHS.put(')', 4);
        CHAR_WIDTHS.put('-', 5);
        CHAR_WIDTHS.put('_', 5);
        CHAR_WIDTHS.put('+', 5);
        CHAR_WIDTHS.put('=', 5);
        CHAR_WIDTHS.put('{', 4);
        CHAR_WIDTHS.put('}', 4);
        CHAR_WIDTHS.put('[', 3);
        CHAR_WIDTHS.put(']', 3);
        CHAR_WIDTHS.put(':', 1);
        CHAR_WIDTHS.put(';', 1);
        CHAR_WIDTHS.put('"', 3);
        CHAR_WIDTHS.put('\'', 1);
        CHAR_WIDTHS.put('<', 4);
        CHAR_WIDTHS.put('>', 4);
        CHAR_WIDTHS.put('?', 5);
        CHAR_WIDTHS.put('/', 5);
        CHAR_WIDTHS.put('\\', 5);
        CHAR_WIDTHS.put('|', 1);
        CHAR_WIDTHS.put('~', 5);
        CHAR_WIDTHS.put('`', 2);
        CHAR_WIDTHS.put('.', 1);
        CHAR_WIDTHS.put(',', 1);
        CHAR_WIDTHS.put(' ', SPACE_WIDTH);
    }

    // 私有构造函数，防止实例化
    private FontInfo() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 获取字符的正常宽度
     *
     * @param character 字符
     * @return 字符宽度（像素）
     */
    public static int getLength(char character) {
        return CHAR_WIDTHS.getOrDefault(character, DEFAULT_WIDTH);
    }

    /**
     * 获取字符的粗体宽度
     *
     * @param character 字符
     * @return 字符粗体宽度（像素）
     */
    public static int getBoldLength(char character) {
        return character == ' ' ? SPACE_WIDTH : getLength(character) + 1;
    }

    /**
     * 获取空格的宽度
     *
     * @return 空格宽度（像素）
     */
    public static int getSpaceWidth() {
        return SPACE_WIDTH;
    }
}
