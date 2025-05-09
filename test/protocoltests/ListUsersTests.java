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
import java.util.List;
import java.util.Properties;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class ListUsersTests {

    private final static Properties PROPS = new Properties();

    private Socket socketUser1, socketUser2;
    private BufferedReader inUser1, inUser2;
    private PrintWriter outUser1, outUser2;

    private final static int MAX_DELTA_ALLOWED_MS = 500;

    @BeforeAll
    static void setupAll() throws IOException {
        InputStream in = ListUsersTests.class.getResourceAsStream("testconfig.properties");
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

    private String receiveLineWithTimeout(BufferedReader reader) {
        return assertTimeoutPreemptively(ofMillis(MAX_DELTA_ALLOWED_MS), reader::readLine);
    }

    @Test
    void tc61ListUsersWithSingleConnectedUserReturnsSelf() throws JsonProcessingException {
        receiveLineWithTimeout(inUser1);
        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        receiveLineWithTimeout(inUser1);

        outUser1.println(Utils.objectToMessage(new ListUsersReq()));
        outUser1.flush();

        String serverResponse = receiveLineWithTimeout(inUser1);
        ListUsersResp listUsersResp = Utils.messageToObject(serverResponse);
        assertEquals("OK", listUsersResp.status());
        assertEquals(0, listUsersResp.code());

        serverResponse = receiveLineWithTimeout(inUser1);
        ListUsers listUsers = Utils.messageToObject(serverResponse);
        assertEquals(List.of("user1"), listUsers.users());
    }


    @Test
    void tc62ListUsersWithMultipleConnectedUsersReturnsAllUsers() throws JsonProcessingException {
        receiveLineWithTimeout(inUser1);
        receiveLineWithTimeout(inUser2);

        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        receiveLineWithTimeout(inUser1);


        outUser2.println(Utils.objectToMessage(new Enter("user2")));
        outUser2.flush();
        receiveLineWithTimeout(inUser2);

        String joinedMessage = receiveLineWithTimeout(inUser1);
        Joined joined = Utils.messageToObject(joinedMessage);
        assertEquals(new Joined("user2"), joined, "Expected JOINED message about user2");

        outUser1.println(Utils.objectToMessage(new ListUsersReq()));
        outUser1.flush();

        String serverResponse = receiveLineWithTimeout(inUser1);
        ListUsersResp listUsersResp = Utils.messageToObject(serverResponse);
        assertEquals("OK", listUsersResp.status());
        assertEquals(0, listUsersResp.code());

        // Expect the actual list of users
        serverResponse = receiveLineWithTimeout(inUser1);
        ListUsers listUsers = Utils.messageToObject(serverResponse);
        assertEquals(new ListUsers(java.util.List.of("user1", "user2")), listUsers);
    }


    @Test
    void tc63ListUsersAfterUserLogsOut() throws JsonProcessingException, IOException {
        receiveLineWithTimeout(inUser1);
        receiveLineWithTimeout(inUser2);

        outUser1.println(Utils.objectToMessage(new Enter("user1")));
        outUser1.flush();
        receiveLineWithTimeout(inUser1); // ENTER_RESP {"status":"OK","code":0}

        outUser2.println(Utils.objectToMessage(new Enter("user2")));
        outUser2.flush();
        receiveLineWithTimeout(inUser2); // ENTER_RESP {"status":"OK","code":0}

        String joinedMessage = receiveLineWithTimeout(inUser1); // JOINED {"username":"user2"}
        Joined joined = Utils.messageToObject(joinedMessage);
        assertEquals(new Joined("user2"), joined);

        outUser2.println(Utils.objectToMessage(new Bye()));
        outUser2.flush();
        receiveLineWithTimeout(inUser2);

        String leftMessage = receiveLineWithTimeout(inUser1);
        Left left = Utils.messageToObject(leftMessage);
        assertEquals(new Left("user2"), left);

        outUser1.println(Utils.objectToMessage(new ListUsersReq()));
        outUser1.flush();

        String listUsersRespMessage = receiveLineWithTimeout(inUser1);
        ListUsersResp listUsersResp = Utils.messageToObject(listUsersRespMessage);
        assertEquals(new ListUsersResp("OK", 0), listUsersResp);

        String listUsersMessage = receiveLineWithTimeout(inUser1);
        ListUsers listUsers = Utils.messageToObject(listUsersMessage);
        assertEquals(new ListUsers(java.util.List.of("user1")), listUsers);
    }

    @Test
    void tc64ListUsersRequestBeforeLoginReturnsError() throws JsonProcessingException {
        receiveLineWithTimeout(inUser1);

        outUser1.println(Utils.objectToMessage(new ListUsersReq()));
        outUser1.flush();

        String serverResponse = receiveLineWithTimeout(inUser1);
        ListUsersResp response = Utils.messageToObject(serverResponse);

        assertEquals(new ListUsersResp("ERROR", 6000), response);
    }

    @Test
    void tc65InvalidListUsersRequestReturnsParseError() throws JsonProcessingException {
        receiveLineWithTimeout(inUser1);

        outUser1.println("LIST_USERS_REQ INVALID {}");
        outUser1.flush();

        String serverResponse = receiveLineWithTimeout(inUser1);
        ParseError parseError = Utils.messageToObject(serverResponse);

        assertEquals(new ParseError(), parseError);
    }
}
