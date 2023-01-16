package common.src.main;

import java.util.ArrayList;

public interface IPlayer {

    void addToOutput(Card card); //Add card to list of cards to be played and remove the card from hand 

    ArrayList<Card> getPlayableCards(ArrayList<Card> hand, Card topCard); //find all playable cards from the player's hand

    String computeReturnToken(String ID); //can maybe be omitted
    
    void run() throws InterruptedException;
}
