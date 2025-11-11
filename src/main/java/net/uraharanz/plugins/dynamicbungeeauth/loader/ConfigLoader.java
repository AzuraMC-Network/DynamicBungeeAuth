package net.uraharanz.plugins.dynamicbungeeauth.loader;

import java.util.HashMap;
import java.util.List;
import net.uraharanz.plugins.dynamicbungeeauth.main;

public class ConfigLoader {
    private final main plugin;
    private final HashMap<String, String> configString;
    private final HashMap<String, Boolean> configBoolean;
    private final HashMap<String, Integer> configInteger;
    private final HashMap<String, List<String>> configStringList;
    private final HashMap<String, String> messageString;
    private final HashMap<String, Integer> messageInteger;
    private final HashMap<String, List<String>> messageStringList;

    public ConfigLoader(main main2) {
        this.plugin = main2;
        this.configString = new HashMap<>();
        this.configBoolean = new HashMap<>();
        this.configInteger = new HashMap<>();
        this.configStringList = new HashMap<>();
        this.messageString = new HashMap<>();
        this.messageInteger = new HashMap<>();
        this.messageStringList = new HashMap<>();
    }

    public String getStringCFG(String string) {
        String string2 = this.getConfigString().get(string);
        if (string2 == null) {
            String string3 = this.plugin.getFiles().getCFG().getString(string);
            this.configString.put(string, string3);
            return string3;
        }
        return string2;
    }

    public String getStringMSG(String string) {
        String string2 = this.getMessageString().get(string);
        if (string2 == null) {
            String string3 = this.plugin.getFiles().getMSG().getString(string);
            this.messageString.put(string, string3);
            return string3;
        }
        return string2;
    }

    public Integer getIntegerCFG(String string) {
        if (this.getConfigInteger().get(string) == null) {
            Integer n = this.plugin.getFiles().getCFG().getInt(string);
            this.configInteger.put(string, n);
            return n;
        }
        return this.getConfigInteger().get(string);
    }

    public Integer getIntegerMSG(String string) {
        if (this.getMessageInteger().get(string) == null) {
            Integer n = this.plugin.getFiles().getMSG().getInt(string);
            this.messageInteger.put(string, n);
            return n;
        }
        return this.getMessageInteger().get(string);
    }

    public Boolean getBooleanCFG(String string) {
        if (this.getConfigBoolean().get(string) == null) {
            boolean bl = this.plugin.getFiles().getCFG().getBoolean(string);
            this.configBoolean.put(string, bl);
            return bl;
        }
        return this.getConfigBoolean().get(string);
    }

    public List<String> getStringListCFG(String string) {
        List<String> list = this.getConfigStringList().get(string);
        if (list == null) {
            List<String> list2 = this.plugin.getFiles().getCFG().getStringList(string);
            this.configStringList.put(string, list2);
            return list2;
        }
        return list;
    }

    public List<String> getStringListMSG(String string) {
        List<String> list = this.getMessageStringList().get(string);
        if (list == null) {
            List<String> list2 = this.plugin.getFiles().getMSG().getStringList(string);
            this.messageStringList.put(string, list2);
            return list2;
        }
        return list;
    }

    private HashMap<String, String> getConfigString() {
        return this.configString;
    }

    private HashMap<String, Integer> getConfigInteger() {
        return this.configInteger;
    }

    private HashMap<String, List<String>> getConfigStringList() {
        return this.configStringList;
    }

    private HashMap<String, Boolean> getConfigBoolean() {
        return this.configBoolean;
    }

    private HashMap<String, String> getMessageString() {
        return this.messageString;
    }

    private HashMap<String, Integer> getMessageInteger() {
        return this.messageInteger;
    }

    private HashMap<String, List<String>> getMessageStringList() {
        return this.messageStringList;
    }
}
