package io.github.devvydoo.levellingoverhaul.managers;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

/**
 * A class used to do any necessary calculations for damage calculations BEFORE our plugin modifies anything whether it
 * be enchants, the overkill protection mechanic, etc. Since our base HP is 100 rather than 20, we start by making
 * all damage in this plugin multiplied by 5 to start, things will be balanced as time goes on
 */
public class GlobalDamageManager implements Listener {

    private LevellingOverhaul plugin;
    private String ARROW_DMG_METANAME = "base_damage";

    public GlobalDamageManager(LevellingOverhaul plugin) {
        this.plugin = plugin;
    }

    private double getRangedWeaponBaseDamage(Material tool){
        switch (tool){
            case BOW:
                return 20;
            case CROSSBOW:
                return 60;
            default:
                return 0;
        }
    }

    private double getMeleeWeaponBaseDamage(Material tool){
        switch (tool){

            case DIAMOND_AXE:
                return 140;
            case DIAMOND_SWORD:
                return 100;

            case IRON_AXE:
                return 90;
            case IRON_SWORD:
                return 75;

            case GOLDEN_AXE:
                return 70;
            case GOLDEN_SWORD:
                return 50;

            case STONE_AXE:
                return 44;
            case STONE_SWORD:
                return 32;

            case WOODEN_AXE:
                return 28;
            case WOODEN_SWORD:
                return 20;
            default:
                return 0;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamaged(EntityDamageEvent event){
        event.setDamage(event.getDamage() * 5);
    }

    /**
     * Vanilla Sharpness and Power are bad, we are going to buff them
     *
     * @param event The event in which any entity is damaged by another one
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerMeleeAttack(EntityDamageByEntityEvent event){

        // Make sure a player is attacking
        if (!(event.getDamager() instanceof Player)){
            return;
        }

        Player player = (Player) event.getDamager();
        ItemStack tool = player.getInventory().getItemInMainHand();

        // First let's get a base setup for how much certain weapons should do
        double baseDamage = getMeleeWeaponBaseDamage(tool.getType());
        // If baseDamage returns 0, we have something that we don't care to modify
        if (baseDamage <= 0){ return; }

        double newDamage = baseDamage;
        int sharpnessLevel = tool.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        // In the case we have sharpness, we should increase raw damage
        if (sharpnessLevel > 0) { newDamage *= 1 + sharpnessLevel / 10.;  }
        // In the case we have smite, we should increase damage against certain entities
        else if (event.getEntity() instanceof Zombie || event.getEntity() instanceof Skeleton || event.getEntity() instanceof Phantom || event.getEntity() instanceof Wither || event.getEntity() instanceof SkeletonHorse){
            int smiteLevel = tool.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);
            if (smiteLevel > 0) { newDamage *= 1 + smiteLevel / 15.; }
        }
        // In the case we have bane of artho, we should increase against certain entities
        else if (event.getEntity() instanceof Spider || event.getEntity() instanceof Bee || event.getEntity() instanceof Silverfish || event.getEntity() instanceof Endermite){
            int baneLevel = tool.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);
            if (baneLevel > 0) { newDamage *= 1 + baneLevel / 15.; }
        }

        // Lastly, see if we should give a 50% bonus for crits
        if (!player.isOnGround() && player.getVelocity().getY() < 0) { newDamage *= 1.5; }

        // Give it a 5% variance
        newDamage *= 1 + ((Math.random() - .5) / 10.);

        // Now set the damage, good to go
        event.setDamage(newDamage);
    }

    /**
     * Everytime an arrow is shot by a player, we will calculate how much damage that arrow will do before it hits
     * something. We will keep track of this data by setting some meta data on the arrow entity, then when an entity
     * is hit by an arrow, we will read that meta data value and set the damage to that
     *
     * @param event The EntityShootBowEvent we are listening to
     */
    @EventHandler
    public void onPlayerShotBow(EntityShootBowEvent event){
        if (event.getEntity() instanceof Player){
            ItemStack bow = event.getBow();
            if (bow == null){ return; }  // Don't care if we dont have a bow
            double damage = getRangedWeaponBaseDamage(event.getBow().getType());
            if (damage <= 0) { return; }  // Don't care if we don't have any damage to apply

            // Now we take into account force the bow shot with
            damage *= event.getForce();

            // And then we calculate damage increased from the power enchantment
            int powerLevel = bow.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
            if (powerLevel > 0) { damage *= powerLevel; }

            // 20% chance to crit for double damage
            if (Math.random() < 2) { damage *= 2;
            event.getEntity().getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0, 1.6, 0), 50);
            event.getEntity().getWorld().playSound(event.getEntity().getLocation().add(0, 1.6, 0), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, .3f, .6f);
            }

            // 5% variance
            damage *= 1 + ((Math.random() - .5) / 10.);

            // Now we can put this value on the arrow to modify later in a different event
            event.getProjectile().setMetadata(ARROW_DMG_METANAME, new FixedMetadataValue(plugin, damage));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityHitByBow(EntityDamageByEntityEvent event){

        // Make sure we are dealing with arrows and arrows being shot by players here
        if (!(event.getDamager() instanceof Arrow)){ return; }
        Arrow arrow = (Arrow) event.getDamager();
        if (!(arrow.getShooter() instanceof Player)) { return; }

        double newDamage = 0;

        // Our plugin adds a metadata value on the arrow entity that says how much damage it should do, override if it exists
        if (arrow.hasMetadata(ARROW_DMG_METANAME)){
            for (MetadataValue metadataValue : arrow.getMetadata(ARROW_DMG_METANAME)){
                if (metadataValue.getOwningPlugin() == null) { continue; }
                if (metadataValue.getOwningPlugin().equals(plugin)) {
                    newDamage = metadataValue.asDouble();
                    break;
                }
            }
        }

        if (newDamage > 0) { event.setDamage(newDamage); }
    }

}
