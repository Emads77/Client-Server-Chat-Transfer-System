package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;
import shared.messages.ByeResp;
import shared.messages.Left;

import java.io.IOException;

public class LogoutCommand implements Command<Void> {
    private final CommandContext context;

    public LogoutCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public void execute(Void input) throws IOException {
        ClientState clientState = context.getClientState();
        ClientManager clientManager = context.getClientManager();
        ResponseSender responseSender = context.getResponseSender();

        responseSender.sendResponse(new ByeResp("OK"));
        clientManager.broadcastExcept(clientState.getUsername(), new Left(clientState.getUsername()));
        clientManager.removeClient(clientState.getUsername());
        clientState.logOut();


    }
}
