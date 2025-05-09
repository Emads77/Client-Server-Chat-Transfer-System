# Protocol description

This client-server protocol describes the following scenarios:
- Setting up a connection between client and server.
- Broadcasting a message to all connected clients.
- Periodically sending heartbeat to connected clients.
- Disconnection from the server.
- Handling invalid messages.

In the description below, `C -> S` represents a message from the client `C` is send to server `S`. When applicable, `C` is extended with a number to indicate a specific client, e.g., `C1`, `C2`, etc. The keyword `others` is used to indicate all other clients except for the client who made the request. Messages can contain a JSON body. Text shown between `<` and `>` are placeholders.

The protocol follows the formal JSON specification, RFC 8259, available on https://www.rfc-editor.org/rfc/rfc8259.html

# 1. Establishing a connection

The client first sets up a socket connection to which the server responds with a welcome message. The client supplies a username on which the server responds with an OK if the username is accepted or an ERROR with a number in case of an error.
_Note:_ A username may only consist of characters, numbers, and underscores ('_') and has a length between 3 and 14 characters.

## 1.1 Happy flow

client.Client sets up the connection with server.
```
S -> C: READY {"version": "<server version number>"}
```
- `<server version number>`: the semantic version number of the server.

After a while when the client logs the user in:
```
C -> S: ENTER {"username":"<username>"}
S -> C: ENTER_RESP {"status":"OK"}
```

- `<username>`: the username of the user that needs to be logged in.
      To other clients (Only applicable when working on Level 2):
```
S -> others: JOINED {"username":"<username>"}
```

## 1.2 Unhappy flow
```
S -> C: ENTER_RESP {"status":"ERROR", "code":<error code>}
```      
Possible `<error code>`:

| Error code | Description                              |
|------------|------------------------------------------|
| 5000       | User with this name already exists       |
| 5001       | Username has an invalid format or length |      
| 5002       | Already logged in                        |

# 2. Broadcast message

Sends a message from a client to all other clients. The sending client does not receive the message itself but gets a confirmation that the message has been sent.

## 2.1 Happy flow

```
C -> S: BROADCAST_REQ {"message":"<message>"}
S -> C: BROADCAST_RESP {"status":"OK"}
```
- `<message>`: the message that must be sent.

Other clients receive the message as follows:
```
S -> others: BROADCAST {"username":"<username>","message":"<message>"}   
```   
- `<username>`: the username of the user that is sending the message.

## 2.2 Unhappy flow

```
S -> C: BROADCAST_RESP {"status": "ERROR", "code": <error code>}
```
Possible `<error code>`:

| Error code | Description            |
|------------|------------------------|
| 6000       | User is not logged in  |

# 3. Heartbeat message

Sends a ping message to the client to check whether the client is still active. The receiving client should respond with a pong message to confirm it is still active. If after 3 seconds no pong message has been received by the server, the connection to the client is closed. Before closing, the client is notified with a HANGUP message, with reason code 7000.

The server sends a ping message to a client every 10 seconds. The first ping message is send to the client 10 seconds after the client is logged in.

When the server receives a PONG message while it is not expecting one, a PONG_ERROR message will be returned.

## 3.1 Happy flow

```
S -> C: PING
C -> S: PONG
```     

## 3.2 Unhappy flow

```
S -> C: HANGUP {"reason": <reason code>}
[Server disconnects the client]
```      
Possible `<reason code>`:

| Reason code | Description      |
|-------------|------------------|
| 7000        | No pong received |    

```
S -> C: PONG_ERROR {"code": <error code>}
```
Possible `<error code>`:

| Error code | Description         |
|------------|---------------------|
| 8000       | Pong without ping   |    

# 4. Termination of the connection

When the connection needs to be terminated, the client sends a bye message. This will be answered (with a BYE_RESP message) after which the server will close the socket connection.

## 4.1 Happy flow
```
C -> S: BYE
S -> C: BYE_RESP {"status":"OK"}
[Server closes the socket connection]
```

Other, still connected clients, clients receive:
```
S -> others: LEFT {"username":"<username>"}
```

## 4.2 Unhappy flow

- None

# 5. Invalid message header

If the client sends an invalid message header (not defined above), the server replies with an unknown command message. The client remains connected.

Example:
```
C -> S: MSG This is an invalid message
S -> C: UNKNOWN_COMMAND
```

# 6. Invalid message body

If the client sends a valid message, but the body is not valid JSON, the server replies with a pars error message. The client remains connected.

Example:
```
C -> S: BROADCAST_REQ {"aaaa}
S -> C: PARSE_ERROR
```

# 7. List users

The user must be logged it to send LIST_USERS to the servers, to see all the connected online users


## 7.1 happy flow

```
C -> S: LIST_USERS_REQ 
S -> C: LIST_USERS {"users":["<username1>","<username2>",....]}
```
`users`: A JSON array containing usernames of all connected clients.


## 7.2 unhappy flow

```
S -> C: LIST_USERS_RESP {"status": "ERROR", "code": <error code>}

```
Possible `<error code>`:

| Reason code | Description           |
|-------------|-----------------------|
| 6000        | User is not logged in |


# 8. Sending Private Messages
To allow clients to send private messages to specific user


## 8.1 happy flow


Example:
```
C1 -> S: PRIVATE_MSG_REQ {"username":"<username>", "message":"<message>"
```

`<username>`: the user (recipient) you want to send the private message to

`<message>`: the message you want to send
```
S -> C: PRIVATE_MSG_RESP {"status":"OK"}

```

Other clients receive the message as follows:
```
S -> C2: PRIVATE_MSG_RECEIVED {"username":"<username>", "message":"<message>"}
```

```

`<username>`: the user (sender) that sent the message

`<message>`: the message received
```


## 8.2 unhappy flow
```
S -> C: PRIVATE_MSG_RESP {"status":"ERROR", "code":<error code>}
```

Possible `<error code>`:

| Reason code | Description              |
|-------------|--------------------------|
| 6000        | User is not logged in    |                   |   
| 9000        | username does not exist  |




# 9. Rock, Paper, Scissors Game

To allow a user to initiate an RPS game with another user



## 9.1  Starting a Game 



## 9.1.1 happy flow 
```
C1 -> S: START_GAME_REQ {"opponent":"<username>"}
```
`<opponent>: The username of the opponent.`

```
S -> C1: START_GAME_RESP {"status":"OK"}
```



Other clients receive the message as follows

Notification to Opponent:
```
S -> C2: START_GAME {"opponent":"<username>"}

```
`<opponent>: The username of the user who started the game (C1).
`



## 9.1.2 unhappy flow 
```
S -> C: START_GAME_RESP {"status":"ERROR", "code":<error code>}
```

Possible `<error code>`:

| Reason code | Description                                          |
|-------------|------------------------------------------------------|
| 6000        | User is not logged in                                |                   |   
| 9000        | username does not exist                              |
| 10000       | Another game is already running between other users. |


# 9.2 Making a Move
To allow users to make their move during the RPS game:

## 9.2.1 happy flow

Client Request
```
C1 -> S: GAME_MOVE_REQ {"choice":"<rock|paper|scissors>"}
```

```
S -> C: GAME_MOVE_RESP {"status":"OK"}
```


## 9.2.2 unhappy flow 

```
S -> C: GAME_MOVE_RESP {"status":"ERROR", "code":<error code>}

```

| Reason code | Description                                    |
|-------------|------------------------------------------------|
| 6000        | User is not logged in                          |                   |   
| 9000        | username does not exist                        |
| 10001       | Invalid choice (not rock, paper, or scissors)  |


# 9.3 Game result 

When the server receives both users' moves, it determines the result and sends the outcome to both users

Notification to Both Players:
```
S -> C: GAME_RESULT {"winner":"<username>|Null", "opponent_choice":"<rock|paper|scissors>|Null","reason":"normal|timeout"}

```

  `winner`: The username of the winner, or `Null` if it's a tie.
  `opponent_choice`: The opponent’s chosen move, or `Null` if the opponent didn’t make a move in time.
  `reason`:
  `normal`: Both players made their moves in time.
  `timeout`: One player failed to make a move within the time limit.



# **10. File Transmission**
This section describes the file transmission functionality, which involves sending files between clients via the server.

## **10.1 Initiating File Transfer**

Initiate the transfer of a file from the sender to the recipient.

### **10.1.1 Happy Flow**

**Step 1:** Sender requests a file transfer.
```
C1 -> S: FILE_TRANSFER_REQ {"receiver":"<username>", "filename":"<file_name>", "size":<file_size>}
```
- `<username>`: The intended recipient of the file.
- `<file_name>`: The name of the file being sent.
- `<file_size>`: The size of the file in bytes.

**Step 2:** Server forwards the request to the intended recipient.
```
S -> C2: FILE_TRANSFER_OFFER {"sender":"<username>", "filename":"<file_name>", "size":<file_size>,"sessionId": "<session_id>"}
```
- `<username>`: The sender  of the file.
- `<file_name>`: The name of the file being received.
- `<file_size>`: The size of the file in bytes.
- `<session_id>`: A unique identifier generated for this transfer session.


**Step 3:** Recipient responds with confirmation or rejection.
```
C2 -> S: FILE_TRANSFER_REPLY {"status":"<confirm|reject>"}
```
- `<status>`: `confirm` if the recipient agrees to receive the file, `reject` otherwise.

**Step 4:** Server informs the sender of the recipient’s decision.

```
  S -> C1: FILE_TRANSFER_RESP {"status":"OK"}
  ```

### **10.1.2 Unhappy Flow**

- If the recipient rejects:
```
  S -> C1: FILE_TRANSFER_RESP {"status":"ERROR", "code":<error code>}
```
Possible `<error code>`:

| Error Code | Description                                  |
|------------|----------------------------------------------|
| 6000       | sender is not logged in                      |
| 9000       | Recipient is not logged in or does not exist |
| 11000      | The recipient rejected the file transfer     |
| 11500      | Invalid response (not rejected or accepted)  |

---



## **10.2 Uploading the File**

Once the recipient confirms the transfer, the sender opens a new socket connection to the server to upload the file data.
The server uses a separate data channel to receive the file.
### **10.2.1 Happy Flow**

**Step 1:** Sender requests to start the upload.

```
C1 -> S: FILE_UPLOAD_START {"sessionId": "<session_id>"}
```

**Step 2:** Server assigns and confirms the upload port.

```
S -> C1: FILE_UPLOAD_READY {"port": <upload_port>, "sessionId": "<session_id>"}

```

`<port_number>`: The dynamically allocated port where the server is listening for file data


**Step 3:** The sender opens a data connection (a separate socket connection) to the allocated port and streams the file
(read from its local storage).

**Step 4:** The server accepts the connection, writes the incoming bytes to a file, and computes a checksum.

**Step 5:** Server confirms successful upload on the control channel.
After successfully saving the file and calculating the checksum, the server sends:
```
S -> C1: FILE_UPLOAD_RESP {"status": "OK", "checksum": "<sha256_checksum>"}
```

`<sha256_checksum>`: The SHA-256 hash of the uploaded file, computed during upload and stored in the transfer session.

### **10.2.2 Unhappy Flow**

```
S -> C1: FILE_UPLOAD_RESP {"status":"ERROR", "code":<error code>}
```
Possible `<error code>`:

| Error Code | Description                                    |
|------------|------------------------------------------------|
| 12000      | File upload failed                             |
| 12500      | Checksum calculation failed                    |
| 13000      | Server failed to assign a port for file upload |
---



## **10.3 Downloading the File**

After the file is successfully uploaded, the recipient can request to download the file from the server.

### **10.3.1 Happy Flow**

**Step 1:** Recipient requests to download the file.
```
C2 -> S: FILE_DOWNLOAD_START {"sessionId": "<session_id>"}
```
`<seesion_id>`:the session ID of the desired file.


**Step 2:** Server assigns a port for the download and retrieves the stored checksum from the session.

```
S -> C2: FILE_DOWNLOAD_READY {"port": <download_port>, "sessionId": "<session_id>", "checksum": "<sha256_checksum>"}
```

`<download_port>`: A dynamically allocated port dedicated to the download data connection.
`<sha256_checksum>`: The checksum that was computed during the upload and stored in the session.


**Step 3:**  File Data Transfer
- The recipient opens a new connection to the allocated download port and downloads the file data.
- The file is transferred from the server’s disk to the recipient’s machine.


### **10.3.2 Unhappy Flow**

Possible `<error code>`:

| Error Code | Description                                      |
|------------|--------------------------------------------------|
| 12010      | File not found on the server                     |
| 12011      | Checksum verification failed                     |
| 12012      | File download failed                             |
| 13000      | Server failed to assign a port for file download |






