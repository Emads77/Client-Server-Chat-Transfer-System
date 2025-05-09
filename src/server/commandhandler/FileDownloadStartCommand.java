package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;
import server.connection.FileTransferSession;
import shared.messages.FileDownloadStart;
import shared.messages.FileDownloadResp;
import shared.messages.FileDownloadReady;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Command to initiate a file download. This command checks the user's login status,
 * validates the session, allocates a dynamic port for the file transfer, and starts the download process.
 */
public class FileDownloadStartCommand implements Command<FileDownloadStart> {

    private final CommandContext context;

    /**
     * Constructs a new FileDownloadStartCommand.
     *
     * @param context the command context providing access to client state, client manager, and response sender.
     */
    public FileDownloadStartCommand(CommandContext context) {
        this.context = context;
    }

    /**
     * Executes the file download start command. Verifies the user is logged in, validates the session ID,
     * allocates a port for the file download, starts the file download handler, and sends a response
     * to the client notifying them of the readiness for download.
     *
     * @param input the {@link FileDownloadStart} message containing the session ID of the file download.
     * @throws IOException if an error occurs during port allocation or file download handler startup.
     */
    @Override
    public void execute(FileDownloadStart input) throws IOException {
        ClientState clientState = context.getClientState();
        ClientManager clientManager = context.getClientManager();

        if (!clientState.isLoggedIn()) {
            context.getResponseSender().sendResponse(new FileDownloadResp("ERROR", "Not logged in"));
            return;
        }

        String sessionId = input.sessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            context.getResponseSender().sendResponse(new FileDownloadResp("ERROR", "Invalid session ID"));
            return;
        }

        FileTransferSession session = clientManager.getFileTransferSession(sessionId);
        if (session == null) {
            context.getResponseSender().sendResponse(new FileDownloadResp("ERROR", "Session not found"));
            return;
        }

        String storedChecksum = session.getChecksum();
        int downloadPort;
        try {
            downloadPort = allocateDynamicPort();
        } catch (IOException e) {
            System.err.println("Failed to allocate port for download: " + e.getMessage());
            context.getResponseSender().sendResponse(new FileDownloadResp("ERROR", "Port allocation failed"));
            return;
        }

        System.out.println("Allocated port for download: " + downloadPort);

        FileDownloadHandler downloadHandler = new FileDownloadHandler(downloadPort, sessionId, clientState.getUsername(), clientManager);
        new Thread(downloadHandler).start();

        context.getResponseSender().sendResponse(new FileDownloadReady(downloadPort, sessionId, storedChecksum));
    }

    /**
     * Allocates a dynamic port by creating a server socket bound to port 0, allowing the operating system
     * to assign an available port automatically.
     *
     * @return the dynamically allocated port number.
     * @throws IOException if the port allocation fails.
     */
    private int allocateDynamicPort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
