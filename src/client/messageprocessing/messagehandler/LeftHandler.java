package client.messageprocessing.messagehandler;

import shared.messages.Left;

public class LeftHandler implements MessageHandler<Left> {

    @Override
    public void handle(Left message) {
        System.out.println(message.username() + " has left the chat.");
    }
}
