package client.messageprocessing.messagehandler;

import client.util.ErrorMessageHandler;
import shared.messages.PrivateMessageResp;

public class PrivateMessageRespHandler implements MessageHandler<PrivateMessageResp> {
    @Override
    public void handle(PrivateMessageResp message) {
        if ("OK".equals(message.status())) {
            System.out.println("Message was sent successfully.");
        } else {
            String errorMessage = ErrorMessageHandler.getErrorMessage(message.code());
            System.out.println("Sending private message failed: " + errorMessage);
        }
    }
}
