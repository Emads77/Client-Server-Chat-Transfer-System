package server.commandhandler;

import server.connection.ClientManager;
import shared.messages.FileDownloadReady;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles file download requests by creating a server socket to transfer the file to the client.
 * This class runs on a separate thread to manage the file transfer independently.
 */
public class FileDownloadHandler implements Runnable {

    private final int port;
    private final String sessionId;
    private final String recipientUsername;
    private final ClientManager clientManager;
    private final String filesDirPath = "server/filesToUpload";

    /**
     * Constructs a new FileDownloadHandler.
     *
     * @param port             the port on which the file download server socket will listen.
     * @param sessionId        the unique identifier for the file transfer session.
     * @param recipientUsername the username of the client requesting the file download.
     * @param clientManager    the client manager to retrieve session details and send responses.
     */
    public FileDownloadHandler(int port, String sessionId, String recipientUsername, ClientManager clientManager) {
        this.port = port;
        this.sessionId = sessionId;
        this.recipientUsername = recipientUsername;
        this.clientManager = clientManager;
    }

    /**
     * Starts the file download process. Listens for an incoming connection on the specified port,
     * transfers the file to the client, and notifies the client of the download status via a response message.
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket clientSocket = serverSocket.accept();

            String originalFilename = clientManager.getFileTransferSession(sessionId).getFilename();
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex != -1) {
                extension = originalFilename.substring(dotIndex);
            }

            File fileToDownload = new File(filesDirPath, sessionId + extension);

            if (!fileToDownload.exists()) {
                return;
            }

            try (OutputStream out = clientSocket.getOutputStream();
                 FileInputStream fis = new FileInputStream(fileToDownload)) {
                fis.transferTo(out);
            }

            String storedChecksum = clientManager.getFileTransferSession(sessionId).getChecksum();

            clientManager.getClientResponseSender(recipientUsername)
                    .sendResponse(new FileDownloadReady(port, sessionId, storedChecksum));
        } catch (IOException e) {
            System.err.println("Error in FileDownloadHandler: " + e.getMessage());
        }
    }
}
