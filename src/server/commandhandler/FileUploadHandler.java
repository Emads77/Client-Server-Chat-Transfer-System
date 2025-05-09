package server.commandhandler;

import server.connection.ClientManager;
import server.connection.FileTransferSession;
import shared.messages.FileUploadResp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUploadHandler implements Runnable {
    private final int port;
    private final String sessionId;
    private final String senderUsername;
    private final ClientManager clientManager;
    private volatile boolean uploadStarted = false;

    public FileUploadHandler(int port, String sessionId, String senderUsername, ClientManager clientManager) {
        this.port = port;
        this.sessionId = sessionId;
        this.senderUsername = senderUsername;
        this.clientManager = clientManager;
    }

    public synchronized void startUpload(){
        uploadStarted = true;
        notify();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            synchronized (this){
                while(!uploadStarted){
                    System.out.println("fileUploadHandler waiting for the FILE_UPLOAD_START");
                    wait();
                }
            }
            Socket clientSocket = serverSocket.accept();
            System.out.println("Accepted connection for file upload, session: " + sessionId);

            //directory for uploaded files
            File uploadsDir = new File("server/filesToUpload");
            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs();
            }


            String originalFilename = clientManager.getFileTransferSession(sessionId).getFilename();
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex != -1) {
                extension = originalFilename.substring(dotIndex);
            }

            File uploadedFile = new File(uploadsDir, sessionId + extension);
            System.out.println("Saving file as: " + uploadedFile.getAbsolutePath());

            try (InputStream inputStream = clientSocket.getInputStream();
                 FileOutputStream fos = new FileOutputStream(uploadedFile)) {
                long bytesTransferred = inputStream.transferTo(fos);
                System.out.println("Transferred " + bytesTransferred + " bytes.");
            }

            // Compute checksum for file integrity.
            String checksum = calculateChecksum(uploadedFile);
            System.out.println("\nFile uploaded successfully.||  Checksum is : " + checksum);

            FileTransferSession session = clientManager.getFileTransferSession(sessionId);
            if (session != null) {
                session.setChecksum(checksum);
            }

            notifySender(checksum);
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Error in FileUploadHandler: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Notifies the sender of the successful file upload by sending a FileUploadResp message,
     * which includes the computed checksum.
     *
     * @param checksum the computed SHA-256 checksum of the uploaded file
     */
    private void notifySender(String checksum) {
        try {
            clientManager.getClientResponseSender(senderUsername)
                    .sendResponse(new FileUploadResp("OK", checksum));
            System.out.println("Notified sender (" + senderUsername + ") about successful file upload.");
        } catch (IOException e) {
            System.err.println("Failed to notify sender: " + e.getMessage());
        }
    }

    /**
     * Computes the SHA-256 checksum of the given file.
     *
     * @param file the file to compute the checksum for
     * @return a hexadecimal string representation of the checksum
     * @throws NoSuchAlgorithmException if SHA-256 is not available
     * @throws IOException              if an I/O error occurs
     */
    private String calculateChecksum(File file) throws NoSuchAlgorithmException, IOException {
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
