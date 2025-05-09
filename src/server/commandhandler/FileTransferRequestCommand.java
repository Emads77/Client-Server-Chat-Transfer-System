package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;
import server.connection.FileTransferSession;
import shared.messages.FileTransferOffer;
import shared.messages.FileTransferReq;
import shared.messages.FileTransferResp;
import shared.messages.SessionIdResp;

import java.io.IOException;

public class FileTransferRequestCommand implements Command<FileTransferReq> {
    private final CommandContext context;

    public FileTransferRequestCommand(CommandContext context) {
        this.context = context;
    }

    /**
     * @param message The FileTransferReq object received from the sender.
     * @throws IOException If an I/O error occurs while sending responses.
     */
    @Override
    public void execute(FileTransferReq message) throws IOException {
        ClientState clientState = context.getClientState();
        ClientManager clientManager = context.getClientManager();
        ResponseSender responseSender = context.getResponseSender();

        String senderUsername = clientState.getUsername();
        String recipientUsername = message.receiver();

        if (!clientState.isLoggedIn()) {
            responseSender.sendResponse(new FileTransferResp("ERROR", 6000));
            return;
        }

        if (!clientManager.isClientConnected(recipientUsername)) {
            responseSender.sendResponse(new FileTransferResp("ERROR", 9000));
            return;
        }

        FileTransferSession session = new FileTransferSession(senderUsername, message.filename(), message.size());

        clientManager.addFileTransferSession(session);

        FileTransferOffer offerMsg = new FileTransferOffer(
                senderUsername,
                message.filename(),
                message.size()
                ,session.getSessionId()
        );

        ResponseSender recipientSender = clientManager.getClientResponseSender(recipientUsername);
        recipientSender.sendResponse(offerMsg);

        responseSender.sendResponse(new FileTransferResp("OK", 0));
        responseSender.sendResponse(new SessionIdResp(session.getSessionId()));
    }
}
