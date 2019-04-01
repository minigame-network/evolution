package me.chasertw123.evolution.game.guis;

import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.maps.GameMap;
import me.chasertw123.evolution.user.EvolutionPlayer;
import me.chasertw123.minigames.core.utils.gui.AbstractGui;
import me.chasertw123.minigames.core.utils.items.cItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by Chase on 4/23/2017.
 */
public class Gui_MapVote extends AbstractGui {

    public Gui_MapVote(EvolutionPlayer user) {
        super(1, "Vote for a map!", user.getCoreUser());

        int i = 1;
        for (GameMap map : Main.getInstance().getMapManager().getMaps()) {
            setItem(new cItemStack(Material.EMPTY_MAP, ChatColor.YELLOW + map.getName() + ChatColor.WHITE + " [" + ChatColor.GREEN
                    + Main.getInstance().getVoteManager().getVotes(map) + ChatColor.WHITE + "]"), i, (s, c, p) -> {

                if (!Main.getInstance().getVoteManager().isVotingActive()) {
                    user.getCoreUser().getPlayer().sendMessage(ChatColor.RED + "Voting has already ended!");
                    user.getCoreUser().getPlayer().playSound(user.getPlayer().getLocation(), Sound.VILLAGER_NO, 1F, 1F);
                    user.getCoreUser().getPlayer().closeInventory();
                    return;
                }

                user.setVotedMap(map.getName());
                user.getCoreUser().getPlayer().sendMessage(ChatColor.WHITE + "You voted for the map " + ChatColor.AQUA + map.getName().toUpperCase() + ChatColor.WHITE + "!");
                user.getCoreUser().getPlayer().playSound(user.getPlayer().getLocation(), Sound.VILLAGER_YES, 1F, 1F);
                user.getCoreUser().getPlayer().closeInventory();

                refreshDataDisplays();
            });

            i += 3;
        }
    }

    private void refreshDataDisplays() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            EvolutionPlayer u = Main.getInstance().getEvolutionPlayerManager().getPlayer(player);

            if (u.getCoreUser().getCurrentGui() != null && u.getCoreUser().getCurrentGui().getClass().getName().equalsIgnoreCase(this.getClass().getName()))
                u.getCoreUser().setCurrentGui(new Gui_MapVote(u));
        }
    }
}
