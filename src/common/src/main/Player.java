package common.src.main;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import common.src.main.Messages.CallOutCommand;
import common.src.main.Messages.DrawCardsCommand;
import common.src.main.Messages.NewGameStateMessage;
import common.src.main.Messages.NextPlayerCommand;
import common.src.main.Messages.PlayCardsCommand;
import common.src.main.Messages.UIMessage;
import common.src.main.Messages.UpdateMessage;

public class Player implements IPlayer {

    private String playerName;
    private ArrayList<ACard> hand = new ArrayList<>();
    private ArrayList<ACard> output = new ArrayList<>();
    private GameState gameState;
    private Space playerInbox;
    private Space UISpace;
    private Space gameSpace;
    private boolean saidUNO = false;
    private Thread callOutCheckerThread;

    public Player (String name, Space gameSpace, Space UISpace, Space playerInbox) {
        playerName = name;
        this.UISpace = UISpace;
        this.gameSpace = gameSpace;
        this.playerInbox = playerInbox;
        callOutCheckerThread = new Thread(new CallOutChecker(playerInbox, gameSpace, playerName));
        }

    public void run() throws InterruptedException {
        NextPlayerCommand turnMessage = (NextPlayerCommand) gameSpace.get(new FormalField(NextPlayerCommand.class))[0];
        this.gameState = ((NewGameStateMessage) gameSpace.get(new FormalField(NewGameStateMessage.class))[0]).getState();
        if (turnMessage.getState().equals("turnToken")) {
            // It is your turn
            PlayerAction[] actions = {PlayerAction.PLAY,PlayerAction.DRAW,PlayerAction.ENDTURN,PlayerAction.OBJECT,PlayerAction.UNO};
            while (true) {
                UISpace.put(gameState, getPlayableCards(hand, gameState.topCard), hand, actions); //message?
                IMessage newMessage = (IMessage) playerInbox.get(new FormalField(IMessage.class))[0];
                if(newMessage.getMessageType() == MessageType.CallOutCommand){
                    CallOutCommand message = (CallOutCommand) newMessage;
                    //This should probably be a message
                    UISpace.put("There has been an objection by " + message.getMessageText());
                }
                else if (newMessage.getMessageType() == MessageType.Update){
                    UpdateMessage message = (UpdateMessage) newMessage;
                    //This should probably be a message
                    UISpace.put(message.getMessageText());
                }
                else if (newMessage.getMessageType() == MessageType.UIMessage){
                    UIMessage message = (UIMessage) newMessage;
                    //This should probably be a message
                    String action = message.getMessageText();
                    switch (action) {
                        case "Draw": gameSpace.put(new DrawCardsCommand("Draw"));
                                     return;
                        case "End":  gameSpace.put(new PlayCardsCommand(saidUNO, output));
                                     return;
                        case "UNO":  saidUNO = true;
                                     break;
                        case "Object": gameSpace.put(new CallOutCommand(playerName));
                                       break;
                        case "Play": addToOutput(message.getState());
                                     hand.remove(message.getState());
                                     actions = new PlayerAction[] {PlayerAction.PLAY,PlayerAction.UNO,PlayerAction.OBJECT,PlayerAction.ENDTURN};
                                     break;
                    }
                }
            }
        }
        else {
            PlayerAction[] actions = {PlayerAction.OBJECT};
            // It is not your turn
            if(!callOutCheckerThread.isAlive()){
                callOutCheckerThread.start();
            }
            UISpace.put(gameState, new ArrayList<ACard>(), hand, actions);
            //Objection message to player
            //should the entire run method be in a while (true) loop, and then we just check for
            //update messages and NextPlayerCommand messages?
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
    public ArrayList<ACard> getPlayableCards(ArrayList<ACard> hand, ACard topCard) {
        //finds playable cards
        ArrayList<ACard> playables = new ArrayList<>(hand); //This will work if cards aren't changed until after they are played
        playables.removeIf(card -> !card.canBePlayedOn(topCard));
        return playables;
    }

    @Override
    public String computeReturnToken(String ID) {
        return  ID.equals("object") ? "null" : "TurnToken";
    }

    //getters and setters

    public ArrayList<ACard> getHand() {
        return hand;
    }

    public void setHand(ArrayList<ACard> newHand) {
        hand = newHand;
    }

    // public void setGameState(GameState gameState){
    //     this.gameState = gameState;
    // }    

    
}


class CallOutChecker implements Runnable {
    private Space checkingSpace;
    private Space sendingSpace;
    private String playerName;

    public CallOutChecker(Space checkSpace, Space sendSpace, String name){
        this.checkingSpace = checkSpace;
        this.sendingSpace = sendSpace;
        this.playerName = name;
    }

    public void run() {
        try {
            while(true){
                checkingSpace.get(new ActualField(PlayerAction.OBJECT), new FormalField(Integer.class)); //change to correct format
                sendingSpace.put(new CallOutCommand(playerName));
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
