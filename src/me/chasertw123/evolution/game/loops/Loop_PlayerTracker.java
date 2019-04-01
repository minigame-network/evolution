package me.chasertw123.evolution.game.loops;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.GameState;
import me.chasertw123.minigames.core.api.misc.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class Loop_PlayerTracker extends GameLoop{

    DecimalFormat df = new DecimalFormat("0.0");

    public Loop_PlayerTracker() {
        super(1, 3);
    }

    @Override
    public void run() {

        if (Main.getInstance().getGameManager().getGameState() != GameState.INGAME) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            Player target = null;
            double distance = 0;
            for (Entity entity : player.getNearbyEntities(200, 200, 200)) {

                if (!(entity instanceof Player))
                    continue;

                double distanceToEntity = player.getLocation().distanceSquared(entity.getLocation());
                if (target == null || distance > distanceToEntity) {
                    target = (Player) entity;
                    distance = distanceToEntity;
                }
            }

            if (target == null)
                continue;

            player.setCompassTarget(target.getLocation());

            if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.COMPASS) {
                Title.sendActionbar(player, ChatColor.WHITE + "" + ChatColor.BOLD + "Nearest Target: " + ChatColor.YELLOW + target.getName()
                        + ChatColor.WHITE + "     " + ChatColor.BOLD + "Distance: " + ChatColor.YELLOW + df.format(player.getLocation().distance(target.getLocation()))
                        + ChatColor.WHITE + "     " + ChatColor.BOLD + "Height: " + ChatColor.YELLOW + df.format((player.getLocation().getY() - target.getLocation().getY())));

                Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                    if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.COMPASS)
                        Title.sendActionbar(player, " ");
                }, 1L);
            }
        }

    }

}
