package common.src.main;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RandomSpace;
import org.jspace.Space;

import java.util.ArrayList;
import java.util.List;

import static common.src.main.Color.*;


import static common.src.main.Action.*;

public class Game implements IGame{

    public static void main(String[] args) throws InterruptedException {
        Game g = new Game();
        g.generateDeck();
    }

    @Override
    public void generateDeck() {
        try {
            for(Action action : Action.values()){
                for(Color color : Color.values()){
                    for (int i = 0;i < 2;i++){
                        if(color == BLACK && (action == WILD || action == WILDDRAW4)){
                            deck.put(new Card(color,action));
                            deck.put(new Card(color,action));
                        }
                        if(color != BLACK && action != WILD && action != WILDDRAW4 && action != ZERO){
                            deck.put(new Card(color,action));
                        }
                        if(color != BLACK && action == ZERO && i == 0){
                            deck.put(new Card(color,action));
                        }
                    }
                }
            }
            System.out.println("I'm Here");
        } catch (InterruptedException e) {
            e.printStackTrace(); // TODO error handeling
        }  
    }

    @Override
    public void shuffleDeck() {
        ACard topCard = discardPile.pop();
        try
        {
            for(ACard c : discardPile){
                deck.put(new Card(c.getColor(), c.getAction()));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        discardPile.push(topCard);     
    }

    @Override
    public ACard[] draw(int amount) throws InterruptedException {
        if(amount > deck.size()){
            shuffleDeck();
        }
        ACard[] cards = new ACard[amount];
        for(int i = 0; i < amount;i++){
            cards[i] = (ACard) (deck.get(new FormalField(ACard.class))[0]);
        }
        return cards;
    }

    @Override
    public void startNextRound(IPlayer[] players) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isObjectionCorrect() {
        // TODO Auto-generated method stub
        return false;
    }
    
}
