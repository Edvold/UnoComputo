package common.src.main.Messages;

import static common.src.main.MessageType.Update;

/**
 *  A message for informing Players and UIs of something that requires no action on their part
 */
public class UpdateMessage extends AMessage {

    public UpdateMessage(String message) {
        super(Update, message);
    }
    
}
