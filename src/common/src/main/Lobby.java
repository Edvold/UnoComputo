package common.src.main;

import java.io.IOException;
import org.jspace.QueueSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

public class Lobby implements Runnable {
    private SpaceRepository repository;
    private LobbyUI ui = LobbyUI.getInstance();

    public Lobby(SpaceRepository repository) {
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

    private void start() throws InterruptedException, IOException {
        ui.showMessage(
                "Do you want to host a new game or join another hosts game?"
                        + "\n\t0 - Host Game\n\t1 - Join Game");

        var option = -1;
        while (true) {
            String selection = null;
            selection = ui.getInput("Enter your selection: ");

            try {
                option = Integer.parseInt(selection);
                if (option == 0 || option == 1) break; // Valid option was entered
            } catch (NumberFormatException e) {
                ui.showMessage("Error: Input must be an integer.");
            }

            
            ui.showMessage(
                    "Invalid option. You entered: '" + selection + "'"
                            + "\nValid options are:\n\t0 - Host Game\n\t1 - Join Game");
        }

        Space hostSpace = new QueueSpace();
        Space clientSpace = new SequentialSpace();

        switch (option) {
            case 0 -> {
                new Thread(new Client(repository, clientSpace, hostSpace)).start(); 
                new Thread(new Host(repository, hostSpace)).start();
            }
            case 1 -> new Client(repository, clientSpace).start();
            default -> 
                throw new UnsupportedOperationException("No option '" + option + "'' has been implemented.");
        }
    }
}
