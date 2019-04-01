package me.chasertw123.evolution.game.boards;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.ai.GeneralPlayerContainer;
import me.chasertw123.evolution.user.EvolutionPlayer;
import me.chasertw123.minigames.core.utils.scoreboard.Entry;
import me.chasertw123.minigames.core.utils.scoreboard.EntryBuilder;
import me.chasertw123.minigames.core.utils.scoreboard.ScoreboardHandler;
import me.chasertw123.minigames.core.utils.scoreboard.SimpleScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class Scoreboard_Game extends SimpleScoreboard {

    public Scoreboard_Game(EvolutionPlayer evolutionPlayer) {
        super(evolutionPlayer.getPlayer());

        this.setUpdateInterval(2L);
        this.setHandler(new ScoreboardHandler() {
            @Override
            public String getTitle(Player player) {
                int time = Main.getInstance().getGameManager().getCurrentGameLoop().interval, minutes = time / 60, seconds = time % 60;
                String mins = (minutes < 10 ? "0" : "") + minutes, secs = (seconds < 10 ? "0" : "") + seconds;

                return ChatColor.AQUA + "" + ChatColor.BOLD + "Evolution " + ChatColor.GREEN + "" + mins + ":" + secs;
            }

            @Override
            public List<Entry> getEntries(Player player) {
                EvolutionPlayer evolutionPlayer = Main.getInstance().getEvolutionPlayerManager().getPlayer(player);

//                List<EvolutionPlayer> evolutionPlayers = Main.getInstance().getEvolutionPlayerManager().getEvolutionPlayersSortedByScore();
                List<GeneralPlayerContainer> generalPlayerContainers = Main.getInstance().getGameManager().getGeneralPlayersByScore();

                EntryBuilder entryBuilder = new EntryBuilder()
                        .blank()
                        .next(ChatColor.GREEN + "" + ChatColor.BOLD + "My Info")
                        .next(ChatColor.WHITE + "Score: " + ChatColor.GREEN + evolutionPlayer.getScore())
                        .next(ChatColor.WHITE + "Ability: " + ChatColor.GREEN + evolutionPlayer.getEvolution().getCustomName())
                        .blank()
                        .next(ChatColor.YELLOW + "" + ChatColor.BOLD + "Game Info");


                int count = 0;
                for(GeneralPlayerContainer e : generalPlayerContainers) {
                    entryBuilder.next(ChatColor.WHITE + e.getName() + ChatColor.GREEN + " (" + e.getScore() + ") ");

                    if(count != 4)
                        count++;
                    else
                        break;
                }

                return entryBuilder.blank().build();
            }
        });
    }
}
