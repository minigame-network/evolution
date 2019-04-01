package me.chasertw123.evolution.listeners;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.GameState;
import me.chasertw123.evolution.game.kits.Kit;
import me.chasertw123.evolution.user.EvolutionPlayer;
import me.chasertw123.minigames.core.api.misc.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Event_PlayerSneak implements Listener {

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        EvolutionPlayer evolutionPlayer = Main.getInstance().getEvolutionPlayerManager().getPlayer(p);

        final int evolutionSpeed = (evolutionPlayer.getKit() == Kit.FASTER_EVOLUTION ? 64 : 80);

        if(e.isSneaking() && evolutionPlayer.isEvolutionPrimed() && Main.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            new BukkitRunnable() {
                int count = 0;

                @Override
                public void run() {
                    if(!p.isSneaking() || !evolutionPlayer.isEvolutionPrimed()) {
                        Title.sendAll(p, 1, 3, 1, ChatColor.RED + "" + ChatColor.BOLD + "EVOLUTION FAILED", "", null, null, null);

                        this.cancel();

                        return;
                    }

                    count++;

                    float percentage = (float) ((count * 100) / (evolutionSpeed));
                    int percentComplete = 5 * (Math.round(percentage / 5));

                    StringBuilder sb = new StringBuilder();
                    sb.append(ChatColor.GREEN + "" + ChatColor.BOLD);

                    int smallPercent = percentComplete / 5;
                    for(int i = 0; i < smallPercent; i++)
                        sb.append(":");

                    sb.append(ChatColor.GRAY + "" + ChatColor.BOLD);

                    for(int i = 0; i < (20 - smallPercent); i++)
                        sb.append(":");

                    Title.sendAll(p, 0, 20, 0, sb.toString(), ChatColor.GRAY + "" + ChatColor.ITALIC + "" + percentComplete + "%", null, null, null);

                    if(count >= evolutionSpeed) {
                        // Evolute
                        Main.getInstance().getGameManager().evolute(evolutionPlayer);

                        cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 1);
        }
    }

}
