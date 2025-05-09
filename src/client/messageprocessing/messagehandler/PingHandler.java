package client.messageprocessing.messagehandler;

import shared.messages.Ping;
import shared.messages.Pong;
import shared.network.SocketManager;
import shared.Utils;

import java.io.IOException;

public class PingHandler implements MessageHandler<Ping> {
    private final SocketManager socketManager;

    public PingHandler(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void handle(Ping message) {
        try {
            Pong pong = new Pong();
            String pongMessage = Utils.objectToMessage(pong);
            socketManager.sendMessage(pongMessage);
        } catch (IOException e) {
            System.err.println("Error sending PONG response: " + e.getMessage());
        }
    }
}
