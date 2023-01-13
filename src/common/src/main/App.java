package common.src.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import org.jspace.FormalField;
import org.jspace.RandomSpace;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import common.src.main.GameState.PlayerState;
import common.src.main.Messages.PlayerMessage;


public class App {

	public static void main(String[] argv) throws UnknownHostException, IOException, InterruptedException {

		/*/
		// (HOST) Set up repo
		SpaceRepository repo = new SpaceRepository();
		String ip = InetAddress.getLocalHost().getHostAddress();
		
		Space inbox = new SequentialSpace();
		repo.add("inbox", inbox);
		
		repo.addGate("tcp://" + ip +":9001/?keep");

		// (USER) Get ip addr of host @ user
		Scanner scanner = new Scanner(System.in);
		String hostIP = scanner.next();
		scanner.close();
		
		// (USER) Connect to host's space
		RemoteSpace space = new RemoteSpace("tcp://" + hostIP + ":9001/inboxB?keep");
		space.put("Message from player :)");

		// (HOST) get message
		inbox.get(new FormalField(String.class));*/


		SpaceRepository repo = new SpaceRepository();

		Space inbox = new SequentialSpace();
		repo.add("test", inbox);
		repo.addGate("tcp://localhost:9001/?keep");


		GameUI ui = new GameUI("test", "panda");
		
		String outboxName = (String)inbox.get(new FormalField(String.class))[0];
		RemoteSpace outbox = new RemoteSpace("tcp://localhost:9002/" + outboxName + "?keep");

		
		PlayerState ps = new PlayerState("panda", 5);
		PlayerState[] turnOrder = {ps};
		
		Card tc = new Card(Color.GREEN, Action.FOUR);
		
		GameState gs = new GameState(ps, tc, turnOrder, 0);


		Card card1 = new Card(Color.GREEN, Action.TWO);
		Card card2 = new Card(Color.BLUE, Action.FOUR);
		Card card3 = new Card(Color.RED, Action.THREE);
		
		ArrayList<Card> hand = new ArrayList<>();
		hand.add(card1);
		hand.add(card2);
		hand.add(card3);
		
		//Player player = new Player("panda", null, null, null);
		
		ArrayList<Card> possibleCards = new ArrayList<>();
		possibleCards.add(card1);
		possibleCards.add(card2);

		ArrayList<PlayerAction> pas = new ArrayList<>();
		pas.add(PlayerAction.DRAW);
		pas.add(PlayerAction.ENDTURN);
		pas.add(PlayerAction.PLAY);
		
		Thread uithread = new Thread(ui);
		uithread.start();
		
		outbox.put(new PlayerMessage(gs, (Card[]) possibleCards.toArray(new Card[0]), (Card[]) hand.toArray(new Card[0]), (PlayerAction[]) pas.toArray(new PlayerAction[0])));
	}

}