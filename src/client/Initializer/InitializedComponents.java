package client.Initializer;

import client.commandhandler.UserInputHandler;
import client.messageprocessing.ServerMessageHandler;

public  class InitializedComponents {
    public final UserInputHandler userInputHandler;
    public final ServerMessageHandler messageHandler;

    public InitializedComponents(UserInputHandler userInputHandler, ServerMessageHandler messageHandler) {
        this.userInputHandler = userInputHandler;
        this.messageHandler = messageHandler;
    }
}
