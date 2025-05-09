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

class BroadcastTests {

    private final static Properties PROPS = new Properties();

    private Socket socketUser1, socketUser2;
    private BufferedReader inUser1, inUser2;
    private PrintWriter outUser1, outUser2;

    private final static int MAX_DELTA_ALLOWED_MS = 500;

    @BeforeAll
    static void setupAll() throws IOException {
        InputStream in = BroadcastTests.class.getResourceAsStream("testconfig.properties");
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
        return assertTimeoutPreemptively(ofMillis(MAX_DELTA_ALLOWED_MS), () -> reader.readLine());
    }

    /**
     * Test Case 1: Verify that a user receives the READY message upon connecting.
     */
    @Test
    void tc91UserReceivesReadyMessageUponConnecting() throws JsonProcessingException {
        String readyMessage1 = receiveLineWithTimeout(inUser1);
        String readyMessage2 = receiveLineWithTimeout(inUser2);

        // Parse and assert READY messages
        Ready ready1 = Utils.messageToObject(readyMessage1);
        Ready ready2 = Utils.messageToObject(readyMessage2);

        assertEquals(new Ready("1.6.0"), ready1, "User1 should receive READY message with version 1.6.0");
        assertEquals(new Ready("1.6.0"), ready2, "User2 should receive READY message with version 1.6.0");
    }

    /**
     * Test Case 2: Verify that existing users receive a JOINED message when a new user joins.
     */
    @Test
    void tc92ExistingUsersReceiveJoinedMessageWhenNewUserJoins() throws JsonProcessingException {
        // Receive READY messages
        receiveLineWithTimeout(inUser1);
        receiveLineWithTimeout(inUser2);

        // User1 logs in
        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        String enterResp1 = receiveLineWithTimeout(inUser1);
        EnterResp resp1 = Utils.messageToObject(enterResp1);
        assertEquals("OK", resp1.status(), "User1 should receive ENTER_RESP with status OK");

        // User2 logs in
        outUser2.println(Utils.objectToMessage(new Enter("user2")));
        outUser2.flush();
        String enterResp2 = receiveLineWithTimeout(inUser2);
        EnterResp resp2 = Utils.messageToObject(enterResp2);
        assertEquals("OK", resp2.status(), "User2 should receive ENTER_RESP with status OK");

        // User1 should receive a JOINED message about User2
        String joinedMessage = receiveLineWithTimeout(inUser1);
        Joined joined = Utils.messageToObject(joinedMessage);
        assertEquals(new Joined("user2"), joined, "User1 should receive JOINED message for user2");

    }

    /**
     * Test Case 3: Verify that a logged-in user can broadcast a message and other users receive it.
     */
    @Test
    void tc93BroadcastMessageIsReceivedByOtherUsers() throws JsonProcessingException {
        // Receive READY messages
        receiveLineWithTimeout(inUser1);
        receiveLineWithTimeout(inUser2);

        // User1 logs in
        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        String enterResp1 = receiveLineWithTimeout(inUser1);
        EnterResp resp1 = Utils.messageToObject(enterResp1);
        assertEquals("OK", resp1.status(), "User1 should receive ENTER_RESP with status OK");

        // User2 logs in
        outUser2.println(Utils.objectToMessage(new Enter("user2")));
        outUser2.flush();
        String enterResp2 = receiveLineWithTimeout(inUser2);
        EnterResp resp2 = Utils.messageToObject(enterResp2);
        assertEquals("OK", resp2.status(), "User2 should receive ENTER_RESP with status OK");

        // User1 should receive a JOINED message about User2
        String joinedMessage = receiveLineWithTimeout(inUser1);
        Joined joined = Utils.messageToObject(joinedMessage);
        assertEquals(new Joined("user2"), joined, "User1 should receive JOINED message for user2");

        // User1 sends a broadcast message
        String broadcastContent = "Hello, this is user1!";
        outUser1.println(Utils.objectToMessage(new BroadcastReq(broadcastContent)));
        outUser1.flush();

        // Assert User1 receives BroadcastResp OK
        String broadcastResp1 = receiveLineWithTimeout(inUser1);
        BroadcastResp respBroadcast1 = Utils.messageToObject(broadcastResp1);
        assertEquals("OK", respBroadcast1.status(), "User1 should receive BroadcastResp with status OK");

        // Assert User2 receives the Broadcast message
        String broadcastMessage = receiveLineWithTimeout(inUser2);
        Broadcast broadcast = Utils.messageToObject(broadcastMessage);
        assertEquals(new Broadcast("user1", broadcastContent), broadcast, "User2 should receive the broadcast from user1");
    }

    /**
     * Test Case 4: Verify that attempting to broadcast without logging in returns an error (code 6000).
     */
    @Test
    void tc94BroadcastWithoutLoggingInReturnsError() throws JsonProcessingException {
        receiveLineWithTimeout(inUser1);

        receiveLineWithTimeout(inUser2);

        String broadcastContent = "This should fail!";
        outUser1.println(Utils.objectToMessage(new BroadcastReq(broadcastContent)));
        outUser1.flush();

        String broadcastResp1 = receiveLineWithTimeout(inUser1);
        BroadcastResp respBroadcast1 = Utils.messageToObject(broadcastResp1);
        assertEquals(new BroadcastResp("ERROR", 6000), respBroadcast1, "Broadcast without login should return error 6000");

    }
}
