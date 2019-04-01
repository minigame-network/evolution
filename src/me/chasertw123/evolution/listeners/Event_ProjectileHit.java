package me.chasertw123.evolution.listeners;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.evolutions.Evolution;
import me.chasertw123.evolution.user.EvolutionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Event_ProjectileHit implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        for (EvolutionPlayer evolutionPlayer : Main.getInstance().getEvolutionPlayerManager().getEvolutionPlayers()) {
            if (evolutionPlayer.getAbilityEntities().contains(e.getEntity().getUniqueId())) {
                evolutionPlayer.removeAbilityEntity(e.getEntity().getUniqueId());

                if(e.getEntity().getType() == EntityType.SNOWBALL) {
                    if(evolutionPlayer.getEvolution() == Evolution.SPIDER) {

                        Location blockLocation = e.getEntity().getLocation().clone();

                        for(Player player : Bukkit.getServer().getOnlinePlayers())
                            if(player.getLocation().distance(e.getEntity().getLocation()) <= 8f && player.getUniqueId()
                                    != evolutionPlayer.getPlayer().getUniqueId()) {

                                blockLocation = player.getLocation().clone();

                                break;
                            }

                        Location[] cylLocs = generateCylLocs(blockLocation);

                        // This is the player that shot it
                        for(Location b : cylLocs)
                            if(b.getBlock().getType() == Material.AIR)
                                b.getBlock().setType(Material.WEB);

                        final Location insideLocation = blockLocation.clone();

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (insideLocation.getBlock().getType() != Material.WEB) {
                                    cancel();

                                    return;
                                }

                                for(Location l : cylLocs)
                                    for (Player player : Bukkit.getServer().getOnlinePlayers())
                                        if (player.getLocation().getBlockX() == l.getBlockX()
                                                && player.getLocation().getBlockY() == l.getBlockY()
                                                && player.getLocation().getBlockZ() == l.getBlockZ()
                                                && player.getUniqueId() != evolutionPlayer.getUUID())
                                            player.damage(3, evolutionPlayer.getPlayer());
                            }
                        }.runTaskTimer(Main.getInstance(), 0, 20);

                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(),
                                () -> {
                            for(Location l : cylLocs)
                                if(l.getBlock().getType() == Material.WEB)
                                    l.getBlock().setType(Material.AIR);
                            }, 20 * 3);
                    } else if (evolutionPlayer.getEvolution() == Evolution.SNOW_GOLEM) {
                        // Find the nearest player
                        for(Player player : Bukkit.getServer().getOnlinePlayers())
                            if(player.getLocation().distance(e.getEntity().getLocation()) <= 3f && player.getUniqueId()
                                    != evolutionPlayer.getPlayer().getUniqueId()) {
                                player.damage(2, evolutionPlayer.getPlayer());
                        }
                    }
                } else if (e.getEntity().getType() == EntityType.ARROW) {
                    if (evolutionPlayer.getEvolution() == Evolution.SKELETON) {
                        TNTPrimed tntPrimed = (TNTPrimed) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.PRIMED_TNT);
                        tntPrimed.setFuseTicks(1);

                        evolutionPlayer.addAbilityEntity(tntPrimed.getUniqueId());

                        Bukkit.getOnlinePlayers().forEach(player -> me.chasertw123.minigames.core.Main.getEntityHider().hideEntity(player, tntPrimed));
                    } else if (evolutionPlayer.getEvolution() == Evolution.BLAZE) {
                        Location tempLocation = e.getEntity().getLocation().clone();

                        for(Player player : Bukkit.getServer().getOnlinePlayers())
                            if(player.getLocation().distance(tempLocation) <= 4f && player.getUniqueId()
                                    != evolutionPlayer.getPlayer().getUniqueId())
                                player.setFireTicks(20 * 2);
                    } else if (evolutionPlayer.getEvolution() == Evolution.CREEPER) {
                        TNTPrimed tntPrimed = (TNTPrimed) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.PRIMED_TNT);
                        tntPrimed.setFuseTicks(10);

                        evolutionPlayer.addAbilityEntity(tntPrimed.getUniqueId());

                        Bukkit.getOnlinePlayers().forEach(player -> me.chasertw123.minigames.core.Main.getEntityHider().hideEntity(player, tntPrimed));
                    }

                    e.getEntity().remove();
                } else if (e.getEntity().getType() == EntityType.SPLASH_POTION) {

                    for(Player player : Bukkit.getServer().getOnlinePlayers())
                        if(player.getLocation().distance(e.getEntity().getLocation()) <= 4f && player.getUniqueId()
                                != evolutionPlayer.getPlayer().getUniqueId())
                            player.damage(8, evolutionPlayer.getPlayer());

                } else if (e.getEntity().getType() == EntityType.ENDER_PEARL) {

                    for(Player player : Bukkit.getServer().getOnlinePlayers())
                        if(player.getLocation().distance(e.getEntity().getLocation()) <= 5f && player.getUniqueId()
                                != evolutionPlayer.getPlayer().getUniqueId()) {

                            Vector direction = e.getEntity().getLocation().clone().toVector().subtract(player.getLocation().toVector()).normalize();
                            player.setVelocity(direction);
                            player.damage(6, evolutionPlayer.getPlayer());
                        }
                }

            }
        }
    }

    private Location[] generateCylLocs(Location location) {
        return new Location[]{
                location,
                location.getBlock().getRelative(0, 1, 0).getLocation(),
                location.getBlock().getRelative(0, -1, 0).getLocation(),
                location.getBlock().getRelative(1, 0, 0).getLocation(),
                location.getBlock().getRelative(-1, 0, 0).getLocation(),
                location.getBlock().getRelative(0, 0, 1).getLocation(),
                location.getBlock().getRelative(0, 0, -1).getLocation()
        };
    }

}
