package common.src.main;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;

public class UI {
    
    RemoteSpace outbox;
    SequentialSpace inbox;
    GameState state;
    String username;


    public UI(String channel) {
        try {
            outbox = new RemoteSpace("tcp://localhost/" + channel + "?keep");
        } catch (IOException e) {
            e.printStackTrace();
        }

        inbox = new SequentialSpace();

    }

    public void runLobby() {
        // UI for the lobby

        Scanner scanner = new Scanner(System.in);

        boolean getChoice = true;
        System.out.println("Hello and welcome to UNO!\n");

        System.out.println("What is your name? ");
        username = scanner.nextLine();
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
                        System.out.println(input + "is not an available option. Try again!");
                }

            } catch (InputMismatchException e) {
                e.printStackTrace();
            }
        }
        scanner.close();

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
        // UI for the actual UNO game
    }



}
