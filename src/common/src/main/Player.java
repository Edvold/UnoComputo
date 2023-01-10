package common.src.main;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.jspace.FormalField;
import org.jspace.RandomSpace;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

public class Player implements IPlayer {

    private String playerName;
    private int playerNumber;
    private ArrayList<ACard> hand = new ArrayList<>();
    private ArrayList<ACard> output = new ArrayList<>();
    private GameState gameState;
    private SpaceRepository playerRepo = new SpaceRepository();
    private SequentialSpace playerInbox = new SequentialSpace();
    public Space UISpace;
    public Space gameSpace;
    private UI ui;

    public Player (String name, int playerNumber) {
        playerName = name;
        this.playerNumber = playerNumber;
        playerRepo.add("playerInbox" + playerNumber,playerInbox);
        this.ui = new UI("playerInbox" + playerNumber);
        String UIChannelName;
        try {
            UIChannelName = (String) playerInbox.get(new FormalField(String.class))[0];
            UISpace = new RemoteSpace("tcp://localhost/" + UIChannelName + "?conn");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        }
        }
        
    public void setGameSpace(String gameChannelName) {
        try {
            gameSpace = new RemoteSpace("tcp://localhost/" + gameChannelName + "?conn");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendMessage(){ // not done
        //create body
    }

    @Override
    public void addToOutput(ACard card) {
        output.add(card);
    }

    @Override
    public ArrayList<ACard> getPlayableCards(ArrayList<Card> hand, ACard topCard) {
        ArrayList<ACard> playables = new ArrayList<>();
        for (ACard card : hand){
            if (card.canBePlayedOn(topCard)){
                playables.add(card);
            }
        }
        return playables;
    }

    @Override
    public String computeReturnToken(String ID) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
