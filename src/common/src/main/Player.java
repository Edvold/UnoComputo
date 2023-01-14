package common.src.main;

import java.util.ArrayList;
import java.util.Arrays;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import common.src.main.Messages.CallOutCommand;
import common.src.main.Messages.DrawCardsCommand;
import common.src.main.Messages.MessageFactory;
import common.src.main.Messages.NewGameStateMessage;
import common.src.main.Messages.NextPlayerCommand;
import common.src.main.Messages.PlayCardsCommand;
import common.src.main.Messages.PlayerMessage;
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
    private PlayerAction[] actions;
    private boolean playedFirstCard;

    public Player (String name, Space gameSpace, Space UISpace, Space playerInbox) {
        playerName = name;
        this.UISpace = UISpace;
        this.gameSpace = gameSpace;
        this.playerInbox = playerInbox;
        callOutCheckerThread = new Thread(new CallOutChecker(playerInbox, gameSpace, playerName));
        }

    @Override
    public void run() throws InterruptedException {
        getDrawnCards(); // get initial hand
        String token = "";
        while (true) {
            playedFirstCard = false;
            NextPlayerCommand turnMessage = (NextPlayerCommand) gameSpace.get(new FormalField(NextPlayerCommand.class))[0];
            this.gameState = ((NewGameStateMessage) gameSpace.get(new FormalField(NewGameStateMessage.class))[0]).getState();
            token = turnMessage.getState();
            if (token.equals("turnToken")) {
            // It is your turn
                computeActions(token);
                while (token.equals("turnToken")) {
                    ArrayList<ACard> playables = playedFirstCard ? getStackingCards(hand, gameState.topCard) : getPlayableCards(hand, gameState.topCard);
                    UISpace.put(new PlayerMessage(gameState, (Card[]) playables.toArray(), (Card[]) hand.toArray(), actions)); //message?
                    IMessage newMessage = (IMessage) playerInbox.get(new FormalField(IMessage.class))[0];
                    if(newMessage.getMessageType() == MessageType.CallOutCommand){
                        CallOutCommand message = (CallOutCommand) newMessage;
                        UISpace.put(new UpdateMessage("There has been an objection by " + message.getMessageText()));
                    }
                    else if (newMessage.getMessageType() == MessageType.Update){
                        UpdateMessage message = (UpdateMessage) newMessage;
                        UISpace.put(message);
                    }
                    else if (newMessage.getMessageType() == MessageType.UIMessage){
                        UIMessage message = (UIMessage) newMessage;
                        PlayerAction action = message.getState();
                        switch (action) {
                            case DRAW: String reason = gameState.streak > 0 ? "Streak" : "Draw";
                                gameSpace.put(new DrawCardsCommand(reason));
                                getDrawnCards();
                                    token = "";
                                    break;
                            case ENDTURN:  gameSpace.put(new PlayCardsCommand(saidUNO, output));
                                    token = "";
                                    break;
                            case UNO:  saidUNO = true;
                                    break;
                            case OBJECT: gameSpace.put(new CallOutCommand(playerName));
                                    break;
                            case PLAY: ACard playedCard = hand.get(Integer.parseInt(message.getMessageText()));
                                    gameState.topCard = (Card) playedCard;
                                    playedFirstCard = true;
                                    if (playedCard.getAction().equals(Action.DRAW2) || playedCard.getAction().equals(Action.WILDDRAW4)){
                                        gameState.streak++;
                                    }
                                    addToOutput(playedCard);
                                    hand.remove(playedCard);
                                    actions = new PlayerAction[] {PlayerAction.PLAY,PlayerAction.UNO,PlayerAction.OBJECT,PlayerAction.ENDTURN};
                                    break;
                    }
                }
            }
        }
            else {
                computeActions(token);
                // It is not your turn
                if(!callOutCheckerThread.isAlive()){
                    callOutCheckerThread.start();
                }
                UISpace.put(new PlayerMessage(gameState, new Card[0], (Card[]) hand.toArray(), actions));
        }
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

    public ArrayList<ACard> getStackingCards(ArrayList<ACard> hand, ACard topCard){
        ArrayList<ACard> stackables = new ArrayList<>();
        for (ACard card : hand){
            if (card.getAction().equals(topCard.getAction())){
                stackables.add(card);
            }
        }
        return stackables;
    }

    @Override
    public String computeReturnToken(String ID) {
        return  ID.equals("object") ? "null" : "TurnToken";
        //This method is not needed
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

    private void getDrawnCards() throws InterruptedException {
        var template = new DrawCardsCommand().getTemplateBuilder()
            .addActualType()
            .build();
        var response = playerInbox.get(template.getFields());
        var message = (DrawCardsCommand) MessageFactory.create(response);

        var newCards = Arrays.asList(message.getState());

        this.hand.addAll(newCards);
    } 

    public void computeActions(String token) {
        if (token.equals("turnToken")){
            if(gameState.streak > 0){
                actions = new PlayerAction[] {PlayerAction.PLAY,PlayerAction.DRAW,PlayerAction.OBJECT,PlayerAction.UNO};
            }
            else{
                actions = new PlayerAction[] {PlayerAction.PLAY,PlayerAction.DRAW,PlayerAction.ENDTURN,PlayerAction.OBJECT,PlayerAction.UNO};
            }
        }
        else {
            actions = new PlayerAction[] {PlayerAction.OBJECT};
        }
    }
    
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
