// File: client/commands/SendPrivateCommand.java
package client.commandhandler.commands;

import shared.messages.PrivateMessageReq;
import shared.network.SocketManager;
import shared.Utils;

import java.io.IOException;

public class SendPrivateCommand implements Command {
    private final SocketManager socketManager;

    public SendPrivateCommand(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (arguments.length < 2) {
            System.out.println("Usage: sendprivate <username> <message>");
            return;
        }
        String username = arguments[0];
        String message = arguments[1];
        PrivateMessageReq req = new PrivateMessageReq(username, message);
        String jsonMessage = Utils.objectToMessage(req);
        socketManager.sendMessage(jsonMessage);
    }
}
