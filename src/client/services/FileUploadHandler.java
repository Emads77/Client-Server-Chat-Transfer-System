package client.services;

import java.io.*;
import java.net.Socket;

/**
 * Handles the file upload process by establishing a socket connection and streaming the file.
 */
public class FileUploadHandler {
    private final FileTransferManager fileTransferManager;
    private final String sessionId;
    private final int uploadPort;
    private final String serverAddress;

    /**
     * Constructor for FileUploadHandler.
     *
     * @param fileTransferManager The FileTransferManager instance to manage file transfer sessions.
     * @param sessionId           The unique session ID for this file transfer.
     * @param uploadPort          The port number on which the server is ready to receive the file.
     * @param serverAddress       The server's IP address or hostname.
     */
    public FileUploadHandler(FileTransferManager fileTransferManager, String sessionId, int uploadPort, String serverAddress) {
        this.fileTransferManager = fileTransferManager;
        this.sessionId = sessionId;
        this.uploadPort = uploadPort;
        this.serverAddress = serverAddress;
    }

    /**
     * Initiates the file upload process.
     */
    public void uploadFile() {
        String filePath = "D:/YEAR 2/QUARTILE 2/InternetTechnology/repo/17/src/client/uploads/"
                + fileTransferManager.getUploadFilePath(sessionId);

        System.out.println("the file path is " + filePath);
        if (filePath == null) {
            System.err.println("No file path found for session ID: " + sessionId);
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File does not exist: " + filePath);
            return;
        }
        if (file.isDirectory()) {
            System.err.println("Specified path is a directory, not a file: " + filePath);
            return;
        }
        try (Socket uploadSocket = new Socket(serverAddress, uploadPort);
             FileInputStream fis = new FileInputStream(file);
             OutputStream os = uploadSocket.getOutputStream();
             BufferedWriter uploadWriter = new BufferedWriter(new OutputStreamWriter(os))) {
            System.out.println("Connected to server at " + serverAddress + " on port " + uploadPort);
            System.out.println("Uploading file: " + file.getName());
            uploadWriter.write(sessionId + "\n");
            uploadWriter.flush();
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
            System.err.println("Error during file upload: " + e.getMessage());
        }
    }
}
