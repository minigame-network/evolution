package me.chasertw123.evolution.game.loops;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.ai.AIPlayer;

public class Loop_AI extends GameLoop {

    public Loop_AI() {
        super(1, 1);
    }

    @Override
    public void run() {
        for(AIPlayer aiPlayer : Main.getInstance().getGameManager().getAiPlayers())
            aiPlayer.aiTick();
    }
}
