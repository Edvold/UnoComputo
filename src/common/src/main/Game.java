package common.src.main;

import static common.src.main.Action.WILD;
import static common.src.main.Action.WILDDRAW4;
import static common.src.main.Action.ZERO;
import static common.src.main.Color.BLACK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jspace.FormalField;
import org.jspace.Space;

import common.src.main.GameState.PlayerState;
import common.src.main.Messages.DrawCardsCommand;
import common.src.main.Messages.GenericMessage;

public class Game implements IGame {

    private Map<String, IPlayerConnection> players;
    private Space inbox;
    private ArrayList<String> playerNames;
    private Map<String, Integer> playersHandSize;
    private GameState gameState;


    public Game(Map<String, IPlayerConnection> players, Space inbox) {
        this.players = players;
        this.inbox = inbox;
        playerNames = new ArrayList(players.keySet());
        playersHandSize = new HashMap(players.size());
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
                c.resetWildCard();
                deck.put(new Card(c.getColor(), c.getAction()));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        discardPile.push(topCard);     
    }

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
    public void startNextRound() throws InterruptedException {
        String currentPlayer = playerNames.get(0);
        players.get(currentPlayer).getPlayerInbox().put("turnToken");

        //send to all
        for(IPlayerConnection player : players.values()){
            player.getPlayerInbox().put("Current gameState");
            // TODO insert:  player.getPlayerInbox().put("begun", currentPlayer, gameState); 
        }
        
        //inbox.get(IMessage.getGeneralTemplate().getFields());
        
        
        // awaits a description of players current turn
        Object[] obj = inbox.get(new FormalField(ITurnDescription.class));
        

        //Send no time left to controller
        // or
        // Update internal gameState

        // first figure out next player (what is the player order and which direction are we going)
        // send message to all that new round has begun. (include turn token in message to new current player)
        // send game state to all players 
        // await turn description from current player and objections from all players
        // if objection is raised check it and send new game state if valid
        // update internal state to reflect player commands
    }

    public boolean isObjectionCorrect() {

        String lastPlayerName = playerNames.get(playerNames.size()-1);

        PlayerState lastPlayer = null;

        for (PlayerState player : gameState.turnOrder) {
            if (player.userName.equals(lastPlayerName)) lastPlayer = player;
        }

        return !gameState.saidUNO && lastPlayer.handSize == 1;

    }

    @Override
    public boolean isGameOver() {
        for(int val : playersHandSize.values()){
            if(val == 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public String getWinner() {
        String s = "";
        for (Map.Entry<String, Integer> pair : playersHandSize.entrySet()) {
            if(pair.getValue() == 0){
                s = pair.getKey();
            }
        }
        return s;
    }


    public void skip(int n){
        for(int j = 0; j < n;j++){
            playerNames.add(playerNames.get(playerNames.size()-1));
            playerNames.remove(0);
        }
    }

    public void reverse(int n){
        if(n%2 == 1){
            Collections.reverse(playerNames);
        }
    }

    @Override
    public void startGame() {
        generateDeck();
        //Put first can on discardPile
        do {
            try {
                ACard[] fstCard = draw(1);
                discardPile.push(fstCard[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(!discardPile.peek().isNumberCard());


        //send to 7 cards to each player
        for(IPlayerConnection player : players.values()){
            try {
                ACard[] hand = draw(7);
                player.getPlayerInbox().put(new DrawCardsCommand(hand, "Getting Starting Hand"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    
    @Override
    public void endGame(String winner) {
        for (var p : players.values()) {
            IMessage message;
            if (p.getPlayerName().equalsIgnoreCase(winner)) {
                message = new GenericMessage(MessageType.GameOver, "Congratulations! You Won!");
            } else {
                message = new GenericMessage(MessageType.GameOver, "Game Over, " + winner + " has won the game...");
            }

            try {
                p.getPlayerInbox().put(message.getFields());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int computeStreak(ACard[] playedCards) {
        int streak = 0;
        for (ACard card : playedCards) {
            if(card.getAction().equals(Action.DRAW2) || card.getAction().equals(Action.WILDDRAW4)){
                streak++;
            }
        }
        return streak;
    }
}
