package client.messageprocessing.messagehandler;

import shared.messages.GameResult;

public class GameResultHandler implements MessageHandler<GameResult> {
    @Override
    public void handle(GameResult message) {
        if (message.winner() == null) {
            System.out.println("Game is finished, it's a draw!");
        } else {
            System.out.println("Game is finished, the winner is: " + message.winner());
            System.out.println("The reason for winning is: " + message.reason());
        }

        String opponentChoice = message.opponentChoice() != null ? message.opponentChoice() : "No move made";
        System.out.println("The opponent's choice was: " + opponentChoice);
    }
}

