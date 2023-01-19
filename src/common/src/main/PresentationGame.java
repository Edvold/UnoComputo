package common.src.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jspace.Space;

import static common.src.main.Color.*;
import static common.src.main.Action.*;

public class PresentationGame extends Game {

    public PresentationGame(Map<String, IPlayerConnection> players, Space inbox) {
        super(players, inbox);
    }

    @Override
    public void generateDeck() {
        var startCard = new Card(RED, SIX);
        var player1Cards = List.of(
            new Card(RED, TWO), 
            new Card(GREEN, TWO), 
            new Card(BLUE, SKIP), 
            new Card(BLUE, SKIP), 
            new Card(BLUE, ONE), 
            new Card(YELLOW, NINE), 
            new Card(YELLOW, EIGHT)
        );

        var player2Cards = List.of(
            new Card(GREEN, DRAW2),
            new Card(BLACK, WILDDRAW4),
            new Card(YELLOW, REVERSE),
            new Card(BLUE, REVERSE),
            new Card(RED, ZERO),
            new Card(RED, ONE),
            new Card(RED, TWO)
        );

        var player3Cards = List.of(
            new Card(BLUE, DRAW2),
            new Card(BLACK, WILD),
            new Card(BLUE, NINE),
            new Card(YELLOW, NINE),
            new Card(GREEN, FIVE),
            new Card(RED, FIVE),
            new Card(BLUE, FIVE)
        );

        var player4Cards = List.of(
            new Card(BLUE, REVERSE),
            new Card(GREEN, ONE),
            new Card(GREEN, ONE),
            new Card(BLUE, ONE),
            new Card(BLUE, ONE),
            new Card(BLUE, TWO),
            new Card(GREEN, THREE),
            new Card(BLUE, THREE),
            new Card(BLUE, THREE),
            new Card(RED, THREE),
            new Card(RED, THREE)
        );

        var cards = new ArrayList<Card>(108);
        for(Action action : Action.values()){
            for(Color color : Color.values()){
                for (int i = 0;i < 2;i++){
                    if(color == BLACK && (action == WILD || action == WILDDRAW4)){
                        cards.add(new Card(color,action));
                        cards.add(new Card(color,action));
                    }
                    if(color != BLACK && action != WILD && action != WILDDRAW4 && action != ZERO){
                        cards.add(new Card(color,action));
                    }
                    if(color != BLACK && action == ZERO && i == 0){
                        cards.add(new Card(color,action));
                    }
                }
            }
        }

        cards.remove(startCard);
        cards.removeAll(player1Cards);
        cards.removeAll(player2Cards);
        cards.removeAll(player3Cards);
        cards.removeAll(player4Cards);
        Collections.shuffle(cards, new Random(1));

        try {
            for (Card card : cards) {
                deck.put(card);
            }
            for (Card card : player4Cards) {
                deck.put(card);
            }
            for (Card card : player3Cards) {
                deck.put(card);
            }
            for (Card card : player2Cards) {
                deck.put(card);
            }
            for (Card card : player1Cards) {
                deck.put(card);
            }
            deck.put(startCard);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }
    
}
