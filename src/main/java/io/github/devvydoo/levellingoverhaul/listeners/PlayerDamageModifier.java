package io.github.devvydoo.levellingoverhaul.listeners;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.ChatColor;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerDamageModifier implements Listener {

    private LevellingOverhaul plugin;

    public PlayerDamageModifier(LevellingOverhaul plugin){
        this.plugin = plugin;
    }

    /**
     * All armor in the game have a % resist value, that is defined here
     *
     * @param armor The ItemStack that the player is wearing
     * @return a % amount of resist
     */
    private double getArmorDamageResistPercent(ItemStack armor){
        switch (armor.getType()){
            case DIAMOND_CHESTPLATE:
                return .19;
            case DIAMOND_LEGGINGS:
                return .17;
            case DIAMOND_HELMET:
            case IRON_CHESTPLATE:
                return .14;
            case IRON_LEGGINGS:
                return .11;
            case DIAMOND_BOOTS:
                return .10;
            case IRON_HELMET:
            case CHAINMAIL_CHESTPLATE:
                return .09;
            case CHAINMAIL_LEGGINGS:
                return .08;
            case CHAINMAIL_HELMET:
            case GOLDEN_CHESTPLATE:
                return .07;
            case GOLDEN_LEGGINGS:
            case IRON_BOOTS:
                return .06;
            case LEATHER_CHESTPLATE:
                return .05;
            case CHAINMAIL_BOOTS:
            case GOLDEN_HELMET:
            case LEATHER_LEGGINGS:
                return .04;
            case LEATHER_HELMET:
            case TURTLE_HELMET:
            case GOLDEN_BOOTS:
                return .03;
            case LEATHER_BOOTS:
                return .02;
            default:
                return 0;
        }
    }

    /**
     * Gets the total % resist that a player should resist from taking damage
     *
     * @param player the Player that got hit
     * @return A % of damage that we should reduce
     */
    private double getPlayerDamageResist(Player player){
        PlayerInventory inventory = player.getInventory();
        double damageResist = 0;
        if (inventory.getHelmet() != null) {damageResist += getArmorDamageResistPercent(inventory.getHelmet()); }
        if (inventory.getChestplate() != null) {damageResist += getArmorDamageResistPercent(inventory.getChestplate()); }
        if (inventory.getLeggings() != null) {damageResist += getArmorDamageResistPercent(inventory.getLeggings()); }
        if (inventory.getBoots() != null) {damageResist += getArmorDamageResistPercent(inventory.getBoots()); }
        if (damageResist > .99){ damageResist = .99; }
        return damageResist;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
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
            System.out.println("[" + source.getName() + "] Base: " + event.getDamage() + " dmg. x" + (Math.round(damageMultiplier * 1000) / 1000.) + " Final -> " + (Math.round(event.getDamage() * damageMultiplier)));
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void playerHitByMiscSource(EntityDamageEvent event){

        if (!(event.getEntity() instanceof Player)){
            return;
        }

        Player player = (Player) event.getEntity();
        double fivePercentHP = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 20.;

        // If we get hit by more natural causes rather than mobs, base this off % hp
        if (event.getCause().equals(EntityDamageEvent.DamageCause.CONTACT) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.CRAMMING) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.DROWNING) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.STARVATION) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)){
            if (event.getDamage() < fivePercentHP) {
                event.setDamage(fivePercentHP);
            }
        } else if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) || event.getCause().equals(EntityDamageEvent.DamageCause.VOID)){
            int blocksFallen = (int)((event.getDamage() / 5) - 3) / 2;
            if (blocksFallen <= 0) { event.setCancelled(true); return; }
            event.setDamage(blocksFallen * fivePercentHP);
        } else {

            // Here the player is being hit by things that armor should resist, here's how armor works:
            // Every armor has a base % value to subtract off of the total damage, prot will be flat dmg reduction
            double percentResisted = getPlayerDamageResist(player);
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
            System.out.println(ChatColor.AQUA + "[DEBUG] " + player.getName() + " was hit, armor resist: " + Math.round(percentResisted * 100) + "% before: " + Math.round(event.getDamage()) + " after: " + (Math.round(event.getDamage() * (1 - percentResisted))));
            event.setDamage(event.getDamage() * (1 - percentResisted));

        }

    }

    /**
     * Overkill protection mechanic, if we are hit by a blow that is supposed to kill a player but they have 50% hp
     * keep them at 1 hp
     *
     * @param event The EntityDamageEvent we are listening to
     */
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
