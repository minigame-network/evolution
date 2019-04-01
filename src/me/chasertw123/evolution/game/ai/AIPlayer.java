package me.chasertw123.evolution.game.ai;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.evolutions.Evolution;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Controlled by Zombie AI. This is because it's the simplest AI with just a target system that is similar to players.
 */
public class AIPlayer implements GeneralPlayerContainer {

    private static int aiPlayerCount = 0;

    private Entity entity;
    private Zombie zombieEntity;
    private long lastAttackTime;
    private Evolution evolution;
    private int id;
    private UUID currentTarget;
    private List<UUID> abilityEntities;

    public AIPlayer(Location spawnPoint) {
        this.id = aiPlayerCount++;
        this.evolution = Evolution.ZOMBIE;
        this.lastAttackTime = 0;
        this.abilityEntities = new ArrayList<>();

        resetEntity(spawnPoint);

        // This first entity is a zombie.
        Zombie firstEvolution = (Zombie) this.entity;
        firstEvolution.setBaby(false);
        firstEvolution.setVillager(false);

        this.zombieEntity = (Zombie) spawnPoint.getWorld().spawnEntity(spawnPoint, EntityType.ZOMBIE);
        this.zombieEntity.setBaby(false);
        this.zombieEntity.setVillager(false);
        hideEntity(this.zombieEntity);
    }

    private void resetEntity(Location spawnPoint) {
        if(this.entity != null)
            this.entity.remove();

        this.entity = spawnPoint.getWorld().spawnEntity(spawnPoint, this.evolution.getEntityType());
        this.entity.setCustomName(getName());
        this.entity.setCustomNameVisible(true);
        stripAi(this.entity);
    }

    public void setTarget(LivingEntity livingEntity) {
        Location l = livingEntity.getLocation();

        ((CraftZombie) zombieEntity).getHandle().getNavigation().a(l.getX(), l.getY(), l.getZ(), 1.5);
    }

    public static void stripAi(Entity entity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null)
            tag = new NBTTagCompound();

        nmsEntity.c(tag);
        tag.setInt("NoAI", 1);
        nmsEntity.f(tag);
    }

    private void hideEntity(Entity entity) {
        Bukkit.getServer().getOnlinePlayers()
                .forEach(p -> me.chasertw123.minigames.core.Main.getEntityHider().hideEntity(p, entity));
    }

    private void showEntity(Entity entity) {
        Bukkit.getServer().getOnlinePlayers()
                .forEach(p -> me.chasertw123.minigames.core.Main.getEntityHider().showEntity(p, entity));
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public String getName() {
        return "AI #" + id;
    }

    @Override
    public void addAbilityEntity(UUID uuid) {
        this.abilityEntities.add(uuid);
    }

    @Override
    public List<UUID> getAbilityEntities() {
        return this.abilityEntities;
    }

    @Override
    public void removeAbilityEntity(UUID uuid) {
        this.abilityEntities.remove(uuid);
    }

    public void evolute() {
        if(evolution == Evolution.ENDERMAN) {
            // The AI has won hahahahahha

            return;
        }

        evolution = Evolution.values()[evolution.getPositionOrder() + 1];

        // Set the mob type
        resetEntity(entity.getLocation().clone());
    }

    public Zombie getControllerEntity() {
        return zombieEntity;
    }

    public Evolution getEvolution() {
        return evolution;
    }

    // Called once a tick. Do the updates here.
    public void aiTick() {
        // This is gonna require a lot of work lol

        if(inRadiusOfEnemy()) {
            triggerAttack();

            if(new Random().nextInt(100) == 25)
                evolute(); //TODO: Remove, this is a test
        } else {
            if(zombieEntity.getTarget() == null || zombieEntity.getTarget().getUniqueId() != getNearestEnemy().getUniqueId()) {
                System.out.println("Zombie target: " + zombieEntity.getTarget());
                setTarget(getNearestEnemy());

                System.out.println("AI #" + id + " set target UUID to " + getNearestEnemy().getUniqueId() + ", with the distance of " + getNearestEnemyDistance());
            }
        }

        // Update the shown entities location
        entity.teleport(zombieEntity);
    }

    public UUID getTargetUniqueId() {
        return currentTarget;
    }

    public void triggerAttack() {
        // Check last attack time, etc
        if(lastAttackTime + (int) (evolution.getCooldown() * 1000) > System.currentTimeMillis())
            return;

        lastAttackTime = System.currentTimeMillis();

        System.out.println("Triggering Attack for AI #" + id);
        evolution.triggerAbility(this);
    }

    @Override
    public int getScore() {
        return evolution.getPositionOrder();
    }

    public void faceTowards(Entity entity) {

    }

    public double getNearestEnemyDistance() {
        double closestDistance = -1;

        for(GeneralPlayerContainer player : Main.getInstance().getGameManager().getGeneralPlayerControllers())
            if(player.getEntity().getUniqueId() != getEntity().getUniqueId()
                    && (closestDistance == -1 || player.getEntity().getLocation().distance(getEntity().getLocation()) < closestDistance))
                closestDistance = player.getEntity().getLocation().distance(getEntity().getLocation());

        return closestDistance;
    }

    public LivingEntity getNearestEnemy() {
        double closestDistance = -1;
        GeneralPlayerContainer closestEntity = null;

        for(GeneralPlayerContainer player : Main.getInstance().getGameManager().getGeneralPlayerControllers()) {
            LivingEntity e = (LivingEntity) player.getEntity();

            if(player.getEntity().getUniqueId() != getEntity().getUniqueId()
                    && (closestDistance == -1 || player.getEntity().getLocation().distance(getEntity().getLocation()) < closestDistance)) {
                closestDistance = e.getLocation().distance(getEntity().getLocation());
                closestEntity = player;
            }
        }

        return closestEntity.isPlayer() ? closestEntity.getLivingEntity() : ((AIPlayer) closestEntity).getControllerEntity();
    }

    public boolean inRadiusOfEnemy() {
        return getNearestEnemyDistance() < 5;
    }
}
