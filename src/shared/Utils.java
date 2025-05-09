package shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import shared.messages.*;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    private final static ObjectMapper mapper = new ObjectMapper();
    private final static Map<Class<?>, String> objToNameMapping = new HashMap<>();
    static {
        objToNameMapping.put(Enter.class, "ENTER");
        objToNameMapping.put(EnterResp.class, "ENTER_RESP");
        objToNameMapping.put(BroadcastReq.class, "BROADCAST_REQ");
        objToNameMapping.put(BroadcastResp.class, "BROADCAST_RESP");
        objToNameMapping.put(Broadcast.class, "BROADCAST");
        objToNameMapping.put(Joined.class, "JOINED");
        objToNameMapping.put(ParseError.class, "PARSE_ERROR");
        objToNameMapping.put(Unknown.class, "UNKNOWN_COMMAND");
        objToNameMapping.put(Pong.class, "PONG");
        objToNameMapping.put(PongError.class, "PONG_ERROR");
        objToNameMapping.put(Ready.class, "READY");
        objToNameMapping.put(Ping.class, "PING");
        objToNameMapping.put(Bye.class, "BYE");
        objToNameMapping.put(ByeResp.class, "BYE_RESP");
        objToNameMapping.put(Left.class,"LEFT");
        objToNameMapping.put(Hangup.class, "HANGUP");
        objToNameMapping.put(ListUsers.class, "LIST_USERS");
        objToNameMapping.put(ListUsersReq.class, "LIST_USERS_REQ");
        objToNameMapping.put(ListUsersResp.class, "LIST_USERS_RESP");
        objToNameMapping.put(PrivateMessageReceived.class, "PRIVATE_MSG_RECEIVED");
        objToNameMapping.put(PrivateMessageReq.class, "PRIVATE_MSG_REQ");
        objToNameMapping.put(PrivateMessageResp.class,"PRIVATE_MSG_RESP");
        objToNameMapping.put(StartGameReq.class,"START_GAME_REQ");
        objToNameMapping.put(StartGameResp.class, "START_GAME_RESP");
        objToNameMapping.put(StartGame.class, "START_GAME");
        objToNameMapping.put(GameMoveReq.class, "GAME_MOVE_REQ");
        objToNameMapping.put(GameMoveResp.class, "GAME_MOVE_RESP");
        objToNameMapping.put(GameResult.class,"GAME_RESULT");
        objToNameMapping.put(FileTransferReq.class, "FILE_TRANSFER_REQ");
        objToNameMapping.put(FileTransferOffer.class, "FILE_TRANSFER_OFFER");
        objToNameMapping.put(FileTransferResp.class, "FILE_TRANSFER_RESP");
        objToNameMapping.put(FileTransferReply.class, "FILE_TRANSFER_REPLY");
        objToNameMapping.put(FileUploadReady.class, "FILE_UPLOAD_READY");
        objToNameMapping.put(FileUploadResp.class, "FILE_UPLOAD_RESP");
        objToNameMapping.put(FileUploadStart.class, "FILE_UPLOAD_START");
        objToNameMapping.put(FileDownloadStart.class, "FILE_DOWNLOAD_START");
        objToNameMapping.put(FileDownloadResp.class, "FILE_DOWNLOAD_RESP");
        objToNameMapping.put(FileDownloadReady.class, "FILE_DOWNLOAD_READY");
        objToNameMapping.put(SessionIdResp.class, "SESSION_ID");
    }

    public static String objectToMessage(Object object) throws JsonProcessingException {
        Class<?> clazz = object.getClass();
        String header = objToNameMapping.get(clazz);
        if (header == null) {
            throw new RuntimeException("Cannot convert this class to a message");
        }
        String body = mapper.writeValueAsString(object);
        return header + " " + body;
    }

    public static <T> T messageToObject(String message) throws JsonProcessingException {
        String[] parts = message.split(" ", 2);
        if (parts.length > 2 || parts.length == 0) {
            throw new RuntimeException("Invalid message");
        }
        String header = parts[0];
        String body = "{}";
        if (parts.length == 2) {
            body = parts[1];
        }
        Class<?> clazz = getClass(header);
        Object obj = mapper.readValue(body, clazz);
        return (T) clazz.cast(obj);
    }

    public static Class<?> getClass(String header) {
        return objToNameMapping.entrySet().stream()
                .filter(e -> e.getValue().equals(header))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find class belonging to header " + header));
    }
}
