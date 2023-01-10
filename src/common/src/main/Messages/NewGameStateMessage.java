package common.src.main.Messages;

import common.src.main.GameState;
import static common.src.main.MessageType.NewGameState;;

/**
 * A message sent when the game state has changed containing the new state.
 */
public class NewGameStateMessage extends AStateMessage<GameState> {
    public NewGameStateMessage(GameState state) {
        super(NewGameState, state, "");
    }
    public NewGameStateMessage(GameState state, String message) {
        super(NewGameState, state, message);
    }
    
}
