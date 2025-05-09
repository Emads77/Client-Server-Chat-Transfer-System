package client.messageprocessing.messagehandler;

import client.util.ErrorMessageHandler;
import shared.messages.GameMoveResp;

public class GameMoveRespHandler implements MessageHandler<GameMoveResp>{
    @Override
    public void handle(GameMoveResp message) {
        if ("OK".equals(message.status())) {
            System.out.println("move was successful.");
        } else {
            String errorMessage = ErrorMessageHandler.getErrorMessage(message.code());
            System.out.println("move failed: " + errorMessage);
        }
    }
}
