package me.chasertw123.evolution.maps;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapManager {

    private World game = null, lobby = null;
    private List<GameMap> maps = new ArrayList<>();

    public MapManager() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() throws IOException {
        if(new File("game").exists())
            FileUtils.deleteDirectory(new File("game"));

        if(new File("lobby").exists())
            FileUtils.deleteDirectory(new File("lobby"));

        new File("game").mkdir();
        new File("lobby").mkdir();

        // Load in the lobby
        FileUtils.copyDirectory(new File("olobby"), new File("lobby"));

        this.lobby = Bukkit.getServer().createWorld(new WorldCreator("lobby"));
        standardOptions(this.lobby);

        // Load possible map options
        List<GameMap> maps = new ArrayList<>();
        for(File f : new File("maps").listFiles())
            if(f.isDirectory())
                maps.add(new GameMap(f.getName(), YamlConfiguration.loadConfiguration(new File("maps/" + f.getName() + "/config.yml"))));

        Collections.shuffle(maps);

        if(maps.size() > 3) {
            this.maps.add(maps.get(0));
            this.maps.add(maps.get(1));
            this.maps.add(maps.get(2));
        } else {
            this.maps = maps;
        }
    }

    public void loadGameWorld(String name) {
        // Load in the game
        try {
            FileUtils.copyDirectory(new File("maps/" + name), new File("game"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.game = Bukkit.getServer().createWorld(new WorldCreator("game"));

        standardOptions(this.game);
    }

    private void standardOptions(World world) {
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setDifficulty(Difficulty.EASY);
        world.setTime(6000);
    }

    public boolean hasLoadedGameWorld() {
        return game != null;
    }

    public List<GameMap> getMaps() {
        return maps;
    }

    public World getLobby() {
        return lobby;
    }

    public World getGame() {
        return game;
    }

}
