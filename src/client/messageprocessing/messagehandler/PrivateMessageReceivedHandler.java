package client.messageprocessing.messagehandler;

import shared.messages.PrivateMessageReceived;

public class PrivateMessageReceivedHandler implements MessageHandler<PrivateMessageReceived> {
    @Override
    public void handle(PrivateMessageReceived message) {
        System.out.println(message.username()+" sent a private message:" + message.message());
    }
}
