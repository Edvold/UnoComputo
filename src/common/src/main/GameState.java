package common.src.main;

public class GameState {
    
    private String currentPlayerName;
    private ACard topCard;
    private IPlayer[] players;
    private byte streak;
    private ACard[] hand; //maybe this should just be directly through game and player
}
