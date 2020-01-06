package io.github.devvydoo.levellingoverhaul.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageModifier implements Listener {


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
