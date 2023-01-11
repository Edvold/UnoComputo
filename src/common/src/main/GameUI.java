package common.src.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

public class GameUI {
    
    RemoteSpace outbox;
    SequentialSpace inbox;
    GameState state;
    String username;
    final static String wrongInput = "Sorry that is not an option. Try again!";


    public GameUI(String channel) {
        try {
            outbox = new RemoteSpace("tcp://localhost/" + channel + "?keep");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        SpaceRepository repo = new SpaceRepository();
        inbox = new SequentialSpace();
        repo.add("UIInbox", inbox);
        repo.addGate("tcp://localhost/?keep");
        try {
            outbox.put("UIInbox");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public void takeTurn() {
        // UI for the actual UNO game

        boolean getChoice = true;

        while (getChoice) {

            Scanner scanner = new Scanner(System.in);
            try {
                Object[] message = inbox.get(new ActualField("token"), new FormalField(Object.class), new FormalField(Object.class));
                ArrayList<ACard> possibleCards = (ArrayList<ACard>) message[1];
                ArrayList<ACard> hand = (ArrayList<ACard>) message[2];
                
                printHand(hand);
                
                System.out.println("Choose an option:\n 1. Play a card\n 2. Draw a card\n 3. Say \"UNO!\"\n 4. Object\n 5. End turn");
                
                int option = scanner.nextInt();
                
                getChoice = false;

                switch(option) {
                    case 1:
                        playCard(hand, possibleCards);
                        break;
                    case 2:
                        drawCard();
                        break;
                    case 3:
                        sayUno();
                        break;
                    case 4:
                        object();
                        break;
                    case 5:
                        endTurn();
                        break;
                    default:
                        System.out.println(wrongInput);
                        getChoice = true;
                        break;
            }
            
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                outbox.put("play", card);

            } catch (InputMismatchException e) {
                System.out.println(wrongInput);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                scanner.close();
            }
        }
    }

    private void drawCard() {

    }

    private void sayUno() {

    }

    private void object() {

    }

    private void endTurn() {

    }

    private void printHand(ArrayList<ACard> hand) {
        int counter = 1;

        System.out.println("Your hand consist of the following cards:");
        for (ACard card : hand) {
            System.out.println(counter++ + ". " + card.toString());
        }
    }


}
