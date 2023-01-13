package common.src.main.Messages;

import static common.src.main.MessageType.PlayerMessage;

import common.src.main.Card;
import common.src.main.GameState;
import common.src.main.GameStateUpdate;
import common.src.main.PlayerAction;

public class PlayerMessage extends AStateMessage<GameStateUpdate> {
    
    public PlayerMessage(GameState gameState, Card[] possibleCards, Card[] hand, PlayerAction[] possibleActions) {
        this(new GameStateUpdate(gameState, possibleCards, hand, possibleActions));
    }
    
    private PlayerMessage(GameStateUpdate gameStateUpdate) {
        super(PlayerMessage, gameStateUpdate, "");
    }
}
