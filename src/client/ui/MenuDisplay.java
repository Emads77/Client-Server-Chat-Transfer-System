// File: client/ui/MenuDisplay.java
package client.ui;

public class MenuDisplay {

    /**
     * Displays the main menu with available commands.
     */
    public static void showMainMenu() {
        System.out.println("\nAvailable commands:");
        System.out.println("1.  login <username>                         - Login to the chat");
        System.out.println("2.  broadcast <message>                      - Send a message to all users");
        System.out.println("3.  sendprivate <username> <message>         - Send a private message to a specific user");
        System.out.println("4.  startgame <username>                     - Invite a user to play Rock-Paper-Scissors");
        System.out.println("5.  move <rock|paper|scissors>               - Make a move in an ongoing game");
        System.out.println("6.  listusers                                - List all connected users");
        System.out.println("7.  logout                                   - Exit the chat");
        System.out.println("8.  upload                                   - Upload a file");
        System.out.println("9.  confirm/reject                           - Confirm or reject a file transfer request");
        System.out.println("10. download <sessionId>                     - Download a file");
        System.out.println("11. help                                     - Show this menu");
        System.out.println();
    }
}