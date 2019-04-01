package me.chasertw123.evolution.user;

import me.chasertw123.evolution.game.ai.GeneralPlayerContainer;
import me.chasertw123.evolution.game.evolutions.Evolution;
import me.chasertw123.evolution.game.kits.Kit;
import me.chasertw123.minigames.core.api.misc.Title;
import me.chasertw123.minigames.core.api.v2.CoreAPI;
import me.chasertw123.minigames.core.user.data.stats.Stat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EvolutionPlayer implements GeneralPlayerContainer {

    private UUID uuid;
    private String votedMap;
    private Evolution evolution;
    private boolean isEvolutionPrimed;
    private long lastAbilityUsageTime;
    private List<UUID> abilityEntities;
    private Kit kit;

    public EvolutionPlayer(UUID uuid) {
        this.uuid = uuid;
        this.votedMap = null;
        this.evolution = Evolution.ZOMBIE;
        this.isEvolutionPrimed = false;
        this.lastAbilityUsageTime = 0;
        this.abilityEntities = new ArrayList<>();
        this.kit = Kit.LIFE_STEAL;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    @Override
    public void addAbilityEntity(UUID uuid) {
        this.abilityEntities.add(uuid);
    }

    @Override
    public List<UUID> getAbilityEntities() {
        return abilityEntities;
    }

    @Override
    public void removeAbilityEntity(UUID uuid) {
        this.abilityEntities.remove(uuid);
    }

    public boolean isEvolutionPrimed() {
        return isEvolutionPrimed;
    }

    public void tryAbility() {
        double cooldownTime = kit == Kit.FASTER_COOLDOWN ? evolution.getCooldown() * 0.8 : evolution.getCooldown();

        if(lastAbilityUsageTime + (1000 * cooldownTime) > System.currentTimeMillis()) {

            double timeRemaining = ((lastAbilityUsageTime + 1000 * cooldownTime) - System.currentTimeMillis()) / 1000;

            Title.sendActionbar(getPlayer(), ChatColor.RED + "That is on cooldown. Time remaining: " + ChatColor.GOLD
                    + String.format("%.2f", timeRemaining) + "s" + ChatColor.RED + ".");

            return;
        }

        // This is when it will trigger the ability for sure
        getCoreUser().incrementStat(Stat.EVOLUTION_ABILITIES_USED);

        lastAbilityUsageTime = System.currentTimeMillis();
        evolution.triggerAbility(this);
    }

    public void decrementEvolution() {
        if(evolution.getPositionOrder() == 0)
            return;

        this.evolution = Evolution.values()[evolution.getPositionOrder() - 1];
    }

    public void setEvolutionPrimed(boolean evolutionPrimed) {
        isEvolutionPrimed = evolutionPrimed;
    }

    @Override
    public int getScore() {
        return evolution.getPositionOrder();
    }

    public Evolution getEvolution() {
        return evolution;
    }

    public void setEvolution(Evolution evolution) {
        this.evolution = evolution;
    }

    public Evolution incrementEvolution() {
        this.evolution = Evolution.values()[evolution.getPositionOrder() + 1];

        return this.evolution;
    }

    public boolean hasVoted() {
        return votedMap != null;
    }

    public String getVotedMap() {
        return votedMap;
    }

    public void setVotedMap(String votedMap) {
        this.votedMap = votedMap;
    }

    public UUID getUUID() {
        return uuid;
    }

    public me.chasertw123.minigames.core.user.User getCoreUser() {
        return CoreAPI.getUser(getPlayer());
    }

    public Player getPlayer() {
        return Bukkit.getServer().getPlayer(uuid);
    }

    @Override
    public Entity getEntity() {
        return getPlayer();
    }

    @Override
    public String getName() {
        return getPlayer().getName();
    }
}
