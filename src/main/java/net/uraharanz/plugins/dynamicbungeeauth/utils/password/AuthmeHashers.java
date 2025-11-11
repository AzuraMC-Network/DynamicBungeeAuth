package net.uraharanz.plugins.dynamicbungeeauth.utils.password;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthmeHashers {
    public static boolean compareSHA256(String string, String string2) {
        String[] stringArray = string2.split("\\$");
        String string3 = stringArray[2];
        String string4 = "$SHA$" + string3 + "$" + AuthmeHashers.hashSHA256(AuthmeHashers.hashSHA256(string) + string3);
        return string2.equals(string4);
    }

    private static String hashSHA256(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            messageDigest.update(string.getBytes());
            byte[] byArray = messageDigest.digest();
            return String.format("%0" + (byArray.length << 1) + "x", new BigInteger(1, byArray));
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new IllegalStateException(noSuchAlgorithmException);
        }
    }
}
