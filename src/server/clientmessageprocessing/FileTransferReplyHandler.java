package server.clientmessageprocessing;

import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.messages.FileTransferReply;

import java.io.IOException;

public class FileTransferReplyHandler implements ServerMessageHandler<FileTransferReply> {
    private final CommandHandler commandHandler;

    public FileTransferReplyHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handle(FileTransferReply message, ClientHandler clientHandler) throws IOException {
        // Delegate handling the reply to CommandHandler
        commandHandler.handleFileTransferReply(message);
    }
}
