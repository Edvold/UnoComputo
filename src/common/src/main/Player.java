package common.src.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.jspace.FormalField;
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
    private Space UISpace;
    private Space gameSpace;
    private UI ui;

    public Player (String name, int playerNumber, String gameAddress) { //constructor for player that connects them to both UI and Game
        playerName = name;
        this.playerNumber = playerNumber;
        playerRepo.add("playerInbox" + playerNumber,playerInbox);
        try {
            playerRepo.addGate("tcp://" + InetAddress.getLocalHost().getHostAddress() +":9001/?keep");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.ui = new UI("playerInbox" + playerNumber); //maybe UI should get the IPAddress as well
        String UIChannelName;
        try {
            UIChannelName = (String) playerInbox.get(new FormalField(String.class))[0];
            UISpace = new RemoteSpace("tcp://localhost/" + UIChannelName + "?conn");
            gameSpace = new RemoteSpace("tcp://" + gameAddress + "/game?conn");
            gameSpace.put("tcp://" + InetAddress.getLocalHost().getHostAddress() + "/playerInbox" + playerNumber + "?conn", playerName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        
    // public void setGameSpace(String gameChannelName) {
    //     try {
    //         gameSpace = new RemoteSpace("tcp://" + gameChannelName + "?conn");
    //     } catch (UnknownHostException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     } catch (IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    //     gameSpace.put(null)
    // }

    public void sendMessage(){ // not done
        //create body
    }

    @Override
    public void addToOutput(ACard card) {
        output.add(card);
    }

    @Override
    public ArrayList<ACard> getPlayableCards(ArrayList<Card> hand, ACard topCard) {
        //finds playable cards
        ArrayList<ACard> playables = new ArrayList<>(hand); //This will work if cards aren't changed until after they are played
        playables.removeIf(card -> !card.canBePlayedOn(topCard));
        return playables;
    }

    @Override
    public String computeReturnToken(String ID) {
        return  ID.equals("object") ? "null" : "TurnToken";
    }

    public ArrayList<ACard> getHand() {
        return hand;
    }

    public void setHand(ArrayList<ACard> newHand) {
        hand = newHand;
    }

    public void setGameState(GameState gameState){
        this.gameState = gameState;
    }    

    
}
