package net.uraharanz.plugins.dynamicbungeeauth.cache.fix;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Fix {
    private String name;
    private String uuid;

    public Fix(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }
}
