package me.chasertw123.evolution;

import me.chasertw123.evolution.game.GameManager;
import me.chasertw123.evolution.listeners.EventManager;
import me.chasertw123.evolution.maps.MapManager;
import me.chasertw123.evolution.maps.VoteManager;
import me.chasertw123.evolution.user.EvolutionPlayerManager;
import me.chasertw123.minigames.core.api.v2.CoreAPI;
import me.chasertw123.minigames.shared.framework.ServerGameType;
import me.chasertw123.minigames.shared.framework.ServerType;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

//    public static String PREFIX = ChatColor.BLUE + "Evolution> " + ChatColor.RESET;

    private static Main instance;

    private EvolutionPlayerManager evolutionPlayerManager;
    private GameManager gameManager;
    private MapManager mapManager;
    private VoteManager voteManager;

    @Override
    public void onLoad() {
        CoreAPI.getServerDataManager().setServerType(ServerType.MINIGAME);
        CoreAPI.getServerDataManager().setServerGameType(ServerGameType.EVOLUTION);
    }

    @Override
    public void onEnable() {
        instance = this;

        mapManager = new MapManager();
        voteManager = new VoteManager();
        evolutionPlayerManager = new EvolutionPlayerManager();
        gameManager = new GameManager();

        new EventManager();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public EvolutionPlayerManager getEvolutionPlayerManager() {
        return evolutionPlayerManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }

    // STATIC INSTANCE //

    public static Main getInstance() {
        return instance;
    }

}
