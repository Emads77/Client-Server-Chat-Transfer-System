package client.messageprocessing.messagehandler;

import shared.messages.FileUploadResp;

import java.io.IOException;

public class FileUploadRespHandler implements MessageHandler<FileUploadResp> {
    @Override
    public void handle(FileUploadResp message) throws IOException {
        System.out.println("The checksum of the uploaded file is " + message.checksum());
    }
}
