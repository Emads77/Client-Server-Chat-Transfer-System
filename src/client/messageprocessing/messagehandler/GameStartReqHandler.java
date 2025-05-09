package client.messageprocessing.messagehandler;

import client.util.ErrorMessageHandler;
import shared.messages.StartGameResp;

public class GameStartReqHandler implements MessageHandler<StartGameResp>{
    @Override
    public void handle(StartGameResp message) {
        if ("OK".equals(message.status())) {
            System.out.println("Game invitation sent.");
        } else {
            String errorMessage = ErrorMessageHandler.getErrorMessage(message.code());
            System.out.println("Sending game invitation failed: " + errorMessage);
        }
    }
}
