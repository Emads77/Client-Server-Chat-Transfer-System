package server.game;

import server.connection.ClientManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    // Singleton design pattern: only one instance


    private static final GameManager instance = new GameManager();
    private final Map<String, GameSession> activeGames = new HashMap<>();


    private GameManager() {

    }

    public static GameManager getInstance() {
        return instance;
    }

    /**
     * Checks if a game is active between any two players.
     * @return true if any game is active, false otherwise.
     */
    public synchronized boolean isGameActive() {
        return !activeGames.isEmpty();
    }

    /**
     * Checks if a specific player is in an active game.
     * @param player1 The username of one player.
     * @param player2 The username of the other player.
     * @return true if a game is active between the two players, false otherwise.
     */
    public synchronized boolean isGameActiveBetween(String player1, String player2) {
        GameSession session1 = activeGames.get(player1);
        GameSession session2 = activeGames.get(player2);
        return session1 != null && session1.equals(session2);
    }

    /**
     * Starts a game between two players, if not already active.
     * @param player1 The username of one player.
     * @param player2 The username of the other player.
     * @param clientManager The ClientManager for game session communication.
     */
    public synchronized void startGame(String player1, String player2, ClientManager clientManager) {
        if (isGameActiveBetween(player1, player2)) {
            throw new IllegalStateException("A game is already active between " + player1 + " and " + player2);
        }
        GameSession session = new GameSession(player1, player2, clientManager); // Updated to pass ClientManager
        activeGames.put(player1, session);
        activeGames.put(player2, session);
    }

    /**
     * Ends a game between two players and removes it from active games.
     * @param player1 The username of one player.
     * @param player2 The username of the other player.
     */
    public synchronized void endGame(String player1, String player2) {
        GameSession session = activeGames.get(player1);
        if (session != null && session.equals(activeGames.get(player2))) {
            activeGames.remove(player1);
            activeGames.remove(player2);
        }
    }

    /**
     * Retrieves the game session for a given player.
     * @param player The username of the player.
     * @return The active GameSession or null if none exists.
     */
    public synchronized GameSession getGame(String player) {
        return activeGames.get(player);
    }


}
