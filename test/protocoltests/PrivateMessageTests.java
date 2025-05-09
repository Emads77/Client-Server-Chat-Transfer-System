package protocoltests;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shared.Utils;
import shared.messages.*;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class PrivateMessageTests {

    private final static Properties PROPS = new Properties();

    private Socket socketUser1, socketUser2;
    private BufferedReader inUser1, inUser2;
    private PrintWriter outUser1, outUser2;

    private final static int MAX_DELTA_ALLOWED_MS = 500;

    @BeforeAll
    static void setupAll() throws IOException {
        InputStream in = PrivateMessageTests.class.getResourceAsStream("testconfig.properties");
        PROPS.load(in);
        in.close();
    }

    @BeforeEach
    void setup() throws IOException {
        socketUser1 = new Socket(PROPS.getProperty("host"), Integer.parseInt(PROPS.getProperty("port")));
        inUser1 = new BufferedReader(new InputStreamReader(socketUser1.getInputStream()));
        outUser1 = new PrintWriter(socketUser1.getOutputStream(), true);

        socketUser2 = new Socket(PROPS.getProperty("host"), Integer.parseInt(PROPS.getProperty("port")));
        inUser2 = new BufferedReader(new InputStreamReader(socketUser2.getInputStream()));
        outUser2 = new PrintWriter(socketUser2.getOutputStream(), true);
    }

    @AfterEach
    void cleanup() throws IOException {
        socketUser1.close();
        socketUser2.close();
    }

    @Test
    void tc71PrivateMessageBetweenTwoUsersWorksCorrectly() throws JsonProcessingException {
        receiveLineWithTimeout(inUser1);
        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        receiveLineWithTimeout(inUser1);

        receiveLineWithTimeout(inUser2);
        outUser2.println(Utils.objectToMessage(new Enter("user2")));
        outUser2.flush();
        receiveLineWithTimeout(inUser2);

        String joinedMessage = receiveLineWithTimeout(inUser1);
        Joined joined = Utils.messageToObject(joinedMessage);
        assertEquals(new Joined("user2"), joined);

        outUser1.println(Utils.objectToMessage(new PrivateMessageReq("user2", "Hello!")));
        outUser1.flush();

        String user1Response = receiveLineWithTimeout(inUser1);
        PrivateMessageResp resp = Utils.messageToObject(user1Response);
        assertEquals(new PrivateMessageResp("OK", 0), resp);

        String user2Response = receiveLineWithTimeout(inUser2);
        PrivateMessageReceived receivedMessage = Utils.messageToObject(user2Response);
        assertEquals(new PrivateMessageReceived("user1", "Hello!"), receivedMessage);
    }

    @Test
    void tc72PrivateMessageToNonexistentUserReturnsError() throws JsonProcessingException {
        receiveLineWithTimeout(inUser1);
        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        receiveLineWithTimeout(inUser1);

        outUser1.println(Utils.objectToMessage(new PrivateMessageReq("nonexistent", "Hello!")));
        outUser1.flush();
        String serverResponse = receiveLineWithTimeout(inUser1);
        PrivateMessageResp privateMessageResp = Utils.messageToObject(serverResponse);
        assertEquals(new PrivateMessageResp("ERROR", 9000), privateMessageResp);
    }

    @Test
    void tc73PrivateMessageWithoutLoginReturnsError() throws JsonProcessingException {
        receiveLineWithTimeout(inUser1);

        outUser1.println(Utils.objectToMessage(new PrivateMessageReq("user2", "Hello!")));
        outUser1.flush();
        String serverResponse = receiveLineWithTimeout(inUser1);
        PrivateMessageResp privateMessageResp = Utils.messageToObject(serverResponse);
        assertEquals(new PrivateMessageResp("ERROR", 6000), privateMessageResp);
    }

    @Test
    void tc74PrivateMessageToSelfReturnsCorrectResponse() throws JsonProcessingException {
        receiveLineWithTimeout(inUser1);
        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        receiveLineWithTimeout(inUser1);

        outUser1.println(Utils.objectToMessage(new PrivateMessageReq("user1", "Hello Self!")));
        outUser1.flush();
        String user1Response = receiveLineWithTimeout(inUser1);
        PrivateMessageResp resp = Utils.messageToObject(user1Response);
        assertEquals(new PrivateMessageResp("OK", 0), resp);

        String selfReceivedMessage = receiveLineWithTimeout(inUser1);
        PrivateMessageReceived receivedMessage = Utils.messageToObject(selfReceivedMessage);
        assertEquals(new PrivateMessageReceived("user1", "Hello Self!"), receivedMessage);
    }

    @Test
    void tc75PrivateMessageInvalidJsonFormatReturnsParseError() throws JsonProcessingException {
        receiveLineWithTimeout(inUser1);

        outUser1.println("PRIVATE_MSG_REQ {invalid_json}");
        outUser1.flush();
        String serverResponse = receiveLineWithTimeout(inUser1);
        ParseError parseError = Utils.messageToObject(serverResponse);
        assertEquals(new ParseError(), parseError);
    }

    private String receiveLineWithTimeout(BufferedReader reader) {
        return assertTimeoutPreemptively(ofMillis(MAX_DELTA_ALLOWED_MS), reader::readLine);
    }
}
