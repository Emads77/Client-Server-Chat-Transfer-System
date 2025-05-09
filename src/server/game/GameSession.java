package server.game;

import server.connection.ClientManager;
import shared.messages.GameResult;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameSession {
    private final String player1;
    private final String player2;
    private final ClientManager clientManager;
    private final ConcurrentHashMap<String, String> moves = new ConcurrentHashMap<>();
    private volatile boolean timedOut = false;
    private volatile String timedOutPlayer = null;

    private final ScheduledExecutorService timeoutScheduler = Executors.newScheduledThreadPool(1);

    public GameSession(String player1, String player2, ClientManager clientManager) {
        this.player1 = player1;
        this.player2 = player2;
        this.clientManager = clientManager;
        timeoutScheduler.schedule(() -> {
            if (moves.size() < 2) {
                timedOut = true;
                timedOutPlayer = moves.containsKey(player1) ? player2 : player1;
                try {
                    notifyPlayersOfTimeout();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 20, TimeUnit.SECONDS);
    }

    public boolean isComplete() {
        return moves.size() == 2 || timedOut;
    }

    public synchronized void makeMove(String player, String move) {
        moves.put(player, move);
        if (isComplete()) {
            timeoutScheduler.shutdownNow();
        }
    }

    private void notifyPlayersOfTimeout() throws IOException {
        String winner = moves.containsKey(player1) ? player1 : player2;

        clientManager.getClientResponseSender(player1).sendResponse(
                new GameResult(winner, null, "timeout")
        );
        clientManager.getClientResponseSender(player2).sendResponse(
                new GameResult(winner, null, "timeout")
        );

        clientManager.getGameManager().endGame(player1, player2);
    }

    public void notifyPlayersOfResult() throws IOException {
        String move1 = moves.get(player1);
        String move2 = moves.get(player2);
        String winner = determineWinner();

        clientManager.getClientResponseSender(player1).sendResponse(
                new GameResult(winner, move2, "normal")
        );

        clientManager.getClientResponseSender(player2).sendResponse(
                new GameResult(winner, move1, "normal")
        );

        clientManager.getGameManager().endGame(player1, player2);
    }

    public String determineWinner() {
        String move1 = moves.get(player1);
        String move2 = moves.get(player2);

        if (move1.equals(move2)) {
            return null; // Tie
        }
        if ((move1.equals("rock") && move2.equals("scissors")) ||
                (move1.equals("scissors") && move2.equals("paper")) ||
                (move1.equals("paper") && move2.equals("rock"))) {
            return player1;
        } else {
            return player2;
        }
    }
}
