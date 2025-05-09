package client.messageprocessing.messagehandler;

import shared.messages.Broadcast;

public class BroadcastMessageHandler implements MessageHandler<Broadcast> {

    @Override
    public void handle(Broadcast message) {
        System.out.println(message.username() + " sent: " + message.message());
    }
}
