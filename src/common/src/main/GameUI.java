package common.src.main;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.jspace.FormalField;
import org.jspace.Space;
import common.src.main.GameState.PlayerState;


public class GameUI implements Runnable{
    
    Space outbox;
    Space inbox;
    final static String inboxName = "UIInbox";
    GameState state;
    String userName;
    final static String wrongInput = "Sorry that is not an option. Try again!";


    public GameUI(Space inbox, Space outbox) {
        this.inbox = inbox;
        this.outbox = outbox;
    }

    public void run() {
        // Get message
        // If it is player's turn call takeTurn
        // otherwise call some other method
        
        try {

            while (true) {
                Object[] message = inbox.get(new FormalField(GameState.class), new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class));
                System.out.println("Got message!");
                

                GameState gameState = (GameState) message[0];
                ArrayList<ACard> possibleCards = (ArrayList<ACard>) message[1];
                ArrayList<ACard> hand = (ArrayList<ACard>) message[2];
                ArrayList<PlayerAction> possibleActions = (ArrayList<PlayerAction>) message[3];
                
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

        System.out.println("It is currently " + gameState.currentPlayerName.userName == userName ? "your turn" : (gameState.currentPlayerName.userName + "'s turn"));
        System.out.println("The top card is: " + gameState.topCard.toString());
        if (gameState.streak > 0) {
            System.out.println("There is currently a streak of " + gameState.streak);
        }
    }

    private void takeTurn(ArrayList<ACard> possibleCards, ArrayList<ACard> hand, ArrayList<PlayerAction> possibleActions) {

        boolean getChoice = true;

        while (getChoice) {

            Scanner scanner = new Scanner(System.in);
            try {
                
                if (possibleActions.contains(PlayerAction.PLAY)) printHand(hand);
                
                System.out.println("Choose an option:");
                
                for (int i = 1; i <= possibleActions.size(); i++) {
                    System.out.println(i + ". " + possibleActions.get(i-1).toString());
                }

                int option = scanner.nextInt();
                
                getChoice = false;

                if (option > possibleActions.size() || option <= 0) {
                System.out.println(wrongInput);
                getChoice = true;
                }

                if (possibleActions.get(option-1) == PlayerAction.PLAY) {
                    playCard(hand, possibleCards);
                } else {
                    try {
                        outbox.put(possibleActions.get(option-1), -1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

        } catch (InputMismatchException e) {
            System.out.println(wrongInput);
        }
        
        finally {
            scanner.close();
        }
        
    }

    }

    private void playCard(ArrayList<ACard> hand, ArrayList<ACard> possibleCards) {
        boolean getChoice = true;
        while (getChoice) {

            Scanner scanner = new Scanner(System.in);
            try {
                printHand(hand);
                System.out.println("Choose a card to play");
                int card = scanner.nextInt();
                
                if (card > hand.size()) {
                    System.out.println(wrongInput);
                    continue;
                }
                
                if (!possibleCards.contains(hand.get(card))) {
                    System.out.println("That is not a valid card.");
                    continue;
                }
                
                getChoice = false;
                outbox.put(PlayerAction.PLAY, card);

            } catch (InputMismatchException e) {
                System.out.println(wrongInput);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                scanner.close();
            }
        }
    }


    private void printHand(ArrayList<ACard> hand) {
        int counter = 1;

        System.out.println("Your hand consist of the following cards:");
        for (ACard card : hand) {
            System.out.println(counter++ + ". " + card.toString());
        }
    }


 


}
