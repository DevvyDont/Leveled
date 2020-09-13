package me.devvy.leveled.managers;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.events.EntityDamagedByMiscEvent;
import me.devvy.leveled.events.EntityHitByProjectileEvent;
import me.devvy.leveled.events.EntityShootArrowEvent;
import me.devvy.leveled.events.PlayerDealtMeleeDamageEvent;
import me.devvy.leveled.items.CustomItemType;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;

import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

/**
 * A class used to do any necessary calculations for damage calculations BEFORE our plugin modifies anything whether it
 * be enchants, the overkill protection mechanic, etc. Since our base HP is 100 rather than 20, we start by making
 * all damage in this plugin multiplied by 5 to start, things will be balanced as time goes on
 */
public class GlobalDamageManager implements Listener {

    private final Leveled plugin;

    public static final String ARROW_SNIPE_ENCHANT_METANAME = "snipe_enchant_level";
    public static final String ARROW_FMJ_ENCHANT_METANAME = "fmj_enchant_level";
    public static final String ARROW_EXECUTE_ENCHANT_METANAME = "exe_enchant_level";

    public GlobalDamageManager(Leveled plugin) {
        this.plugin = plugin;
    }

    private double getRangedWeaponBaseDamage(ItemStack tool) {

        if (plugin.getCustomItemManager().isCustomItem(tool))
            return plugin.getCustomItemManager().getCustomItemType(tool).STAT_AMOUNT;

        if (CustomItemType.Category.getFallbackCategory(tool.getType()) != CustomItemType.Category.RANGED)
            return 3;

        return CustomItemType.getFallbackStat(tool.getType());
    }

    private double getMeleeWeaponBaseDamage(ItemStack tool) {

        if (plugin.getCustomItemManager().isCustomItem(tool))
            return plugin.getCustomItemManager().getCustomItemType(tool).STAT_AMOUNT;

        if (CustomItemType.Category.getFallbackCategory(tool.getType()) != CustomItemType.Category.MELEE)
            return 3;

        return CustomItemType.getFallbackStat(tool.getType());
    }

    /**
     * Vanilla Sharpness and Power are bad, we are going to buff them
     *
     * @param event The event in which any entity is damaged by another one
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMeleeAttack(EntityDamageByEntityEvent event) {

        // Make sure a player is attacking
        if (!(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getDamager();
        ItemStack tool = player.getInventory().getItemInMainHand();

        PlayerDealtMeleeDamageEvent playerDealtMeleeDamageEvent = new PlayerDealtMeleeDamageEvent(player, event.getEntity(), event.getCause(), getMeleeWeaponBaseDamage(tool));
        plugin.getServer().getPluginManager().callEvent(playerDealtMeleeDamageEvent);

        // Player strength (THIS INCLUDES STRENGTH POTS)
        playerDealtMeleeDamageEvent.multiplyDamage(plugin.getPlayerManager().getLeveledPlayer(player).getStrengthBonus());

        // Give it a 5% variance
        playerDealtMeleeDamageEvent.multiplyDamage(1 + ((Math.random() - .5) / 10f));

        // Sweeping edge needs to have reduced damage
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
            int sweepLevel = tool.getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
            float mult = sweepLevel > 0 ? sweepLevel / 11f : .03f;
            playerDealtMeleeDamageEvent.multiplyDamage(mult);
        }

        // Now set the damage, good to go
        event.setDamage(playerDealtMeleeDamageEvent.getDamage());
    }

    /**
     * Everytime an arrow is shot by a player, we will calculate how much damage that arrow will do before it hits
     * something. We will keep track of this data by setting some meta data on the arrow entity, then when an entity
     * is hit by an arrow, we will read that meta data value and set the damage to that
     *
     * @param event The EntityShootBowEvent we are listening to
     */
    @EventHandler
    public void onEntityShotBow(EntityShootBowEvent event) {

        ItemStack bow = event.getBow();

        // Don't care if we dont have a bow
        if (bow == null)
            return;

        double baseDamage = getRangedWeaponBaseDamage(event.getBow());

        if (baseDamage <= 0)
            return;

        EntityShootArrowEvent shootArrowEvent = new EntityShootArrowEvent(event.getEntity(), bow, event.getArrowItem(), event.getProjectile(), event.getForce(), baseDamage);
        plugin.getServer().getPluginManager().callEvent(shootArrowEvent);

        if (shootArrowEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof Player)
            shootArrowEvent.multiplyDamage((float) plugin.getPlayerManager().getLeveledPlayer((Player) event.getEntity()).getStrengthBonus());
        else if (event.getEntity().getPotionEffect(PotionEffectType.INCREASE_DAMAGE) != null && event.getEntity().getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() != 0)
            shootArrowEvent.multiplyDamage(event.getEntity().getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() * 1.3f);

        // Marks the arrow with the damage calculated
        shootArrowEvent.finalizeDamage();

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityHitByBow(EntityDamageByEntityEvent event) {

        // Make sure we are dealing with arrows and arrows being shot by players here
        if (!(event.getDamager() instanceof Arrow))
            return;

        Arrow arrow = (Arrow) event.getDamager();

        if (!(arrow.getShooter() instanceof LivingEntity))
            return;

        EntityHitByProjectileEvent entityHitByProjectileEvent = new EntityHitByProjectileEvent(arrow, event.getEntity(), event.getFinalDamage());
        plugin.getServer().getPluginManager().callEvent(entityHitByProjectileEvent);
        
        event.setDamage(entityHitByProjectileEvent.getDamage());
    }

    @EventHandler
    public void onMobHitByThorns(EntityDamageByEntityEvent event){

        // Player vs Player thorns is handled elsewhere
        if (event.getEntity() instanceof Player){ return; }

        // If something other than a player is getting hit my thorns
        if (event.getCause().equals(EntityDamageEvent.DamageCause.THORNS)) {
            int level;
            try { level = plugin.getMobManager().getMobLevel((LivingEntity) event.getDamager()); } catch (ClassCastException ignored) { return; }
            event.setDamage(level * level * ((Math.random() - .5) / 100));
        }
    }

    private double calculateEntityDamage(LivingEntity entity, Entity victim){

        int mobLevel = plugin.getMobManager().getMobLevel(entity);

        // For context, this damage value is what the average mob should be doing. Certain mobs will hit harder/softer
        double damage = Math.pow(mobLevel, 1.5) * (mobLevel / 25.) + 25;
        double damagePercent = 1.0;

        switch (entity.getType()){

            case HUSK:
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case DROWNED:
            case EVOKER:
            case STRAY:
            case PILLAGER:
                break;

            case SKELETON:
                damagePercent = .95;
                break;

            case SPIDER:
            case CAVE_SPIDER:
            case SHULKER:
            case SHULKER_BULLET:
            case BLAZE:
                damagePercent = .9;
                break;

            case CREEPER:
                Creeper creeper = (Creeper)entity;
                damagePercent = 5.0;
                if (creeper.isPowered())
                    damagePercent += 5.5;

                double distanceFromExplosion = creeper.getLocation().distance(victim.getLocation());
                damagePercent -= distanceFromExplosion;
                if (damagePercent < .1)
                    damagePercent = .1;
                break;

            case ENDERMAN:
            case ILLUSIONER:
            case ENDER_DRAGON:
            case WITHER:
            case ELDER_GUARDIAN:
                damagePercent = 1.2;
                break;

            case SILVERFISH:
            case BEE:
            case ENDERMITE:
            case POLAR_BEAR:
            case PANDA:
                damagePercent = .5;
                break;
            case PHANTOM:
                damagePercent = .4;
                break;

            case SLIME:
            case MAGMA_CUBE:
                //TODO: balance this, not tested
                Slime s = (Slime) entity;
                damagePercent = .2;
                if (s instanceof MagmaCube) { damagePercent += .1; }
                damagePercent *= (s.getSize() + 1);
                break;

            case WITHER_SKELETON:
            case ZOMBIFIED_PIGLIN:
            case PIGLIN:
                damagePercent = 1.25;
                break;

            case PIGLIN_BRUTE:
                damagePercent = 1.4;
                break;

            case GHAST:
            case HOGLIN:
            case ZOGLIN:
                damagePercent = 1.4;
                break;

            case RAVAGER:
            case IRON_GOLEM:
                damagePercent = 2.0;
                break;

            case VINDICATOR:
                damagePercent = 1.6;
                break;

            case GUARDIAN:
                damagePercent = 1.0;
                break;
            case VEX:
                damagePercent = .70;
                break;
            case FOX:
            case WOLF:
            case CAT:
                damagePercent = .45;
                break;
            case LLAMA:
                damagePercent = .3;
                break;
            case PUFFERFISH:
                damagePercent = .01;
                break;
            default:
                System.out.println("[PlayerDamageModifer] Came across unknown entity to calculate damage for: " + entity.getType());
                damagePercent = 1;
                break;
        }

        damage *= damagePercent;
        damage *= (1 + ((Math.random() - .5) / 10));

        if (entity instanceof Ageable && !((Ageable) entity).isAdult())
            damage *= .33;

        // Players get resist against mobs
        if (victim instanceof Player)
            damage *= plugin.getPlayerManager().getLeveledPlayer((Player) victim).getEnvResist();

        return damage;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMobInflictedDamage(EntityDamageByEntityEvent event) {

        if (event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM) || event.getCause().equals(EntityDamageEvent.DamageCause.VOID) || event.getDamage() == 0)
            return;

        if (event.getDamager() instanceof Firework) {
            event.setCancelled(true);
            return;
        }

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
        newDamage = calculateEntityDamage(source, event.getEntity());

        // Sanity check
        if (newDamage < 0) { newDamage = 0; }
        event.setDamage(newDamage);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityHit(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player) {

            int delay = event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE ? 1 : 12;
            new BukkitRunnable() {
                @Override
                public void run() {
                    ((LivingEntity) event.getEntity()).setNoDamageTicks(delay);
                }
            }.runTaskLater(plugin, 0);
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void entityDamagedByMiscSource(EntityDamageEvent event) {

        if (event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM))
            return;

        if (!(event.getEntity() instanceof LivingEntity))
            return;

        LivingEntity livingEntity = (LivingEntity) event.getEntity();
        double fivePercentHP = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 20.;
        double dmg = event.getDamage();

        // Sets up base damage
        switch (event.getCause()){
            case CONTACT:
            case CRAMMING:
            case DROWNING:
            case STARVATION:
            case SUFFOCATION:
            case FALLING_BLOCK:
            case MAGIC:
            case WITHER:
            case POISON:
            case FIRE_TICK:
                dmg = fivePercentHP;
                break;

            case FIRE:
                dmg = fivePercentHP * 2;
                break;

            case LAVA:
            case VOID:
                dmg = fivePercentHP * 5;
                break;

            case FALL:
                dmg = livingEntity.getFallDistance() * fivePercentHP / 1.5;
                break;

            case LIGHTNING:
                dmg = fivePercentHP * 10;
                break;

            case FLY_INTO_WALL:
                dmg = livingEntity.getVelocity().length() * fivePercentHP;
                break;

            case HOT_FLOOR:
                dmg = fivePercentHP / 1.5f;
                break;

            case BLOCK_EXPLOSION:
                dmg = fivePercentHP * 3;
                break;

            case THORNS:
                break;

        }

        // Call an event for enchants, resist calculations to do some math
        EntityDamagedByMiscEvent miscDamageEvent = new EntityDamagedByMiscEvent(livingEntity, event.getCause(), dmg);
        plugin.getServer().getPluginManager().callEvent(miscDamageEvent);  // Makes the server do stuff wherever this listener is at

        // Did some class cancel the event?
        if (miscDamageEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        try {
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
            event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);
        } catch (Exception e) {
            plugin.getLogger().warning("DamageModifier broke shit again");
            e.printStackTrace();
        }

        event.setDamage(miscDamageEvent.getDamage());
    }

    /**
     * Overkill protection mechanic, if we are hit by a blow that is supposed to kill a player but they have 50% hp
     * keep them at 1 hp
     *
     * @param event The EntityDamageEvent we are listening to
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGotHit(EntityDamageEvent event) {

        if (event.getEntity().isDead() || event.isCancelled())
            return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM) || event.getCause().equals(EntityDamageEvent.DamageCause.VOID) || event.getDamage() == 0) {
            return;
        }

        // Is a player being hit?
        if (!(event.getEntity() instanceof Player))
            return;

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

    @EventHandler
    public void onPlayerRegen(EntityRegainHealthEvent event) {

        if (event.getEntity() instanceof LivingEntity){
            if (((LivingEntity) event.getEntity()).getHealth() < 1) { event.setCancelled(true); return; }
        }

        // This is basically just natural regen
        if (event.getEntity() instanceof Player && (event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) || event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.REGEN) || event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.MAGIC_REGEN))  ) {
            Player player = (Player) event.getEntity();
            double maxHP = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            double halfHeartAmount = maxHP / 20.;
            double amountToRegen;
            if (maxHP / 2 > player.getHealth()) {amountToRegen = halfHeartAmount * 1.33; }
            else { amountToRegen = halfHeartAmount * .66; }
            event.setAmount(amountToRegen);
        }
    }

}
