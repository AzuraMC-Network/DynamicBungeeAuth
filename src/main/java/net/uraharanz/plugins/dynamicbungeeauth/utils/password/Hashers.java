package net.uraharanz.plugins.dynamicbungeeauth.utils.password;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.jasypt.util.password.StrongPasswordEncryptor;

public class Hashers {
    public static String md5(String string) {
        String string2 = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(string.getBytes(), 0, string.length());
            string2 = new BigInteger(1, messageDigest.digest()).toString();
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        }
        return string2;
    }

    public static String sha512c(String string) {
        String string2 = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.reset();
            messageDigest.update(string.getBytes());
            byte[] byArray = messageDigest.digest();
            string2 = String.format("%0" + (byArray.length << 1) + "x", new BigInteger(1, byArray));
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        }
        return string2;
    }

    public static String olddefault(String string) {
        String string2 = null;
        try {
            StrongPasswordEncryptor strongPasswordEncryptor = new StrongPasswordEncryptor();
            string2 = strongPasswordEncryptor.encryptPassword(string);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return string2;
    }
}
