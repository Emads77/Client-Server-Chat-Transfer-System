package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;
import server.game.GameManager;
import shared.Utils;
import shared.messages.StartGame;
import shared.messages.StartGameReq;
import shared.messages.StartGameResp;

import java.io.IOException;

public class StartGameCommand implements Command<String> {
    private final CommandContext context;

    public StartGameCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public void execute(String input) throws IOException {
        ClientState clientState = context.getClientState();
        ClientManager clientManager = context.getClientManager();
        ResponseSender responseSender = context.getResponseSender();

        StartGameReq gameReq = Utils.messageToObject(input);
        String opponent = gameReq.opponent();

        if (!clientState.isLoggedIn()) {
            responseSender.sendResponse(new StartGameResp("ERROR", 6000));
            return;
        }

        if (!clientManager.isClientConnected(opponent)) {
            responseSender.sendResponse(new StartGameResp("ERROR", 9000));
            return;
        }

        if (clientManager.isGameActive()) {
            responseSender.sendResponse(new StartGameResp("ERROR", 10000));
            return;
        }

        GameManager.getInstance().startGame(clientState.getUsername(), opponent,context.getClientManager());

        responseSender.sendResponse(new StartGameResp("OK", 0));
        clientManager.getClientResponseSender(opponent)
                .sendResponse(new StartGame(clientState.getUsername()));
    }

}
