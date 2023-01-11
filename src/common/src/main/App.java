package common.src.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.util.Scanner;

import org.jspace.FormalField;
import org.jspace.RandomSpace;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

public class App {

	public static void main(String[] argv) throws UnknownHostException, IOException, InterruptedException {


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
			inbox.get(new FormalField(String.class));



	}

}