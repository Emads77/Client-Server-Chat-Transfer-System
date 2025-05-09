package shared.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SocketManager {
    private final BufferedReader input;
    private final BufferedWriter output;

    public SocketManager(BufferedReader input, BufferedWriter output) {
        this.input = input;
        this.output = output;
    }

    public String readMessage() throws IOException{
        return input.readLine();
    }

    public void sendMessage(String message) throws IOException {
        output.write(message + "\n");
        output.flush();
    }

    public void closeConnection() throws IOException {
        input.close();
        output.close();
    }

}
