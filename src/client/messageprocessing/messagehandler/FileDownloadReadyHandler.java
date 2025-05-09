package client.messageprocessing.messagehandler;

import client.services.FileTransferManager;
import shared.messages.FileDownloadReady;
import shared.network.SocketManager;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles incoming FILE_DOWNLOAD_READY messages from the server (on the recipient side).
 * Saves the file locally with the original extension instead of ".downloaded".
 */
public class FileDownloadReadyHandler implements MessageHandler<FileDownloadReady> {
    private final SocketManager socketManager;
    private final FileTransferManager fileTransferManager;
    private final String serverAddress;
    private final Set<String> processedSessionIds = ConcurrentHashMap.newKeySet();


    public FileDownloadReadyHandler(SocketManager socketManager,
                                    FileTransferManager fileTransferManager,
                                    String serverAddress) {
        this.socketManager = socketManager;
        this.fileTransferManager = fileTransferManager;
        this.serverAddress = serverAddress;
    }

    @Override
    public void handle(FileDownloadReady message) {

        String sessionId = message.sessionId();
        if (processedSessionIds.contains(sessionId)) {
            System.out.println("Already handled FILE_DOWNLOAD_READY for session " + sessionId
                    + ". Ignoring duplicate.");
            return;
        }
        processedSessionIds.add(sessionId);
        final int downloadPort = message.port();
        final String serverChecksum = message.checksum();

        System.out.println("Server is ready for file download on port " + downloadPort);
        System.out.println("Session ID: " + sessionId);
        System.out.println("Server-reported checksum: " + serverChecksum);

        String originalFilename = fileTransferManager.getDownloadFilePath(sessionId);
        if (originalFilename == null) {
            originalFilename = sessionId + ".unknown";
            System.err.println("No original filename found for sessionId=" + sessionId
                    + ", defaulting to " + originalFilename);
        }

        String extension = getExtension(originalFilename); // e.g. ".png" or ".txt"

        File downloadsDir = new File("client/downloads");
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }
        final File saveFile = new File(downloadsDir, sessionId + extension);

        System.out.println("Saving file to: " + saveFile.getAbsolutePath());

        new Thread(() -> doDownload(downloadPort, sessionId, serverChecksum, saveFile)).start();
    }

    /**
     * Connects to the server's download port, reads the file bytes,
     * writes them to saveFile, then compares checksums.
     */
    private void doDownload(int downloadPort, String sessionId, String serverChecksum, File saveFile) {
        try (Socket downloadSocket = new Socket(serverAddress, downloadPort);
             InputStream in = downloadSocket.getInputStream();
             FileOutputStream fos = new FileOutputStream(saveFile)) {
            System.out.println("Connected to server at " + serverAddress + " on port " + downloadPort);
            System.out.println("Downloading file for sessionId=" + sessionId + "...");

            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesReceived = 0;

            while ((bytesRead = in.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalBytesReceived += bytesRead;
            }
            fos.flush();
            System.out.println("File downloaded successfully. Total bytes received: " + totalBytesReceived);
        } catch (IOException e) {
            System.err.println("Error during file download: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        String localChecksum;
        try {
            localChecksum = computeSha256Of(saveFile);
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Failed to compute local checksum: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        if (localChecksum.equalsIgnoreCase(serverChecksum)) {
            System.out.println("Checksums match. Download is verified!");
        } else {
            System.err.println("Checksum mismatch! The downloaded file may be corrupted.");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return ""; // no extension or dot at end
        }

        return filename.substring(dotIndex);
    }

    /**
     * Computes the SHA-256 checksum of the given file.
     */
    private String computeSha256Of(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
