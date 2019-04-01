package me.chasertw123.evolution.maps;

import me.chasertw123.evolution.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class GameMap {

    private class SimpleLocation {

        private int x, y, z;

        public SimpleLocation(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

    }

    private String name, description, directoryName;
    private List<String> builders;
    private SimpleLocation upgradeLocation;
    private List<SimpleLocation> spawnLocations;
    private List<Location> loadedSpawns = null;

    public GameMap(String directoryName, FileConfiguration config) {
        this.directoryName = directoryName;
        this.name = config.getString("name");
        this.description = config.getString("description");
        this.builders = config.getStringList("builders");
        this.spawnLocations = new ArrayList<>();

        for(String s : config.getStringList("spawns")) {
            String[] spawnData = s.split(", ");
            spawnLocations.add(new SimpleLocation(Integer.parseInt(spawnData[0]), Integer.parseInt(spawnData[1]), Integer.parseInt(spawnData[2])));
        }

        String[] upgradeData = config.getString("upgradeloc").split(", ");
        upgradeLocation = new SimpleLocation(Integer.parseInt(upgradeData[0]), Integer.parseInt(upgradeData[1]), Integer.parseInt(upgradeData[2]));
    }

    public Location getUpgradeLocation(World world) {
        return new Location(world, upgradeLocation.getX(), upgradeLocation.getY(), upgradeLocation.getZ()).clone().add(0.5, 0, 0.5);
    }

    public List<Location> getSpawns(World world) {
        if(loadedSpawns != null)
            return loadedSpawns;

        List<Location> spawns = new ArrayList<>();

        for(SimpleLocation simpleLocation : spawnLocations)
            spawns.add(new Location(world, simpleLocation.getX(), simpleLocation.getY(), simpleLocation.getZ()));

        this.loadedSpawns = spawns;

        return this.loadedSpawns;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public String getName() {
        return name;
    }

    public List<String> getBuilders() {
        return builders;
    }

    public String getDescription() {
        return description;
    }

    public int getVotes() {
        return Main.getInstance().getVoteManager().getVotes(getName());
    }

}
