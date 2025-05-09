package client.services;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manages active file transfer sessions by mapping session IDs to file paths.
 */
public class FileTransferManager {
    // Maps sessionId to filename for uploads
    private final Map<String, String> uploadSessions = new ConcurrentHashMap<>();
    // Maps sessionId to filePath for downloads
    private final Map<String, String> downloadSessions = new ConcurrentHashMap<>();

    // Maps sessionId to status (e.g., "pending", "confirmed", "rejected")
    private final Map<String, String> sessionStatuses = new ConcurrentHashMap<>();
    private final Queue<String> pendingSessions = new ConcurrentLinkedQueue<>();
    private final Queue<String> pendingFilenames = new ConcurrentLinkedQueue<>();
    public static final String uploadDirPath = "src/client/uploads";

    /**
     * Associates a sessionId with a filename for uploads.
     *
     * @param sessionId The unique session ID.
     * @param filename  The name of the file being uploaded.
     */
    public void addUploadSession(String sessionId, String filename) {
        uploadSessions.put(sessionId, filename);
        System.out.println("Upload session added: " + sessionId + " -> " + filename);
    }

    /**
     * Retrieves the filename associated with a sessionId for uploads.
     *
     * @param sessionId The unique session ID.
     * @return The filename, or null if not found.
     */
    public String getUploadFilePath(String sessionId) {
        return uploadSessions.get(sessionId);
    }

    /**
     * Associates a sessionId with a file path for downloads.
     *
     * @param sessionId The unique session ID.
     * @param filePath  The path where the file will be saved.
     */
    public void addDownloadSession(String sessionId, String filePath) {
        downloadSessions.put(sessionId, filePath);
    }

    /**
     * Retrieves the file path associated with a sessionId for downloads.
     *
     * @param sessionId The unique session ID.
     * @return The file path, or null if not found.
     */
    public String getDownloadFilePath(String sessionId) {
        return downloadSessions.get(sessionId);
    }

    /**
     * Adds a session to pending sessions.
     *
     * @param sessionId The session ID to add.
     */
    public void addPendingSession(String sessionId) {
        pendingSessions.offer(sessionId);
        System.out.println("Pending session added: " + sessionId);
    }

    /**
     * Retrieves and removes the next session ID from the queue.
     *
     * @return The next session ID, or null if the queue is empty.
     */
    public String pollNextSession() {
        String sessionId = pendingSessions.poll();
        if (sessionId != null) {
            System.out.println("Polled pending session: " + sessionId);
        }
        return sessionId;
    }

    /**
     * Checks if there are any pending sessions.
     *
     * @return True if there are pending sessions, false otherwise.
     */
    public boolean hasPendingSessions() {
        return !pendingSessions.isEmpty();
    }

    public void enqueueFilename(String filename) {
        pendingFilenames.offer(filename);
    }

    public String dequeueFilename() {
        return pendingFilenames.poll();
    }
}
