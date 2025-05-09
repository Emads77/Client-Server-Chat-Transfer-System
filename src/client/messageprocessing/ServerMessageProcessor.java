package client.messageprocessing;


import client.messageprocessing.messagehandler.*;
import client.services.FileTransferManager;
import shared.Utils;
import shared.messages.*;
import shared.network.SocketManager;

import java.util.HashMap;
import java.util.Map;

public class ServerMessageProcessor {
    private final SocketManager socketManager;
    private final FileTransferManager fileTransferManager;
    private final Map<Class<?>, MessageHandler<?>> handlers = new HashMap<>();

    public ServerMessageProcessor(SocketManager socketManager, FileTransferManager fileTransferManager) {
        this.socketManager = socketManager;
        this.fileTransferManager = fileTransferManager;
        registerHandlers();
    }
    private void registerHandlers() {
        handlers.put(Ready.class, new ReadyHandler());
        handlers.put(EnterResp.class, new EnterRespHandler());
        handlers.put(Broadcast.class, new BroadcastMessageHandler());
        handlers.put(BroadcastResp.class, new BroadcastRespHandler());
        handlers.put(ByeResp.class, new ByeRespHandler());
        handlers.put(Left.class, new LeftHandler());
        handlers.put(Joined.class,new JoinedHandler());
        handlers.put(ListUsers.class,new ListUsersHandler());
        handlers.put(ListUsersResp.class,new ListUsersRespHandler());
        handlers.put(PrivateMessageResp.class, new PrivateMessageRespHandler());
        handlers.put(PrivateMessageReceived.class, new PrivateMessageReceivedHandler());
        handlers.put(StartGameResp.class, new GameStartReqHandler());
        handlers.put(GameMoveResp.class,new GameMoveRespHandler());
        handlers.put(StartGame.class ,  new StartGameHandler());
        handlers.put(GameResult.class ,  new GameResultHandler());
        handlers.put(FileTransferResp.class,new FileTransferRespHandler());
        handlers.put(FileTransferOffer.class,new FileTransferOfferHandler(socketManager,fileTransferManager));
        handlers.put(FileUploadReady.class, new FileUploadReadyHandler(socketManager,fileTransferManager,"localhost"));
        handlers.put(SessionIdResp.class, new SessionIdHandler(fileTransferManager));
        handlers.put(FileUploadResp.class, new FileUploadRespHandler());
        handlers.put(FileDownloadReady.class, new FileDownloadReadyHandler(socketManager,fileTransferManager,"localhost"));
        handlers.put(Ping.class, new PingHandler(socketManager));
        handlers.put(ParseError.class, new ParseErrorHandler());
    }

    public void processMessage(String message) {
        try {
            Object deserializedMessage = Utils.messageToObject(message);
            MessageHandler<Object> handler = (MessageHandler<Object>) handlers.get(deserializedMessage.getClass());
            if (handler != null) {
                handler.handle(deserializedMessage);
            } else {
                System.err.println("Unknown message type received: " + message);
            }
        } catch (Exception e) {
            System.err.println("Malformed server message: " + message);
            e.printStackTrace();
        }
    }
}
