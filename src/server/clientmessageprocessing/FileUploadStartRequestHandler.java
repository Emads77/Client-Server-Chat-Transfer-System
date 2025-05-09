package server.clientmessageprocessing;

import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.messages.FileUploadStart;

import java.io.IOException;

/**
 * Handler for processing the FILE_UPLOAD_START command.
 * This handler receives the message and then delegates to the CommandHandler,
 * which contains the actual logic for starting the file upload phase.
 */
public class FileUploadStartRequestHandler implements ServerMessageHandler<FileUploadStart> {

    private final CommandHandler commandHandler;

    public FileUploadStartRequestHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handle(FileUploadStart message, ClientHandler clientHandler) throws IOException {
        try {
            commandHandler.handleFileUploadStart(message);
        } catch (IOException e) {
            System.err.println("Error handling FILE_UPLOAD_START: " + e.getMessage());
            throw e;
        }
    }
}
