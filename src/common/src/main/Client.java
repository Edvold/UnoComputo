package common.src.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.jspace.PileSpace;
import org.jspace.RemoteSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import common.src.main.Messages.AStateMessage;

public class Client implements Runnable {
    private final String CLIENT_NAME = "client";
    
    private String ip = null;
    private Space lobbySpace;
    private Space playerInbox;
    private SpaceRepository repository;
    private LobbyUI ui = LobbyUI.getInstance();
    private URI inboxUri; 


    /**
     * Join a game with an as of yet missing lobby space (connecting to a remote
     * lobby for example)
     */
    public Client(SpaceRepository repository, Space inbox) {
        this(repository, inbox, null);
    }

    /**
     * Join a game with a known lobby. This is the case if a remote space has been
     * made for a lobby or
     * if the game is run locally.
     * 
     * @param lobby the lobby to communicate with to join. if null it will be
     *              ignored and user is asked
     *              to provide remote lobby uri.
     */
    public Client(SpaceRepository repository, Space inbox, Space lobby) {
        lobbySpace = lobby;
        this.playerInbox = inbox;
        this.repository = repository;
    }

    @Override
    public void run() {
        try {
            start();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws InterruptedException, UnknownHostException, IOException {
        setupGate();

        if (lobbySpace == null) {
            setupRemoteLobby();
        }
        
        String name = null;

        var gotAccepted = false;
        do {
            name = ui.getInput("Enter your name: ");
            lobbySpace.put(MessageType.JoinGameRequest, inboxUri, name);
            var response = playerInbox.get(IMessage.getGeneralTemplate().getFields());
            
            switch((String)response[2]) {
                case "Accepted" -> { gotAccepted = true; ui.showMessage("You have successfully joined the game lobby"); }
                case "NameTaken" -> ui.showMessage("Name was taken, try again.");
                default -> ui.showMessage("An unexpected answer arraived: " + Arrays.toString(response));
            };
        } while(!gotAccepted);

        var hasGameStarted = false;
        while (!hasGameStarted) {
            var response = playerInbox.get(IMessage.getGeneralTemplate().getFields());
            var messageType = (MessageType)response[0];
            switch(messageType) {
                case StartGame -> hasGameStarted = true;
                default -> {
                    var message = AStateMessage.fromResponse(response);
                    ui.showMessage(message);
                }
            }
        }

        ui.stop();

        PileSpace uiSpace = new PileSpace();
        new Thread(new GameUI(uiSpace, playerInbox, name)).start();
        
        IPlayer player = new Player(name, lobbySpace, uiSpace, playerInbox);
        var playerController = new PlayerController(player);
        playerController.start();
    }

    public void setupRemoteLobby() throws InterruptedException, UnknownHostException, IOException {
        var lobbyIp = ui.getInput("Enter the ip-address of the lobby you wish to join: ");
        var lobbyUri = ipToUri(lobbyIp, Host.HOST_PORT, Host.HOST_NAME);
        lobbySpace = new RemoteSpace(lobbyUri);
    }

    private void setupGate() {
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int port = 9001;
        
        while (true) {
            try {
                var portInput = ui.getInput("Enter your port or press 'Enter' to use default (" + port + ")");
                
                if (portInput.isBlank()) {
                    break;
                }

                try {
                    port = Integer.parseInt(portInput);
                    break;
                } catch (NumberFormatException e) {
                    ui.showMessage("Not a valid port number: " + e.getMessage());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        var uri = ipToUri(ip, port);
        repository.addGate(uri);
        repository.add(CLIENT_NAME, playerInbox);
        inboxUri = ipToUri(ip, port, CLIENT_NAME);
    }

    private static URI ipToUri(String ipString, int port) { return ipToUri(ipString, port, ""); }
    private static URI ipToUri(String ipString, int port, String spaceName) {
        var trimmedIp = ipString.replace(" ", "");
        var uri = URI.create("tcp://" + trimmedIp +":" + port + "/" + spaceName + "?keep");
        return uri;
    }
}
