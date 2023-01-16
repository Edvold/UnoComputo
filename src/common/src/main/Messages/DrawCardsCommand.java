package common.src.main.Messages;

import static common.src.main.MessageType.DrawCardsCommand;

import common.src.main.Card;


public class DrawCardsCommand extends AStateMessage<Card[]> {
    public DrawCardsCommand() {
        this("");
    }
    public DrawCardsCommand(String reason) {
        this(new Card[0], reason);
    }
    public DrawCardsCommand(Card[] cards, String reason) {
        super(DrawCardsCommand, cards, reason);
    }
}
