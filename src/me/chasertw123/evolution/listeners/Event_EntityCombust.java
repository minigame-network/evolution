package me.chasertw123.evolution.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

public class Event_EntityCombust implements Listener {

    @EventHandler
    public void onEntityCombust(EntityCombustEvent e) {
        if(e.getEntity().getType() != EntityType.PLAYER)
            e.setCancelled(true);
    }

}
