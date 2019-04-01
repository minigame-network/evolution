package me.chasertw123.evolution.listeners;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.ai.GeneralPlayerContainer;
import me.chasertw123.evolution.user.EvolutionPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class Event_EntityDeath implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        e.getDrops().clear();

        if(e.getEntity().getType() == EntityType.ZOMBIE)
            for(GeneralPlayerContainer evolutionPlayer : Main.getInstance().getGameManager().getGeneralPlayerControllers())
                if(evolutionPlayer.getAbilityEntities().contains(e.getEntity().getUniqueId()))
                    evolutionPlayer.removeAbilityEntity(e.getEntity().getUniqueId());
    }

}
