package io.github.devvydoo.levellingoverhaul.listeners;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageModifier implements Listener {

    private LevellingOverhaul plugin;

    public PlayerDamageModifier(LevellingOverhaul plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerGotHitByMob(EntityDamageByEntityEvent event){

        // If the event is cancelled we don't gotta do anything
        if (event.isCancelled()){
            return;
        }

        // If a player got hit by a living entity, check the levels and see if we should multiply it
        if (event.getEntity() instanceof Player && event.getDamager() instanceof LivingEntity){

            LivingEntity attacker = (LivingEntity) event.getDamager();
            Player player = (Player) event.getEntity();

            int attackerLevel = this.plugin.getMobManager().getMobLevel(attacker);
            int playerLevel = player.getLevel();

            // If attacker is 5 levels higher than player, multiply damage by level difference / 10
            if (attackerLevel > playerLevel + 5){
                double damageMultiplier = 1 + ((attackerLevel - playerLevel) / 10.);
                event.setDamage(event.getDamage() * damageMultiplier);
            }
        }

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGotHit(EntityDamageEvent event){

        // Was the event cancelled anyway?
        if (event.isCancelled()){
            return;
        }

        // Is a player being hit?
        if (!(event.getEntity() instanceof Player)){
            return;
        }

        Player player = (Player) event.getEntity();

        // Is the player supposed to die?
        if (player.getHealth() + player.getAbsorptionAmount() - event.getFinalDamage() > 0){
            return;
        }

        AttributeInstance maxHPAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maxHP = maxHPAttribute != null ? maxHPAttribute.getValue() : 20;

        // Does the player have more than 50% of their max hp?
        if (player.getHealth() / maxHP < .5){
            return;
        }

        // At this point a player is due to die, but has > 50% of their hp, leave them at 1/2 a heart
        event.setDamage(0);
        player.setAbsorptionAmount(0);
        player.setHealth(1);
    }

}
