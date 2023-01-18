package common.src.main;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import common.src.main.Messages.CallOutCommand;
import common.src.main.Messages.DrawCardsCommand;
import common.src.main.Messages.GenericMessage;
import common.src.main.Messages.MessageFactory;
import common.src.main.Messages.PlayCardsCommand;
import common.src.main.Messages.PlayerMessage;
import common.src.main.Messages.UpdateMessage;

public class Player implements IPlayer {

    private String playerName;
    private ArrayList<Card> hand = new ArrayList<>();
    private ArrayList<Card> output = new ArrayList<>();
    private GameState gameState;
    private Space playerInbox;
    private Space UISpace;
    private Space gameSpace;
    private boolean saidUNO = false;
    private CallOutChecker callOutChecker;
    private Thread callOutCheckerThread;
    private ArrayList<PlayerAction> actions;
    private boolean playedFirstCard;
    private boolean gameOver = false;

    public Player(String name, Space gameSpace, Space UISpace, Space playerInbox) {
        playerName = name;
        this.UISpace = UISpace;
        this.gameSpace = gameSpace;
        this.playerInbox = playerInbox;
        callOutChecker = new CallOutChecker(playerInbox, gameSpace, playerName);
        callOutCheckerThread = new Thread(callOutChecker);
        callOutCheckerThread.setDaemon(true);
    }

    @Override
    public void run() throws InterruptedException {
        getDrawnCards(); // get initial hand
        sortHand();
        String token = "";
        while (true) {
            playedFirstCard = false;

            var message = playerInbox.get(IMessage.getGeneralTemplate().getFields());
            
            if (message[0] == MessageType.NewGameState) {
                gameState = (GameState) message[1];
                computeInitialActions(token);
                ArrayList<Card> playables = playedFirstCard || gameState.streak > 0
                            ? getStackingCards(hand, gameState.topCard)
                            : getPlayableCards(hand, gameState.topCard);
                if (playables.size() == 0) {
                    actions.remove(PlayerAction.PLAY);
                }
                UISpace.put(new PlayerMessage(
                    gameState, 
                    (Card[]) playables.toArray(new Card[0]), 
                    (Card[]) hand.toArray(new Card[0]),
                    (PlayerAction[]) actions.toArray(new PlayerAction[0])).getFields());
                continue;
            } else if(message[0] == MessageType.Update) {
                UISpace.put(message);
                continue;
            } else if(message[0] == MessageType.NextPlayerCommand) {
                token = (String) message[1];
                gameState = (GameState)playerInbox.get(
                    new ActualField(MessageType.NewGameState), 
                    new FormalField(Object.class), 
                    new FormalField(String.class))[1];
                callOutChecker.hasObjected = false;
            } else if(message[0] == MessageType.DrawCardsCommand) {
                playerInbox.put(message);
                getDrawnCards();
                continue;
            } else if (message[0] == MessageType.GameOver) {
                UISpace.put(new GenericMessage(MessageType.GameOver, (String)message[2]).getFields());
                UISpace.put(new PlayerMessage(gameState, new Card[0], hand.toArray(new Card[hand.size()]),
                    (PlayerAction[]) actions.toArray(new PlayerAction[0])).getFields());
            } 
            else  {
                playerInbox.put(message);
                continue;
            }

            if (token.equals("turnToken")) {
                // It is your turn
                computeInitialActions(token);
                saidUNO = false;
                while (token.equals("turnToken")) {
                    var newerStateList = playerInbox.getAll(
                        new ActualField(MessageType.NewGameState),
                        new FormalField(GameState.class), 
                        new FormalField(String.class));

                    if(newerStateList.size() > 0) {
                        gameState = (GameState) newerStateList.get(newerStateList.size() - 1)[1];
                    }


                    ArrayList<Card> playables = playedFirstCard || gameState.streak > 0
                            ? getStackingCards(hand, gameState.topCard)
                            : getPlayableCards(hand, gameState.topCard);



                    if (playables.size() == 0) {
                        actions.remove(PlayerAction.PLAY);
                    }

                    UISpace.put(new PlayerMessage(gameState, (Card[]) playables.toArray(new Card[0]), (Card[]) hand.toArray(new Card[0]),
                    (PlayerAction[]) actions.toArray(new PlayerAction[0])).getFields());

                    var newMessage = playerInbox.get(IMessage.getGeneralTemplate().getFields());
                    
                    if (newMessage[0] == MessageType.CallOutCommand) {
                        UISpace.put(new UpdateMessage("There has been an objection by " + newMessage[1]).getFields());
                    } else if (newMessage[0] == MessageType.Update) {
                        UISpace.put(new UpdateMessage((String) newMessage[2]).getFields());
                    } else if (newMessage[0] == MessageType.NewGameState) {
                        int handsize = hand.size();
                        this.gameState = (GameState) newMessage[1];
                        gameState.turnOrder[0].handSize = handsize;
                        gameState.saidUNO = saidUNO;
                    } else if (newMessage[0] == MessageType.UIMessage) {
                        PlayerAction action = (PlayerAction) newMessage[1];
                        switch (action) {
                            case DRAW:
                                String reason = gameState.streak > 0 ? "Streak" : "Draw";
                                gameSpace.put(new DrawCardsCommand(reason).getFields());
                                getDrawnCards();
                                token = "";
                                break;
                            case ENDTURN:
                                gameSpace.put(new PlayCardsCommand(saidUNO, output).getFields());
                                token = "";
                                output.clear();
                                break;
                            case UNO:
                                saidUNO = true;
                                actions.remove(PlayerAction.UNO);
                                break;
                                case OBJECT:
                                gameSpace.put(new CallOutCommand(playerName).getFields());
                                actions.remove(PlayerAction.OBJECT);
                                break;
                            case PLAY:

                                String[] cardValues = ((String) newMessage[2]).split(" ");

                                int index = Integer.parseInt(cardValues[0]);
                                Card playedCard = hand.get(index);

                                if (cardValues.length == 2) {
                                    playedCard.setColor(Color.valueOf(cardValues[1]));
                                }

                                gameState.topCard = playedCard;
                                playedFirstCard = true;
                                if (playedCard.getAction().equals(Action.DRAW2)
                                        || playedCard.getAction().equals(Action.WILDDRAW4)) {
                                    gameState.streak++;
                                }
                                addToOutput(playedCard);
                                hand.remove(index);
                                gameState.currentPlayerName.handSize--;
                                gameState.turnOrder[0].handSize--;

                                if (!actions.contains(PlayerAction.ENDTURN))
                                    actions.add(PlayerAction.ENDTURN);
                                if (actions.contains(PlayerAction.DRAW))
                                    actions.remove(PlayerAction.DRAW);
                                break;
                        }
                    }
                }
            } else {
                computeInitialActions(token);

                var newerStateList = playerInbox.getAll(
                        new ActualField(MessageType.NewGameState),
                        new FormalField(GameState.class), 
                        new FormalField(String.class));

                if(newerStateList.size() > 0) {
                    gameState = (GameState) newerStateList.get(newerStateList.size() - 1)[1];
                }

                // It is not your turn
                if (!callOutCheckerThread.isAlive()) {
                    callOutCheckerThread.start();
                }

                UISpace.put(new PlayerMessage(gameState, new Card[0], hand.toArray(new Card[hand.size()]),
                        (PlayerAction[]) actions.toArray(new PlayerAction[0])).getFields());
            }
        }
    }

    @Override
    public void addToOutput(Card card) {
        output.add(card);
    }

    @Override
    public ArrayList<Card> getPlayableCards(ArrayList<Card> hand, Card topCard) {
        // finds playable cards
        ArrayList<Card> playables = new ArrayList<>(hand);
        playables.removeIf(card -> !card.canBePlayedOn(topCard));
        return playables;
    }

    public ArrayList<Card> getStackingCards(ArrayList<Card> hand, Card topCard) {
        ArrayList<Card> stackables = new ArrayList<>();
        for (Card card : hand) {
            if (card.getAction().equals(topCard.getAction())) {
                stackables.add(card);
            }
        }
        return stackables;
    }

    private void getDrawnCards() throws InterruptedException {
        var template = new DrawCardsCommand().getTemplateBuilder()
                .addActualType()
                .build();
        var response = playerInbox.get(template.getFields());
        var message = (DrawCardsCommand) MessageFactory.create(response);

        var newCards = message.getState();

        insertIntoHand(newCards);
    }

    public void computeInitialActions(String token) {

        actions = new ArrayList<>();

        if (token.equals("turnToken")) {
            actions.add(PlayerAction.PLAY);
            actions.add(PlayerAction.DRAW);
            actions.add(PlayerAction.OBJECT);
            actions.add(PlayerAction.UNO);
        } else {
            if (!callOutChecker.hasObjected) actions.add(PlayerAction.OBJECT);
        }
    }

    private void sortHand() {
        ArrayList<Card> newHand = new ArrayList<>();

        newHand.add(hand.get(0));

        for (int i = 1; i < hand.size(); i++) {
            int j = 0;
            Card card = hand.get(i);

            while(j != newHand.size() ) {
                if (card.compareTo(newHand.get(j)) < 0) {
                    break;
                }
                j++;
            }
            newHand.add(j, card);


        }
        hand = newHand;
    }

    private void insertIntoHand(Card... cards) {
        for (int i = 0; i < cards.length; i++) {
            int j = 0;
            Card card = cards[i];

            while(j != hand.size() ) {
                if (card.compareTo(hand.get(j)) < 0) {
                    break;
                }
                j++;
            }
            hand.add(j, card);
        }
    }


}

class CallOutChecker implements Runnable {
    private Space checkingSpace;
    private Space sendingSpace;
    private String playerName;
    public Boolean hasObjected;

    public CallOutChecker(Space checkSpace, Space sendSpace, String name) {
        this.checkingSpace = checkSpace;
        this.sendingSpace = sendSpace;
        this.playerName = name;
        hasObjected = false;
    }

    public void run() {
        try {
            while (true) {
                checkingSpace.get(new ActualField(MessageType.UIMessage), new ActualField(PlayerAction.OBJECT),
                        new FormalField(String.class));
                hasObjected = true;
                sendingSpace.put(new CallOutCommand(playerName).getFields());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}