package server.commandhandler;

import java.io.IOException;

public interface Command<T> {//Command pattern - command interface

    void execute(T input) throws IOException;
}

