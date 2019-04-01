package me.chasertw123.evolution.listeners;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.GameState;
import me.chasertw123.evolution.user.EvolutionPlayer;
import me.chasertw123.minigames.core.api.misc.Title;
import me.chasertw123.minigames.core.user.data.stats.Stat;
import me.chasertw123.minigames.core.utils.items.AbstractItem;
import me.chasertw123.minigames.core.utils.items.cItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Event_PlayerDeath implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(Main.getInstance().getGameManager().getGameState() != GameState.INGAME)
            return;

        Main.getInstance().getEvolutionPlayerManager().getPlayer(e.getEntity()).setEvolutionPrimed(false);

        e.getDrops().clear();

        Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            e.getEntity().spigot().respawn();
            EvolutionPlayer evolutionPlayer = Main.getInstance().getEvolutionPlayerManager().getPlayer(e.getEntity());

            evolutionPlayer.getPlayer().getInventory().clear();
            new AbstractItem(evolutionPlayer.getEvolution().getItemStack(), evolutionPlayer.getCoreUser(), 0, (type) -> {
                if(type == AbstractItem.InteractType.RIGHT)
                    evolutionPlayer.tryAbility();
            });

            evolutionPlayer.getPlayer().getInventory().setItem(1, new cItemStack(Material.COMPASS, ChatColor.GREEN + "Player Tracker"));

            e.getEntity().setMaxHealth(evolutionPlayer.getEvolution().getHealth());
            e.getEntity().setHealth(evolutionPlayer.getEvolution().getHealth());

            e.getEntity().setFoodLevel(20);
            e.getEntity().teleport(Main.getInstance().getGameManager().getSpawnLocation(e.getEntity()));

            Title.sendAll(e.getEntity(), 0, 20, 0, "", " ", null, null, null); // Clear current titles
        }, 1L);

        EvolutionPlayer deadPlayer = Main.getInstance().getEvolutionPlayerManager().getPlayer(e.getEntity());

        deadPlayer.getCoreUser().incrementStat(Stat.EVOLUTION_DEATHS);

        if(e.getEntity().getKiller() != null && e.getEntity().getKiller().getUniqueId() != e.getEntity().getUniqueId()) {
            EvolutionPlayer killer = Main.getInstance().getEvolutionPlayerManager().getPlayer(e.getEntity().getKiller());

            killer.getCoreUser().incrementStat(Stat.EVOLUTION_KILLS);

            e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + deadPlayer.getEvolution().getCustomName()
                    + ChatColor.GRAY + "] " + ChatColor.WHITE + deadPlayer.getCoreUser().getColoredName() + ChatColor.GRAY + " was killed by "
                    + ChatColor.GRAY + "[" + ChatColor.GOLD + killer.getEvolution().getCustomName() + ChatColor.GRAY + "] " + ChatColor.WHITE + killer.getCoreUser().getColoredName());

            Title.sendAll(killer.getPlayer(), 0, Integer.MAX_VALUE, 0, "", ChatColor.LIGHT_PURPLE + ""
                    + ChatColor.BOLD + "HOLD SHIFT TO EVOLVE", null, null, null);
            killer.setEvolutionPrimed(true);
        } else {
            e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + deadPlayer.getEvolution().getCustomName()
                    + ChatColor.GRAY + "] " + ChatColor.WHITE + deadPlayer.getCoreUser().getColoredName() + ChatColor.GRAY + " died");
        }
    }

}
