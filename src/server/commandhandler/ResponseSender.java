package server.commandhandler;

import shared.network.SocketManager;
import shared.Utils;

import java.io.IOException;

public class ResponseSender {
    private final SocketManager socketManager;

    public ResponseSender(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    /**
     * Sends an object as a serialized message to the client.
     *
     * @param message The object to be serialized and sent.
     * @throws IOException If an I/O error occurs during message sending.
     */
    public void sendResponse(Object message) throws IOException {
        try {
            String serializedMessage = Utils.objectToMessage(message);
            socketManager.sendMessage(serializedMessage);
        } catch (IOException e) {
            System.err.println("Error sending response: " + e.getMessage());
            throw e;
        }
    }
}
