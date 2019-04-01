package me.chasertw123.evolution.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class Event_EntityExplode implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.blockList().clear();

        /*
        List<Block> blocks = e.blockList();

        Random random = new Random();

        for(Block b : blocks) {
            final BlockState blockState = b.getState();

            int delay = random.nextInt(100);

            if(b.getType() == Material.SAND || b.getType() == Material.GRAVEL || b.getType() == Material.TORCH
                    || b.getType() == Material.ITEM_FRAME || b.getType() == Material.SIGN || b.getType() == Material.BED){

                delay = 100;
            }

            b.setType(Material.AIR);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(),
                    () -> blockState.update(true, false), 200 + delay);
        } */
    }

}
