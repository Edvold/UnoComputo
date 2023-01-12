package common.src.main;

public class GameState {
    
    final public PlayerState currentPlayerName;
    final public ACard topCard;
    final public PlayerState[] turnOrder; //players in order of their turns (not containing current)
    final public int streak;
    
    public GameState (PlayerState currentPlayerName, ACard topCard, PlayerState[] turnOrder, int streak){
        this.currentPlayerName = currentPlayerName;
        this.topCard = topCard;
        this.turnOrder = turnOrder;
        this.streak = streak;
    }

    public static class PlayerState {
        final public String userName;
        final public int handSize;

        public PlayerState(String name, int size) {
            userName = name;
            handSize = size;
        }
    }
}
