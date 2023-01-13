package common.src.main.Messages;

import common.src.main.ACard;
import static common.src.main.MessageType.UIMessage;;;

public class UIMessage extends AStateMessage<ACard> {

    public UIMessage(ACard state, String message) {
        super(UIMessage, state, message);
    }
    
}
