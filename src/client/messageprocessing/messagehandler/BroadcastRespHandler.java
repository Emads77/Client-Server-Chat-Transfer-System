package client.messageprocessing.messagehandler;

import shared.messages.BroadcastResp;
import client.util.ErrorMessageHandler;

public class BroadcastRespHandler implements MessageHandler<BroadcastResp> {

    @Override
    public void handle(BroadcastResp message) {
        if ("OK".equals(message.status())) {
            System.out.println("Broadcast successful.");
        } else {
            String errorMessage = ErrorMessageHandler.getErrorMessage(message.code());
            System.out.println("Broadcast failed: " + errorMessage);
        }
    }
}
