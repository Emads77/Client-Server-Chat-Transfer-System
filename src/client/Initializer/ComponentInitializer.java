package client.Initializer;

import client.commandhandler.CommandRegistry;
import client.commandhandler.UserInputHandler;
import client.commandhandler.commands.*;
import client.messageprocessing.ServerMessageProcessor;
import client.services.FileTransferManager;
import shared.network.SocketManager;

public class ComponentInitializer {
    private final SocketManager socketManager;
    private final FileTransferManager fileTransferManager;

    public ComponentInitializer(SocketManager socketManager) {
        this.socketManager = socketManager;
        this.fileTransferManager = new FileTransferManager();
    }

    public InitializedComponents initialize() {
        CommandRegistry registry = new CommandRegistry();
        registry.register("login", new LoginCommand(socketManager));
        registry.register("broadcast", new BroadcastCommand(socketManager));
        registry.register("logout", new LogoutCommand(socketManager));
        registry.register("listusers", new ListUsersCommand(socketManager));
        registry.register("sendprivate", new SendPrivateCommand(socketManager)); // Single-word command
        registry.register("startgame", new StartGameCommand(socketManager));
        registry.register("move", new MakeMoveCommand(socketManager));
        registry.register("upload", new FileTransferReqCommand(socketManager,fileTransferManager));
        registry.register("confirm", new FileTransferReplyCommand(socketManager,fileTransferManager,"confirm"));
        registry.register("reject", new FileTransferReplyCommand(socketManager,fileTransferManager,"reject"));
        registry.register("download", new FileDownloadCommand(socketManager,fileTransferManager));

        registry.register("help", (argument) -> client.ui.MenuDisplay.showMainMenu());

        UserInputHandler userInputHandler = new UserInputHandler(registry);

        ServerMessageProcessor messageProcessor = new ServerMessageProcessor(socketManager,fileTransferManager);
        client.messageprocessing.ServerMessageHandler messageHandler =
                new client.messageprocessing.ServerMessageHandler(socketManager, messageProcessor);

        return new InitializedComponents(userInputHandler, messageHandler);
    }
}
