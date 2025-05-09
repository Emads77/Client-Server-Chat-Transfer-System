package server.commandhandler;

import server.connection.PingHandler;
import shared.messages.Pong;

import java.io.IOException;

public class PongCommand implements Command<Pong> {
    private final PingHandler pingHandler;

    public PongCommand(PingHandler pingHandler) {
        this.pingHandler = pingHandler;
    }

    @Override
    public void execute(Pong message) throws IOException {
        if (pingHandler != null) {
            pingHandler.handlePong();
        } else {
            throw new IOException("PingHandler is not initialized.");
        }
    }
}
