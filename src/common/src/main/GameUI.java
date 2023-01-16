package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.jspace.FormalField;
import org.jspace.Space;
import common.src.main.GameState.PlayerState;
import common.src.main.Messages.MessageFactory;
import common.src.main.Messages.PlayerMessage;
import common.src.main.Messages.UIMessage;
import common.src.main.Messages.UpdateMessage;


public class GameUI implements Runnable {
    
    Space outbox;
    Space inbox;
    GameState gameState;
    String userName;
    final static String wrongInput = "Sorry that is not an option. Try again!";
    BufferedReader reader = new BufferedReader((new InputStreamReader(System.in)));


    public GameUI(Space inbox, Space outbox, String name) {
        this.inbox = inbox;
        this.outbox = outbox;
        userName = name;
    }

    public void run() {
    
        try {

            while (true) {

                GameStateUpdate gsu = (GameStateUpdate)inbox.get(new FormalField(MessageType.PlayerMessage.getClass()), new FormalField(Object.class), new FormalField(String.class))[1];

                
                ArrayList<Card> possibleCards = new ArrayList<Card>(Arrays.asList(gsu.possibleCards));
                ArrayList<Card> hand =  new ArrayList<Card>(Arrays.asList(gsu.hand));
                ArrayList<PlayerAction> possibleActions =  new ArrayList<PlayerAction>(Arrays.asList(gsu.possibleActions));
                gameState = gsu.gameState;
                
                
                
                // Print the current state of the game
                printOverview(gameState);
                
                // Get and print update message if any exists
                var message = inbox.getp(new FormalField(MessageType.Update.getClass()), new FormalField(Object.class), new FormalField(String.class));
                if (message != null) {
                    printUpdateMessage((String)message[2]);
                }

                takeTurn(possibleCards, hand, possibleActions);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    private void printUpdateMessage(String message) {
        System.out.println("===========================================");
        System.out.println("UPDATE FROM GAME:");
        System.out.println(message);
        System.out.println("===========================================");
    }

    private void printOverview(GameState gameState) {

        System.out.println("A new round has begun!");
        System.out.println("The turn-order is:");

        for (PlayerState player : gameState.turnOrder) {
            System.out.println(player.toString());
        }

        System.out.println("It is currently " + (gameState.currentPlayerName.userName.equals(userName) ? "your turn" : (gameState.currentPlayerName.userName + "'s turn")));
        
        printTopCard();
        
        if (gameState.streak > 0) {
            System.out.println("There is currently a streak of " + gameState.streak);
        }
    }

    private void takeTurn(ArrayList<Card> possibleCards, ArrayList<Card> hand, ArrayList<PlayerAction> possibleActions) {

        boolean getChoice = true;

        while (getChoice) {

            try {
                
                if (possibleActions.contains(PlayerAction.PLAY)) printHand(hand);
                
                System.out.println("Choose an option:");
                
                for (int i = 1; i <= possibleActions.size(); i++) {
                    PlayerAction pa = possibleActions.get(i-1);
                    System.out.println(i + ". " + pa.toString());
                }
                
                // Get choice input from player
                int option = Integer.parseInt(reader.readLine()) - 1;
                
                getChoice = false;

                // Player choses a non-existing option
                if (option >= possibleActions.size() || option < 0) {
                System.out.println(wrongInput);
                getChoice = true;
                }

                clearScreen();

                if (possibleActions.get(option) == PlayerAction.PLAY) {
                    playCard(hand, possibleCards);
                } else {
                    try {
                        // Inform player of choice
                        outbox.put((new UIMessage(possibleActions.get(option), "")).getFields());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (NumberFormatException e) {
                clearScreen();
                System.out.println(wrongInput);
            } catch (IOException e) {
                e.printStackTrace();
            }        
        }
    }

    private void playCard(ArrayList<Card> hand, ArrayList<Card> possibleCards) {
        
        boolean getChoice = true;
        
        while (getChoice) {

            try {

                printHand(hand);
                printTopCard();

                System.out.println("Choose a card to play");

                // Get choice input from player
                int card = Integer.parseInt(reader.readLine())-1;
                
                // Player chooses non-existing card
                if (card >= hand.size() || card < 0) {
                    clearScreen();
                    System.out.println(wrongInput);
                    continue;
                }
                
                // Player chooses a card that is impossible to play (according to rules)
                if (!possibleCards.contains(hand.get(card))) {
                    clearScreen();
                    System.out.println("That is not a valid card.");
                    continue;
                }
                
                clearScreen();

                getChoice = false;

                // Inform player of choice
                outbox.put(new UIMessage(PlayerAction.PLAY, String.valueOf(card)).getFields());
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                clearScreen();
                System.out.println(wrongInput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void printHand(ArrayList<Card> hand) {
        int counter = 1;

        System.out.println("Your hand consist of the following cards:");
        for (ACard card : hand) {
            System.out.println(counter++ + ". " + card.toString());
        }
    }

    private void printTopCard() {
        System.out.println("The top card is: " + gameState.topCard);
    }

    private void clearScreen() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

}
