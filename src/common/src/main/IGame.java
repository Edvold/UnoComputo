package common.src.main;

import java.util.Stack;

import org.jspace.RandomSpace;
import org.jspace.Space;

interface IGame {

    Space deck = new RandomSpace();
    Stack<Card> discardPile = new Stack<>();

    void startGame();
    void generateDeck();
    
    /**
     * Takes all but the top card of the discardPile and shuffles them into the deck
     * Not used outside of game
     */
    void shuffleDeck();
    void startNextRound() throws InterruptedException;
    boolean isGameOver();
    String getWinner();
    void endGame(String winner);
}