package client.commandhandler.commands;

import shared.Utils;
import shared.messages.GameMoveReq;
import shared.network.SocketManager;

import java.io.IOException;

public class MakeMoveCommand implements Command{

    private final SocketManager socketManager;

    public MakeMoveCommand(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (arguments.length < 1) {
            System.out.println("Usage: move <rock|paper|scissors>");
            return;
        }
        String choice = arguments[0];
        GameMoveReq req = new GameMoveReq(choice);
        String jsonMessage = Utils.objectToMessage(req);
        socketManager.sendMessage(jsonMessage);
    }
}
