package server.clientmessageprocessing;

import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.messages.ListUsersReq;

import java.io.IOException;


public class ListUsersRequestHandler implements ServerMessageHandler<ListUsersReq> {

    CommandHandler commandHandler;

    public ListUsersRequestHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handle(ListUsersReq message, ClientHandler clientHandler) throws IOException {
        commandHandler.handleListUsers();

    }
}
