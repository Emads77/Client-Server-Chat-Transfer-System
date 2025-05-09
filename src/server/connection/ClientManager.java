package server.connection;

import server.commandhandler.ResponseSender;
import server.game.GameManager;
import shared.messages.Broadcast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {
    private final ConcurrentHashMap<String, ResponseSender> clients = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, FileTransferSession> fileTransferSessionsById = new ConcurrentHashMap<>();
    private final GameManager gameManager = GameManager.getInstance();




    public boolean isGameActive() {
        return gameManager.isGameActive();
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void addClient(String username, ResponseSender responseSender) {
        clients.put(username, responseSender);
        System.out.println("Added client: " + username);
    }
    public void removeClient(String username) {
        if (username == null) {
            System.out.println("Attempted to remove a null username. Ignoring.");
            return;
        }
        clients.remove(username);
        System.out.println("Removed client: " + username);
    }

    public boolean isClientConnected(String username) {
        return clients.containsKey(username);
    }


    public ResponseSender getClientResponseSender(String username) {
        ResponseSender responseSender = clients.get(username);
        if (responseSender == null) {
            throw new IllegalStateException("No client found with username: " + username);
        }
        return responseSender;
    }

    public void broadcast(String sender, String message) {
        clients.forEach((username, responseSender) -> {
            if (!username.equals(sender)) {
                try {
                    responseSender.sendResponse(new Broadcast(sender, message));
                } catch (IOException e) {
                    System.err.println("Error broadcasting to " + username + ": " + e.getMessage());
                }
            }
        });
    }

    public void broadcastExcept(String senderUsername, Object message) {
        clients.forEach((username, responseSender) -> {
            if (!username.equals(senderUsername)) {
                try {
                    responseSender.sendResponse(message);
                    System.out.println("Sent message to " + username + ": " + message.toString());
                } catch (IOException e) {
                    System.err.println("Error broadcasting message to client: " + e.getMessage());
                }
            }
        });
    }

    public void addFileTransferSession(FileTransferSession session) {
        fileTransferSessionsById.put(session.getSessionId(), session);
    }


    public FileTransferSession getFileTransferSession(String sessionId) {
        return fileTransferSessionsById.get(sessionId);
    }



    public void removeFileTransferSession(String sessionId) {
        fileTransferSessionsById.remove(sessionId);
    }
    public List<String> getAllConnectedUsers() {
        return new ArrayList<>(clients.keySet());
    }

}
