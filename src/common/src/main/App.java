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


public class App {

	public static void main(String[] argv) throws UnknownHostException, IOException, InterruptedException {
			SpaceRepository repo = new SpaceRepository();
			var lobbyUI = LobbyUI.getInstance();

			new Thread(new Lobby(repo)).start();

			lobbyUI.start();

			// Space inbox = new SequentialSpace();
			// repo.add("test", inbox);
			// repo.addGate("tcp://localhost:9001/test?keep");


			// GameUI ui = new GameUI("test", "panda");
			
			// String outboxName = (String)inbox.get(new FormalField(String.class))[0];
			// RemoteSpace outbox = new RemoteSpace("tcp://localhost:9001/" + outboxName + "?keep");

			
			// PlayerState ps = new PlayerState("panda", 5);
			// PlayerState[] turnOrder = {ps};
			
			// Card tc = new Card(Color.GREEN, Action.FOUR);
			
			// GameState gs = new GameState(ps, tc, turnOrder, 0);


			// ACard card1 = new Card(Color.GREEN, Action.TWO);
			// ACard card2 = new Card(Color.BLUE, Action.FOUR);
			// ACard card3 = new Card(Color.RED, Action.THREE);
			
			// ArrayList<ACard> hand = new ArrayList<>();
			// hand.add(card1);
			// hand.add(card2);
			// hand.add(card3);
			
			// //Player player = new Player("panda", null, null, null);
			
			// ArrayList<ACard> possibleCards = new ArrayList<>();
			// possibleCards.add(card1);
			// possibleCards.add(card2);

			// ArrayList<PlayerAction> pas = new ArrayList<>();
			// pas.add(PlayerAction.DRAW);
			// pas.add(PlayerAction.ENDTURN);
			// pas.add(PlayerAction.PLAY);
			
			// Thread uithread = new Thread(ui);

			// System.out.println("Starting thread...");
			// uithread.start();
			// System.out.println("Thread running");

			// outbox.put(gs, possibleCards, hand, pas);

	}

}