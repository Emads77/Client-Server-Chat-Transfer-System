package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;
import shared.messages.ListUsers;
import shared.messages.ListUsersResp;

import java.io.IOException;
import java.util.List;

public class ListUsersCommand implements Command<Void> {
    private final CommandContext context;

    public ListUsersCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public void execute(Void input) throws IOException {
        ClientManager clientManager = context.getClientManager();
        ClientState clientState = context.getClientState();
        ResponseSender responseSender = context.getResponseSender();

        if (!clientState.isLoggedIn()) {
            responseSender.sendResponse(new ListUsersResp("ERROR", 6000));
            return;
        }

        responseSender.sendResponse(new ListUsersResp("OK", 0));

        List<String> usernames = clientManager.getAllConnectedUsers();
        responseSender.sendResponse(new ListUsers(usernames));
    }

}
