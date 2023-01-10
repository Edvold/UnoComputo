package common.src.main;

import java.util.ArrayList;

public interface IPlayer {

    void addToOutput(ACard card); //Add card to list of cards to be played and remove the card from hand 

    ArrayList<ACard> getPlayableCards(ACard[] hand, ACard topCard); //find all playable cards from the player's hand

    String computeReturnToken(String ID); //can maybe be omitted
    
}
