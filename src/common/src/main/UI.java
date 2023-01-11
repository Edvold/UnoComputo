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

public class UI {
    
    RemoteSpace outbox;
    SequentialSpace inbox;
    GameState state;
    String username;
    final static String wrongInput = "Sorry that is not an option. Try again!";


    public UI(String channel) {
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

    public void runLobby() {
        // UI for the lobby

        Scanner scanner = new Scanner(System.in);

        boolean getChoice = true;
        System.out.println("Hello and welcome to UNO!\n");

        System.out.println("What is your name? ");
        username = scanner.next();
        System.out.println("Succesfully changed your name to: " + username);

        while (getChoice) {
            System.out.println("Choose an option\n 1. Create a lobby\n 2. Join a lobby");
            try {
                int input = scanner.nextInt();
                switch(input) {
                    case 1: 
                        getChoice = false;
                        createLobby();
                        break;
                    case 2:
                        getChoice = false;
                        joinLobby();
                        break;
                    default:
                        System.out.println(wrongInput);
                }

            } catch (InputMismatchException e) {
                e.printStackTrace();
            } finally {
                scanner.close();
            }
        }

    }
    
    void createLobby() {
        System.out.println("What do you wany your lobby to be named? ");
        Scanner scanner = new Scanner(System.in);

        String name = scanner.nextLine();
        System.out.println("Creating lobby with name " + name + "...");
        // Send message to lobby to create a new lobby with name name


        scanner.close();
    }

    void joinLobby() {
        System.out.println("What lobby do you want to join? ");
        Scanner scanner = new Scanner(System.in);

        String name = scanner.nextLine();
        System.out.println("Joining lobby with name" + name + "...");

        // Send message to lobby to join lobby with name name

        scanner.close();
    }

    public void runGame() {
        // TODO: Surround in while loop
        // UI for the actual UNO game
        Scanner scanner = new Scanner(System.in);
        try {
            Object[] message = inbox.get(new ActualField("token"), new FormalField(Object.class), new FormalField(Object.class));
            ArrayList<ACard> possibleCards = (ArrayList<ACard>) message[1];
            ArrayList<ACard> hand = (ArrayList<ACard>) message[2];
            
            printHand(hand);
            
            System.out.println("Choose an option:\n 1. Play a card\n 2. Draw a card\n 3. Say \"UNO!\"\n 4. Object\n 5. End turn");
            
            int option = scanner.nextInt();

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

    private void playCard(ArrayList<ACard> hand, ArrayList<ACard> possibleCards) {
        // TODO: Surround in while loop
        Scanner scanner = new Scanner(System.in);
        try {

            printHand(hand);
            System.out.println("Choose a card to play");
            int card = scanner.nextInt();
            outbox.put("play", card);

        } catch (InputMismatchException e) {
            System.out.println(wrongInput);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
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