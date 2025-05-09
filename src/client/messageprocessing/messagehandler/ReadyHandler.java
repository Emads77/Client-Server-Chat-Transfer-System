package client.messageprocessing.messagehandler;

import shared.messages.Ready;

public class ReadyHandler implements MessageHandler<Ready> {

    @Override
    public void handle(Ready message) {
        System.out.println("Server is ready. Version: " + message.version());
    }
}
