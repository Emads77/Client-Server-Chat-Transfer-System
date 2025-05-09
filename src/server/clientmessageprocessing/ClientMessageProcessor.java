package server.clientmessageprocessing;

import com.fasterxml.jackson.core.JsonProcessingException;
import server.commandhandler.CommandHandler;
import server.connection.ClientHandler;
import shared.Utils;
import shared.messages.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClientMessageProcessor {
    private final Map<Class<?>, ServerMessageHandler<?>> handlers = new HashMap<>();

    public ClientMessageProcessor(CommandHandler commandHandler, ServerMessageHandler<Pong> pongHandler) {
        registerHandlers(commandHandler, pongHandler);
    }

    private void registerHandlers(CommandHandler commandHandler, ServerMessageHandler<Pong> pongHandler) {
        handlers.put(Enter.class, new LoginRequestHandler(commandHandler));
        handlers.put(BroadcastReq.class, new BroadcastRequestHandler(commandHandler));
        handlers.put(Bye.class, new LogoutRequestHandler(commandHandler));
        handlers.put(Pong.class, pongHandler);
        handlers.put(ListUsersReq.class, new ListUsersRequestHandler(commandHandler));
        handlers.put(PrivateMessageReq.class, new PrivateMessageHandler(commandHandler));
        handlers.put(StartGameReq.class, new StartGameRequestHandler(commandHandler));
        handlers.put(GameMoveReq.class, new GameMoveRequestHandler(commandHandler));
        handlers.put(FileTransferReq.class, new FileTransferRequestHandler(commandHandler));
        handlers.put(FileTransferReply.class, new FileTransferReplyHandler(commandHandler));
        handlers.put(FileUploadStart.class, new FileUploadStartRequestHandler(commandHandler));
        handlers.put(FileDownloadStart.class, new FileDownloadStartRequestHandler(commandHandler));
    }

    public void processMessage(String rawMessage, ClientHandler clientHandler) throws IOException {
        try {
            System.out.println("Raw message received: " + rawMessage);
                //Enter {username}

            // parse the raw message into header and body
            String[] parts = rawMessage.split(" ", 2);
            if (parts.length == 0 || parts[0].isEmpty()) {
                clientHandler.sendUnknownCommand();
                return;
            }
            String header = parts[0];
            String body = (parts.length > 1) ? parts[1] : "{}";

            System.out.println("Header: " + header);
            System.out.println("Body: " + body);

            // Determine the class type based on the header using a utility method
            Class<?> messageClass = Utils.getClass(header);
            if (messageClass == null) {
                // If the header doesn't match any known message type, send an "Unknown Command" response
                clientHandler.sendUnknownCommand();
                return;
            }

            Object message;
            try {
                //deserialize the message, into appropriate message type
                message = Utils.messageToObject(header + " " + body);
            } catch (JsonProcessingException e) {
                clientHandler.sendParseError();
                return;
            }

            // Find and invoke the handler for the message class
            ServerMessageHandler<Object> handler = (ServerMessageHandler<Object>) handlers.get(message.getClass());
            if (handler != null) {
                handler.handle(message, clientHandler);
            } else {
                clientHandler.sendUnknownCommand();
            }
        } catch (RuntimeException e) {
            System.err.println("Unexpected error processing message: " + e.getMessage());
            clientHandler.sendUnknownCommand();
        }
    }

}




