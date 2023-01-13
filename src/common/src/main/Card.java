package common.src.main;

import static common.src.main.Color.*;
import static common.src.main.Action.*;

public class Card extends ACard {

    private Color color;
    private Action action;

    public Card() {

    }

    public Card(Color c, Action a){
        this.color = c;
        this.action = a;
    }

    @Override
    public Color getColor() { return color; }
    @Override
    public Action getAction() {return action; }

    @Override
    public boolean canBePlayedOn(ACard card) {
        if(action == WILD || action == WILDDRAW4){
            return true;
        }
        return (color == card.getColor()) || (action == card.getAction());
    }

    @Override
    public void setColor(Color c) {
        if(color == BLACK){
            color = c;
        } else {
            throw new RuntimeException("You can not change the color of a " + color + " " + action);
        }
        
    }

    @Override
    public boolean canChainWith(ACard other) {
        Action oAction = other.getAction();
        if(!(oAction == DRAW2 || oAction == WILDDRAW4)){
            return false;
        }
        if(!(action == oAction)){
            return false;
        }
        return true;
    }

    @Override
    public void resetWildCard() {
        if(action == WILD || action == WILDDRAW4){
            color = BLACK; 
        } else {
            //throw new RuntimeException("You cannot reset a card that is not a wildcard");
        }
    }
    
}
