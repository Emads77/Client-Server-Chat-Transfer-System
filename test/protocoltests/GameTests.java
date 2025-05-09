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

class GameTests {

    private final static Properties PROPS = new Properties();

    private Socket socketUser1, socketUser2;
    private BufferedReader inUser1, inUser2;
    private PrintWriter outUser1, outUser2;

    private final static int MAX_DELTA_ALLOWED_MS = 500;

    @BeforeAll
    static void setupAll() throws IOException {
        InputStream in = GameTests.class.getResourceAsStream("testconfig.properties");
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
        if (socketUser1 != null) socketUser1.close();
        if (socketUser2 != null) socketUser2.close();
    }


    @AfterEach
    void cleanupGames() throws JsonProcessingException {
        if (socketUser1 != null && inUser1 != null && outUser1 != null) {
            outUser1.println(Utils.objectToMessage(new GameMoveReq("rock")));
            outUser1.flush();
            receiveLineWithTimeout(inUser1);
        }
        if (socketUser2 != null && inUser2 != null && outUser2 != null) {
            outUser2.println(Utils.objectToMessage(new GameMoveReq("scissors")));
            outUser2.flush();
            receiveLineWithTimeout(inUser2);
        }
    }



    private String receiveLineWithTimeout(BufferedReader reader) {
        return assertTimeoutPreemptively(ofMillis(MAX_DELTA_ALLOWED_MS), reader::readLine);
    }

    @Test
    void tc81StartGameWithValidOpponentWorks() throws JsonProcessingException {
        // User 1 logs in
        receiveLineWithTimeout(inUser1);
        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        receiveLineWithTimeout(inUser1);

        // User 2 logs in
        receiveLineWithTimeout(inUser2);
        outUser2.println(Utils.objectToMessage(new Enter("user2")));
        outUser2.flush();
        receiveLineWithTimeout(inUser2);


        String joinedMessage = receiveLineWithTimeout(inUser1);
        Joined joined = Utils.messageToObject(joinedMessage);
        assertEquals(new Joined("user2"), joined);

        outUser1.println(Utils.objectToMessage(new StartGameReq("user2")));
        outUser1.flush();

        String user1Response = receiveLineWithTimeout(inUser1);
        StartGameResp resp = Utils.messageToObject(user1Response);
        assertEquals(new StartGameResp("OK", 0), resp);

        String user2Notification = receiveLineWithTimeout(inUser2);
        StartGame gameNotification = Utils.messageToObject(user2Notification);
        assertEquals(new StartGame("user1"), gameNotification);
    }


    @Test
    void tc82StartGameWithNonexistentOpponentFails() throws JsonProcessingException {
        // user 1 logs in
        receiveLineWithTimeout(inUser1);
        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        receiveLineWithTimeout(inUser1);

        outUser1.println(Utils.objectToMessage(new StartGameReq("nonexistent")));
        outUser1.flush();

        String user1Response = receiveLineWithTimeout(inUser1);
        StartGameResp resp = Utils.messageToObject(user1Response);
        assertEquals(new StartGameResp("ERROR", 9000), resp);
    }

    @Test
    void tc83GameMoveWithValidChoiceWorks() throws JsonProcessingException {
        tc81StartGameWithValidOpponentWorks();

        outUser1.println(Utils.objectToMessage(new GameMoveReq("rock")));
        outUser1.flush();

        String user1Response = receiveLineWithTimeout(inUser1);
        GameMoveResp moveResp = Utils.messageToObject(user1Response);
        assertEquals(new GameMoveResp("OK", 0), moveResp);
    }

    @Test
    void tc84GameMoveWithInvalidChoiceFails() throws JsonProcessingException {
        tc81StartGameWithValidOpponentWorks();

        outUser1.println(Utils.objectToMessage(new GameMoveReq("invalid_choice")));
        outUser1.flush();

        String user1Response = receiveLineWithTimeout(inUser1);
        GameMoveResp moveResp = Utils.messageToObject(user1Response);
        assertEquals(new GameMoveResp("ERROR", 10001), moveResp);
    }



    //the server needs to be restarted to  pass this test
    @Test
    void tc85GameResultAfterBothMoves() throws JsonProcessingException {
        // Setup: User 1 and User 2 start a game
        tc81StartGameWithValidOpponentWorks();

        // User 1 makes a move
        outUser1.println(Utils.objectToMessage(new GameMoveReq("rock")));
        outUser1.flush();
        String user1MoveResponse = receiveLineWithTimeout(inUser1);
        GameMoveResp moveResp1 = Utils.messageToObject(user1MoveResponse);
        assertEquals(new GameMoveResp("OK", 0), moveResp1);

        // User 2 makes a move
        outUser2.println(Utils.objectToMessage(new GameMoveReq("scissors")));
        outUser2.flush();
        String user2MoveResponse = receiveLineWithTimeout(inUser2);
        GameMoveResp moveResp2 = Utils.messageToObject(user2MoveResponse);
        assertEquals(new GameMoveResp("OK", 0), moveResp2);

        // Both users receive the game result
        String user1Result = receiveLineWithTimeout(inUser1);
        GameResult result1 = Utils.messageToObject(user1Result);
        assertEquals(new GameResult("user1", "scissors", "normal"), result1);

        String user2Result = receiveLineWithTimeout(inUser2);
        GameResult result2 = Utils.messageToObject(user2Result);
        assertEquals(new GameResult("user1", "rock", "normal"), result2);
    }


    @Test
    void tc86GameTimeout() throws JsonProcessingException {
        // Setup: User 1 and User 2 start a game
        tc81StartGameWithValidOpponentWorks();

        // User 1 makes a move
        outUser1.println(Utils.objectToMessage(new GameMoveReq("rock")));
        outUser1.flush();
        receiveLineWithTimeout(inUser1); // OK response

        // Wait for timeout to occur
        try {
            Thread.sleep(5000); // Wait 31 seconds to ensure timeout
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Both users receive the timeout result
        String user1Result = receiveLineWithTimeout(inUser1);
        GameResult result1 = Utils.messageToObject(user1Result);
        assertEquals(new GameResult("user1", null, "timeout"), result1);

        String user2Result = receiveLineWithTimeout(inUser2);
        GameResult result2 = Utils.messageToObject(user2Result);
        assertEquals(new GameResult("user1", null, "timeout"), result2);
    }

    @Test
    void tc87BroadcastWithoutLoginReturnsError6000() throws IOException {
        String readyMessage = receiveLineWithTimeout(inUser1);
        Ready ready = Utils.messageToObject(readyMessage);
        assertEquals(new Ready("1.6.0"), ready, "User1 should receive READY message with version 1.6.0");

        String broadcastContent = "This should fail!";
        outUser1.println(Utils.objectToMessage(new BroadcastReq(broadcastContent)));
        outUser1.flush();

        String broadcastResp = receiveLineWithTimeout(inUser1);
        BroadcastResp respBroadcast = Utils.messageToObject(broadcastResp);
        assertEquals("ERROR", respBroadcast.status(), "Broadcast without login should return ERROR status");
        assertEquals(6000, respBroadcast.code(), "Error code should be 6000 for unauthorized broadcast attempt");

    }

}
