package common.src.main.Messages;

import static common.src.main.MessageType.CallOutCommand;


public class CallOutCommand extends AMessage {
    /**
     * 
     * @param callingPlayer the player that calls the lack of Uno
     */
    public CallOutCommand(String callingPlayer) {
        super(CallOutCommand, callingPlayer);
    }
}
