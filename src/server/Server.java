package server;

import server.connection.ClientHandler;
import server.connection.ClientManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
    private final ClientManager clientManager = new ClientManager();
    private int clientCount = 0;

    public static void main(String[] args) {
        new Server().startServer();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(1338)) {
            System.out.println("Server started at " + new Date());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientCount++;
                System.out.println("New client connected. Total clients: " + clientCount);
                System.out.println("Client " + clientCount + " connected from: " + clientSocket.getInetAddress().getHostAddress());
                Thread clientHandler = new Thread(new ClientHandler(clientSocket, clientManager));
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}
