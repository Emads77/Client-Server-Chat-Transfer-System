package server.commandhandler;

import server.connection.ClientManager;
import server.connection.ClientState;
import server.connection.PingHandler;
import shared.messages.FileDownloadStart;
import shared.messages.FileTransferReply;
import shared.messages.FileTransferReq;
import shared.messages.FileUploadStart;
import shared.network.SocketManager;

import java.io.IOException;

public class CommandHandler {//Invoker
    private final Command<String> loginCommand;
    private final Command<String> broadcastCommand;
    private final Command byeCommand;
    private final Command listUsersCommand;
    private final Command privateMessageCommand;
    private final Command startGame;
    private final Command<String> gameMoveCommand;
    private final Command<FileTransferReq> fileTransferRequestCommand;
    private final Command<FileTransferReply> fileTransferReplyCommand;
    private final Command<FileUploadStart> fileUploadStartCommand;
    private final Command<FileDownloadStart> fileDownloadStartCommand;


    public CommandHandler(ClientState clientState, ClientManager clientManager, ResponseSender responseSender, SocketManager socketManager) throws IOException {
        CommandContext context = new CommandContext(clientState, clientManager, responseSender);
        PingHandler pingHandler = new PingHandler(socketManager);

        this.loginCommand = new LoginCommand(context, pingHandler);
        this.broadcastCommand = new BroadcastCommand(context);
        this.byeCommand = new LogoutCommand(context);
        this.listUsersCommand = new ListUsersCommand(context);
        this.privateMessageCommand = new PrivateMessageCommand(context);
        this.startGame = new StartGameCommand(context);
        this.gameMoveCommand = new GameMoveCommand(context);
        this.fileTransferRequestCommand = new FileTransferRequestCommand(context);
        this.fileTransferReplyCommand = new FileTransferReplyCommand(context);
        this.fileUploadStartCommand = new FileUploadStartCommand(context);
        this.fileDownloadStartCommand = new FileDownloadStartCommand(context);
    }

    public void handleListUsers() throws IOException {
        listUsersCommand.execute(null);
    }

    public void handleLogin(String username) throws IOException {
        loginCommand.execute(username);
    }

    public void handleBroadcast(String message) throws IOException {
        broadcastCommand.execute(message);
    }

    public void handleBye() throws IOException {
        byeCommand.execute(null);
    }

    public void handlePrivateMessage(String input) throws IOException {
        privateMessageCommand.execute(input);
    }

    public void handleStartGame(String username) throws IOException {
        startGame.execute(username);
    }

    public void handleGameMove(String input) throws IOException {
        gameMoveCommand.execute(input);
    }

    public void handleFileTransferRequest(FileTransferReq message) throws IOException {
        fileTransferRequestCommand.execute(message);
    }

    public void handleFileTransferReply(FileTransferReply reply) throws IOException {
        fileTransferReplyCommand.execute(reply);
    }

    public void handleFileUploadStart(FileUploadStart start) throws IOException {
        fileUploadStartCommand.execute(start);
    }

    public void handleFileDownloadStart(FileDownloadStart start) throws IOException {
        fileDownloadStartCommand.execute(start);
    }

}
