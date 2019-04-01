package me.chasertw123.evolution.game.loops;

import me.chasertw123.evolution.Main;
import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.Items;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Loop_GameOver extends GameLoop {

    private static Random rand = new Random();

    private final int radius = 32;

    public Loop_GameOver() {
        super(1, 20);
    }

    @Override
    public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {

            Location center = player.getLocation(), newLoc;
            double angle = rand.nextDouble() * 360;
            double x = center.getX() + (rand.nextDouble() * radius * Math.cos(Math.toRadians(angle)));
            double z = center.getZ() + (rand.nextDouble() * radius * Math.sin(Math.toRadians(angle)));
            newLoc = new Location(center.getWorld(), x, center.getY() + 2, z);

            playFirework(newLoc);
        }

    }

    private void playFirework(Location l) {

        FireworkEffect.Builder fwB = FireworkEffect.builder();
        Random r = new Random();
        fwB.flicker(r.nextBoolean());
        fwB.trail(r.nextBoolean());
        fwB.with(FireworkEffect.Type.values()[r.nextInt(FireworkEffect.Type.values().length)]);
        fwB.withColor(Color.fromRGB(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
        fwB.withFade(Color.fromRGB(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
        FireworkEffect fe = fwB.build();

        ItemStack item = new ItemStack(Items.FIREWORKS);


        FireworkMeta meta = (FireworkMeta) CraftItemStack.asCraftMirror(item).getItemMeta();
        meta.addEffect(fe);

        CraftItemStack.setItemMeta(item, meta);

        double y = l.getY();
        new BukkitRunnable() {

            @Override
            public void run() {

                EntityFireworks entity = new EntityFireworks(((CraftWorld) l.getWorld()).getHandle(), l.getX(), l.getY(), l.getZ(), item) {
                    @Override
                    public void t_() {
                        this.world.broadcastEntityEffect(this, (byte)17);
                        die();
                    }
                };

                entity.setInvisible(true);
                ((CraftWorld) l.getWorld()).getHandle().addEntity(entity);
            }
        }.runTaskLater(Main.getInstance(), 2);
    }

}
