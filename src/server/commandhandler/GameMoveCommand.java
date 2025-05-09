package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;
import server.game.GameSession;
import shared.Utils;
import shared.messages.GameMoveReq;
import shared.messages.GameMoveResp;

import java.io.IOException;

public class GameMoveCommand implements Command<String> {
    private final CommandContext context;

    public GameMoveCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public void execute(String input) throws IOException {
        ClientState clientState = context.getClientState();
        ClientManager clientManager = context.getClientManager();
        ResponseSender responseSender = context.getResponseSender();

        GameMoveReq moveReq = Utils.messageToObject(input);

        if (!clientState.isLoggedIn()) {
            responseSender.sendResponse(new GameMoveResp("ERROR", 6000));
            return;
        }

        String choice = moveReq.choice();
        if (!choice.equalsIgnoreCase("rock") && !choice.equalsIgnoreCase("paper") && !choice.equalsIgnoreCase("scissors")) {
            responseSender.sendResponse(new GameMoveResp("ERROR", 10001));
            return;
        }

        GameSession gameSession = clientManager.getGameManager().getGame(clientState.getUsername());
        if (gameSession == null) {
            responseSender.sendResponse(new GameMoveResp("ERROR", 9000));
            return;
        }

        gameSession.makeMove(clientState.getUsername(), choice);

        responseSender.sendResponse(new GameMoveResp("OK", 0));

        if (gameSession.isComplete()) {
            gameSession.notifyPlayersOfResult();
        }
    }
}
