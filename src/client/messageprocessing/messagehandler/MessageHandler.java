package client.messageprocessing.messagehandler;

import java.io.IOException;

public interface MessageHandler<T> {//strategy pattern
    void handle(T message) throws IOException;
}
