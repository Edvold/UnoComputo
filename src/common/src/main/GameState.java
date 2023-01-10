package common.src.main;

public class GameState {
    
    final public String currentPlayerName;
    final public ACard topCard;
    final public IPlayer[] players;
    final public byte streak;

    public GameState (String currentPlayerName, ACard topCard, IPlayer[] players, byte streak){
        this.currentPlayerName = currentPlayerName;
        this.topCard = topCard;
        this.players = players;
        this.streak = streak;
    }
}
