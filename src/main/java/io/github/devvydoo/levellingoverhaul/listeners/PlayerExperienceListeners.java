package io.github.devvydoo.levellingoverhaul.listeners;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import io.github.devvydoo.levellingoverhaul.util.BaseExperience;
import io.github.devvydoo.levellingoverhaul.util.LevelRewards;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


/**
 * A class responsible for how we should handle experience gain for players. In general, we want to be completely in
 * charge of player experience, so we should cancel all vanilla experience gains
 */
public class PlayerExperienceListeners implements Listener {

    private LevellingOverhaul plugin;

    public PlayerExperienceListeners(LevellingOverhaul plugin){
        this.plugin = plugin;
    }

    private void playerLeveledUp(Player player, int oldLevel, int newLevel){

        this.plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD +  player.getDisplayName() + ChatColor.GRAY + " is now level " + ChatColor.GREEN + ChatColor.BOLD +  newLevel);
        if (newLevel == BaseExperience.LEVEL_CAP) { player.sendTitle(ChatColor.RED + "MAX Level!", ChatColor.GOLD + "You are now Level " + BaseExperience.LEVEL_CAP + "!", 10, 140, 20); }
        else { player.sendTitle(ChatColor.GREEN + "Level Up!", ChatColor.DARK_GREEN + "You are now Level " + newLevel + "!", 10, 70, 20); }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .7f, .5f);
        LevelRewards.playerLeveledUp(player,oldLevel, newLevel);
        double newMaxHp = this.plugin.getHpManager().calculatePlayerExpectedHealth(player, newLevel);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMaxHp);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setHealthScale(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        for ( int i = 0; i < 3; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation().add(Math.random() * 2 - 1, 0, Math.random() * 2 - 1), EntityType.FIREWORK);
                    FireworkMeta meta = firework.getFireworkMeta();
                    FireworkEffect.Builder effectBuilder = FireworkEffect.builder();
                    effectBuilder.with(FireworkEffect.Type.BALL_LARGE);
                    effectBuilder.withColor(Color.GREEN,Color.PURPLE);
                    meta.addEffect(effectBuilder.build());
                    firework.setFireworkMeta(meta);
                }
            }.runTaskLater(plugin, i * 5);
        }
    }

    @EventHandler
    public void onPlayerDamagedByFireworks(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Firework && event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event){

        // If we are max level, or over max level, we should go to the max
        if (event.getPlayer().getLevel() >= BaseExperience.LEVEL_CAP){
            event.getPlayer().setExp(.9999f);
            event.getPlayer().setLevel(BaseExperience.LEVEL_CAP);
            return;
        }

        // If we are gaining exp normally don't do anything
        if (event.getAmount() > 0){
            return;
        }

        // At this point we are losing exp, we don't want that set it to 0
        event.setAmount(0);
    }

    /**
     * Every time a player's level changes, we want to make sure
     *
     * @param event - the PlayerLevelChangeEvent we are listening to
     */
    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event){

        // Our plugin is probably modifying the level, don't do anything
        if (event.getOldLevel() == BaseExperience.DEBUG_LEVEL || event.getNewLevel() == BaseExperience.DEBUG_LEVEL){
            return;
        }

        // We are already max level, we should stay at max level
        if (event.getOldLevel() >= BaseExperience.LEVEL_CAP){
            event.getPlayer().setLevel(BaseExperience.LEVEL_CAP);
            event.getPlayer().setExp(.9999f);
            return;
        }

        // We just hit max level
        if (event.getNewLevel() >= BaseExperience.LEVEL_CAP){
            event.getPlayer().setExp((float) .9999);
            event.getPlayer().setLevel(BaseExperience.LEVEL_CAP);
            playerLeveledUp(event.getPlayer(), event.getOldLevel(), event.getNewLevel());
            return;
        }

        // First we want to make sure we are never going down in levels for now, our xp system doesn't go backwards
        if (event.getNewLevel() < event.getOldLevel()){
            event.getPlayer().setLevel(event.getOldLevel());  // Simply sets their level back to what it was
            event.getPlayer().setExp(0);  // This sets their progress to their current level to 0%
            return;
        }

        // At this point we are levelling up, let's tell the server
        playerLeveledUp(event.getPlayer(), event.getOldLevel(), event.getNewLevel());
    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Boolean rule = player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY);
            if (rule == null || !rule) { return; }  // If keep inventory is off, proceed with expected behavior
            player.setExp(0);  // If keep inventory is on, set the players exp progress to next level to 0
        }
    }
}
