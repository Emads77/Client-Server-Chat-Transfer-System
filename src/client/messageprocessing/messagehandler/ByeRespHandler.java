package client.messageprocessing.messagehandler;

import shared.messages.ByeResp;

public class ByeRespHandler implements MessageHandler<ByeResp> {

    @Override
    public void handle(ByeResp message) {
        if ("OK".equals(message.status())) {
            System.out.println("Logged out successfully. Goodbye!");
        }
    }
}
