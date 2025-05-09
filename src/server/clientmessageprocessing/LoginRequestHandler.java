package server.clientmessageprocessing;

import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.messages.Enter;

import java.io.IOException;

public class LoginRequestHandler implements ServerMessageHandler<Enter> {

    private final CommandHandler commandHandler;

    public LoginRequestHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handle(Enter message, ClientHandler clientHandler) throws IOException {
        commandHandler.handleLogin(message.username());
    }
}
