package io.github.devvydoo.levellingoverhaul.listeners;

import io.github.devvydoo.levellingoverhaul.util.BaseMobExperience;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
        int xp = BaseMobExperience.getBaseExperienceFromMob(livingEntity);
        player.giveExp(xp); // Gives player exp
        player.sendMessage(ChatColor.YELLOW + "+" + xp + " XP");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, (float) .5, 1);
    }





}
