// File: client/commands/ListUsersCommand.java
package client.commandhandler.commands;

import shared.messages.ListUsersReq;
import shared.network.SocketManager;
import shared.Utils;

import java.io.IOException;

public class ListUsersCommand implements Command {
    private final SocketManager socketManager;

    public ListUsersCommand(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        ListUsersReq req = new ListUsersReq();
        String jsonMessage = Utils.objectToMessage(req);
        socketManager.sendMessage(jsonMessage);
        System.out.println("Requesting list of connected users...");
    }
}
