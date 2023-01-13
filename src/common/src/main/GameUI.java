package common.src.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import common.src.main.GameState.PlayerState;
import common.src.main.Messages.PlayerMessage;


public class GameUI implements Runnable{
    
    RemoteSpace outbox;
    SequentialSpace inbox;
    final static String inboxName = "UIInbox";
    GameState state;
    String userName;
    final static String wrongInput = "Sorry that is not an option. Try again!";
    SpaceRepository repo = new SpaceRepository();


    public GameUI(String channel, String userName) {

        this.userName = userName;

        try {
            outbox = new RemoteSpace("tcp://localhost:9001/" + channel + "?keep");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        inbox = new SequentialSpace();
        repo.add(inboxName, inbox);
        repo.addGate("tcp://localhost:9002/?keep");
        try {
            outbox.put(inboxName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        // Get message
        // If it is player's turn call takeTurn
        // otherwise call some other method
        
        try {

            while (true) {
                //var m  = new PlayerMessage(null, null, null, null).getTemplateBuilder()
                //.addActualType().build();
                Object message = (inbox.get(IMessage.getGeneralTemplate().getFields()))[1];
                
                assert true;
                //ArrayList<ACard> possibleCards = new ArrayList<ACard>(Arrays.asList(message.possibleCards));
                //ArrayList<ACard> hand =  new ArrayList<ACard>(Arrays.asList(message.hand));
                //ArrayList<PlayerAction> possibleActions =  new ArrayList<PlayerAction>(Arrays.asList(message.possibleActions));

                // ArrayList<String> possibleStringActions = (ArrayList<String>) message[3];
                
                // ArrayList<PlayerAction> possibleActions = new ArrayList<>();
                
                // for (String playerAction : possibleStringActions) {
                //     possibleActions.add(PlayerAction.valueOf(playerAction));
                // }

                //printOverview(message.gameState);
                //
                //takeTurn(possibleCards, hand, possibleActions);
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

        System.out.println("It is currently " + (gameState.currentPlayerName.userName == userName ? "your turn" : (gameState.currentPlayerName.userName + "'s turn")));
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
                    PlayerAction pa = possibleActions.get(i-1);
                    System.out.println(i + ". " + pa.toString());
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
