package me.chasertw123.evolution.game.loops;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.GameManager;
import me.chasertw123.evolution.maps.GameMap;
import me.chasertw123.evolution.user.EvolutionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Loop_Lobby extends GameLoop {

    private static final int LOBBY_COUNTDOWN = 60;

    public Loop_Lobby() {
        super(LOBBY_COUNTDOWN, 20L);
    }

    @Override
    public void run() {
        int currentPlayerCount = Bukkit.getServer().getOnlinePlayers().size();

        if(currentPlayerCount >= GameManager.REQUIRED_PLAYERS) {
            setInterval(getInterval() - 1);

            if(getInterval() == 5) {
                // Load the game world
                Main.getInstance().getVoteManager().setVotingActive(false);

                GameMap winningMap = Main.getInstance().getVoteManager().getWinningMap();
                Bukkit.broadcastMessage(ChatColor.GREEN + "Voting has ended, the winning map is " + ChatColor.AQUA + winningMap.getName());

                Main.getInstance().getGameManager().setGameMap(winningMap);
                Main.getInstance().getMapManager().loadGameWorld(winningMap.getDirectoryName());
            }

            if(getInterval() <= 0) {
                Main.getInstance().getGameManager().startGame();

                this.cancel();
            }
        } else {
            setInterval(LOBBY_COUNTDOWN);
        }
    }

}
