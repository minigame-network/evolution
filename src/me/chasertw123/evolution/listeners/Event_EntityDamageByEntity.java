package me.chasertw123.evolution.listeners;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.kits.Kit;
import me.chasertw123.evolution.user.EvolutionPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Event_EntityDamageByEntity implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if(e.getEntity().getType() != EntityType.PLAYER)
            return;

        for(EvolutionPlayer evolutionPlayer : Main.getInstance().getEvolutionPlayerManager().getEvolutionPlayers())
            if(evolutionPlayer.getAbilityEntities().contains(e.getDamager().getUniqueId())) {
                ((Player) e.getEntity()).damage(e.getDamage(), evolutionPlayer.getPlayer());

                if(evolutionPlayer.getKit() == Kit.LIFE_STEAL)
                    evolutionPlayer.getPlayer().setHealth(evolutionPlayer.getPlayer().getHealth()
                            + ((int)(e.getDamage() / 4)) > evolutionPlayer.getPlayer().getMaxHealth()
                            ? evolutionPlayer.getPlayer().getMaxHealth() : evolutionPlayer.getPlayer().getHealth()
                            + ((int)(e.getDamage() / 4)));

                e.setCancelled(true);
                break;
            }

    }

}
