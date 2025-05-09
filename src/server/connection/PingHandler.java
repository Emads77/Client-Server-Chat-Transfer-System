package server.connection;

import shared.Utils;
import shared.messages.Hangup;
import shared.messages.Ping;
import shared.messages.PongError;
import shared.network.SocketManager;

import java.io.IOException;

public class PingHandler {
        private final SocketManager socketManager;
        private volatile boolean pongReceived = true;
        private volatile boolean wasPingSent = false;
        private static final long PING_INTERVAL = 10000;//SHOULD BE 10000
        private static final long PONG_TIMEOUT = 3000;
        private volatile boolean running = true;

        public PingHandler(SocketManager socketManager) {
            this.socketManager = socketManager;
        }

        public synchronized void handlePong() throws IOException {
            System.out.println("Handling PONG. Was PING sent? " + wasPingSent);
            if (!wasPingSent) {
                System.err.println("PONG received without preceding PING.");
                socketManager.sendMessage(Utils.objectToMessage(new PongError(8000)));
            } else {
                pongReceived = true;
                wasPingSent = false;
                System.out.println("PONG successfully received and acknowledged.");
                System.out.println("Received PONG. wasPingSent=" + wasPingSent);

            }
        }
        private synchronized void sendPing() throws IOException {
            wasPingSent = true;
            pongReceived = false;
            Ping pingMessage = new Ping();
            String serializedPing = Utils.objectToMessage(pingMessage);
            socketManager.sendMessage(serializedPing);
            System.out.println("Sent PING message.");
            System.out.println("Sent PING. wasPingSent=" + wasPingSent + ", pongReceived=" + pongReceived);

        }

    public void startHeartbeat() {
        new Thread(() -> {
            try {
                Thread.sleep(PING_INTERVAL);

                while (running) {
                    sendPing();

                    Thread.sleep(PONG_TIMEOUT);

                    if (!pongReceived) {
                        System.err.println("No PONG received. Closing connection.");
                        socketManager.sendMessage(Utils.objectToMessage(new Hangup(7000)));
                        socketManager.closeConnection();
                        break;
                    }
                    Thread.sleep(PING_INTERVAL - PONG_TIMEOUT);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Heartbeat error: " + e.getMessage());
            }
        }).start();
    }


    public void stopHeartbeat() {
            running = false;
            System.out.println("Heartbeat stopped.");
        }
    }


