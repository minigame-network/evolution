package me.chasertw123.evolution.game.evolutions;

import de.robingrether.idisguise.disguise.DisguiseType;
import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.ai.AIPlayer;
import me.chasertw123.evolution.game.ai.GeneralPlayerContainer;
import me.chasertw123.evolution.user.EvolutionPlayer;
import me.chasertw123.minigames.core.utils.items.cItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum Evolution {

    ZOMBIE(EntityType.ZOMBIE, DisguiseType.ZOMBIE, "Zombie", 26, 3, new cItemStack(Material.ROTTEN_FLESH,
            ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Zambie " + ChatColor.GRAY + "(Right Click)"), (ep) -> {

        GeneralPlayerContainer target = null;
        double closestDistance = -1;

        for(GeneralPlayerContainer player : Main.getInstance().getGameManager().getGeneralPlayerControllers()) {
            if((closestDistance == -1 || player.getEntity().getLocation().distance(ep.getLivingEntity().getLocation()) < closestDistance)
                    && player.getEntity().getUniqueId() != ep.getLivingEntity().getUniqueId()) {

                closestDistance = player.getEntity().getLocation().distance(ep.getLivingEntity().getLocation());
                target = player;
            }
        }

        for(int i = 0; i < 5; i++) {
            Zombie zombie = (Zombie) ep.getLivingEntity().getLocation().getWorld().spawnEntity(ep.getLivingEntity().getLocation(), EntityType.ZOMBIE);
            zombie.setBaby(true);

            zombie.setCustomName(ChatColor.WHITE + ep.getName());
            zombie.setCustomNameVisible(true);

            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, true));

            ep.addAbilityEntity(zombie.getUniqueId());

            // Find nearest Player
            if(target != null) {
//                zombie.setTarget(target.getLivingEntity());

                final GeneralPlayerContainer thisTarget = target;
                Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
                    if(!zombie.isDead()) {
                        Location l = thisTarget.getLivingEntity().getLocation();
                        ((CraftZombie) zombie).getHandle().getNavigation().a(l.getX(), l.getY(), l.getZ(), 1);
                    }
                }, 0, 1);


                System.out.println("Set attack closest entity to " + target.getEntity().getUniqueId() + ". Is player? " + target.isPlayer());
            }

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), zombie::remove, new Random().nextInt(125 - 90) + 90);
        }
    }),
    SPIDER(EntityType.SPIDER, DisguiseType.SPIDER, "Spider", 24, 6, new cItemStack(Material.WEB,
            ChatColor.WHITE + "" + ChatColor.BOLD + "Web Shot " + ChatColor.GRAY + "(Right Click)"), (ep) -> {

        Snowball snowball = ep.getLivingEntity().launchProjectile(Snowball.class);

        ep.addAbilityEntity(snowball.getUniqueId());
    }),
    SKELETON(EntityType.SKELETON, DisguiseType.SKELETON, "Skeleton", 22, 7.5f, new cItemStack(Material.ARROW,
            ChatColor.AQUA + "" + ChatColor.BOLD + "Arrow Cannon " + ChatColor.GRAY + "(Right Click)"), (ep) -> {

        Arrow arrow = ep.getLivingEntity().launchProjectile(Arrow.class);

        ep.addAbilityEntity(arrow.getUniqueId());
    }),
    WITCH(EntityType.WITCH, DisguiseType.WITCH, "Witch", 20, 5, new cItemStack(Material.GLASS_BOTTLE,
            ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "VooDoo " + ChatColor.GRAY + "(Right Click)"), (ep) -> {

        ThrownPotion thrownPotion = ep.getLivingEntity().launchProjectile(ThrownPotion.class);

        ep.addAbilityEntity(thrownPotion.getUniqueId());
    }),
    BLAZE(EntityType.BLAZE, DisguiseType.BLAZE, "Blaze", 18, 5, new cItemStack(Material.BLAZE_ROD,
            ChatColor.GOLD + "" + ChatColor.BOLD + "Flame Thrower " + ChatColor.GRAY + "(Right Click)"), (ep) -> {

        Arrow fireball = ep.getLivingEntity().launchProjectile(Arrow.class);

        ep.addAbilityEntity(fireball.getUniqueId());
    }),
    IRON_GOLEM(EntityType.IRON_GOLEM, DisguiseType.IRON_GOLEM, "Iron Golem", 16, 8, new cItemStack(Material.IRON_INGOT,
            ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Super Smash " + ChatColor.GRAY + "(Right Click)"), (ep) -> {

        Location landingLocation = ep.getLivingEntity().getLocation().clone();

        for(Player player : Bukkit.getServer().getOnlinePlayers())
            if(player.getLocation().distance(landingLocation) <= 8f && player.getUniqueId()
                    != ep.getLivingEntity().getUniqueId()) {
                player.setVelocity(new Vector(0, 2, 0));

                ep.addAbilityEntity(player.getUniqueId());
            }
    }),
    SNOW_GOLEM(EntityType.SNOWMAN, DisguiseType.SNOWMAN, "Snow Golem", 14, 3.5f, new cItemStack(Material.DIAMOND_SPADE,
            ChatColor.RED + "" + ChatColor.BOLD + "Iceberg " + ChatColor.GRAY + "(Right Click)"), (ep) -> {

        for(int i = 0; i < 8; i++){
            Snowball snowball = ep.getLivingEntity().launchProjectile(Snowball.class);
            Vector vector = ep.getLivingEntity().getLocation().getDirection();

            List<Double> randomList = new ArrayList<>();

            for(int a = 0; a < 3; a++){
                double random = Math.random();
                if(random > .5)
                    random = random - .5;

                randomList.add(random);
            }

            vector.add(new Vector(randomList.get(0), randomList.get(1), randomList.get(2)));
            snowball.setVelocity(vector);

            ep.addAbilityEntity(snowball.getUniqueId());
        }
    }),
    CREEPER(EntityType.CREEPER, DisguiseType.CREEPER, "Creeper", 12, 5, new cItemStack(Material.SULPHUR,
            ChatColor.GREEN + "" + ChatColor.BOLD + "Throwing TNT " + ChatColor.GRAY + "(Right Click)"), (ep) -> {

        Arrow arrow = ep.getLivingEntity().launchProjectile(Arrow.class);

        ep.addAbilityEntity(arrow.getUniqueId());
    }),
    ENDERMAN(EntityType.ENDERMAN, DisguiseType.ENDERMAN, "Enderman", 10, 5, new cItemStack(Material.EYE_OF_ENDER,
            ChatColor.BLUE + "" + ChatColor.BOLD + "Ender Bomb " + ChatColor.GRAY + "(Right Click)"), (ep) -> {

        EnderPearl enderPearl = ep.getLivingEntity().launchProjectile(EnderPearl.class);

        ep.addAbilityEntity(enderPearl.getUniqueId());
    });

    private String customName;
    private EntityType entityType;
    private int health;
    private float cooldown;
    private EvolutionCallback evolutionCallback;
    private ItemStack itemStack;
    private DisguiseType disguiseType;

    Evolution(EntityType entityType, DisguiseType disguiseType, String customName, int health, float cooldown, ItemStack itemStack, EvolutionCallback evolutionCallback) {
        this.customName = customName;
        this.entityType = entityType;
        this.health = health;
        this.cooldown = cooldown;
        this.evolutionCallback = evolutionCallback;
        this.itemStack = itemStack;
        this.disguiseType = disguiseType;
    }

    public DisguiseType getDisguiseType() {
        return disguiseType;
    }

    public float getCooldown() {
        return cooldown;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void triggerAbility(GeneralPlayerContainer evolutionPlayer) {
        evolutionCallback.evolutionItemUsage(evolutionPlayer);
    }

    public EvolutionCallback getEvolutionCallback() {
        return evolutionCallback;
    }

    public int getHealth() {
        return health;
    }

    public String getCustomName() {
        return customName;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public int getPositionOrder() {
        return this.ordinal();
    }

    @Override
    public String toString() {
        return customName;
    }
}
