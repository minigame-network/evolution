package me.chasertw123.evolution.game.guis;

import me.chasertw123.evolution.game.kits.Kit;
import me.chasertw123.evolution.user.EvolutionPlayer;
import me.chasertw123.minigames.core.utils.gui.AbstractGui;
import org.bukkit.ChatColor;

public class Gui_KitSelector extends AbstractGui {

    public Gui_KitSelector(EvolutionPlayer evolutionPlayer) {
        super(1, "Select a kit!", evolutionPlayer.getCoreUser());

        int i = 0;
        for(Kit kit : Kit.values()) {
            setItem(kit.getItemStack(), i, (s, c, p) -> {
                evolutionPlayer.setKit(kit);
                p.closeInventory();

                evolutionPlayer.getPlayer().sendMessage(ChatColor.WHITE + "Set your kit to " + ChatColor.AQUA + kit.getName() + ChatColor.WHITE + ".");
            });

            i++;
        }
    }

}
