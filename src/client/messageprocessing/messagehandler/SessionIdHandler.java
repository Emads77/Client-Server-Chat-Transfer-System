package client.messageprocessing.messagehandler;

import client.services.FileTransferManager;
import shared.messages.SessionIdResp;

public class SessionIdHandler implements MessageHandler<SessionIdResp> {
    private final FileTransferManager fileTransferManager;
    public SessionIdHandler(FileTransferManager fileTransferManager) {
        this.fileTransferManager = fileTransferManager;
    }

    @Override
    public void handle(SessionIdResp message) {
        String sessionId = message.sessionId();
        String filename = fileTransferManager.dequeueFilename();
        if (filename == null) {
            System.err.println("No filename in queue for this sessionId: " + sessionId);
            return;
        }
       fileTransferManager.addUploadSession(sessionId, filename);
    }
}
