package common.src.main;

import java.util.Map;
import java.util.Stack;

import org.jspace.RandomSpace;
import org.jspace.Space;

interface IGame {

    RandomSpace deck = new RandomSpace();
    Stack<ACard> discardPile = new Stack<>();

    void startGame(Map<String, IPlayerConnection> players, Space inbox);
    void generateDeck();
    
    /**
     * Takes all but the top card of the discardPile and shuffles them into the deck
     * Not used outside of game
     */
    void shuffleDeck();
    ACard[] draw(int amount) throws InterruptedException; // not useful outside of game
    void startNextRound(Map<String, IPlayerConnection> players, Space inbox) throws InterruptedException; // Needs to take into account skipping & reversing
    boolean isGameOver();
    IPlayerConnection getWinner(Map<String, IPlayerConnection> players);
    boolean isObjectionCorrect(); // not useful outside of game
    

    

}