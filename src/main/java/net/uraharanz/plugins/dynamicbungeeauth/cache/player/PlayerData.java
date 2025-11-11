package net.uraharanz.plugins.dynamicbungeeauth.cache.player;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class PlayerData {
    private String uuid;
    private String name;
    private String email;
    private String reg_ip;
    private String log_ip;
    private String password;
    private String salt;
    private Date firstjoin;
    private Date lastjoin;
    private boolean premium;
    private boolean valid;
    private String server;
    private boolean lwlogged;
    private boolean playing;

    public PlayerData(String string, String string2, String string3, String string4, String string5, String string6, String string7, Date date, Date date2, boolean bl, boolean bl2, String string8, boolean bl3, boolean bl4) {
        this.uuid = string;
        this.name = string2;
        this.email = string3;
        this.reg_ip = string4;
        this.log_ip = string5;
        this.password = string6;
        this.salt = string7;
        this.firstjoin = date;
        this.lastjoin = date2;
        this.premium = bl;
        this.valid = bl2;
        this.server = string8;
        this.lwlogged = bl3;
        this.playing = bl4;
    }

    public void setPremiun(boolean bl) {
        this.premium = bl;
    }

}
