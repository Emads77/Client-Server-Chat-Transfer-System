package client.commandhandler.commands;

import shared.messages.StartGameReq;
import shared.network.SocketManager;
import shared.Utils;

import java.io.IOException;

public class StartGameCommand implements Command {
    private final SocketManager socketManager;

    public StartGameCommand(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (arguments.length < 1) {
            System.out.println("Usage: startgame <username>");
            return;
        }
        String opponentUsername = arguments[0];
        StartGameReq req = new StartGameReq(opponentUsername);
        String jsonMessage = Utils.objectToMessage(req);
        socketManager.sendMessage(jsonMessage);
    }
}
