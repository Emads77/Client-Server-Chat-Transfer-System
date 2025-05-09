package client.commandhandler.commands;

import client.services.FileTransferManager;
import shared.Utils;
import shared.messages.FileDownloadStart;
import shared.network.SocketManager;

import java.io.IOException;

/**
 * Command to request a file download for the specified sessionId.
 * Usage: download <sessionId>
 */
public class FileDownloadCommand implements Command {
    private final SocketManager socketManager;
    private final FileTransferManager fileTransferManager;

    public FileDownloadCommand(SocketManager socketManager, FileTransferManager fileTransferManager) {
        this.socketManager = socketManager;
        this.fileTransferManager = fileTransferManager;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (arguments.length < 1) {
            System.out.println("Usage: download <sessionId>");
            return;
        }
        String sessionId = arguments[0];
        FileDownloadStart startMsg = new FileDownloadStart(sessionId);
        String jsonMessage = Utils.objectToMessage(startMsg);
        socketManager.sendMessage(jsonMessage);
        System.out.println("Requested file download for sessionId=" + sessionId);
    }
}
