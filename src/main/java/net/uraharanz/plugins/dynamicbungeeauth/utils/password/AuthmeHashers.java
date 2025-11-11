package net.uraharanz.plugins.dynamicbungeeauth.utils.password;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author an5w1r@163.com
 */
public class AuthmeHashers {

    private static final String AUTHME_SHA256_PREFIX = "$SHA$";

    public static boolean compareSHA256(String plainPassword, String hashedPassword) {
        String[] parts = hashedPassword.split("\\$");
        if (parts.length < 3) {
            return false;
        }

        String salt = parts[2];
        String expectedHash = AUTHME_SHA256_PREFIX + salt + "$" +
                hashSHA256(hashSHA256(plainPassword) + salt);

        return hashedPassword.equals(expectedHash);
    }

    private static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(input.getBytes(StandardCharsets.UTF_8));
            byte[] hash = digest.digest();

            return String.format("%0" + (hash.length << 1) + "x", new BigInteger(1, hash));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
