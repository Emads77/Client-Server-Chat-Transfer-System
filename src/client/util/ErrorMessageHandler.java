package client.util;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessageHandler {

    private static final Map<Integer,String>ERROR_MESSAGES = new HashMap<>();

    static{
        ERROR_MESSAGES.put(5000, "Error: Username already exists.");
        ERROR_MESSAGES.put(5001, "Error: Username must be between 3 and 14 characters long and contain only letters, numbers, or underscore.");
        ERROR_MESSAGES.put(5002, "Error: You are already logged in.");
        ERROR_MESSAGES.put(6000, "Error: You must log in to send a message.");
        ERROR_MESSAGES.put(7000, "Error: Connection lost due to no response to server's heartbeat.");
        ERROR_MESSAGES.put(8000, "Error: Unexpected pong received.");
        ERROR_MESSAGES.put(9000, "Error: User doesnt exist.");
        ERROR_MESSAGES.put(10000, "Error: another game is already running");
        ERROR_MESSAGES.put(10001, "Error: invalid choice");
        ERROR_MESSAGES.put(11000, "Error: The user has rejected the file transfer request");
        ERROR_MESSAGES.put(12000, "Error: File upload failed");
        ERROR_MESSAGES.put(12001, "Error: Missing or invalid sessionId in FileTransferReply");
        ERROR_MESSAGES.put(12005, "Error: The session ID does not match any active session.");
        ERROR_MESSAGES.put(12010, "Error: File not found on the server");
        ERROR_MESSAGES.put(12011, "Error: checksum verification failed");
        ERROR_MESSAGES.put(12012, "Error: file download failed");
        ERROR_MESSAGES.put(12500, "Error: checksum calculation failed");
        ERROR_MESSAGES.put(13000, "Error:  server failed to assign a port \n");

    }

    public static String getErrorMessage(int code) {
        return ERROR_MESSAGES.getOrDefault(code, "Error: An unknown error occurred. Code: " + code);
    }
}


