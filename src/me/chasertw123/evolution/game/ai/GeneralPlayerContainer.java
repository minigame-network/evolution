package me.chasertw123.evolution.game.ai;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface GeneralPlayerContainer {

    Entity getEntity();

    String getName();

    void addAbilityEntity(UUID uuid);

    List<UUID> getAbilityEntities();

    void removeAbilityEntity(UUID uuid);

    int getScore();

    default boolean isPlayer() {
        return getEntity() instanceof Player;
    }

    default LivingEntity getLivingEntity() {
        return (LivingEntity) getEntity();
    }
}
