package server.commandhandler;

import shared.Utils;
import shared.messages.PrivateMessageReceived;
import shared.messages.PrivateMessageReq;
import shared.messages.PrivateMessageResp;

import java.io.IOException;

public class PrivateMessageCommand implements Command<String> {

    private final CommandContext context;

    public PrivateMessageCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public void execute(String input) throws IOException {
        System.out.println("Executing PrivateMessageCommand with input: " + input);

        PrivateMessageReq messageReq = Utils.messageToObject(input);
        System.out.println("Deserialized PrivateMessageReq: " + messageReq);

        if (!context.getClientState().isLoggedIn()) {
            context.getResponseSender().sendResponse(new PrivateMessageResp("ERROR", 6000));
            return;
        }

        if (!context.getClientManager().isClientConnected(messageReq.recipient())) {
            context.getResponseSender().sendResponse(new PrivateMessageResp("ERROR", 9000));
            return;
        }

        context.getResponseSender().sendResponse(new PrivateMessageResp("OK", 0));
        context.getClientManager()
                .getClientResponseSender(messageReq.recipient())
                .sendResponse(new PrivateMessageReceived(context.getClientState().getUsername(), messageReq.message()));
    }
}
