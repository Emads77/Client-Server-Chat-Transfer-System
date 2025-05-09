package client.messageprocessing;

import shared.network.SocketManager;

import java.io.IOException;

public class ServerMessageHandler {
    private final SocketManager socketManager;
    private final ServerMessageProcessor messageProcessor;
    private volatile boolean isRunning = true;

    public ServerMessageHandler(SocketManager socketManager, ServerMessageProcessor messageProcessor) {
        this.socketManager = socketManager;
        this.messageProcessor = messageProcessor;
    }

    public void handleServerMessages() {
        try {
            String messageFromServer;
            while (isRunning && (messageFromServer = socketManager.readMessage()) != null) {
                messageProcessor.processMessage(messageFromServer);
            }
        } catch (IOException e) {
            if (isRunning) {
                System.err.println("Server connection lost: " + e.getMessage());
            }
        }
    }

}
