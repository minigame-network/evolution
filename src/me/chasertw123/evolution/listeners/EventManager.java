package me.chasertw123.evolution.listeners;

import me.chasertw123.evolution.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class EventManager {

    public EventManager() {
        for(Listener l : new Listener[]{
                new Event_PlayerJoin(),
                new Event_PlayerQuit(),
                new Event_PlayerDeath(),
                new Event_PlayerSneak(),
                new Event_EntityCombust(),
                new Event_EntityTarget(),
                new Event_EntityDeath(),
                new Event_ProjectileHit(),
                new Event_EntityExplode(),
                new Event_EntityDamageByEntity(),
                new Event_EntityDamage()
        })
            Bukkit.getServer().getPluginManager().registerEvents(l, Main.getInstance());
    }

}
