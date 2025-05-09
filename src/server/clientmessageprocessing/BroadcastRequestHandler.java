package server.clientmessageprocessing;

import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.messages.BroadcastReq;

import java.io.IOException;

public class BroadcastRequestHandler implements ServerMessageHandler<BroadcastReq> {
    CommandHandler commandHandler;

    public BroadcastRequestHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handle(BroadcastReq message, ClientHandler clientHandler) throws IOException {
        commandHandler.handleBroadcast(message.message());
    }
}
