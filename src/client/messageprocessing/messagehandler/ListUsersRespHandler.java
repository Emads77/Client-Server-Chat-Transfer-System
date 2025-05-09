// File: client/messageprocessing/messagehandler/ListUsersRespHandler.java
package client.messageprocessing.messagehandler;

import client.util.ErrorMessageHandler;
import shared.messages.ListUsersResp;

public class ListUsersRespHandler implements MessageHandler<ListUsersResp> {

    @Override
    public void handle(ListUsersResp resp) {
        if ("ERROR".equalsIgnoreCase(resp.status())) {
            String errorMessage = ErrorMessageHandler.getErrorMessage(resp.code());
            System.out.println("Listing users failed: " + errorMessage);
        }
    }
}
