package common.src.main.Messages;

import static common.src.main.MessageType.DrawCardsCommand;


public class DrawCardsCommand extends AMessage {
    public DrawCardsCommand() {
        this("");
    }
    public DrawCardsCommand(String reason) {
        super(DrawCardsCommand, reason);
    }
}
