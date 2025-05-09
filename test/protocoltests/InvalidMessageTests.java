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
import static org.junit.jupiter.api.Assertions.*;

class InvalidMessageTests {

    private final static Properties PROPS = new Properties();

    private Socket socketUser1, socketUser2;
    private BufferedReader inUser1, inUser2;
    private PrintWriter outUser1, outUser2;

    private final static int MAX_DELTA_ALLOWED_MS = 500;

    @BeforeAll
    static void setupAll() throws IOException {
        InputStream in = InvalidMessageTests.class.getResourceAsStream("testconfig.properties");
        if (in == null) {
            throw new FileNotFoundException("testconfig.properties not found in resources.");
        }
        PROPS.load(in);
        in.close();
    }

    @BeforeEach
    void setup() throws IOException {
        // Initialize User1
        socketUser1 = new Socket(PROPS.getProperty("host"), Integer.parseInt(PROPS.getProperty("port")));
        inUser1 = new BufferedReader(new InputStreamReader(socketUser1.getInputStream()));
        outUser1 = new PrintWriter(socketUser1.getOutputStream(), true);

        // Initialize User2
        socketUser2 = new Socket(PROPS.getProperty("host"), Integer.parseInt(PROPS.getProperty("port")));
        inUser2 = new BufferedReader(new InputStreamReader(socketUser2.getInputStream()));
        outUser2 = new PrintWriter(socketUser2.getOutputStream(), true);
    }

    @AfterEach
    void cleanup() throws IOException {
        if (socketUser1 != null && !socketUser1.isClosed()) socketUser1.close();
        if (socketUser2 != null && !socketUser2.isClosed()) socketUser2.close();
    }

    private String receiveLineWithTimeout(BufferedReader reader) {
        return assertTimeoutPreemptively(ofMillis(MAX_DELTA_ALLOWED_MS), () -> reader.readLine(),
                "Did not receive expected message within timeout");
    }

    @Test
    void tc101InvalidMessageHeaderReturnsUnknownCommand() throws JsonProcessingException {
        String readyMessage1 = receiveLineWithTimeout(inUser1);
        String readyMessage2 = receiveLineWithTimeout(inUser2);

        Ready ready1 = Utils.messageToObject(readyMessage1);
        Ready ready2 = Utils.messageToObject(readyMessage2);

        assertEquals(new Ready("1.6.0"), ready1, "User1 should receive READY message with version 1.6.0");
        assertEquals(new Ready("1.6.0"), ready2, "User2 should receive READY message with version 1.6.0");

        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        String enterResp1 = receiveLineWithTimeout(inUser1);
        EnterResp resp1 = Utils.messageToObject(enterResp1);
        assertEquals("OK", resp1.status(), "User1 should receive ENTER_RESP with status OK");

        outUser2.println(Utils.objectToMessage(new Enter("user2")));
        outUser2.flush();
        String enterResp2 = receiveLineWithTimeout(inUser2);
        EnterResp resp2 = Utils.messageToObject(enterResp2);
        assertEquals("OK", resp2.status(), "User2 should receive ENTER_RESP with status OK");

        String joinedMessage = receiveLineWithTimeout(inUser1);
        Joined joined = Utils.messageToObject(joinedMessage);
        assertEquals(new Joined("user2"), joined, "User1 should receive JOINED message for user2");

        String invalidMessage = "MSG This is an invalid message";
        outUser1.println(invalidMessage);
        outUser1.flush();

        String unknownCommandMsg = receiveLineWithTimeout(inUser1);
        assertEquals("UNKNOWN_COMMAND {}", unknownCommandMsg, "User1 should receive 'UNKNOWN_COMMAND {}' message");

        String broadcastContent = "Valid broadcast after invalid message.";
        outUser1.println(Utils.objectToMessage(new BroadcastReq(broadcastContent)));
        outUser1.flush();

        String broadcastResp1 = receiveLineWithTimeout(inUser1);
        BroadcastResp respBroadcast1 = Utils.messageToObject(broadcastResp1);
        assertEquals("OK", respBroadcast1.status(), "User1 should receive BroadcastResp with status OK");

        String broadcastMessage = receiveLineWithTimeout(inUser2);
        Broadcast broadcast = Utils.messageToObject(broadcastMessage);
        assertEquals(new Broadcast("user1", broadcastContent), broadcast, "User2 should receive the broadcast from user1");
    }


    /**
     * Test Case 2: Invalid Message Body
 **/
    @Test
    void tc102InvalidMessageBodyReturnsParseError() throws JsonProcessingException {
        String readyMessage1 = receiveLineWithTimeout(inUser1);
        String readyMessage2 = receiveLineWithTimeout(inUser2);

        Ready ready1 = Utils.messageToObject(readyMessage1);
        Ready ready2 = Utils.messageToObject(readyMessage2);

        assertEquals(new Ready("1.6.0"), ready1, "User1 should receive READY message with version 1.6.0");
        assertEquals(new Ready("1.6.0"), ready2, "User2 should receive READY message with version 1.6.0");

        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        String enterResp1 = receiveLineWithTimeout(inUser1);
        EnterResp resp1 = Utils.messageToObject(enterResp1);
        assertEquals("OK", resp1.status(), "User1 should receive ENTER_RESP with status OK");

        outUser2.println(Utils.objectToMessage(new Enter("user2")));
        outUser2.flush();
        String enterResp2 = receiveLineWithTimeout(inUser2);
        EnterResp resp2 = Utils.messageToObject(enterResp2);
        assertEquals("OK", resp2.status(), "User2 should receive ENTER_RESP with status OK");

        String joinedMessage = receiveLineWithTimeout(inUser1);
        Joined joined = Utils.messageToObject(joinedMessage);
        assertEquals(new Joined("user2"), joined, "User1 should receive JOINED message for user2");

        String invalidJsonBroadcast = "BROADCAST_REQ {\"message\":\"Hello World!}";
        outUser1.println(invalidJsonBroadcast);
        outUser1.flush();

        String parseErrorMsg = receiveLineWithTimeout(inUser1);

        assertEquals("PARSE_ERROR {}", parseErrorMsg, "User1 should receive 'PARSE_ERROR {}' message");

        String broadcastContent = "Another valid broadcast after parse error.";
        outUser1.println(Utils.objectToMessage(new BroadcastReq(broadcastContent)));
        outUser1.flush();

        String broadcastResp1 = receiveLineWithTimeout(inUser1);
        BroadcastResp respBroadcast1 = Utils.messageToObject(broadcastResp1);
        assertEquals("OK", respBroadcast1.status(), "User1 should receive BroadcastResp with status OK");

        String broadcastMessage = receiveLineWithTimeout(inUser2);
        Broadcast broadcast = Utils.messageToObject(broadcastMessage);
        assertEquals(new Broadcast("user1", broadcastContent), broadcast, "User2 should receive the broadcast from user1");
    }
}
