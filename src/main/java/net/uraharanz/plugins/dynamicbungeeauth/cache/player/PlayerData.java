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

    public PlayerData(String uuid, String name, String email, String regIp, String logIp, String password, String salt, Date firstJoin, Date lastJoin, boolean premium, boolean valid, String server, boolean lwLogged, boolean playing) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.reg_ip = regIp;
        this.log_ip = logIp;
        this.password = password;
        this.salt = salt;
        this.firstjoin = firstJoin;
        this.lastjoin = lastJoin;
        this.premium = premium;
        this.valid = valid;
        this.server = server;
        this.lwlogged = lwLogged;
        this.playing = playing;
    }

}
