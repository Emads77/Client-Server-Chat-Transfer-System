package server.clientmessageprocessing;

import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.Utils;
import shared.messages.PrivateMessageReq;

import java.io.IOException;

public class PrivateMessageHandler implements ServerMessageHandler<PrivateMessageReq> {

    CommandHandler commandHandler;

    public PrivateMessageHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handle(PrivateMessageReq message, ClientHandler clientHandler) throws IOException {
        System.out.println("Handling PrivateMessageReq: " + message);
        System.out.println("Recipient: " + message.recipient());
        System.out.println("Message: " + message.message());
        commandHandler.handlePrivateMessage(Utils.objectToMessage(message));
    }

}
