package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;
import shared.messages.FileTransferReply;
import shared.messages.FileTransferResp;
import shared.messages.FileUploadReady;
import server.connection.FileTransferSession;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Command to handle file transfer replies. This command processes the recipient's response to
 * a file transfer request, determines the next steps based on the reply, and manages the
 * file transfer session accordingly.
 */
public class FileTransferReplyCommand implements Command<FileTransferReply> {

    private final CommandContext context;

    /**
     * Constructs a new FileTransferReplyCommand.
     *
     * @param context the command context providing access to client state, client manager, and response sender.
     */
    public FileTransferReplyCommand(CommandContext context) {
        this.context = context;
    }

    /**
     * Executes the file transfer reply command. Validates the recipient's response, checks the
     * file transfer session, allocates a port for the file upload if confirmed, and starts
     * the file upload process.
     *
     * @param reply the {@link FileTransferReply} message containing the session ID and the recipient's reply status.
     * @throws IOException if an error occurs during port allocation or file upload handler setup.
     */
    @Override
    public void execute(FileTransferReply reply) throws IOException {
        ClientState clientState = context.getClientState();
        ClientManager clientManager = context.getClientManager();

        if (!clientState.isLoggedIn()) {
            context.getResponseSender().sendResponse(new FileTransferResp("ERROR", 6000));
            return;
        }

        if (reply.sessionId() == null || reply.sessionId().isEmpty()) {
            context.getResponseSender().sendResponse(new FileTransferResp("ERROR", 12010));
            return;
        }

        FileTransferSession matchingSession = clientManager.getFileTransferSession(reply.sessionId());
        if (matchingSession == null) {
            context.getResponseSender().sendResponse(new FileTransferResp("ERROR", 12005));
            return;
        }

        String senderUsername = matchingSession.getSender();

        if (!clientManager.isClientConnected(senderUsername)) {
            System.err.println("Sender is no longer connected: " + senderUsername);
            context.getResponseSender().sendResponse(new FileTransferResp("ERROR", 9000));
            clientManager.removeFileTransferSession(matchingSession.getSessionId());
            return;
        }

        if ("confirm".equalsIgnoreCase(reply.status())) {
            System.out.println("Recipient confirmed file transfer for session: " + matchingSession.getSessionId());

            int uploadPort;
            try {
                uploadPort = allocateDynamicPort();
                matchingSession.setUploadPort(uploadPort);
            } catch (IOException e) {
                System.err.println("Failed to allocate dynamic port for file upload: " + e.getMessage());
                context.getClientManager()
                        .getClientResponseSender(senderUsername)
                        .sendResponse(new FileTransferResp("ERROR", 13000));
                return;
            }

            System.out.println("Allocated dynamic port for file upload: " + uploadPort);

            context.getClientManager()
                    .getClientResponseSender(senderUsername)
                    .sendResponse(new FileUploadReady(uploadPort, matchingSession.getSessionId()));

            FileUploadHandler uploadHandler = new FileUploadHandler(uploadPort,
                    matchingSession.getSessionId(),
                    senderUsername,
                    clientManager);

            matchingSession.setUploadHandler(uploadHandler);
            new Thread(uploadHandler).start();

            context.getResponseSender().sendResponse(new FileTransferResp("OK", 0));

        } else if ("reject".equalsIgnoreCase(reply.status())) {
            System.out.println("Recipient rejected file transfer for session: " + matchingSession.getSessionId());
            context.getClientManager()
                    .getClientResponseSender(senderUsername)
                    .sendResponse(new FileTransferResp("ERROR", 11000));
        } else {
            System.err.println("Unexpected file transfer reply status: " + reply.status());
            context.getClientManager()
                    .getClientResponseSender(senderUsername)
                    .sendResponse(new FileTransferResp("ERROR", 12000));
        }
    }

    /**
     * Allocates a dynamic port by creating a temporary server socket bound to port 0,
     * allowing the operating system to assign an available port.
     *
     * @return the dynamically allocated port number.
     * @throws IOException if the port allocation fails.
     */
    private int allocateDynamicPort() throws IOException {
        try (ServerSocket tempSocket = new ServerSocket(0)) {
            int port = tempSocket.getLocalPort();
            System.out.println("Allocated port: " + port);
            return port;
        }
    }
}
