package client.messageprocessing.messagehandler;

import client.services.FileTransferManager;
import shared.Utils;
import shared.messages.FileUploadReady;
import shared.messages.FileUploadStart;
import shared.network.SocketManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Handles incoming FILE_UPLOAD_READY messages from the server (on the sender side).
 */
public class FileUploadReadyHandler implements MessageHandler<FileUploadReady> {
    private final SocketManager socketManager;
    private final FileTransferManager fileTransferManager;
    private final String serverAddress;

    public FileUploadReadyHandler(SocketManager socketManager,
                                  FileTransferManager fileTransferManager,
                                  String serverAddress) {
        this.socketManager = socketManager;
        this.fileTransferManager = fileTransferManager;
        this.serverAddress = serverAddress;
    }

    @Override
    public void handle(FileUploadReady message) throws IOException {
        String sessionId = message.sessionId();
        int port = message.port();

        System.out.println("Received FILE_UPLOAD_READY with port=" + message.port());
        FileUploadStart startMsg = new FileUploadStart(message.sessionId());
        String json = Utils.objectToMessage(startMsg);
        socketManager.sendMessage(json);
        System.out.println("Sent FILE_UPLOAD_START for sessionId=" + message.sessionId());
        System.out.println("Server is ready for file upload on port " + port);
        System.out.println("Session ID: " + sessionId);
        String filename = fileTransferManager.getUploadFilePath(sessionId);
        if (filename == null) {
            System.err.println("No filename found for sessionId=" + sessionId
                    + ". Possibly not mapped on the sender?");
            return;
        }
        String filePath = FileTransferManager.uploadDirPath + "/" + filename;
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.err.println("File not found locally: " + filePath);
            return;
        }
        System.out.println("Initiating file upload for " + filePath);
        new Thread(() -> doUpload(file, sessionId, port)).start();
    }

    private void doUpload(File file, String sessionId, int port) {
        try (Socket uploadSocket = new Socket(serverAddress, port);
             FileInputStream fis = new FileInputStream(file);
             OutputStream os = uploadSocket.getOutputStream()) {
            System.out.println("Connected to server at " + serverAddress + " on port " + port);
            System.out.println("Uploading file: " + file.getName());
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesSent = 0;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                totalBytesSent += bytesRead;
            }
            os.flush();
            System.out.println("File uploaded successfully. Total bytes sent: " + totalBytesSent);
        } catch (IOException e) {
            System.err.println("Error uploading file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
