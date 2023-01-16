package common.src.main.Messages;

import common.src.main.ACard;

import static common.src.main.MessageType.PlayCardsCommand;

import java.util.List;;

public class PlayCardsCommand extends AStateMessage<List<ACard>> {

    public PlayCardsCommand(boolean sayUno, ACard... cards) {
        this(sayUno, List.of(cards));
    }

    public PlayCardsCommand(boolean sayUno, List<ACard> cards) {
        super(PlayCardsCommand, cards, sayUno ? "UNO" : "");
    }

    PlayCardsCommand(String sayUno, List<ACard> cards) {
        this(!sayUno.isBlank(), cards);
    }
    
    public boolean didSayUno() {
        return message.equalsIgnoreCase("uno");
    }
}
