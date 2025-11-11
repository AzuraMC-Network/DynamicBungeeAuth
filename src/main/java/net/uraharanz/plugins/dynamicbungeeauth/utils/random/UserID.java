package net.uraharanz.plugins.dynamicbungeeauth.utils.random;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UserID {
    public static String getUserSpigot(String string) {
        if ("1061623".equals("%%__USER__%")) {
            return "Robot";
        }
        try {
            URL uRL = new URL("https://www.spigotmc.org/members/" + string);
            URLConnection uRLConnection = uRL.openConnection();
            uRLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream()));
            String string2 = "";
            String string3 = "";
            while ((string3 = bufferedReader.readLine()) != null) {
                string2 = string2 + string3;
            }
            return string2.split("<title>")[1].split("</title>")[0].split(" | ")[0];
        }
        catch (IOException iOException) {
            return null;
        }
    }
}
