package io.github.devvydoo.levelingoverhaul.managers;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.CustomEnchantment;
import io.github.devvydoo.levelingoverhaul.util.ToolTypeHelpers;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

/**
 * A class used to do any necessary calculations for damage calculations BEFORE our plugin modifies anything whether it
 * be enchants, the overkill protection mechanic, etc. Since our base HP is 100 rather than 20, we start by making
 * all damage in this plugin multiplied by 5 to start, things will be balanced as time goes on
 */
public class GlobalDamageManager implements Listener {

    private final LevelingOverhaul plugin;
    private final String ARROW_DMG_METANAME = "base_damage";
    private final String ARROW_SNIPE_ENCHANT_METANAME = "snipe_enchant_level";
    private final String ARROW_FMJ_ENCHANT_METANAME = "fmj_enchant_level";
    private final String ARROW_EXECUTE_ENCHANT_METANAME = "exe_enchant_level";

    public GlobalDamageManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    private double getRangedWeaponBaseDamage(ItemStack tool) {

        if (plugin.getCustomItemManager().isEnderBow(tool))
            return 65;

        switch (tool.getType()) {
            case BOW:
                return 40;
            case CROSSBOW:
                return 50;
            default:
                return 0;
        }
    }

    private double getMeleeWeaponBaseDamage(ItemStack tool) {

        if (plugin.getCustomItemManager().isDragonSword(tool))
            return 110;

        switch (tool.getType()) {

            case DIAMOND_AXE:
                return 80;
            case DIAMOND_SWORD:
                return 70;

            case IRON_AXE:
                return 65;
            case IRON_SWORD:
                return 55;

            case GOLDEN_AXE:
                return 50;
            case GOLDEN_SWORD:
                return 40;

            case STONE_AXE:
                return 35;
            case STONE_SWORD:
                return 25;

            case WOODEN_AXE:
                return 20;
            case WOODEN_SWORD:
                return 15;
            default:
                return 5;
        }
    }

    /**
     * Vanilla Sharpness and Power are bad, we are going to buff them
     *
     * @param event The event in which any entity is damaged by another one
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMeleeAttack(EntityDamageByEntityEvent event) {

        // Make sure a player is attacking
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        ItemStack tool = player.getInventory().getItemInMainHand();

        // First let's get a base setup for how much certain weapons should do
        double newDamage = getMeleeWeaponBaseDamage(tool);

        // In the case we have sharpness, we should increase raw damage
        int sharpnessLevel = tool.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        if (sharpnessLevel > 0)
            newDamage *= (1.5 * sharpnessLevel);  // adds 5x^2 + 15x % damage

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

        Map<CustomEnchantType, CustomEnchantment> customEnchantments = plugin.getEnchantmentManager().getCustomEnchantmentMap(tool);

        // Check for boss damage
        if (event.getEntity() instanceof Boss || event.getEntity() instanceof EnderDragonPart || event.getEntity() instanceof EnderDragon || event.getEntity() instanceof ComplexLivingEntity) {
            if (customEnchantments.containsKey(CustomEnchantType.FULL_METAL_JACKET))
                newDamage *= (customEnchantments.get(CustomEnchantType.FULL_METAL_JACKET).getLevel() / 2.);
        }

        // Check for axe berserk
        if (customEnchantments.containsKey(CustomEnchantType.BERSERK))
            newDamage *= 2;

        // Check if entity is low on HP for execute
        if (customEnchantments.containsKey(CustomEnchantType.EXECUTIONER) && event.getEntity() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) event.getEntity();
            if (livingEntity.getHealth() / livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() > .25)
                newDamage *= (1.10 + (customEnchantments.get(CustomEnchantType.EXECUTIONER).getLevel() / 20.));
        }

        // Lastly, see if we should give a 100% bonus for crits
        // Axe crits
        if (ToolTypeHelpers.isAxe(tool) && Math.random() < .3){
            if (customEnchantments.containsKey(CustomEnchantType.CRITICAL_STRIKE))
                newDamage *= (2 + (.5 * customEnchantments.get(CustomEnchantType.CRITICAL_STRIKE).getLevel()));
            else
                newDamage *= 2;
        }
        // Sword crits
        else if (!player.isOnGround() && player.getVelocity().getY() < 0) {
            if (customEnchantments.containsKey(CustomEnchantType.CRITICAL_STRIKE))
                newDamage *= (1.5 + (customEnchantments.get(CustomEnchantType.CRITICAL_STRIKE).getLevel() * .2));
            else
                newDamage *= 1.5;
        }

        // dimension boosts
        switch (player.getWorld().getEnvironment()){
            case NETHER:
                if (customEnchantments.containsKey(CustomEnchantType.NETHER_HUNTER))
                    newDamage *= (1 + (customEnchantments.get(CustomEnchantType.NETHER_HUNTER).getLevel() / 20.));
            case THE_END:
                if (customEnchantments.containsKey(CustomEnchantType.ENDER_HUNTER))
                    newDamage *= (1 + (customEnchantments.get(CustomEnchantType.ENDER_HUNTER).getLevel() / 18.));
        }

        // Player strength (THIS INCLUDES STRENGTH POTS)
        newDamage *= plugin.getPlayerManager().getLeveledPlayer(player).getStrengthBonus();

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
    @EventHandler
    public void onPlayerShotBow(EntityShootBowEvent event) {

        if (event.getEntity() instanceof Player) {

            ItemStack bow = event.getBow();

            // Don't care if we dont have a bow
            if (bow == null)
                return;

            double damage = getRangedWeaponBaseDamage(event.getBow());

            if (damage <= 0)
                return;

            // Now we take into account force the bow shot with
            damage *= event.getForce();

            // And then we calculate damage increased from the power enchantment
            int powerLevel = bow.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
            if (powerLevel > 0) {
                damage *= (1 + 9 * powerLevel);
            }

            // 20% chance to crit for double damage
            double critPercent = .2;

            // Get enchants
            Map<CustomEnchantType, CustomEnchantment> customEnchantments = plugin.getEnchantmentManager().getCustomEnchantmentMap(bow);

            // crit enchant level * 10% chance bonus
            if (customEnchantments.containsKey(CustomEnchantType.CRITICAL_SHOT))
                critPercent += (customEnchantments.get(CustomEnchantType.CRITICAL_SHOT).getLevel() / 10.);

            // dimension boosts
            switch (event.getProjectile().getWorld().getEnvironment()){
                case NETHER:
                    if (customEnchantments.containsKey(CustomEnchantType.NETHER_HUNTER))
                        damage *= (1 + (customEnchantments.get(CustomEnchantType.NETHER_HUNTER).getLevel() / 20.));
                case THE_END:
                    if (customEnchantments.containsKey(CustomEnchantType.ENDER_HUNTER))
                        damage *= (1 + (customEnchantments.get(CustomEnchantType.ENDER_HUNTER).getLevel() / 18.));
            }

            // Test for crit
            if (Math.random() < critPercent) {
                damage *= 1.5;
                event.getEntity().getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0, 1.6, 0), 50);
                event.getEntity().getWorld().playSound(event.getEntity().getLocation().add(0, 1.6, 0), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, .3f, .6f);
            }

            // Check for strength (INCLUDES STRENGTH POTS)
            damage *= plugin.getPlayerManager().getLeveledPlayer((Player) event.getEntity()).getStrengthBonus();

            // 5% variance
            damage *= (1 + ((Math.random() - .5) / 10.));

            // Now we can put this value on the arrow to modify later in a different event
            event.getProjectile().setMetadata(ARROW_DMG_METANAME, new FixedMetadataValue(plugin, damage));

            // Attempts to put the snipe level on the arrow if it exists
            if (customEnchantments.containsKey(CustomEnchantType.SNIPE))
                event.getProjectile().setMetadata(ARROW_SNIPE_ENCHANT_METANAME, new FixedMetadataValue(plugin, customEnchantments.get(CustomEnchantType.SNIPE).getLevel()));

            // Attempts to put the fmj level on the arrow if it exists
            if (customEnchantments.containsKey(CustomEnchantType.FULL_METAL_JACKET))
                event.getProjectile().setMetadata(ARROW_FMJ_ENCHANT_METANAME, new FixedMetadataValue(plugin, customEnchantments.get(CustomEnchantType.FULL_METAL_JACKET).getLevel()));

            // Check if entity is low on HP for execute
            if (customEnchantments.containsKey(CustomEnchantType.EXECUTIONER))
                event.getProjectile().setMetadata(ARROW_EXECUTE_ENCHANT_METANAME, new FixedMetadataValue(plugin, customEnchantments.get(CustomEnchantType.EXECUTIONER).getLevel()));
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
        if ((event.getEntity() instanceof Boss || event.getEntity() instanceof ComplexLivingEntity || event.getEntity() instanceof ComplexEntityPart) &&  arrow.hasMetadata(ARROW_FMJ_ENCHANT_METANAME)) {
            for (MetadataValue metadataValue : arrow.getMetadata(ARROW_FMJ_ENCHANT_METANAME)){
                if (metadataValue.getOwningPlugin() != null && metadataValue.getOwningPlugin().equals(plugin)){
                    newDamage *= (metadataValue.asInt() / 2.);
                    break;
                }
            }
        }
        // Test for executioner enchant
        if (event.getEntity() instanceof LivingEntity){
            LivingEntity livingEntity = (LivingEntity) event.getEntity();
            if (livingEntity.getHealth() < livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()){
                for (MetadataValue metadataValue : arrow.getMetadata(ARROW_EXECUTE_ENCHANT_METANAME)){
                    if (metadataValue.getOwningPlugin() != null && metadataValue.getOwningPlugin().equals(plugin)){
                        newDamage *= (1.10 + (metadataValue.asInt() / 20.));
                        break;
                    }
                }
            }
        }

        if (newDamage > 0) {
            event.setDamage(newDamage);
        }
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

    private double calculateEntityDamage(LivingEntity entity){

        int mobLevel = plugin.getMobManager().getMobLevel(entity);
        double damage = mobLevel * mobLevel * (mobLevel / 100.);
        if (mobLevel < 20)
            damage += 15;

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
                damagePercent = .9;
                break;

            case SPIDER:
            case CAVE_SPIDER:
            case SHULKER:
            case SHULKER_BULLET:
            case BLAZE:
                damagePercent = .8;
                break;

            case CREEPER:
                // TODO: Make scale based on distance
                damagePercent = 2.0;
                if (((Creeper) entity).isPowered()) { damagePercent += 1.0; }
                break;

            case ENDERMAN:
            case ILLUSIONER:
            case ENDER_DRAGON:
            case WITHER:
            case ELDER_GUARDIAN:
                damagePercent = 1.5;
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
                Slime s = (Slime) entity;
                damagePercent = .2;
                if (s instanceof MagmaCube) { damagePercent += .1; }
                damagePercent *= (s.getSize() + 1);
                break;

            case WITHER_SKELETON:
            case PIG_ZOMBIE:
                damagePercent = 1.2;
                break;

            case GHAST:
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
                damagePercent = 3;
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
        return damage;
    }

    @EventHandler(priority = EventPriority.LOWEST)
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
        newDamage = calculateEntityDamage(source);

        // Sanity check
        if (newDamage < 0) { newDamage = 0; }
        event.setDamage(newDamage);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerHitByMiscSource(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;

        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);

        if (event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM))
            return;

        Player player = (Player) event.getEntity();
        double fivePercentHP = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 20.;
        double dmg = event.getDamage();

        // Sets up base damage
        switch (event.getCause()){

            case CUSTOM:
            case VOID:
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setNoDamageTicks(0);
                        player.setMaximumNoDamageTicks(0);
                    }
                }.runTaskLater(plugin, 1);
                return;
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
                dmg = fivePercentHP * 5;
                break;

            case FALL:
                dmg = player.getFallDistance() * fivePercentHP / 1.1;
                break;

            case LIGHTNING:
                dmg = fivePercentHP * 10;
                break;

            case FLY_INTO_WALL:
                dmg = player.getVelocity().length() * fivePercentHP;
                break;

            case HOT_FLOOR:
                dmg = fivePercentHP / 2.;
                break;

            case BLOCK_EXPLOSION:
                dmg = fivePercentHP * 3;
                break;

            case THORNS:
                if (event instanceof EntityDamageByEntityEvent){
                    Entity thornsOwner = ((EntityDamageByEntityEvent) event).getDamager();
                    if (thornsOwner instanceof LivingEntity) {
                        int level = plugin.getMobManager().getMobLevel((LivingEntity) thornsOwner);
                        double newDamage = level * 2;

                        newDamage -= plugin.getPlayerManager().getLeveledPlayer(player).getDefense();
                        if (newDamage < 1) {
                            newDamage = 1;
                        }
                        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
                        event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);
                        event.setDamage(newDamage);
                    }
                }

        }

        // Now lets do some resist calculations
        switch (event.getCause()){

            // Environmental
            case ENTITY_ATTACK:
            case THORNS:
            case CONTACT:
            case FALLING_BLOCK:
            case FLY_INTO_WALL:
            case DRAGON_BREATH:
            case ENTITY_SWEEP_ATTACK:
                dmg *= plugin.getPlayerManager().getLeveledPlayer(player).getEnvResist();
                break;

            // Explosions
            case ENTITY_EXPLOSION:
            case BLOCK_EXPLOSION:
            case LIGHTNING:
                dmg *= plugin.getPlayerManager().getLeveledPlayer(player).getEnvResist();
                dmg *= plugin.getPlayerManager().getLeveledPlayer(player).getExplosionResist();
                break;

            // Fire
            case FIRE:
            case FIRE_TICK:
            case LAVA:
            case HOT_FLOOR:
                dmg *= plugin.getPlayerManager().getLeveledPlayer(player).getFireResist();
                break;

            // Projectiles
            case PROJECTILE:
                dmg *= plugin.getPlayerManager().getLeveledPlayer(player).getEnvResist();
                dmg *= plugin.getPlayerManager().getLeveledPlayer(player).getProjResist();
                break;

            // Fall
            case FALL:
                if (player.getInventory().getBoots() != null)
                    dmg /= (player.getInventory().getBoots().getEnchantmentLevel(Enchantment.PROTECTION_FALL) + 1);
                break;
        }

        // Some last things to take care of
        if (plugin.getEnchantmentManager().hasEnchant(player.getInventory().getItemInMainHand(), CustomEnchantType.BERSERK))
            dmg *= 3;

        event.setDamage(dmg);
    }

    /**
     * Overkill protection mechanic, if we are hit by a blow that is supposed to kill a player but they have 50% hp
     * keep them at 1 hp
     *
     * @param event The EntityDamageEvent we are listening to
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGotHit(EntityDamageEvent event) {

        if (event.getEntity().isDead())
            return;

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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event){
//        String playerName = ChatColor.GRAY.toString() + ChatColor.BOLD + "Lv. " + event.getEntity().getLevel() + " " + ChatColor.GREEN + event.getEntity().getDisplayName();
//        event.setDeathMessage(ChatColor.BLACK + "[" + ChatColor.DARK_RED + "☠" + ChatColor.BLACK + "] " + Objects.requireNonNull(event.getDeathMessage()).replace(event.getEntity().getName(), playerName));
    }

}
