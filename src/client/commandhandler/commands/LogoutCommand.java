// File: client/commands/LogoutCommand.java
package client.commandhandler.commands;

import shared.Utils;
import shared.messages.Bye;
import shared.network.SocketManager;

import java.io.IOException;

public class LogoutCommand implements Command {
    private final SocketManager socketManager;

    public LogoutCommand(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        Bye req = new Bye();
        String jsonMessage = Utils.objectToMessage(req);
        socketManager.sendMessage(jsonMessage);
        System.out.println("Logging out...");
    }
}
