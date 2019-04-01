package me.chasertw123.evolution.listeners;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.ai.AIPlayer;
import me.chasertw123.evolution.user.EvolutionPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class Event_EntityDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        for(AIPlayer aiPlayer : Main.getInstance().getGameManager().getAiPlayers()) {
            if(e.getEntity().getUniqueId() == aiPlayer.getEntity().getUniqueId() || e.getEntity().getUniqueId() == aiPlayer.getControllerEntity().getUniqueId()) {
                e.setCancelled(true);

                return;
            }
        }

        if(e.getEntity().getType() == EntityType.PLAYER && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            for(EvolutionPlayer evolutionPlayer : Main.getInstance().getEvolutionPlayerManager().getEvolutionPlayers())
                if(evolutionPlayer.getAbilityEntities().contains(e.getEntity().getUniqueId())) {
                    final double damage = e.getDamage();

                    ((Player) e.getEntity()).damage(damage, evolutionPlayer.getPlayer());

                    evolutionPlayer.getAbilityEntities().remove(e.getEntity().getUniqueId());
                }

        }
    }

}
