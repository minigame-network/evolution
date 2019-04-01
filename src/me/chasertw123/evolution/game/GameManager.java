package me.chasertw123.evolution.game;

import de.robingrether.idisguise.iDisguise;
import me.chasertw123.evolution.Main;
import me.chasertw123.evolution.game.ai.AIPlayer;
import me.chasertw123.evolution.game.ai.GeneralPlayerContainer;
import me.chasertw123.evolution.game.boards.Scoreboard_Game;
import me.chasertw123.evolution.game.evolutions.Evolution;
import me.chasertw123.evolution.game.loops.*;
import me.chasertw123.evolution.maps.GameMap;
import me.chasertw123.evolution.user.EvolutionPlayer;
import me.chasertw123.minigames.core.api.misc.Title;
import me.chasertw123.minigames.core.api.v2.CoreAPI;
import me.chasertw123.minigames.core.user.data.stats.Stat;
import me.chasertw123.minigames.core.utils.items.AbstractItem;
import me.chasertw123.minigames.core.utils.items.Items;
import me.chasertw123.minigames.core.utils.items.cItemStack;
import me.chasertw123.minigames.shared.framework.GeneralServerStatus;
import me.chasertw123.minigames.shared.framework.ServerSetting;
import me.chasertw123.minigames.shared.utils.MessageUtil;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class GameManager {

    public static final int REQUIRED_PLAYERS = 2, MAX_PLAYERS = 8, GAME_TIME = 60 * 15;

    private GameState gameState;
    private GameLoop currentGameLoop;
    private GameMap gameMap;

    private UUID winner = null;

    private List<AIPlayer> aiPlayers;

    public GameManager() {
        gameState = GameState.LOBBY;
        currentGameLoop = new Loop_Lobby();

        CoreAPI.getServerDataManager().updateServerState(GeneralServerStatus.LOBBY, MAX_PLAYERS);

        aiPlayers = new ArrayList<>();

        new Loop_AI();
    }

    public List<AIPlayer> getAiPlayers() {
        return aiPlayers;
    }

    public List<GeneralPlayerContainer> getGeneralPlayerControllers() {
        List<GeneralPlayerContainer> generalPlayerContainers = new ArrayList<>();
        generalPlayerContainers.addAll(aiPlayers);
        generalPlayerContainers.addAll(Main.getInstance().getEvolutionPlayerManager().getEvolutionPlayers());

        return generalPlayerContainers;
    }

    public void setWinner(UUID winner) {
        this.winner = winner;
    }

    public void checkGame() {
        if(gameState != GameState.INGAME)
            return;

        if(Bukkit.getServer().getOnlinePlayers().size() < 2) {
            // There is only one player (or less) on the server
            endGame();
        }
    }

    public List<GeneralPlayerContainer> getGeneralPlayersByScore() {
        List<GeneralPlayerContainer> generalPlayerContainers = getGeneralPlayerControllers();

        generalPlayerContainers.sort(Comparator.comparingInt(GeneralPlayerContainer::getScore).reversed());

        return generalPlayerContainers;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void startGame() {

        CoreAPI.getServerDataManager().updateServerState(GeneralServerStatus.INGAME, MAX_PLAYERS); // Send information to the Mongo database for the queue system

        currentGameLoop = new Loop_Game();
        new Loop_Countdown();
        new Loop_PlayerTracker();

        int spawnCount = 0;
        for(me.chasertw123.minigames.core.user.User user : CoreAPI.getOnlinePlayers()) {

            user.incrementStat(Stat.EVOLUTION_GAMES_PLAYED);

            Location spawn = gameMap.getSpawns(Main.getInstance().getMapManager().getGame()).get(spawnCount++);

            user.setScoreboard(new Scoreboard_Game(Main.getInstance().getEvolutionPlayerManager().getPlayer(user.getPlayer())));
            user.getPlayer().getInventory().clear();

            EvolutionPlayer evolutionPlayer = Main.getInstance().getEvolutionPlayerManager().getPlayer(user.getPlayer());

            user.getPlayer().setMaxHealth(evolutionPlayer.getEvolution().getHealth());
            user.getPlayer().setHealth(evolutionPlayer.getEvolution().getHealth());

            user.getPlayer().setFoodLevel(20);
            user.getPlayer().teleport(spawn);

            iDisguise.getInstance().getAPI().disguise(evolutionPlayer.getPlayer(), evolutionPlayer.getEvolution().getDisguiseType().newInstance()); // the mob to be disguised as
        }

//
//        // See how many AI players need to be made
//        if(MAX_PLAYERS - Bukkit.getServer().getOnlinePlayers().size() != 0) {
//            Bukkit.broadcastMessage(ChatColor.GRAY + "It seems as though all of the player slots were not filled! Do not worry - AI will populate those positions!");
//            Bukkit.broadcastMessage(ChatColor.GRAY + "Creating " + ChatColor.AQUA + "" + (MAX_PLAYERS - Bukkit.getServer().getOnlinePlayers().size()) + ChatColor.GRAY + " AI player(s)");
//
//            for (int i = 0; i < (MAX_PLAYERS - Bukkit.getServer().getOnlinePlayers().size()); i++) {
//                Location spawn = gameMap.getSpawns(Main.getInstance().getMapManager().getGame()).get(spawnCount++);
//
//                AIPlayer aiPlayer = new AIPlayer(spawn);
//                aiPlayers.add(aiPlayer);
//            }
//        }
    }

    public void realStartGame() {
        gameState = GameState.INGAME;

        for(me.chasertw123.minigames.core.user.User user : CoreAPI.getOnlinePlayers()) {

            user.setServerSetting(ServerSetting.DAMAGE, true);
            user.getPlayer().getInventory().clear();

            EvolutionPlayer evolutionPlayer = Main.getInstance().getEvolutionPlayerManager().getPlayer(user.getPlayer());

            new AbstractItem(evolutionPlayer.getEvolution().getItemStack(), evolutionPlayer.getCoreUser(), 0, (type) -> {
                if(type == AbstractItem.InteractType.RIGHT)
                    evolutionPlayer.tryAbility();
            });

            user.getPlayer().getInventory().setItem(1, new cItemStack(Material.COMPASS, ChatColor.GREEN + "Player Tracker"));

            user.getPlayer().setMaxHealth(evolutionPlayer.getEvolution().getHealth());
            user.getPlayer().setHealth(evolutionPlayer.getEvolution().getHealth());

            user.getPlayer().setFoodLevel(20);
            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);

        }
    }

    public void endGame() {
        CoreAPI.getServerDataManager().updateServerState(GeneralServerStatus.RESTARTING, MAX_PLAYERS);

        gameState = GameState.ENDING;

        List<EvolutionPlayer> scoredList = Main.getInstance().getEvolutionPlayerManager().getEvolutionPlayersSortedByScore();

        new Loop_GameOver();

        if(winner == null)
            winner = scoredList.get(0).getUUID();

        Main.getInstance().getEvolutionPlayerManager().getPlayer(winner).getCoreUser().incrementStat(Stat.EVOLUTION_GAMES_WON);

        for(EvolutionPlayer evolutionPlayer : Main.getInstance().getEvolutionPlayerManager().getEvolutionPlayers()) {
            Player player = evolutionPlayer.getPlayer();

            String ord = ordinal(scoredList.indexOf(evolutionPlayer) + 1);

            if(evolutionPlayer.getUUID() != winner)
                evolutionPlayer.getPlayer().teleport(Bukkit.getServer().getPlayer(winner)); // TP to the winner

            player.sendMessage(MessageUtil.createCenteredMessage(ChatColor.GOLD  + "" + ChatColor.STRIKETHROUGH + "---------------------------------"));
            player.sendMessage(MessageUtil.createCenteredMessage(" "));
            player.sendMessage(MessageUtil.createCenteredMessage(ChatColor.AQUA + Main.getInstance().getEvolutionPlayerManager().getPlayer(winner).getPlayer().getName() + ChatColor.RED + " has won the game!"));
            player.sendMessage(MessageUtil.createCenteredMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You placed " + ord));
            evolutionPlayer.getCoreUser().addActivity(ord + " Evolution");
            if(!evolutionPlayer.getCoreUser().isDeluxe()) {
                player.sendMessage(MessageUtil.createCenteredMessage(ChatColor.YELLOW + "You don't have deluxe! Help the server at:"));
                player.sendMessage(MessageUtil.createCenteredMessage(ChatColor.YELLOW + "http://store.pvpcentral.net"));
            }
            player.sendMessage(MessageUtil.createCenteredMessage(" "));
            player.sendMessage(MessageUtil.createCenteredMessage(ChatColor.GOLD  + "" + ChatColor.STRIKETHROUGH + "---------------------------------"));

            player.getInventory().clear();

            player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 0.75F, 1F);

            for (Player player1 : Bukkit.getOnlinePlayers())
                if (!player.getUniqueId().equals(player1.getUniqueId())) {
                    player1.showPlayer(player);
                    player.showPlayer(player1);
                }

            player.setAllowFlight(true);
            player.setFlying(true);

            player.setWalkSpeed(0.2f);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

            new AbstractItem(Items.PLAY_AGAIN.getItemStack(), evolutionPlayer.getCoreUser(), 7,
                    (type) -> evolutionPlayer.getCoreUser().addToQueue("evolution"));

            new AbstractItem(Items.RETURN_TO_HUB.getItemStack(), evolutionPlayer.getCoreUser(), 8,
                    (type) -> evolutionPlayer.getCoreUser().sendToServer("hub"));

            if(iDisguise.getInstance().getAPI().isDisguised((OfflinePlayer) player))
                iDisguise.getInstance().getAPI().undisguise(player);
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            for(me.chasertw123.minigames.core.user.User player : CoreAPI.getOnlinePlayers())
                player.sendToServer("hub");
        }, 20 * 15);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart"), 20 * 25);
    }

    public GameLoop getCurrentGameLoop() {
        return currentGameLoop;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void evolute(EvolutionPlayer evolutionPlayer) {
        if(evolutionPlayer.getEvolution() == Evolution.ENDERMAN) {
            // This kids won

            setWinner(evolutionPlayer.getUUID());

            endGame();
            return;
        }

        evolutionPlayer.getCoreUser().incrementStat(Stat.EVOLUTION_EVOLVES);

        evolutionPlayer.getPlayer().getInventory().clear();

        Location originalLocation = evolutionPlayer.getPlayer().getLocation();

        Title.sendActionbar(evolutionPlayer.getPlayer(), ChatColor.GREEN + "" + ChatColor.BOLD + "EVOLUTION COMPLETE");

        Evolution evolution = evolutionPlayer.incrementEvolution();
        evolutionPlayer.getPlayer().setMaxHealth(evolution.getHealth());
        evolutionPlayer.getPlayer().setHealth(evolutionPlayer.getPlayer().getMaxHealth());
        evolutionPlayer.setEvolutionPrimed(false);

        // Send Evolution message
        evolutionPlayer.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        evolutionPlayer.getPlayer().sendMessage(" ");
        evolutionPlayer.getPlayer().sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "You've evolved! New mob: " + ChatColor.RED + evolution.getCustomName());
        evolutionPlayer.getPlayer().sendMessage(" ");
        evolutionPlayer.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Health: " + ChatColor.RED + (evolution.getHealth() / 2) + "❤");
        evolutionPlayer.getPlayer().sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Ability: " + ChatColor.WHITE + evolution.getItemStack().getItemMeta().getDisplayName());
        evolutionPlayer.getPlayer().sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Ability Cooldown: " + ChatColor.WHITE + evolution.getCooldown() + "s");
        evolutionPlayer.getPlayer().sendMessage(" ");
        evolutionPlayer.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "---------------------------------------------------");

        Location loc = gameMap.getUpgradeLocation(Main.getInstance().getMapManager().getGame());

        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, evolution.getEntityType());

        Vector entityDirection = loc.clone().subtract(loc.clone().add(5, 5, 0)).toVector();
        Location newEntityLocation = loc.setDirection(entityDirection);
        entity.teleport(newEntityLocation);

        entity.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + evolution.toString());
        entity.setCustomNameVisible(true);

        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null)
            tag = new NBTTagCompound();

        nmsEntity.c(tag);
        tag.setInt("NoAI", 1);
        tag.setInt("Silent", 1);
        tag.setInt("Invulnerable", 1);
        nmsEntity.f(tag);

        evolutionPlayer.getPlayer().teleport(loc.clone().add(5, 0, 0));

        evolutionPlayer.getPlayer().setWalkSpeed(0f);
        evolutionPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, false, false));

        Vector dir = loc.clone().subtract(evolutionPlayer.getPlayer().getEyeLocation()).toVector();
        Location newLocation = evolutionPlayer.getPlayer().getLocation().setDirection(dir);
        evolutionPlayer.getPlayer().teleport(newLocation);

        LivingEntity descriptionHologram = spawnHolographicDisplay(loc.clone().add(0, 0.5f, 0), ChatColor.AQUA + ""
                + ChatColor.BOLD + "Ability: " + ChatColor.WHITE + evolution.getItemStack().getItemMeta().getDisplayName());
        LivingEntity healthHologram = spawnHolographicDisplay(loc.clone().add(0, 1f, 0), ChatColor.RED
                + "" + ChatColor.BOLD + "Health: " + ChatColor.WHITE + (evolution.getHealth() / 2) + ChatColor.RED + "❤");

        Bukkit.getOnlinePlayers().stream().filter(player -> !player.getUniqueId().equals(evolutionPlayer.getUUID())).forEach(player -> {
            me.chasertw123.minigames.core.Main.getEntityHider().hideEntity(player, descriptionHologram);
            me.chasertw123.minigames.core.Main.getEntityHider().hideEntity(player, healthHologram);
            me.chasertw123.minigames.core.Main.getEntityHider().hideEntity(player, entity);

            player.hidePlayer(evolutionPlayer.getPlayer());
        });

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            evolutionPlayer.getPlayer().teleport(originalLocation);

            entity.remove();
            descriptionHologram.remove();
            healthHologram.remove();

            evolutionPlayer.getPlayer().getInventory().clear();
            new AbstractItem(evolutionPlayer.getEvolution().getItemStack(), evolutionPlayer.getCoreUser(), 0, (type) -> {
                if(type == AbstractItem.InteractType.RIGHT)
                    evolutionPlayer.tryAbility();
            });

            evolutionPlayer.getPlayer().getInventory().setItem(1, new cItemStack(Material.COMPASS, ChatColor.GREEN + "Player Tracker"));

            // un-hide the player
            Bukkit.getOnlinePlayers().stream().filter(player -> !player.getUniqueId().equals(evolutionPlayer.getUUID()))
                    .forEach(player -> player.showPlayer(evolutionPlayer.getPlayer()));

            evolutionPlayer.getPlayer().setWalkSpeed(0.2f);
            evolutionPlayer.getPlayer().removePotionEffect(PotionEffectType.JUMP);

            // Set the player to the new mob
            iDisguise.getInstance().getAPI().disguise(evolutionPlayer.getPlayer(), evolution.getDisguiseType().newInstance()); // the mob to be disguised as
        }, 20 * 5);
    }

    public LivingEntity spawnHolographicDisplay(Location location, String text) {
        ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        entity.setGravity(false);
        entity.setVisible(false);

        entity.setCustomName(text);
        entity.setCustomNameVisible(true);

        return entity;
    }

    public Location getSpawnLocation(Player p) {
        // return the spawn with players the furthest away
        List<Location> spawns = gameMap.getSpawns(Main.getInstance().getMapManager().getGame());

        Map<Location, Integer> maxDistances = new HashMap<>();

        for(Location location : spawns) {
            int maxDistance = 0;

            for(Player player : Bukkit.getServer().getOnlinePlayers())
                if(p.getUniqueId() != player.getUniqueId() && maxDistance < player.getLocation().distance(location))
                    maxDistance = (int) player.getLocation().distance(location);

            maxDistances.put(location, maxDistance);
        }

        int maxDistance = 0;
        Location maxLocation = spawns.get(0);

        for(Map.Entry<Location, Integer> entry : maxDistances.entrySet()) {
            if(entry.getValue() > maxDistance) {
                maxDistance = entry.getValue();
                maxLocation = entry.getKey();
            }
        }

        return maxLocation;
    }

    private String ordinal(int i) {

        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {

            case 11:
            case 12:
            case 13:
                return i + "th";

            default:
                return i + sufixes[i % 10];

        }
    }

}
