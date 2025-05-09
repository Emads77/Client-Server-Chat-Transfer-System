// File: client/UserInputHandler.java
package client.commandhandler;

import client.commandhandler.commands.Command;

import java.io.IOException;
import java.util.Scanner;

public class UserInputHandler {
    private final CommandRegistry registry;

    public UserInputHandler(CommandRegistry registry) {
        this.registry = registry;
    }

    public void processUserInput(Scanner scanner) {
        try {
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }
                String[] tokens = input.split(" ", 3); // Split into at most 3 parts
                String commandKey = tokens[0].toLowerCase();
                String[] arguments;
                if (tokens.length > 1) {
                    // If there's more after the command key, split into arguments
                    if (tokens.length == 2) {
                        arguments = new String[]{tokens[1]};
                    } else {
                        arguments = new String[]{tokens[1], tokens[2]};
                    }
                } else {
                    arguments = new String[0];
                }
                Command command = registry.getCommand(commandKey);
                command.execute(arguments);
            }
        } catch (IOException e) {
            System.err.println("Error processing input: " + e.getMessage());
        }
    }
}
