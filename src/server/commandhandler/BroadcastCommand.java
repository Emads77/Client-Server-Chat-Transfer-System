package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;
import shared.messages.BroadcastResp;

import java.io.IOException;

public class BroadcastCommand implements Command<String> {
    private final CommandContext context;

    public BroadcastCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public void execute(String message) throws IOException {
        ClientState clientState = context.getClientState();
        ClientManager clientManager = context.getClientManager();
        ResponseSender responseSender = context.getResponseSender();

        if (!clientState.isLoggedIn()) {
            responseSender.sendResponse(new BroadcastResp("ERROR", 6000));
            return;
        }

        clientManager.broadcast(clientState.getUsername(), message);
        responseSender.sendResponse(new BroadcastResp("OK", 0));
    }
}
