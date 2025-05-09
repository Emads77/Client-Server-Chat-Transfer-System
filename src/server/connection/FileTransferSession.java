package server.connection;

import server.commandhandler.FileUploadHandler;

import java.util.UUID;

public class FileTransferSession {
    private final String sessionId; // unique identifier for this session
    private final String sender;
    private final String filename;
    private final long size;
    private int uploadPort;
    private String checkSum;
    private FileUploadHandler uploadHandler; // Add this field



    public FileTransferSession(String sender, String filename, long size) {
        this.sessionId = UUID.randomUUID().toString(); // generate a unique ID
        this.sender = sender;
        this.filename = filename;
        this.size = size;

    }
    public int getUploadPort() {
        return uploadPort;
    }

    public void setUploadPort(int uploadPort) {
        this.uploadPort = uploadPort;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSender() {
        return sender;
    }

    public String getFilename() {
        return filename;
    }

    public long getSize() {
        return size;
    }


    @Override
    public String toString() {
        return "FileTransferSession{" +
                "sessionId='" + sessionId + '\'' +
                ", sender='" + sender + '\'' +
                ", filename='" + filename + '\'' +
                ", size=" + size +
                '}';
    }

    public void setChecksum(String checksum) {
        this.checkSum=checksum;
    }

    public String getChecksum(){
        return checkSum;
    }
    public FileUploadHandler getUploadHandler() {
        return uploadHandler;
    }

    public void setUploadHandler(FileUploadHandler uploadHandler) {
        this.uploadHandler = uploadHandler;
    }
}
