package net.uraharanz.plugins.dynamicbungeeauth.utils.random;

import java.security.SecureRandom;
import java.util.Random;

public class SaltGenerator {
    private static final Random RANDOM = new SecureRandom();
    private static final char[] CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] INTS = "0123456789".toCharArray();
    private static final int HEX_MAX_INDEX = 16;

    public static String generateString() {
        return SaltGenerator.generateNumbers(CHARS.length);
    }

    public static String generateCaptcha(int n) {
        return SaltGenerator.generateNumb(n);
    }

    private static String generateNumbers(int n) {
        StringBuilder stringBuilder = new StringBuilder(10);
        for (int i = 0; i < 10; ++i) {
            stringBuilder.append(CHARS[RANDOM.nextInt(n)]);
        }
        return stringBuilder.toString();
    }

    private static String generateNumb(int n) {
        StringBuilder stringBuilder = new StringBuilder(n);
        for (int i = 0; i < n; ++i) {
            stringBuilder.append(INTS[RANDOM.nextInt(9)]);
        }
        return stringBuilder.toString();
    }

    public static String generateHex(int n) {
        return SaltGenerator.generateString(n, 16);
    }

    private static String generateString(int n, int n2) {
        if (n < 0) {
            throw new IllegalArgumentException("Length must be positive but was " + n);
        }
        StringBuilder stringBuilder = new StringBuilder(n);
        for (int i = 0; i < n; ++i) {
            stringBuilder.append(CHARS[RANDOM.nextInt(n2)]);
        }
        return stringBuilder.toString();
    }
}
