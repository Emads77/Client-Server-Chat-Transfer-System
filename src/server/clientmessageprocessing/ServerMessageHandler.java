package server.clientmessageprocessing;

import server.connection.ClientHandler;

import java.io.IOException;

public interface ServerMessageHandler <T>{//strategy pattern
    void handle(T message, ClientHandler clientHandler) throws IOException;
}
