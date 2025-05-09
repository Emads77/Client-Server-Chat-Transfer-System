package client;

import client.Initializer.ComponentInitializer;
import client.Initializer.InitializedComponents;
import client.services.ThreadManager;
import shared.network.SocketManager;
import client.ui.MenuDisplay;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 1338;

    private SocketManager socketManager;

    public static void main(String[] args) {
        new Client().startClient();
    }

    public void startClient() {
        try (Socket clientSocket = new Socket(HOST, PORT);
             BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter toServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
             Scanner scanner = new Scanner(System.in)) {
            this.socketManager = new SocketManager(fromServer, toServer);

            ComponentInitializer initializer = new ComponentInitializer(socketManager);
            InitializedComponents components = initializer.initialize();

            System.out.println("You have successfully connected to the server.");
            MenuDisplay.showMainMenu();

            ThreadManager threadManager = new ThreadManager(components.messageHandler, components.userInputHandler);
            threadManager.startThreads(scanner);

        } catch (IOException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
