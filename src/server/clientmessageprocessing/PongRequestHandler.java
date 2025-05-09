package server.clientmessageprocessing;

import server.connection.ClientHandler;
import server.connection.ClientState;
import server.connection.PingHandler;
import shared.messages.Pong;

import java.io.IOException;

public class PongRequestHandler implements ServerMessageHandler<Pong> {

    @Override
    public void handle(Pong message, ClientHandler clientHandler) throws IOException {
        ClientState clientState = clientHandler.getClientState(); // Access ClientState
        PingHandler pingHandler = clientState.getPingHandler();

        if (pingHandler != null) {
            pingHandler.handlePong();

        }
    }
}



