package common.src.main;

public class GameStateUpdate {
    public final GameState gameState;
    public final Card[] possibleCards;
    public final Card[] hand;
    public final PlayerAction[] possibleActions;

    public GameStateUpdate(GameState gameState, Card[] possibleCards, Card[] hand, PlayerAction[] possibleActions) {
        this.gameState = gameState;
        this.possibleCards = possibleCards;
        this.hand = hand;
        this.possibleActions = possibleActions;
    }
}
