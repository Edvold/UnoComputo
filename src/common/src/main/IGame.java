package common.src.main;

import java.util.Stack;

import org.jspace.RandomSpace;

interface IGame {

    RandomSpace deck = new RandomSpace();
    Stack<ACard> discardPile = new Stack<>();

    void generateDeck();
    void shuffleDeck();
    ACard[] draw(int amount);
    void startNextRound(IPlayer[] players); // Needs to take into account skipping & reversing
    boolean isObjectionCorrect();


    

}