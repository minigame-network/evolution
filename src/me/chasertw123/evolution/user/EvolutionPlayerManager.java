package me.chasertw123.evolution.user;

import org.bukkit.entity.Player;

import java.util.*;

public class EvolutionPlayerManager {

    private Map<UUID, EvolutionPlayer> evolutionPlayerMap;

    public EvolutionPlayerManager() {
        evolutionPlayerMap = new HashMap<>();
    }

    public Map<UUID, EvolutionPlayer> getEvolutionPlayerMap() {
        return evolutionPlayerMap;
    }

    public List<EvolutionPlayer> getEvolutionPlayers() {
        return new ArrayList<>(evolutionPlayerMap.values());
    }

    public void addEvolutionPlayer(EvolutionPlayer evolutionPlayer) {
        this.evolutionPlayerMap.put(evolutionPlayer.getUUID(), evolutionPlayer);
    }

    public void removeEvolutionPlayer(UUID uuid) {
        this.evolutionPlayerMap.remove(uuid);
    }

    public EvolutionPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public EvolutionPlayer getPlayer(UUID uuid) {
        return evolutionPlayerMap.get(uuid);
    }

    public List<EvolutionPlayer> getEvolutionPlayersSortedByScore() {
        List<EvolutionPlayer> evolutionPlayers = getEvolutionPlayers();

        evolutionPlayers.sort(Comparator.comparingInt(EvolutionPlayer::getScore).reversed());

        return evolutionPlayers;
    }

}
