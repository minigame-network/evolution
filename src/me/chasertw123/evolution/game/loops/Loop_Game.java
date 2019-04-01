package me.chasertw123.evolution.game.loops;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.GameManager;
import me.chasertw123.evolution.game.GameState;

public class Loop_Game extends GameLoop {

    public Loop_Game() {
        super(GameManager.GAME_TIME, 20L);
    }

    @Override
    public void run() {
        if(Main.getInstance().getGameManager().getGameState() != GameState.INGAME) {
            return;
        }

        Main.getInstance().getGameManager().checkGame();

        setInterval(getInterval() - 1);

        if(getInterval() == 0) {
            // end game
            Main.getInstance().getGameManager().endGame();

            this.cancel();
        }
    }

}
