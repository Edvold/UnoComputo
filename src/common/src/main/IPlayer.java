package common.src.main;

import java.util.ArrayList;

public interface IPlayer {

    boolean verify(ACard card, ACard topCard); //verify that the card is playable

    ArrayList<ACard> getPlayableCards(ACard[] hand, ACard topCard); //find all playable cards from the player's hand

    String computeReturnToken(String ID); //can maybe be omitted
    
}
