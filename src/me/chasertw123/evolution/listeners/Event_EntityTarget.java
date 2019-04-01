package me.chasertw123.evolution.listeners;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.ai.AIPlayer;
import me.chasertw123.evolution.user.EvolutionPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class Event_EntityTarget implements Listener {

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        if(e.getEntity().getType() != EntityType.ZOMBIE || e.getTarget() == null)
            return;

        for(AIPlayer aiPlayer : Main.getInstance().getGameManager().getAiPlayers())
            if(aiPlayer.getControllerEntity().getUniqueId() == e.getEntity().getUniqueId() && e.getTarget().getUniqueId() != aiPlayer.getTargetUniqueId()) {
                System.out.println("Deflected Entity Targeting.");

                e.setCancelled(true);
                return;
            }

        // Zombie has targeted a player
        if(Main.getInstance().getEvolutionPlayerManager().getPlayer((Player) e.getTarget()).getAbilityEntities()
                .contains(e.getEntity().getUniqueId()))
            e.setCancelled(true);
    }

}
