package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.jspace.Space;
import common.src.main.GameState.PlayerState;
import common.src.main.Messages.MessageFactory;


public class GameUI implements Runnable{
    
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
        // Get message
        // If it is player's turn call takeTurn
        // otherwise call some other method
        
        try {

            while (true) {

                var message = inbox.get(IStateMessage.getGeneralTemplate().getFields());
                GameStateUpdate gsu = (GameStateUpdate)((IStateMessage) MessageFactory.create(message)).getState();
                
                ArrayList<Card> possibleCards = new ArrayList<Card>(Arrays.asList(gsu.possibleCards));
                ArrayList<Card> hand =  new ArrayList<Card>(Arrays.asList(gsu.hand));
                ArrayList<PlayerAction> possibleActions =  new ArrayList<PlayerAction>(Arrays.asList(gsu.possibleActions));
                gameState = gsu.gameState;

                printOverview(gameState);
                
                takeTurn(possibleCards, hand, possibleActions);
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
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

                int option = Integer.parseInt(reader.readLine());
                
                getChoice = false;

                if (option > possibleActions.size() || option <= 0) {
                System.out.println(wrongInput);
                getChoice = true;
                }

                clearScreen();
                if (possibleActions.get(option-1) == PlayerAction.PLAY) {
                    playCard(hand, possibleCards);
                } else {
                    try {
                        outbox.put(possibleActions.get(option-1), -1);
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
                int card = Integer.parseInt(reader.readLine())-1;
                
                if (card >= hand.size()) {
                    clearScreen();
                    System.out.println(wrongInput);
                    continue;
                }
                
                if (!possibleCards.contains(hand.get(card))) {
                    clearScreen();
                    System.out.println("That is not a valid card.");
                    continue;
                }
                
                clearScreen();
                getChoice = false;
                outbox.put(PlayerAction.PLAY, card);
                
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
