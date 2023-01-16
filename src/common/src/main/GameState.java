package common.src.main;

public class GameState {
    
    public PlayerState currentPlayerName;
    public Card topCard;
    public PlayerState[] turnOrder; //players in order of their turns (not containing current)
    public int streak;
    public boolean saidUNO;
    
    public GameState() {
        currentPlayerName = null;
        topCard = null;
        turnOrder = null;
        streak = 0;
        saidUNO = false;
    }
    public GameState (PlayerState currentPlayerName, Card topCard, PlayerState[] turnOrder, int streak, boolean saidUno){
        this.currentPlayerName = currentPlayerName;
        this.topCard = topCard;
        this.turnOrder = turnOrder;
        this.streak = streak;
        this.saidUNO = saidUno;
    }

    public static class PlayerState {
        final public String userName;
        final public int handSize;

        public PlayerState(String name, int size) {
            userName = name;
            handSize = size;
        }

        public String toString() {
            return userName + " with " + handSize + " cards on their hand";
        }
    }
}
