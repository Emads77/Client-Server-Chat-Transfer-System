// File: client/commands/BroadcastCommand.java
package client.commandhandler.commands;

import shared.messages.BroadcastReq;
import shared.network.SocketManager;
import shared.Utils;

import java.io.IOException;

public class BroadcastCommand implements Command {
    private final SocketManager socketManager;

    public BroadcastCommand(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (arguments.length < 1) {
            System.out.println("Usage: broadcast <message>");
            return;
        }
        String message = String.join(" ", arguments);
        BroadcastReq req = new BroadcastReq(message);
        String jsonMessage = Utils.objectToMessage(req);
        socketManager.sendMessage(jsonMessage);
        System.out.println("Broadcast message sent.");
    }
}
