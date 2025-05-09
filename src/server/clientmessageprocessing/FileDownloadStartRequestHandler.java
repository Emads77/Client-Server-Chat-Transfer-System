package server.clientmessageprocessing;

import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.messages.FileDownloadStart;

import java.io.IOException;

public class FileDownloadStartRequestHandler implements ServerMessageHandler<FileDownloadStart> {

    private final CommandHandler commandHandler;

    public FileDownloadStartRequestHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }


    @Override
    public void handle(FileDownloadStart message, ClientHandler clientHandler) throws IOException {
        try {
            commandHandler.handleFileDownloadStart(message);
        } catch (IOException e) {
            System.err.println("Error handling FILE_DOWNLOAD_START: " + e.getMessage());
            throw e;
        }

    }
}