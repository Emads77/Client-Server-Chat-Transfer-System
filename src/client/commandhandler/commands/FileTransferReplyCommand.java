package client.commandhandler.commands;

import client.services.FileTransferManager;
import shared.Utils;
import shared.messages.FileTransferReply;
import shared.network.SocketManager;

import java.io.IOException;

public class FileTransferReplyCommand implements Command {
    private final SocketManager socketManager;
    private final FileTransferManager fileTransferManager;
    private final String status;

    public FileTransferReplyCommand(SocketManager socketManager, FileTransferManager fileTransferManager, String status) {
        this.socketManager = socketManager;
        this.fileTransferManager = fileTransferManager;
        this.status = status;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (!fileTransferManager.hasPendingSessions()) {
            System.out.println("No pending file transfer offers to respond to.");
            return;
        }
        String sessionId = fileTransferManager.pollNextSession();
        if (sessionId == null) {
            System.out.println("No pending file transfer offers to respond to.");
            return;
        }
        FileTransferReply rep = new FileTransferReply(sessionId, status);
        String jsonMessage = Utils.objectToMessage(rep);
        socketManager.sendMessage(jsonMessage);
        System.out.println("File transfer " + status + "ed for session ID: " + sessionId);
    }
}