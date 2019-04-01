package me.chasertw123.evolution.game.boards;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.GameManager;
import me.chasertw123.evolution.maps.GameMap;
import me.chasertw123.evolution.user.EvolutionPlayer;
import me.chasertw123.minigames.core.utils.scoreboard.Entry;
import me.chasertw123.minigames.core.utils.scoreboard.EntryBuilder;
import me.chasertw123.minigames.core.utils.scoreboard.ScoreboardHandler;
import me.chasertw123.minigames.core.utils.scoreboard.SimpleScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class Scoreboard_Lobby extends SimpleScoreboard {

    public Scoreboard_Lobby(EvolutionPlayer evolutionPlayer) {
        super(evolutionPlayer.getPlayer());

        this.setUpdateInterval(2L);
        this.setHandler(new ScoreboardHandler() {

            @Override
            public String getTitle(Player player) {
                int time = Main.getInstance().getGameManager().getCurrentGameLoop().interval, minutes = time / 60, seconds = time % 60;
                String mins = (minutes < 10 ? "0" : "") + minutes, secs = (seconds < 10 ? "0" : "") + seconds;

                if(Bukkit.getServer().getOnlinePlayers().size() == 1)
                    return ChatColor.AQUA + "" + ChatColor.BOLD + "Evolution"; // Don't show time

                return ChatColor.AQUA + "" + ChatColor.BOLD + "Evolution " + ChatColor.GREEN + "" + mins + ":" + secs;
            }

            @Override
            public List<Entry> getEntries(Player player) {

                EntryBuilder entryBuilder = new EntryBuilder()
                        .blank()
                        .next(ChatColor.BLUE + "Players: " + ChatColor.WHITE + "" + Bukkit.getServer().getOnlinePlayers().size() + "/" + GameManager.MAX_PLAYERS)
                        .blank()
                        .next(ChatColor.GREEN + "Kit: " + ChatColor.WHITE + evolutionPlayer.getKit().getName())
                        .blank()
                        .next(ChatColor.YELLOW + "Map Votes");

                for (GameMap gameMap : Main.getInstance().getMapManager().getMaps())
                    entryBuilder.next(ChatColor.WHITE + gameMap.getName() + ChatColor.GREEN + " (" + gameMap.getVotes() + ")");

                return entryBuilder.blank().build();
            }
        });
    }



}
