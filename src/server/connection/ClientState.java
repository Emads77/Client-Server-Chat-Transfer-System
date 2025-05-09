package server.connection;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClientState {
    private String username;
    private final AtomicBoolean loggedIn = new AtomicBoolean(false);
    private PingHandler pingHandler;

    public boolean isLoggedIn() {
        return loggedIn.get();
    }

    public void logIn(String username, PingHandler pingHandler) {
        this.username = username;
        this.pingHandler = pingHandler;
        loggedIn.set(true);
        pingHandler.startHeartbeat();
    }

    public void logOut() {
        if (pingHandler != null) {
            pingHandler.stopHeartbeat();
        }
        loggedIn.set(false);

    }
    public String getUsername() {
        return username;
    }

    public PingHandler getPingHandler() {
        return pingHandler;
    }
}
