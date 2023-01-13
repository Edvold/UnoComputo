package common.src.main.Messages;

import common.src.main.PlayerAction;

import static common.src.main.MessageType.UIMessage;;;

public class UIMessage extends AStateMessage<PlayerAction> {

    public UIMessage(PlayerAction state, String message) {
        super(UIMessage, state, message);
    }
    
}
