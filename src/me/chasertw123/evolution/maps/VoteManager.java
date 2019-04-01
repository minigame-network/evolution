package me.chasertw123.evolution.maps;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.user.EvolutionPlayer;

public class VoteManager {

    private boolean isVotingActive = true;

    public int getVotes(String map) {
        int count = 0;

        for(EvolutionPlayer evolutionPlayer : Main.getInstance().getEvolutionPlayerManager().getEvolutionPlayers())
            if(evolutionPlayer.hasVoted() && evolutionPlayer.getVotedMap().equalsIgnoreCase(map))
                count++;

        return count;
    }

    public int getVotes(GameMap gameMap) {
        return getVotes(gameMap.getName());
    }

    public GameMap getWinningMap() {
        GameMap winning = Main.getInstance().getMapManager().getMaps().get(0);
        int winningCount = 0;

        for(GameMap gm : Main.getInstance().getMapManager().getMaps())
            if(getVotes(gm.getName()) > winningCount) {
                winning = gm;
                winningCount = getVotes(gm.getName());
            }

        return winning;
    }

    public boolean isVotingActive() {
        return isVotingActive;
    }

    public void setVotingActive(boolean votingActive) {
        isVotingActive = votingActive;
    }

}
