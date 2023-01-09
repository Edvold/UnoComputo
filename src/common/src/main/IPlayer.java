package common.src.main;

import java.util.ArrayList;

public interface IPlayer {

    boolean verify(ACard card, ACard topCard);

    ArrayList<ACard> getPlayableCards(ACard[] hand, ACard topCard);

    String computeReturnToken(String ID); //can maybe be omitted
    
}
