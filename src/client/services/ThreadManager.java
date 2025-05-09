package client.services;

import client.commandhandler.UserInputHandler;
import client.messageprocessing.ServerMessageHandler;

import java.util.Scanner;

public class ThreadManager {

    private final ServerMessageHandler messageHandler;
    private final UserInputHandler userInputHandler;

    public ThreadManager(ServerMessageHandler messageHandler, UserInputHandler userInputHandler) {
        this.messageHandler = messageHandler;
        this.userInputHandler = userInputHandler;
    }
    public void startThreads(Scanner scanner) throws InterruptedException {
        Thread serverThread = new Thread(messageHandler::handleServerMessages);
        Thread userThread = new Thread(()-> userInputHandler.processUserInput(scanner));
        serverThread.start();
        userThread.start();
        serverThread.join();
        userThread.join();
    }

}
