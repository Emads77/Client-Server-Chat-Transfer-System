package server.clientmessageprocessing;

import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.messages.FileTransferReq;

import java.io.IOException;

public class FileTransferRequestHandler implements ServerMessageHandler<FileTransferReq> {

    private final CommandHandler commandHandler;

    public FileTransferRequestHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handle(FileTransferReq message, ClientHandler clientHandler) throws IOException {
        try {
            commandHandler.handleFileTransferRequest(message);
        } catch (IOException e) {
            System.err.println("Error handling FileTransferReq: " + e.getMessage());
            throw e;
        }
    }
}
