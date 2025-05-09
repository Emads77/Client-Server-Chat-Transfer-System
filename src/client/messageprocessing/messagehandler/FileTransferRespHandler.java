package client.messageprocessing.messagehandler;

import client.util.ErrorMessageHandler;
import shared.messages.FileTransferResp;


public class FileTransferRespHandler implements MessageHandler<FileTransferResp> {
    @Override
    public void handle(FileTransferResp message) {
        if ("OK".equals(message.status())) {
            System.out.println("Request sent successfully.");
        } else {
            String errorMessage = ErrorMessageHandler.getErrorMessage(message.code());
            System.out.println("Request failed: " + errorMessage);
        }
    }
}
