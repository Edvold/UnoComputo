package common.src.main;

public class GameState {

    public PlayerState currentPlayerName;
    public Card topCard;
    public PlayerState[] turnOrder; // players in order of their turns (not containing current)
    public int streak;
    public boolean saidUNO;

    public GameState() {
        currentPlayerName = null;
        topCard = null;
        turnOrder = null;
        streak = 0;
        saidUNO = false;
    }

    public GameState(PlayerState currentPlayerName, Card topCard, PlayerState[] turnOrder, int streak,
            boolean saidUNO) {
        this.currentPlayerName = currentPlayerName;
        this.topCard = topCard;
        this.turnOrder = turnOrder;
        this.streak = streak;
        this.saidUNO = saidUNO;
    }

    public static class PlayerState {
        final public String userName;
        public int handSize;

        public PlayerState(String name, int size) {
            userName = name;
            handSize = size;
        }

        public String toString() {
            return userName + ": " + handSize + " cards";
        }
    }
}
