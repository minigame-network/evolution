package me.chasertw123.evolution.game.loops;

import me.chasertw123.evolution.Main;
import me.chasertw123.minigames.core.api.misc.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Loop_Countdown extends GameLoop {

    public Loop_Countdown() {
        super(10, 20);
    }

    @Override
    public void run() {
        setInterval(getInterval() - 1);

        if (interval > 5)
            for (Player player : Bukkit.getOnlinePlayers())
                Title.sendTitle(player,0, 20, 0, ChatColor.GREEN + "" + interval);

        else if (interval <= 5 && interval > 1)
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1F, 0.1F);
                Title.sendTitle(player, 0, 20, 0, ChatColor.YELLOW + "" + ChatColor.BOLD + interval);
            }

        else if (interval == 1)
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1F, 2F);
                Title.sendTitle(player, 0, 20, 0, ChatColor.RED + "" + ChatColor.BOLD + interval);
            }

        if(interval == 0) {

            Main.getInstance().getGameManager().realStartGame();

            this.cancel();
        }
    }

}
