package server.clientmessageprocessing;

import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.messages.Bye;

import java.io.IOException;

public class LogoutRequestHandler implements ServerMessageHandler<Bye> {
    CommandHandler commandHandler;

    public LogoutRequestHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handle(Bye message, ClientHandler clientHandler) throws IOException {
        commandHandler.handleBye();
        clientHandler.cleanup();
    }
}
