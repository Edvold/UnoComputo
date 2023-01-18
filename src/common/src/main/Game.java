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
import common.src.main.Messages.CallOutCommand;
import common.src.main.Messages.DrawCardsCommand;
import common.src.main.Messages.GenericMessage;
import common.src.main.Messages.MessageFactory;
import common.src.main.Messages.NewGameStateMessage;
import common.src.main.Messages.NextPlayerCommand;
import common.src.main.Messages.PlayCardsCommand;
import common.src.main.Messages.UpdateMessage;

public class Game implements IGame {

    private Map<String, IPlayerConnection> players;
    private Space inbox;
    private ArrayList<String> playerNames;
    private Map<String, Integer> playersHandSize;
    private GameState gameState;


    public Game(Map<String, IPlayerConnection> players, Space inbox) {
        this.players = players;
        this.inbox = inbox;
        playerNames = new ArrayList<String>(players.keySet());
        playersHandSize = new HashMap<String, Integer>(players.size());
        gameState = new GameState();
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  
    }

    @Override
    public void shuffleDeck() {
        Card topCard = discardPile.pop();
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

    public Card[] draw(int amount) throws InterruptedException {
        
        Card[] cards = new Card[amount];
        for(int i = 0; i < amount;i++){
            if(deck.size() == 0){
                shuffleDeck();
            }
            cards[i] = (Card)(deck.get(new FormalField(ACard.class))[0]);
        }
        return cards;
    }

    @Override
    public void startNextRound() throws InterruptedException {
        String currentPlayer = currentPlayer();

        var nextPlayerNoTokenMessage = new NextPlayerCommand(currentPlayer);

        // Inform all that new turn has begun
        for (var player : players.values()){
            if(player.getPlayerName().equals(currentPlayer)) {
                player.getPlayerInbox().put(new NextPlayerCommand("turnToken").getFields());
            } else {
                player.getPlayerInbox().put(nextPlayerNoTokenMessage.getFields());
            }
        }

        IMessage message = null;
        do {
            //send current game state to all
            for(IPlayerConnection player : players.values()){
                player.getPlayerInbox().put(new NewGameStateMessage(gameState).getFields());
            }
            
            var fields = inbox.get(IMessage.getGeneralTemplate().getFields());
            message = MessageFactory.create(fields);
            
            if (message instanceof CallOutCommand) {
                var correctObjection = isObjectionCorrect();
                if(correctObjection) {
                    var player = previousPlayer();
                    var playerConnection = players.get(previousPlayer()).getPlayerInbox();
                    var drawnCards = draw(2);
                    playersHandSize.compute(player, (key, value) -> value + drawnCards.length);
                    

                    playerConnection.put(
                        new DrawCardsCommand(
                            drawnCards, 
                            "You were called out for forgetting to say Uno!").getFields());

                } 

                for(IPlayerConnection player : players.values()){
                    var text = new StringBuilder(message.getMessageText())
                        .append(" objected ")
                        .append(correctObjection ? "correctly" : "incorrectly");

                    player.getPlayerInbox()
                        .put(new UpdateMessage(text.toString()).getFields());
                }
            } else if(message instanceof DrawCardsCommand) {
                var drawAmount = 1;
                var reason = "";

                if(gameState.streak > 0) {
                    var multiplier = gameState.topCard.getAction() == WILDDRAW4 ? 4 : 2;
                    drawAmount = gameState.streak * multiplier;
                    reason = new StringBuilder("A streak of ")
                        .append(gameState.streak)
                        .append(" ")
                        .append(gameState.topCard.getAction())
                        .append(" cards were in play when you drew cards")
                        .toString();
                    gameState.streak = 0;
                }
                var drawnCards = draw(drawAmount);
                players
                    .get(currentPlayer)
                    .getPlayerInbox()
                    .put(new DrawCardsCommand(drawnCards, reason).getFields());
                
                playersHandSize.compute(currentPlayer, (key, value) -> value + drawnCards.length);
            } else if(message instanceof PlayCardsCommand) {
                var playCommand = (PlayCardsCommand) message;
                var playedCards = playCommand.getState();
                var action = playedCards[0].getAction();
                if (action == Action.SKIP) {
                    skip(playedCards.length);
                } else if (action == Action.REVERSE) {
                    reverse(playedCards.length);
                } else if (action == Action.DRAW2 || action == WILDDRAW4) {
                    if(gameState.streak == 0 || playedCards[0].canChainWith(gameState.topCard)) { 
                        gameState.streak += playedCards.length;
                    }
                }
                
                for (Card card : playedCards) {
                    discardPile.add(card);
                }
                playersHandSize.compute(currentPlayer, (key, value) -> value - playedCards.length);
                gameState.topCard = (Card)discardPile.peek();
                gameState.saidUNO = playCommand.didSayUno();
            } else {
                System.out.println("unexpected message encountered and ignored: (" + message.getClass() + ") " + message.toString());
            }
        } while (
            message.getMessageType() != MessageType.PlayCardsCommand 
            && message.getMessageType() != MessageType.DrawCardsCommand
        ); 

        updateStateTurnOrder();
        moveTurnToNextPlayer();
    }

    public boolean isObjectionCorrect() {

        String lastPlayerName = previousPlayer();

        var handSize = playersHandSize.get(lastPlayerName);

        return !gameState.saidUNO && handSize == 1;

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
        for(int j = 0; j < n; j++){
            playerNames.add(playerNames.get(0));
            playerNames.remove(0);
            
            if(playerNames.get(0).equals(currentPlayer())) {
                j--; //The player playing the cards 
            }
        }
    }

    public void reverse(int n){
        var currentPlayer = currentPlayer();
        if(n%2 == 1){
            Collections.reverse(playerNames);
        }
        playerNames.remove(currentPlayer);
        playerNames.add(0, currentPlayer);
    }

    @Override
    public void startGame() {
        generateDeck();
        //Put first can on discardPile
        do {
            try {
                Card[] fstCard = draw(1);
                discardPile.push(fstCard[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(!discardPile.peek().isNumberCard());


        //send to 7 cards to each player
        for(IPlayerConnection player : players.values()){
            try {
                Card[] hand = draw(7);
                playersHandSize.put(player.getPlayerName(), 7);
                player.getPlayerInbox().put(new DrawCardsCommand(hand, "Getting Starting Hand").getFields());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        gameState.currentPlayerName = new PlayerState(playerNames.get(0), 7); 
        gameState.topCard = discardPile.peek();
        updateStateTurnOrder();
    }
    
    @Override
    public void endGame(String winner) {
        // Inform all that new turn has begun
        var nextPlayerMessage = new NextPlayerCommand(winner);    
        for (var p : players.values()) {
            IMessage message;
            //send current game state to all
            
            if (p.getPlayerName().equalsIgnoreCase(winner)) {
                message = new GenericMessage(MessageType.GameOver, "Congratulations! You Won!");
            } else {
                message = new GenericMessage(MessageType.GameOver, "Game Over, " + winner + " has won the game...");
            }

            try {
                // Send a game over message to all players
                p.getPlayerInbox().put(message.getFields());
                // Send message new turn message to all, noone gets token
                p.getPlayerInbox().put(nextPlayerMessage.getFields());
                // Send the gamestate to all
                p.getPlayerInbox().put(new NewGameStateMessage(gameState).getFields());
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

    private void updateStateTurnOrder() {
        if (gameState.turnOrder == null || gameState.turnOrder.length < playerNames.size()) {
            gameState.turnOrder = new PlayerState[playersHandSize.size()];
        }

        for (int i = 0; i < playerNames.size(); i++) {
            var playerState = new PlayerState(playerNames.get(i), playersHandSize.get(playerNames.get(i)));
            gameState.turnOrder[i] = playerState;
        }
    }

    private String currentPlayer() {
        return gameState.turnOrder[0].userName;
    }

    private String previousPlayer() {
        return gameState.turnOrder[playerNames.size() - 1].userName;
    }

    private void moveTurnToNextPlayer() {
        skip(1);
        updateStateTurnOrder();
        gameState.currentPlayerName = gameState.turnOrder[0];
    }
}
