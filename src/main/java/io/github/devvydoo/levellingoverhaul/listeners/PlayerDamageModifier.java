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

        // If a LivingEntity got hit by another living entity, check the levels and see if we should multiply it
        if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof LivingEntity){

            LivingEntity attacker = (LivingEntity) event.getDamager();
            LivingEntity attacked = (LivingEntity) event.getEntity();

            int attackerLevel = this.plugin.getMobManager().getMobLevel(attacker);
            int attackedLevel = this.plugin.getMobManager().getMobLevel(attacked);

            // If attacker is 5 levels higher than the other, multiply damage by level difference / 10
            if (attackerLevel > attackedLevel + 5){
                double damageMultiplier = 1 + ((attackerLevel - attackedLevel) / 10.);
                event.setDamage(event.getDamage() * damageMultiplier);
            }
            // If attacker is 5 levels lower than what they are attacking, reduce damage
            else if (attackerLevel + 5 < attackedLevel) {
                double damageMultiplier = 1 - ((attackedLevel - attackerLevel) * .01);
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
