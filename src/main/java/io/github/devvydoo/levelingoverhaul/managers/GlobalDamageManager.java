package io.github.devvydoo.levelingoverhaul.managers;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantments;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

/**
 * A class used to do any necessary calculations for damage calculations BEFORE our plugin modifies anything whether it
 * be enchants, the overkill protection mechanic, etc. Since our base HP is 100 rather than 20, we start by making
 * all damage in this plugin multiplied by 5 to start, things will be balanced as time goes on
 */
public class GlobalDamageManager implements Listener {

    private LevelingOverhaul plugin;
    private String ARROW_DMG_METANAME = "base_damage";
    private String ARROW_SNIPE_ENCHANT_METANAME = "snipe_enchant_level";
    private HashMap<Player, Long> playerMeleeCooldownMap = new HashMap<>();

    public GlobalDamageManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    private double getRangedWeaponBaseDamage(Material tool) {
        switch (tool) {
            case BOW:
                return 20;
            case CROSSBOW:
                return 30;
            default:
                return 0;
        }
    }

    private double getMeleeWeaponBaseDamage(Material tool) {
        switch (tool) {

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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamaged(EntityDamageEvent event) {
        event.setDamage(event.getDamage() * 5);
    }

    /**
     * Vanilla Sharpness and Power are bad, we are going to buff them
     *
     * @param event The event in which any entity is damaged by another one
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerMeleeAttack(EntityDamageByEntityEvent event) {

        // Make sure a player is attacking
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        ItemStack tool = player.getInventory().getItemInMainHand();

        // First let's get a base setup for how much certain weapons should do
        double baseDamage = getMeleeWeaponBaseDamage(tool.getType());
        if (playerMeleeCooldownMap.containsKey(player)) {
            if (System.currentTimeMillis() < playerMeleeCooldownMap.get(player)) {
                baseDamage *= (double) (System.currentTimeMillis() / playerMeleeCooldownMap.get(player));
            }
        }
        long cooldownMs = 625;
        if (tool.getType().equals(Material.DIAMOND_AXE) || tool.getType().equals(Material.IRON_AXE) || tool.getType().equals(Material.GOLDEN_AXE) || tool.getType().equals(Material.STONE_AXE) || tool.getType().equals(Material.WOODEN_AXE)) {
            cooldownMs += 400;
        }
        playerMeleeCooldownMap.put(player, System.currentTimeMillis() + cooldownMs);
        // If baseDamage returns 0, we have something that we don't care to modify
        if (baseDamage <= 0) {
            return;
        }

        double newDamage = baseDamage;
        int sharpnessLevel = tool.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        // In the case we have sharpness, we should increase raw damage
        if (sharpnessLevel > 0) {
            newDamage *= (1 + ((5 * sharpnessLevel) * (3 + sharpnessLevel) / 100. ));  // adds 5x^2 + 15x % damage
        }
        // In the case we have smite, we should increase damage against certain entities
        else if (event.getEntity() instanceof Zombie || event.getEntity() instanceof Skeleton || event.getEntity() instanceof Phantom || event.getEntity() instanceof Wither || event.getEntity() instanceof SkeletonHorse) {
            int smiteLevel = tool.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);
            if (smiteLevel > 0) {
                newDamage *= (1 + ((7 * smiteLevel) * (5 + smiteLevel) / 100. ));  // adds 7x^2 + 35x % damage
            }
        }
        // In the case we have bane of artho, we should increase against certain entities
        else if (event.getEntity() instanceof Spider || event.getEntity() instanceof Bee || event.getEntity() instanceof Silverfish || event.getEntity() instanceof Endermite) {
            int baneLevel = tool.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);
            if (baneLevel > 0) {
                newDamage *= (1 + ((7 * baneLevel) * (5 + baneLevel) / 100. ));  // adds 7x^2 + 35x % damage
            }
        }

        // Lastly, see if we should give a 50% bonus for crits
        if (!player.isOnGround() && player.getVelocity().getY() < 0) {
            int critLevel = 0;
            try {
                critLevel = CustomEnchantments.getEnchantLevel(tool, CustomEnchantType.CRITICAL_STRIKE);
            } catch (IllegalArgumentException ignored) {
            }
            newDamage *= 1.5 + (critLevel * .15);
        }

        for (PotionEffect pot: player.getActivePotionEffects()){
            if (pot.getType().equals(PotionEffectType.INCREASE_DAMAGE)){
                newDamage *= (pot.getAmplifier() * 1.3);
            }
        }

        // Give it a 5% variance
        newDamage *= (1 + ((Math.random() - .5) / 10.));

        // Sweeping edge needs to have reduced damage
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
            int sweepLevel = tool.getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
            if (sweepLevel > 0) {
                newDamage *= (sweepLevel) / 11.;
            } else {
                newDamage *= .03;
            }
        }

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
    @EventHandler(ignoreCancelled = true)
    public void onPlayerShotBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            ItemStack bow = event.getBow();
            if (bow == null) {
                return;
            }  // Don't care if we dont have a bow
            double damage = getRangedWeaponBaseDamage(event.getBow().getType());
            if (damage <= 0) {
                return;
            }  // Don't care if we don't have any damage to apply

            // Now we take into account force the bow shot with
            damage *= event.getForce();

            // And then we calculate damage increased from the power enchantment
            int powerLevel = bow.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
            if (powerLevel > 0) {
                damage *= (1 + Math.pow(powerLevel, 1.641));
            }

            // 20% chance to crit for double damage
            double critPercent = .2;
            // crit enchant level * 10% chance bonus
            try {
                critPercent += CustomEnchantments.getEnchantLevel(bow, CustomEnchantType.CRITICAL_SHOT) / 10.;
            } catch (IllegalArgumentException ignored) {
            }
            if (Math.random() < critPercent) {
                damage *= 2;
                event.getEntity().getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0, 1.6, 0), 50);
                event.getEntity().getWorld().playSound(event.getEntity().getLocation().add(0, 1.6, 0), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, .3f, .6f);
            }

            for (PotionEffect pot: event.getEntity().getActivePotionEffects()){
                if (pot.getType().equals(PotionEffectType.INCREASE_DAMAGE)){
                    damage *= (pot.getAmplifier() * 1.3);
                }
            }

            // 5% variance
            damage *= (1 + ((Math.random() - .5) / 10.));

            // Now we can put this value on the arrow to modify later in a different event
            event.getProjectile().setMetadata(ARROW_DMG_METANAME, new FixedMetadataValue(plugin, damage));
            // Attempts to put the snipe level on the arrow if it exists, if it fails then no biggie
            try {
                event.getProjectile().setMetadata(ARROW_SNIPE_ENCHANT_METANAME, new FixedMetadataValue(plugin, CustomEnchantments.getEnchantLevel(bow, CustomEnchantType.SNIPE)));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityHitByBow(EntityDamageByEntityEvent event) {

        // Make sure we are dealing with arrows and arrows being shot by players here
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) event.getDamager();
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }

        double newDamage = 0;

        // Our plugin adds a metadata value on the arrow entity that says how much damage it should do, override if it exists
        if (arrow.hasMetadata(ARROW_DMG_METANAME)) {
            for (MetadataValue metadataValue : arrow.getMetadata(ARROW_DMG_METANAME)) {
                if (metadataValue.getOwningPlugin() == null) {
                    continue;
                }
                if (metadataValue.getOwningPlugin().equals(plugin)) {
                    newDamage = metadataValue.asDouble();
                    break;
                }
            }
        }

        // Our plugin adds a metadata value on the arrow entity that says how much snipe damage bonus we should have
        double distanceMultiplier = 1;
        if (arrow.hasMetadata(ARROW_SNIPE_ENCHANT_METANAME)) {
            for (MetadataValue metadataValue : arrow.getMetadata(ARROW_SNIPE_ENCHANT_METANAME)) {
                if (metadataValue.getOwningPlugin() == null) {
                    continue;
                }
                if (metadataValue.getOwningPlugin().equals(plugin)) {
                    double distance = event.getEntity().getLocation().distance(((Player) arrow.getShooter()).getLocation());
                    if (distance > 20) {
                        distanceMultiplier += metadataValue.asInt() * (distance / 100.);
                    }
                    break;
                }
            }
        }
        newDamage *= distanceMultiplier;
        if (newDamage > 0) {
            event.setDamage(newDamage);
        }
    }

    @EventHandler(ignoreCancelled = true)
        public void onMobHitByThorns(EntityDamageByEntityEvent event){

            // Player vs Player thorns is handled elsewhere
            if (event.getEntity() instanceof Player){ return; }

            // If something other than a player is getting hit my thorns
            if (event.getCause().equals(EntityDamageEvent.DamageCause.THORNS)) {
                int level;
                try { level = plugin.getMobManager().getMobLevel((LivingEntity) event.getDamager()); } catch (ClassCastException ignored) { return; }
                event.setDamage(level * (Math.random() - .5));
            }
        }


    @EventHandler
    public void onFallingInVoid(PlayerMoveEvent event) {
        if (event.getTo() != null && event.getTo().getY() < -300) {
            event.getPlayer().damage(event.getTo().getY() * -1);
        }
    }

}
