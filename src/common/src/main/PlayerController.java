package common.src.main;

public class PlayerController {
    private final IPlayer player;

    public PlayerController(IPlayer player) {
        this.player = player;
    }

    public void start() throws InterruptedException {
        player.run();
    }
}
