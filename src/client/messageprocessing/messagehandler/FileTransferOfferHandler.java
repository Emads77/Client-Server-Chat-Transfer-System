package client.messageprocessing.messagehandler;

import client.services.FileTransferManager;
import shared.messages.FileTransferOffer;
import shared.network.SocketManager;

/**
 * Handles incoming FILE_TRANSFER_OFFER messages from the server.
 */

public class FileTransferOfferHandler implements MessageHandler<FileTransferOffer> {
    private final SocketManager socketManager;
    private final FileTransferManager fileTransferManager;

    public FileTransferOfferHandler(SocketManager socketManager, FileTransferManager fileTransferManager) {
        this.socketManager = socketManager;
        this.fileTransferManager = fileTransferManager;
    }

    @Override
    public void handle(FileTransferOffer message) {
        System.out.println("\n--- File Transfer Offer ---");
        System.out.println("Sender: " + message.sender());
        System.out.println("Filename: " + message.filename());
        System.out.println("Size: " + message.size() + " bytes");
        System.out.println("Session ID: " + message.sessionId());
        System.out.println("----------------------------");
        fileTransferManager.addPendingSession(message.sessionId());
        fileTransferManager.addDownloadSession(message.sessionId(), message.filename());
    }

}
