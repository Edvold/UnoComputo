package common.src.main;

public class GameController {
    private final IGame game;

    public GameController(IGame game) {
        this.game = game;
    }

    public void start() {
        game.startGame();
        
        do {
            try {
                game.startNextRound();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!game.isGameOver());
        
        var winner = game.getWinner();
        game.endGame(winner);
    }
}
