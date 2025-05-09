package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;

public class CommandContext {
    private final ClientState clientState;
    private final ClientManager clientManager;
    private final ResponseSender responseSender;


    public CommandContext(ClientState clientState, ClientManager clientManager, ResponseSender responseSender) {
        this.clientState = clientState;
        this.clientManager = clientManager;
        this.responseSender = responseSender;
    }

    public ClientState getClientState() {
        return clientState;
    }

    public ClientManager getClientManager() {
        return clientManager;
    }

    public ResponseSender getResponseSender() {
        return responseSender;
    }

}
