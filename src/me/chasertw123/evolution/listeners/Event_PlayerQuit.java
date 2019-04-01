package me.chasertw123.evolution.listeners;

import me.chasertw123.evolution.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Event_PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Main.getInstance().getEvolutionPlayerManager().removeEvolutionPlayer(e.getPlayer().getUniqueId());
    }

}
