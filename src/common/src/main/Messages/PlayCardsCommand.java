package common.src.main.Messages;

import common.src.main.Card;

import static common.src.main.MessageType.PlayCardsCommand;

import java.util.List;;

public class PlayCardsCommand extends AStateMessage<List<Card>> {

    public PlayCardsCommand(boolean sayUno, Card... cards) {
        this(sayUno, List.of(cards));
    }

    public PlayCardsCommand(boolean sayUno, List<Card> cards) {
        super(PlayCardsCommand, cards, sayUno ? "UNO" : "");
    }

    PlayCardsCommand(String sayUno, List<Card> cards) {
        this(!sayUno.isBlank(), cards);
    }
    
    public boolean didSayUno() {
        return message.equalsIgnoreCase("uno");
    }
}
