
package client.messageprocessing.messagehandler;

import shared.messages.EnterResp;
import client.util.ErrorMessageHandler;

    public class EnterRespHandler implements MessageHandler<EnterResp> {

        @Override
        public void handle(EnterResp message) {
            if ("OK".equals(message.status())) {
                System.out.println("Login successful!");
            } else {
                String errorMessage = ErrorMessageHandler.getErrorMessage(message.code());
                System.out.println("Login failed: " + errorMessage);
            }
        }
    }

