package server.clientmessageprocessing;

import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.Utils;
import shared.messages.StartGameReq;

import java.io.IOException;

public class StartGameRequestHandler implements ServerMessageHandler<StartGameReq> {

    private final CommandHandler commandHandler;

    public StartGameRequestHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handle(StartGameReq message, ClientHandler clientHandler) throws IOException {
        commandHandler.handleStartGame(Utils.objectToMessage(message));
    }
}
