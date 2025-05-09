package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;
import server.connection.PingHandler;
import shared.messages.EnterResp;
import shared.messages.Joined;

import java.io.IOException;

public class LoginCommand implements Command<String> {
    private final CommandContext context;
    private final PingHandler pingHandler;

    public LoginCommand(CommandContext context, PingHandler pingHandler) {
        this.context = context;
        this.pingHandler = pingHandler;
    }

    @Override
    public void execute(String username) throws IOException {
        ClientState clientState = context.getClientState();
        ClientManager clientManager = context.getClientManager();
        ResponseSender responseSender = context.getResponseSender();

        if (clientState.isLoggedIn()) {
            responseSender.sendResponse(new EnterResp("ERROR", 5002));
            return;
        }

        if (!username.matches("^[a-zA-Z0-9_]{3,14}$")) {
            responseSender.sendResponse(new EnterResp("ERROR", 5001));
            return;
        }

        if (clientManager.isClientConnected(username)) {
            responseSender.sendResponse(new EnterResp("ERROR", 5000));
            return;
        }

        clientState.logIn(username, pingHandler);
        clientManager.addClient(username, responseSender);
        responseSender.sendResponse(new EnterResp("OK", 0));

        clientManager.broadcastExcept(username, new Joined(username));
    }
}

