package client.commandhandler.commands;

import client.services.FileTransferManager;
import shared.Utils;
import shared.messages.FileTransferReq;
import shared.network.SocketManager;

import java.io.File;
import java.io.IOException;

/**
 * Command to initiate a file upload to a specific user.
 * Usage: uploadfile <username> <filename>
 */
public class FileTransferReqCommand implements Command {
    private final SocketManager socketManager;
    private final FileTransferManager fileTransferManager;
    private final String uploadDirPath = "src/client/uploads";

    public FileTransferReqCommand(SocketManager socketManager, FileTransferManager fileTransferManager) {
        this.socketManager = socketManager;
        this.fileTransferManager = fileTransferManager;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (arguments.length < 2) {
            System.out.println("Usage: request <username> <filename>");
            return;
        }
        String recipient = arguments[0];
        String filename = arguments[1];
        File file = new File(uploadDirPath + "/" + filename);
        if (!file.exists() || !file.isFile()) {
            System.out.println("File does not exist in the upload directory: " + uploadDirPath + "/" + filename);
            return;
        }
        long fileSize = file.length();
        FileTransferReq req = new FileTransferReq(recipient, filename, fileSize);
        String jsonMessage = Utils.objectToMessage(req);
        socketManager.sendMessage(jsonMessage);
        fileTransferManager.enqueueFilename(filename);
    }
}
