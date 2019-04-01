package me.chasertw123.evolution.listeners;

import de.robingrether.idisguise.iDisguise;
import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.GameManager;
import me.chasertw123.evolution.game.GameState;
import me.chasertw123.evolution.game.boards.Scoreboard_Lobby;
import me.chasertw123.evolution.game.guis.Gui_KitSelector;
import me.chasertw123.evolution.game.guis.Gui_MapVote;
import me.chasertw123.evolution.user.EvolutionPlayer;
import me.chasertw123.minigames.core.utils.items.AbstractItem;
import me.chasertw123.minigames.core.utils.items.Items;
import me.chasertw123.minigames.core.utils.items.cItemStack;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Event_PlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        EvolutionPlayer evolutionPlayer = new EvolutionPlayer(p.getUniqueId());
        Main.getInstance().getEvolutionPlayerManager().addEvolutionPlayer(evolutionPlayer);

        p.setFlying(false);
        p.setAllowFlight(false);

        p.setWalkSpeed(0.2f);
        p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));

        p.setMaxHealth(20);
        p.setHealth(20);
        p.setFoodLevel(20);

        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().clear();

        if(iDisguise.getInstance().getAPI().isDisguised((OfflinePlayer) p))
            iDisguise.getInstance().getAPI().undisguise(p);

        if (Main.getInstance().getGameManager().getGameState() != GameState.LOBBY)
            return;

        evolutionPlayer.getCoreUser().setScoreboard(new Scoreboard_Lobby(evolutionPlayer));
        p.teleport(Main.getInstance().getMapManager().getLobby().getSpawnLocation().clone().add(0.5, 0, 0.5)); // Center them on the spawn location
        e.setJoinMessage(evolutionPlayer.getCoreUser().getColoredName() + ChatColor.GRAY + " has joined the game! ("
                + Bukkit.getServer().getOnlinePlayers().size() + "/" + GameManager.MAX_PLAYERS + ")");
        new AbstractItem(Items.MAP_VOTING.getItemStack(), evolutionPlayer.getCoreUser(), 3, (type) -> new Gui_MapVote(evolutionPlayer));
        new AbstractItem(new cItemStack(Material.ARMOR_STAND, ChatColor.AQUA + "Kit Selector" + ChatColor.GRAY + " (Right Click)"),
                evolutionPlayer.getCoreUser(), 5, (type) -> new Gui_KitSelector(evolutionPlayer));
    }

}
