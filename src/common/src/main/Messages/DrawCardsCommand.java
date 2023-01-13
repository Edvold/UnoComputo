package common.src.main.Messages;

import static common.src.main.MessageType.DrawCardsCommand;

import common.src.main.ACard;


public class DrawCardsCommand extends AStateMessage<ACard[]> {
    public DrawCardsCommand() {
        this("");
    }
    public DrawCardsCommand(String reason) {
        this(new ACard[0], reason);
    }
    public DrawCardsCommand(ACard[] cards, String reason) {
        super(DrawCardsCommand, cards, reason);
    }
}
