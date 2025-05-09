package client.messageprocessing.messagehandler;

import shared.messages.ParseError;

public class ParseErrorHandler implements MessageHandler<ParseError> {

    @Override
    public void handle(ParseError message) {
        System.err.println("Parse error received from server: " + message);
    }
}
