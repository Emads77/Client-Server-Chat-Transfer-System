package client.commandhandler.commands;

import java.io.IOException;

public interface Command {
    void execute(String[] arguments) throws IOException;}

