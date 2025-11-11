package net.uraharanz.plugins.dynamicbungeeauth.utils.random;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author an5w1r@163.com
 */
public class SaltGenerator {

    private static final Random RANDOM = new SecureRandom();

    private static final char[] ALPHANUMERIC_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private static final char[] NUMERIC_CHARS = "0123456789".toCharArray();

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private static final int DEFAULT_SALT_LENGTH = 10;

    public static String generateString() {
        return generateAlphanumeric(DEFAULT_SALT_LENGTH);
    }

    public static String generateCaptcha(int length) {
        return generateNumeric(length);
    }

    public static String generateHex(int length) {
        return generateFromCharset(length, HEX_CHARS);
    }

    public static String generateAlphanumeric(int length) {
        return generateFromCharset(length, ALPHANUMERIC_CHARS);
    }

    public static String generateNumeric(int length) {
        return generateFromCharset(length, NUMERIC_CHARS);
    }

    private static String generateFromCharset(int length, char[] charset) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be positive but was " + length);
        }

        if (charset == null || charset.length == 0) {
            throw new IllegalArgumentException("Charset cannot be null or empty");
        }

        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(charset.length);
            result.append(charset[index]);
        }

        return result.toString();
    }

    public static Random getSecureRandom() {
        return RANDOM;
    }
}
