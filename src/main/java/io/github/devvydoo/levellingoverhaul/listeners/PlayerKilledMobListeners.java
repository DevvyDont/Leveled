package io.github.devvydoo.levellingoverhaul.listeners;

import io.github.devvydoo.levellingoverhaul.util.BaseExperience;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Listeners in charge of listening for mob kill events. This is the main source of experience for our players
 */
public class PlayerKilledMobListeners implements Listener {


    @EventHandler
    public void onPlayerKillEntity(EntityDamageByEntityEvent event){

        // Was the entity living?
        if (!(event.getEntity() instanceof LivingEntity)){
            return;
        }

        LivingEntity livingEntity = (LivingEntity) event.getEntity();

        // Is the entity going to die?
        if (livingEntity.getHealth() - event.getFinalDamage() > 0){
            return;
        }

        // Are they dying to a player?
        if (!(event.getDamager() instanceof Player)){
            return;
        }

        Player player = (Player) event.getDamager();

        // Does the player even need xp? i.e. are they max level
        if (player.getLevel() >= 100){
            player.setLevel(100);
            player.setExp((float) .9999);
            return;
        }

        // At this point a player has killed another entity and we can calculate their xp
        int xp = BaseExperience.getBaseExperienceFromMob(livingEntity);
        player.giveExp(xp); // Gives player exp
        player.sendMessage(ChatColor.YELLOW + "+" + xp + " XP");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, (float) .5, 1);
    }

    /**
     * Several cases where we handle when a boss is killed by a player
     *
     * @param event - The EntityDeathEvent we are listening to
     */
    @EventHandler
    public void onBossDeath(EntityDeathEvent event){
        EntityType enemy = event.getEntity().getType();

        switch (enemy){
            case ENDER_DRAGON:
                // We are going to give all players in the end a bonus
                for (Player p: event.getEntity().getWorld().getPlayers()){
                    p.giveExp(200);
                    p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Ender Dragon" + ChatColor.YELLOW + "! +200XP");
                }
                break;
            case WITHER:
                // All players within 100 block radius from the wither get credit
                for (Player p: event.getEntity().getWorld().getPlayers()){
                    if (p.getLocation().distance(event.getEntity().getLocation()) < 100){
                        p.giveExp(175);
                        p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Wither" + ChatColor.YELLOW + "! +175XP");
                    }
                }
                break;
            case ELDER_GUARDIAN:
                // All players within 100 block radius from the guardian get credit
                for (Player p: event.getEntity().getWorld().getPlayers()){
                    if (p.getLocation().distance(event.getEntity().getLocation()) < 100){
                        p.giveExp(120);
                        p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Elder Guardian" + ChatColor.YELLOW + "! +120XP");
                    }
                }
                break;
        }
    }

}
