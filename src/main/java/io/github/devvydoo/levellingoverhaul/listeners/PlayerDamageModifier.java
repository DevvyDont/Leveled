package io.github.devvydoo.levellingoverhaul.listeners;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerGotHitByMob(EntityDamageByEntityEvent event){

        // If the entity hit wasnt living don't worry
        if (!(event.getEntity() instanceof LivingEntity)){
            return;
        }

        // If the entity that attacked wasn't a mob or a projectile don't worry about it
        if (!(event.getDamager() instanceof LivingEntity || event.getDamager() instanceof Projectile)){
            return;
        }

        // Find the source, if we don't have living entity or projectile, we don't care
        LivingEntity source;
        if (event.getDamager() instanceof LivingEntity){
            source = (LivingEntity) event.getDamager();
        } else if (event.getDamager() instanceof Projectile){
            source = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
        } else {
            return;
        }

        // PVP will be handled somewhere else
        if (event.getEntity() instanceof Player && source instanceof Player){
            return;
        }

        // Get the level of the attacker
        int attackerLevel;
        double damageMultiplier = 1;

        // We will increase damage for mobs based on level, and decrease damage done on mobs by players that are underleveled and thats it
        if (!(source instanceof Player)){
            attackerLevel = plugin.getMobManager().getMobLevel(source);
            damageMultiplier += attackerLevel / (Math.random() * 3. + 6);  // lvl 15 does double dmg basically
            System.out.println(event.getDamage() + " dmg. x" + damageMultiplier + " -> " + event.getDamage() * damageMultiplier);
        } else if (!(event.getEntity() instanceof Player)){  // We already know we have a player here
            attackerLevel = ((Player) source).getLevel();
            int attackedLevel = plugin.getMobManager().getMobLevel(source);
            if (attackerLevel < attackedLevel){  // If a player attacked a mob higher then them
                damageMultiplier -= (attackedLevel - attackerLevel) * .01;  // Take lvlDiff % off damage
            }
        }

        // Sanity check
        if (damageMultiplier < .01){ damageMultiplier = .01; }
        event.setDamage(event.getDamage() * damageMultiplier);
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerGotHit(EntityDamageEvent event){

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
