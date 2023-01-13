package common.src.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.jspace.RemoteSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import common.src.main.Messages.GenericMessage;
import common.src.main.Messages.UpdateMessage;

public final class Host implements Runnable {
    public final static String HOST_NAME = "host";
    public final static int HOST_PORT = 9001;
    private String ip = null;
    private Space lobbySpace;
    private SpaceRepository repository;
    private LobbyUI ui = LobbyUI.getInstance();
    private Map<String, Space> playerSpaces = new HashMap<String, Space>(8);
    private boolean hasGameStarted = false;

    public Host(SpaceRepository repository, Space lobby) {
        this.repository = repository;
        lobbySpace = lobby;
    }

    @Override
    public void run() {
        addHostAsPlayer();
        setupGate();
        hostGame();
    }

    public void hostGame() {
        
        try {
            ui.showMessage("Now waiting for players to join...");
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        openHostInputListener(lobbySpace);

        while (true) { 
            // type message 
            //      = ("ConnectRequest", "<myInboxUri>", "<name>") 
            //      | ("StartGame", "", "")
            try {
                var request = lobbySpace.get(IStateMessage.getGeneralTemplate().getFields());
                var requestCommand = (MessageType) request[0];
                if (requestCommand == MessageType.JoinGameRequest) {
                    var responseUri  = (URI) request[1];
                    var name = (String) request[2];

                    Space responseSpace = new RemoteSpace(responseUri);

                    if(playerSpaces.containsKey(name)){
                        var response = new UpdateMessage("NameTaken");
                        responseSpace.put(response.getFields());
                        continue;
                    }

                    playerSpaces.put(name, responseSpace);
                    responseSpace.put(new UpdateMessage("Accepted").getFields());

                    var update = new UpdateMessage(
                        name + " has joined the game. " 
                        + playerSpaces.size() + " players connected.");
                    playerSpaces.forEach((n, outbox) -> {
                        try {
                            outbox.put(update.getFields());
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    });
                } else if (requestCommand == MessageType.StartGame){
                    if(playerSpaces.size() < 2) {
                        ui.showMessage("Game cannot start with less than 2 players");
                        continue;
                    }
                    break;
                } else {
                    ui.showMessage("Unknown request type: " + Arrays.toString(request) 
                    + "Command: '" + requestCommand + "' is unknown");
                }


                if(playerSpaces.size() >= 8) {
                    break; // Game is ready to be started
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

        
    
        // TODO start game.
        // - Game is managed by controler that starts thread and manages the game
        // progressing
        var startGameMessage = new GenericMessage(MessageType.StartGame, "Game is starting now!");
        playerSpaces.forEach((n, outbox) -> {
            try {
                outbox.put(startGameMessage.getFields());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        hasGameStarted = true;

        var playerConections = new HashMap<String, IPlayerConnection>(playerSpaces.size());
        for (var entry : playerSpaces.entrySet()) {
            var connection = new PlayerConnection(entry.getKey(), entry.getValue());
            playerConections.put(entry.getKey(), connection);
        }

        IGame game = new Game(playerConections, lobbySpace);
        var gameController = new GameController(game);
        gameController.start();
    }
    private void setupGate() {
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
            try {
                ip = ui.getInput("Could not determine local adress to host game.\nEnter the local ip-address to continue: ");
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        var uri = ipToURI(ip);
        repository.addGate(uri);
        repository.add(HOST_NAME, lobbySpace);
    }

    private void addHostAsPlayer() {
        try {
            var request = lobbySpace.get(IStateMessage.getGeneralTemplate().getFields());
            assert(request[0] instanceof MessageType && request[0] == MessageType.JoinGameRequest);

            var responseUri  = (URI) request[1];
            var name = (String) request[2];
            
            Space responseSpace = new RemoteSpace(responseUri);
            
            playerSpaces.put(name, responseSpace);
            responseSpace.put(new UpdateMessage("Accepted").getFields());
        } catch (InterruptedException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static URI ipToURI(String ipString) {
        var uri = URI.create("tcp://" + ipString +":" + HOST_PORT + "/?keep");
        return uri;
    }

    private void openHostInputListener(Space space) {
        var runnable = new Runnable() {

            @Override
            public void run() {
                var ui = LobbyUI.getInstance();
                try {
                    ui.showMessage("When ever you are ready to start the game type 'start'.\nPress 'enter' to view updates on connected players.");
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                while(!hasGameStarted) {
                    try {
                        var text = ui.getInput("> ");
                        if (text.equalsIgnoreCase("start")) {
                            lobbySpace.put(MessageType.StartGame, "", "");
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        };

        new Thread(runnable).start();
    }

    private static class PlayerConnection implements IPlayerConnection {
        private final String name;
        private final Space inbox;

        PlayerConnection(String name, Space inbox) {
            this.name = name; 
            this.inbox = inbox; 
        }

        @Override
        public String getPlayerName() {
            return name;
        }

        @Override
        public Space getPlayerInbox() {
            return inbox;
        }

    }
}
