// File: client/commands/LoginCommand.java
package client.commandhandler.commands;

import shared.messages.Enter;
import shared.network.SocketManager;
import shared.Utils;

import java.io.IOException;

public class LoginCommand implements Command {
    private final SocketManager socketManager;

    public LoginCommand(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (arguments.length < 1) {
            System.out.println("Usage: login <username>");
            return;
        }
        String username = arguments[0];
        Enter enter = new Enter(username);
        String jsonMessage = Utils.objectToMessage(enter);
        socketManager.sendMessage(jsonMessage);
        System.out.println("Attempting to log in as " + username);
    }
}
