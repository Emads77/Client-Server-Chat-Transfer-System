package server.clientmessageprocessing;

import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.Utils;
import shared.messages.GameMoveReq;

import java.io.IOException;

public class GameMoveRequestHandler implements ServerMessageHandler<GameMoveReq> {
    private final CommandHandler commandHandler;

    public GameMoveRequestHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handle(GameMoveReq message, ClientHandler clientHandler) throws IOException {

        commandHandler.handleGameMove(Utils.objectToMessage(message));
    }
}
