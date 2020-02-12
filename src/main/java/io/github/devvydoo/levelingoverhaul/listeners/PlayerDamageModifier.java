package io.github.devvydoo.levelingoverhaul.listeners;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerDamageModifier implements Listener {

    private LevelingOverhaul plugin;

    public PlayerDamageModifier(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    private double calculateEntityDamage(LivingEntity entity, EntityDamageEvent.DamageCause cause){

        int mobLevel = plugin.getMobManager().getMobLevel(entity);
        double onLevelPlayerMaxHP =  100 + (Math.floor(Math.pow(mobLevel, 2.1)));
        double damagePercent;
        switch (entity.getType()){
            case HUSK:
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case DROWNED:
            case EVOKER:
            case STRAY:
            case PILLAGER:
                damagePercent = .25;
                break;
            case SKELETON:
                damagePercent = .15;
                break;
            case SPIDER:
            case CAVE_SPIDER:
            case SHULKER:
            case SHULKER_BULLET:
            case BLAZE:
                damagePercent = .10;
                break;
            case CREEPER:
                // TODO: Make scale based on distance
                damagePercent = .6;
                if (((Creeper) entity).isPowered()) { damagePercent += .2; }
                break;
            case ENDERMAN:
            case ILLUSIONER:
                damagePercent = .30;
                break;
            case SILVERFISH:
            case BEE:
                damagePercent = .05;
                break;
            case PHANTOM:
                damagePercent = .08;
                break;
            case SLIME:
            case MAGMA_CUBE:
                Slime s = (Slime) entity;
                damagePercent = .02;
                if (s instanceof MagmaCube) { damagePercent *= 2; }
                damagePercent *= (s.getSize() + 1);
                break;
            case WITHER_SKELETON:
                damagePercent = .2;
                break;
            case PIG_ZOMBIE:
                damagePercent = .23;
                break;
            case GHAST:
                damagePercent = .40;
                break;
            case RAVAGER:
                damagePercent = .85;
                break;
            case VINDICATOR:
                damagePercent = .80;
                break;
            case GUARDIAN:
                damagePercent = .18;
                break;
            case VEX:
                damagePercent = .70;
                break;
            case POLAR_BEAR:
            case PANDA:
                damagePercent = .35;
                break;
            case FOX:
            case WOLF:
            case CAT:
                damagePercent = .03;
                break;
            case LLAMA:
                damagePercent = .02;
                break;
            case ENDERMITE:
                damagePercent = .07;
                break;
            case IRON_GOLEM:
                damagePercent = .60;
                break;
            case PUFFERFISH:
                damagePercent = .01;
                break;
            case ENDER_DRAGON:
            case WITHER:
            case ELDER_GUARDIAN:
                damagePercent = .22;
                break;
            default:
                System.out.println("[PlayerDamageModifer] Came across unknown entity to calculate damage for: " + entity.getType());
                damagePercent = .005;
                break;
        }

        double damage = damagePercent * onLevelPlayerMaxHP;
        damage *= (1 + ((Math.random() - .5) / 10));
        return damage;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMobInflictedDamage(EntityDamageByEntityEvent event) {

        if (event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM) || event.getCause().equals(EntityDamageEvent.DamageCause.VOID) || event.getDamage() == 0)
            return;

        // If the entity hit wasnt living don't worry
        if (!(event.getEntity() instanceof LivingEntity))
            return;

        // If the entity that attacked wasn't a mob or a projectile don't worry about it
        if (!(event.getDamager() instanceof LivingEntity || event.getDamager() instanceof Projectile))
            return;

        // Find the source, if we don't have living entity or projectile, we don't care
        LivingEntity source;
        if (event.getDamager() instanceof LivingEntity)
            source = (LivingEntity) event.getDamager();
        else if (event.getDamager() instanceof Projectile)
            source = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
        else
            return;


        // We don't care for players doing damage
        if (source instanceof Player)
            return;

        // Get the level of the attacker
        double newDamage;

        // Calculates damage based on damage type, and the entity doing the damage, generally, the damage will be the % of a players max HP at the same level of the mob i.e. zombie does 15% damage to player on level
        newDamage = calculateEntityDamage(source, event.getCause());

        // Sanity check
        if (newDamage < 0) { newDamage = 0; }
        event.setDamage(newDamage);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerHitByMiscSource(EntityDamageEvent event) {


        if (!(event.getEntity() instanceof Player))
            return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM) || event.getCause().equals(EntityDamageEvent.DamageCause.VOID) || event.getDamage() == 0)
            return;

        Player player = (Player) event.getEntity();
        double fivePercentHP = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 20.;

        // If we get hit by more natural causes rather than mobs, base this off % hp
        if (event.getCause().equals(EntityDamageEvent.DamageCause.CONTACT) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.CRAMMING) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.DROWNING) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.STARVATION) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
            if (event.getDamage() < fivePercentHP)
                event.setDamage(fivePercentHP);

        } else if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE)) {
            double dmg = fivePercentHP - plugin.getArmorManager().getPlayerArmorAttributes(player).getFireDefense();
            if (dmg <= 5)
                dmg = 5;
            event.setDamage(dmg);
        }
        else if (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
            double dmg = fivePercentHP * 5 - plugin.getArmorManager().getPlayerArmorAttributes(player).getFireDefense();
            if (dmg <= 20)
                dmg = 20;
            event.setDamage(dmg);
        }
        else if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) || event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            float blocksFallen = player.getFallDistance();
            if (player.getInventory().getBoots() != null)
                blocksFallen /= ((player.getInventory().getBoots().getEnchantmentLevel(Enchantment.PROTECTION_FALL) + 1) / 2.);
            event.setDamage(blocksFallen * fivePercentHP / 1.1);
        }
        else if (event.getCause().equals(EntityDamageEvent.DamageCause.THORNS) && event instanceof EntityDamageByEntityEvent) {

            Entity thornsOwner = ((EntityDamageByEntityEvent) event).getDamager();
            if (thornsOwner instanceof LivingEntity) {
                int level = plugin.getMobManager().getMobLevel((LivingEntity) thornsOwner);
                double newDamage = level * 2;

                newDamage -= plugin.getArmorManager().getPlayerArmorAttributes(player).getDefense();
                if (newDamage < 1) {
                    newDamage = 1;
                }
                event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
                event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);
                event.setDamage(newDamage);
            }
        }
        else if (event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)){
            double dmg = event.getDamage() - plugin.getArmorManager().getPlayerArmorAttributes(player).getExplosionDefense();
            if (dmg <= 20)
                dmg = 20;
            event.setDamage(dmg);
        } else {
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
            event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);
            double newDamage = event.getDamage();
            if (plugin.getEnchantmentManager().hasEnchant(player.getInventory().getItemInMainHand(), CustomEnchantType.BERSERK))
                newDamage *= 3;
            newDamage -= plugin.getArmorManager().getPlayerArmorAttributes(player).getDefense();
            if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE))
                newDamage -= plugin.getArmorManager().getPlayerArmorAttributes(player).getProjectileDefense();
            if (newDamage < 1) { newDamage = 1; }
            event.setDamage(newDamage);
        }
    }

    /**
     * Overkill protection mechanic, if we are hit by a blow that is supposed to kill a player but they have 50% hp
     * keep them at 1 hp
     *
     * @param event The EntityDamageEvent we are listening to
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGotHit(EntityDamageEvent event) {

        if (event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM) || event.getCause().equals(EntityDamageEvent.DamageCause.VOID) || event.getDamage() == 0) {
            return;
        }

        // Is a player being hit?
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (player.getHealth() <= 0) { player.damage(2000000000); }

        // Is the player supposed to die?
        if (player.getHealth() + player.getAbsorptionAmount() - event.getFinalDamage() > 0) {
            return;
        }

        AttributeInstance maxHPAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maxHP = maxHPAttribute != null ? maxHPAttribute.getValue() : 20;

        // Does the player have more than 50% of their max hp?
        if (player.getHealth() / maxHP < .5) {
            return;
        }

        // At this point a player is due to die, but has > 50% of their hp, leave them at 1/2 a heart
        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);
        event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
        event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0);
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, 0);
        event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, 0);
        event.setDamage(0);
        player.setAbsorptionAmount(0);
        player.setHealth(1);
    }

}
