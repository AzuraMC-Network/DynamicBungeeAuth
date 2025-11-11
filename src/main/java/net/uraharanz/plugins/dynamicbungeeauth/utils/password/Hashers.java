package net.uraharanz.plugins.dynamicbungeeauth.utils.password;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author an5w1r@163.com
 */
public class Hashers {

    public static String md5(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes(StandardCharsets.UTF_8), 0, input.length());
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available", e);
        }
    }

    public static String sha512c(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(input.getBytes(StandardCharsets.UTF_8));
            byte[] hash = digest.digest();

            return String.format("%0" + (hash.length << 1) + "x", new BigInteger(1, hash));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 algorithm not available", e);
        }
    }

    public static String oldDefault(String password) {
        try {
            StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
            return encryptor.encryptPassword(password);
        } catch (Exception e) {
            throw new IllegalStateException("Password encryption failed", e);
        }
    }

    public static boolean checkOldDefault(String plainPassword, String encryptedPassword) {
        try {
            StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
            return encryptor.checkPassword(plainPassword, encryptedPassword);
        } catch (Exception e) {
            throw new IllegalStateException("Password verification failed", e);
        }
    }
}
