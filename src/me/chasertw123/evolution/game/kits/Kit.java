package me.chasertw123.evolution.game.kits;

import me.chasertw123.minigames.core.utils.items.cItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Kit {

    FASTER_EVOLUTION("Faster Evolution", "-20% time on each evolution.", new ItemStack(Material.SUGAR)),
    LIFE_STEAL("Life Steal", "As you damage, your health increases.", new ItemStack(Material.DIAMOND_SWORD)),
    FASTER_COOLDOWN("Faster Cooldown", "-20% time on each cooldown.", new ItemStack(Material.GLASS_BOTTLE));

    private String name, description;
    private ItemStack itemStack;

    Kit(String name, String description, ItemStack itemStack) {
        this.name = name;
        this.description = description;
        this.itemStack = new cItemStack(itemStack).setDisplayName(ChatColor.AQUA + name).addFancyLore(description, ChatColor.YELLOW + "");
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getDescription() {
        return description;
    }

}
