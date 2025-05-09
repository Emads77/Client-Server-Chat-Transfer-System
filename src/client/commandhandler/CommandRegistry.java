// File: client/commands/CommandRegistry.java
package client.commandhandler;

import client.commandhandler.commands.Command;

import java.util.HashMap;
import java.util.Map;

/**
 * CommandRegistry is responsible for registering and retrieving Command implementations
 * based on command keys (strings).
 */
public class CommandRegistry {
    private final Map<String, Command> commandMap = new HashMap<>();

    /**
     * Registers a command with the given key.
     *
     * @param key     The command keyword (e.g., "login", "sendprivate").
     * @param command The Command implementation associated with the key.
     */
    public void register(String key, Command command) {
        if (key == null || command == null) {
            throw new IllegalArgumentException("Command key and command cannot be null.");
        }
        commandMap.put(key.toLowerCase(), command);
    }

    /**
     * Retrieves the Command associated with the given key.
     * If the key is not found, returns a default Command that notifies the user of an unknown command.
     *
     * @param key The command keyword entered by the user.
     * @return The corresponding Command implementation.
     */
    public Command getCommand(String key) {
        if (key == null) {
            return getDefaultCommand();
        }

        Command command = commandMap.get(key.toLowerCase());
        return (command != null) ? command : getDefaultCommand();
    }

    /**
     * Provides a default Command implementation for unknown commands.
     *
     * @return A Command that notifies the user about the unknown command.
     */
    private Command getDefaultCommand() {
        return (arguments) -> System.out.println("Unknown command. Type 'help' for a list of commands.");
    }
}
