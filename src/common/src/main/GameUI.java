package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.PileSpace;
import org.jspace.Space;
import common.src.main.GameState.PlayerState;
import common.src.main.Messages.UIMessage;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class GameUI implements Runnable {

    Space outbox;
    PileSpace inbox;
    GameState gameState;
    String userName;
    final static String wrongInput = "Sorry that is not an option. Try again!";
    BufferedReader reader = new BufferedReader((new InputStreamReader(System.in)));
    Thread objectCheckerThread = new Thread();
    ObjectChecker objectChecker;

    public GameUI(PileSpace inbox, Space outbox, String name) {
        this.inbox = inbox;
        this.outbox = outbox;
        userName = name;
        objectCheckerThread.setDaemon(true);
    }

    public void run() {

        try {

            while (true) {

                GameStateUpdate gsu = (GameStateUpdate) inbox.get(new ActualField(MessageType.PlayerMessage),
                        new FormalField(Object.class), new FormalField(String.class))[1];
                

                        inbox.getAll(  //If any older states exits, throw them away
                    new ActualField(MessageType.PlayerMessage), 
                    new FormalField(Object.class), 
                    new FormalField(String.class));

                ArrayList<Card> possibleCards = new ArrayList<Card>(Arrays.asList(gsu.possibleCards));
                ArrayList<Card> hand = new ArrayList<Card>(Arrays.asList(gsu.hand));
                ArrayList<PlayerAction> possibleActions = new ArrayList<PlayerAction>(
                        Arrays.asList(gsu.possibleActions));
                gameState = gsu.gameState;

                // Reset objectCheckerThread

                stopObjectCheckerThread();

                // Print the current state of the game
                printOverview();

                // Has game ended?
                var gameEndMessage = inbox.getp(new ActualField(MessageType.GameOver), new FormalField(Object.class),
                        new FormalField(String.class));

                if (gameEndMessage != null) {

                    // Game has ended
                    end(gameEndMessage);
                    break;
                }

                // Get and print update message if any exists
                var message = inbox.getAll(new ActualField(MessageType.Update), new FormalField(Object.class),
                        new FormalField(String.class));

                if (message.size() > 0) {
                    printUpdateMessage(message);
                }

                if (possibleActions.size() == 1 && possibleActions.contains(PlayerAction.OBJECT)) {
                    objectChecker = new ObjectChecker(outbox);
                    objectCheckerThread.join(10);
                    objectCheckerThread = new Thread(objectChecker);
                    objectCheckerThread.setDaemon(true);
                    objectCheckerThread.start();

                } else {
                    takeTurn(possibleCards, hand, possibleActions);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void stopObjectCheckerThread() {
        if (objectCheckerThread.isAlive()) {
            objectChecker.stop();
            objectCheckerThread.interrupt();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void end(Object[] message) {
        stopObjectCheckerThread();
        printUpdateMessage(message);
        System.exit(0);
    }

    private void printUpdateMessage(Object[] message) {
        List<Object[]> list = new ArrayList<Object[]>() {
        };
        list.add(message);
        printUpdateMessage(list);
    }

    private void printUpdateMessage(List<Object[]> message) {
        System.out.println("===========================================");
        System.out.println("UPDATE FROM GAME:");
        for (Object[] messageArray : message) {
            System.out.println((String) messageArray[2]);
        }
        System.out.println("===========================================");
    }

    private void printOverview() {

        if (gameState.saidUNO)
            System.out.println(gameState.turnOrder[gameState.turnOrder.length - 1].userName + " has said UNO!");
        System.out.println("A new round has begun!");
        System.out.println("The turn-order is:\n");

        for (PlayerState player : gameState.turnOrder) {
            System.out.println(player.toString());
        }

        System.out.println();

        System.out.println("It is currently " + (gameState.currentPlayerName.userName.equals(userName) ? "your turn"
                : (gameState.currentPlayerName.userName + "'s turn")));

        printTopCard();

        if (gameState.streak > 0) {
            System.out.println("There is currently a streak of " + gameState.streak);
        }
    }

    private void takeTurn(ArrayList<Card> possibleCards, ArrayList<Card> hand,
            ArrayList<PlayerAction> possibleActions) {

        boolean getChoice = true;

        while (getChoice) {

            try {
                Object[] update = inbox.getp(new ActualField(MessageType.PlayerMessage),
                        new FormalField(Object.class), new FormalField(String.class));

                if (update != null) {
                    GameStateUpdate gsu = (GameStateUpdate)update[1];
                    possibleCards = new ArrayList<Card>(Arrays.asList(gsu.possibleCards));
                    hand = new ArrayList<Card>(Arrays.asList(gsu.hand));
                    possibleActions = new ArrayList<PlayerAction>(
                            Arrays.asList(gsu.possibleActions));
                    gameState = gsu.gameState;
                    clearScreen();
                    printOverview();
                }

                if (possibleActions.contains(PlayerAction.PLAY))
                    printHand(hand);

                System.out.println("Choose an option:");

                for (int i = 1; i <= possibleActions.size(); i++) {
                    PlayerAction pa = possibleActions.get(i - 1);
                    System.out.println(i + ". " + pa.toString());
                }

                // Get choice input from player
                int option = Integer.parseInt(reader.readLine()) - 1;

                // Player choses a non-existing option
                if (option >= possibleActions.size() || option < 0) {
                    System.out.println(wrongInput);
                    continue;
                }

                clearScreen();

                getChoice = false;

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
                int card = Integer.parseInt(reader.readLine()) - 1;

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

                String returnMessage = String.valueOf(card);

                Color color = null;

                if (hand.get(card).getColor() == Color.BLACK) {
                    System.out.println("Choose a color:");
                    System.out.println("1. " + Color.BLUE);
                    System.out.println("2. " + Color.RED);
                    System.out.println("3. " + Color.GREEN);
                    System.out.println("4. " + Color.YELLOW);

                    int index = Integer.parseInt(reader.readLine());

                    if (index > 4 || index < 1) {
                        clearScreen();
                        System.out.println("That is not a valid color");
                        continue;
                    }

                    switch (index) {
                        case 1:
                            color = Color.BLUE;
                            break;
                        case 2:
                            color = Color.RED;
                            break;
                        case 3:
                            color = Color.GREEN;
                            break;
                        case 4:
                            color = Color.YELLOW;
                            break;
                    }
                }

                clearScreen();

                getChoice = false;

                if (color != null)
                    returnMessage += " " + color.toString().toUpperCase();

                // Inform player of choice
                outbox.put(new UIMessage(PlayerAction.PLAY, returnMessage).getFields());

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
            System.out.println(counter++ + ". " + card.toStringWithColor());
        }
    }

    private void printTopCard() {
        System.out.println("The top card is: " + gameState.topCard.toStringWithColor());

    }

    private void clearScreen() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

}

class ObjectChecker implements Runnable {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final Space outbox;

    public ObjectChecker(Space outbox) {
        this.outbox = outbox;
    }

    @Override
    public void run() {
        System.out.println("You can choose to");
        System.out.println("1. " + PlayerAction.OBJECT);
        try {
            String input = reader.readLine();
            if (input.equals("1")) {
                System.out.println("Got input");
                outbox.put(new UIMessage(PlayerAction.OBJECT, "").getFields());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        try {
            // Can't interrupt reader.readLine()
            // It will keep waiting for input even though we interrupt the thread
            // So a simulation of a press of enter is needed to end the read
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

}
