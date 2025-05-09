package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;
import server.connection.FileTransferSession;
import shared.messages.FileTransferResp;
import shared.messages.FileUploadStart;

import java.io.IOException;

public class FileUploadStartCommand implements Command<FileUploadStart> {
    private final CommandContext context;

    public FileUploadStartCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public void execute(FileUploadStart input) throws IOException {
        ClientState clientState = context.getClientState();
        ClientManager clientManager = context.getClientManager();
        if (!clientState.isLoggedIn()) {
            context.getResponseSender().sendResponse(new FileTransferResp("ERROR", 6000));
            return;
        }
        String sessionId = input.sessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            context.getResponseSender().sendResponse(new FileTransferResp("ERROR", 12001));
            return;
        }
        FileTransferSession session = clientManager.getFileTransferSession(sessionId);
        if (session == null) {
            context.getResponseSender().sendResponse(new FileTransferResp("ERROR", 12005));
            return;
        }

        FileUploadHandler uploadHandler = session.getUploadHandler();
        if (uploadHandler != null) {
            uploadHandler.startUpload();
        }
    }
}