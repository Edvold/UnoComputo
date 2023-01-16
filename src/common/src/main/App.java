package common.src.main;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.SpaceRepository;

public class App {

	public static void main(String[] argv) throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository repo = new SpaceRepository();
		var lobbyUI = LobbyUI.getInstance();

		new Thread(new Lobby(repo)).start();

		lobbyUI.start();
	}

}