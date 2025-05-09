// File: client/messageprocessing/messagehandler/ListUsersHandler.java
package client.messageprocessing.messagehandler;

import shared.messages.ListUsers;

import java.util.List;

public class ListUsersHandler implements MessageHandler<ListUsers> {

    @Override
    public void handle(ListUsers message) {
        List<String> users = message.users();
        if (users.isEmpty()) {
            System.out.println("No users are currently connected.");
        } else {
            System.out.println("Connected users:");
            users.forEach(user -> System.out.println("- " + user));
        }
    }
}
