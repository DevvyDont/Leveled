package io.github.devvydoo.levelingoverhaul.listeners.progression;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.player.PlayerExperience;
import io.github.devvydoo.levelingoverhaul.util.BaseExperience;
import io.github.devvydoo.levelingoverhaul.util.LevelRewards;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;


/**
 * A class responsible for how we should handle experience gain for players. In general, we want to be completely in
 * charge of player experience, so we should cancel all vanilla experience gains
 */
public class PlayerExperienceListeners implements Listener {

    private LevelingOverhaul plugin;

    public PlayerExperienceListeners(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    private void playerLeveledUp(Player player, int oldLevel, int newLevel) {

        if (newLevel <= oldLevel)
            return;

        this.plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + player.getDisplayName() + ChatColor.GRAY + " is now level " + ChatColor.GREEN + ChatColor.BOLD + newLevel);
        if (newLevel == PlayerExperience.LEVEL_CAP) {
            player.sendTitle(ChatColor.RED + "MAX Level!", ChatColor.GOLD + "You are now Level " + PlayerExperience.LEVEL_CAP + "!", 10, 140, 20);
        } else {
            player.sendTitle(ChatColor.GREEN + "Level Up!", ChatColor.DARK_GREEN + "You are now Level " + newLevel + "!", 10, 70, 20);
        }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .7f, .5f);
        LevelRewards.playerLeveledUp(player, oldLevel, newLevel);
        plugin.getPlayerManager().updateLeveledPlayerAttributes(player);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
        for (int i = 0; i < 3; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation().add(Math.random() * 2 - 1, 0, Math.random() * 2 - 1), EntityType.FIREWORK);
                    FireworkMeta meta = firework.getFireworkMeta();
                    FireworkEffect.Builder effectBuilder = FireworkEffect.builder();
                    effectBuilder.with(FireworkEffect.Type.BALL_LARGE);
                    effectBuilder.withColor(Color.GREEN, Color.PURPLE);
                    meta.addEffect(effectBuilder.build());
                    firework.setFireworkMeta(meta);
                }
            }.runTaskLater(plugin, i * 5);
        }
    }

    @EventHandler
    public void onPlayerDamagedByFireworks(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework && event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    /**
     * Every time a player's level changes, we want to make sure
     *
     * @param event - the PlayerLevelChangeEvent we are listening to
     */
    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        // At this point we are levelling up, let's tell the server
        playerLeveledUp(event.getPlayer(), event.getOldLevel(), event.getNewLevel());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setShouldDropExperience(false);
    }
}
