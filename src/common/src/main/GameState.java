package common.src.main;

public class GameState {
    
    final public String currentPlayerName;
    final public ACard topCard;
    final public IPlayer[] players;
    final public byte streak;
    final public ACard[] hand; //maybe this should just be directly through game and player

    public GameState (String currentPlayerName, ACard topCard, IPlayer[] players, byte streak, ACard[] hand){
        this.currentPlayerName = currentPlayerName;
        this.topCard = topCard;
        this.players = players;
        this.streak = streak;
        this.hand = hand;
    }
}
