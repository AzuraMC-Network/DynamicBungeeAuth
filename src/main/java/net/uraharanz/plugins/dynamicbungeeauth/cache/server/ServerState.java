package net.uraharanz.plugins.dynamicbungeeauth.cache.server;

import lombok.Getter;
import lombok.Setter;

public enum ServerState {
    NORMAL,
    ATTACK;

    @Getter
    @Setter
    public static ServerState state;

    public static boolean isState(ServerState serverState) {
        return state == serverState;
    }

}
