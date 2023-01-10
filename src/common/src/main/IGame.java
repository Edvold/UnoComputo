package common.src.main;

import java.util.Stack;

import org.jspace.RandomSpace;

interface IGame {

    RandomSpace deck = new RandomSpace();
    Stack<ACard> discardPile = new Stack<>();

    void generateDeck();
    
    /**
     * Takes all but the top card of the discardPile and shuffles them into the deck
     */
    void shuffleDeck();
    ACard[] draw(int amount) throws InterruptedException;
    void startNextRound(IPlayer[] players); // Needs to take into account skipping & reversing
    boolean isObjectionCorrect();


    

}