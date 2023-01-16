package common.src.main.Messages;

import common.src.main.Card;

import static common.src.main.MessageType.PlayCardsCommand;

import java.util.List;;

public class PlayCardsCommand extends AStateMessage<Card[]> {

    public PlayCardsCommand(boolean sayUno, Card... cards) {
        super(PlayCardsCommand, cards, sayUno ? "UNO" : "");
    }

    public PlayCardsCommand(boolean sayUno, List<Card> cards) {
        this(sayUno, cards.toArray(new Card[0]));
    }

    PlayCardsCommand(String sayUno, Card[] cards) {
        this(!sayUno.isBlank(), cards);
    }
    
    public boolean didSayUno() {
        return message.equalsIgnoreCase("uno");
    }
}
