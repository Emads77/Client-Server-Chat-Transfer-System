package client.messageprocessing.messagehandler;

import shared.messages.StartGame;

public class StartGameHandler implements MessageHandler<StartGame> {
    @Override
    public void handle(StartGame message) {
        System.out.println("Game started with " + message.opponent());
    }
}
