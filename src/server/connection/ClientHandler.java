package server.connection;

import server.clientmessageprocessing.ClientMessageProcessor;
import server.clientmessageprocessing.PongRequestHandler;
import server.commandhandler.CommandHandler;
import server.commandhandler.ResponseSender;
import shared.messages.ParseError;
import shared.messages.Ready;
import shared.messages.Unknown;
import shared.network.SocketManager;

import java.io.*;
import java.net.Socket;

    public class ClientHandler implements Runnable {
        private final SocketManager socketManager;
        private final ClientManager clientManager;
        private final ClientState clientState;
        private final ResponseSender responseSender;
        private final ClientMessageProcessor clientMessageProcessor;

        public ClientHandler(Socket clientSocket, ClientManager clientManager) throws IOException {
            this.socketManager = new SocketManager(
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream())),
                    new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
            );
            this.clientManager = clientManager;
            this.clientState = new ClientState();
            this.responseSender = new ResponseSender(socketManager);

            CommandHandler commandHandler = new CommandHandler(clientState, clientManager, responseSender, socketManager);
            this.clientMessageProcessor = new ClientMessageProcessor(commandHandler, new PongRequestHandler());
        }

        @Override
        public void run() {
            try {
                responseSender.sendResponse(new Ready("1.6.0"));
                String rawMessage;
                while ((rawMessage = socketManager.readMessage()) != null) {
                    clientMessageProcessor.processMessage(rawMessage, this);
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        public ClientState getClientState() {
            return clientState;
        }

        public void sendUnknownCommand() throws IOException {
            responseSender.sendResponse(new Unknown());
            System.out.println("Sent UNKNOWN_COMMAND to client.");
        }

        public void sendParseError() throws IOException {
            responseSender.sendResponse(new ParseError());
            System.out.println("Sent PARSE_ERROR to client.");
        }

        public void cleanup() {
            try {
                if (clientState.isLoggedIn()) {
                    String username = clientState.getUsername();
                    clientState.logOut();
                    clientManager.removeClient(username);
                }
                socketManager.closeConnection();
            } catch (IOException e) {
                System.err.println("Error during cleanup: " + e.getMessage());
            }
        }

    }
