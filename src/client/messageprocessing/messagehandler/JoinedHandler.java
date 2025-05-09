package client.messageprocessing.messagehandler;

import shared.messages.Joined;

public class JoinedHandler implements MessageHandler<Joined>{


    @Override
    public void handle(Joined message) {
        System.out.println(message.username() + " has joined the chat.");

    }
}
