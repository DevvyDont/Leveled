package io.github.devvydoo.levellingoverhaul.managers;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import io.github.devvydoo.levellingoverhaul.listeners.monitors.PlayerNametags;
import io.github.devvydoo.levellingoverhaul.util.MobStatistics;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MobManager implements Listener {

    private String LEVEL_COLOR = (ChatColor.GRAY + "" + ChatColor.BOLD);
    private String HOSTILE_MOB_COLOR = ChatColor.RED.toString();
    private String NEUTRAL_MOB_COLOR = ChatColor.WHITE.toString();
    private String BOSS_MOB_COLOR = ChatColor.DARK_PURPLE.toString();

    private LevellingOverhaul plugin;
    private HashMap<LivingEntity, MobStatistics> entityToLevelMap = new HashMap<>();
    private int MOB_CLEANUP_DELAY = 20 * 60 * 5;

    /**
     * We need to make our plugin able to recover on a world that already has entities setup.
     *
     * @param worlds - A list of World objects that are currently on the server
     */
    public MobManager(LevellingOverhaul plugin, List<World> worlds){
        this.plugin = plugin;
        // Loop through all the worlds
        System.out.println("[Mob Manager] Starting MobManager...");
        int times = 0;
        long start = System.currentTimeMillis();
        for (World w: worlds){
            // Loop through all the entities
            for (LivingEntity e: w.getEntitiesByClass(LivingEntity.class)){
                times++;
                this.entityToLevelMap.put(e, getMobStatistics(e));
            }
        }

        System.out.println("[Mob Manager] Finished! " + times + " mobs successfully checked: " + (System.currentTimeMillis() - start) + "ms");

        // Make a task that runs every minute that cleans up mobs
        new BukkitRunnable(){
            @Override
            public void run(){
                int initialAmount = entityToLevelMap.size();
                System.out.println("[Mob Manager] Cleaning up " + initialAmount + " mobs...");
                for (LivingEntity e: new ArrayList<>(entityToLevelMap.keySet())){
                    if (e.isDead() || entityToLevelMap.get(e).getLevel() == 1 || (e.getType().equals(EntityType.ARMOR_STAND) && e.isCustomNameVisible())){
                        entityToLevelMap.remove(e);
                    }
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, MOB_CLEANUP_DELAY, MOB_CLEANUP_DELAY);
    }

    public HashMap<LivingEntity, MobStatistics> getEntityToLevelMap(){
        return entityToLevelMap;
    }

    private int getAveragePlayerLevel(LivingEntity entity, int distance, boolean wantYModifier){
        int totalLevels = 1;
        int numPlayers = 0;
        for (Player p: entity.getWorld().getPlayers()){
            if (p.getLocation().distance(entity.getLocation()) < distance){
                numPlayers++;
                totalLevels += p.getLevel();
            }
        }
        int yModifier = wantYModifier && entity.getLocation().getY() < 50 ? (int) (12 - entity.getLocation().getY() / 7) : 0;
        if (numPlayers > 0){
            return (int) (totalLevels / numPlayers / 1.2 + (Math.random() * 5 + yModifier));
        }
        return (int) (Math.random() * 5 + yModifier);
    }


    private MobStatistics getMobStatistics(LivingEntity entity){
        MobStatistics stats;
        try {
            stats = new MobStatistics(entity);
        } catch (NullPointerException | NumberFormatException | ArrayIndexOutOfBoundsException error){
            stats = new MobStatistics(entity, this.calculateEntityLevel(entity), entity.getName());
        }
        return stats;
    }

    /**
     * Pass in an entity, and update what its nametag should be. Call this method if no HP modification is being made
     *
     * @param entity - LivingEntity to give a name to
     */
    private void setEntityNametag(LivingEntity entity){
        this.setEntityNametag(entity, 0);
    }

    /**
     * Pass in an entity and how much their HP is changing by. This will update their nametag to what is should be
     *
     * @param entity - LivingEntity to give a name to
     * @param hpModification - Some cases we need do modify how much HP the entity has, so this is an adjustment
     */
    private void setEntityNametag(LivingEntity entity,  double hpModification){

        // Try to retrieve the entity, if we fail, it means the entity is not registered
        int level;
        try {
            level = this.entityToLevelMap.get(entity).getLevel();  // Get the entities level
        } catch (NullPointerException e){
            this.entityToLevelMap.put(entity, this.getMobStatistics(entity));
            level = this.entityToLevelMap.get(entity).getLevel();
        }

        // In the chance our entity doesn't exist in the hashmap, set their level to 1 in case
        if (level == 0){
            level = 1;
        }

        // Now we need to see if our nametag should be white or red determining if they're hostile
        String nametagColor;
        if (entity instanceof Boss){ nametagColor = BOSS_MOB_COLOR; }
        else if (entity instanceof Monster){ nametagColor = HOSTILE_MOB_COLOR; }
        else { nametagColor = NEUTRAL_MOB_COLOR; }


        // Do we need to make an hp modification?
        double entityHP = entity.getHealth() + entity.getAbsorptionAmount();
        if (hpModification != 0){
            entityHP += hpModification;
        }

        String hpTextColor;
        if (entityHP <= 0){
            entityHP = 0;
        }
        hpTextColor = PlayerNametags.getChatColorFromHealth(entityHP);

        // We should be good to set their name
        entity.setCustomName(null);
        entity.setCustomName(LEVEL_COLOR + "Lv. " + level + " "+ nametagColor + entity.getName() + " " +  ChatColor.DARK_RED + "❤" + hpTextColor + (int) entityHP);
    }

    private int calculateEntityLevel(LivingEntity entity){

        // Set some weird edge cases first
        if (entity instanceof IronGolem){ return 35; }
        if (entity instanceof Wolf){ return 10; }
        if (entity instanceof Bee){ return 5; }
        int level = 1;
        double expectedHP;
        if (!(entity instanceof Boss) && !(entity instanceof Mob)){
            return level;
        }

        switch (entity.getType()){
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case HUSK:
            case DROWNED:
                level = getAveragePlayerLevel(entity, 250, true);

                if (level <= 25) { }
                else if (level <= 30){ entity.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD)); }
                else if (level <= 35){ entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD)); }
                else if (level <= 40){ entity.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD)); }
                else {
                    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                    sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level / 2 - 15);
                    entity.getEquipment().setItemInMainHand(sword);
                    entity.getEquipment().setItemInMainHandDropChance(0);  // NEVER ALLOW IT TO DROP
                    entity.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                }
                expectedHP = level / 1.3 + (Math.random() * 5) + 7;
                expectedHP *= 5;
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                return level;
            case CAVE_SPIDER:
            case SPIDER:
                level = getAveragePlayerLevel(entity, 250, true);
                if (level <= 30){ entity.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD)); }
                else if (level <= 35){ entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD)); }
                else if (level <= 40){ entity.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD)); }
                else {
                    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                    sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level / 2 - 15);
                    entity.getEquipment().setItemInMainHand(sword);
                    entity.getEquipment().setItemInMainHandDropChance(0);  // NEVER ALLOW IT TO DROP
                }

                expectedHP = level / 1.7 + (Math.random() * 5) + 5;
                expectedHP *= 5;
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                if (level > 30) { entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, level / 30)); }
                return level;
            case SKELETON:
                level = getAveragePlayerLevel(entity, 250, true);
                if (level > 25){
                    ItemStack bow = new ItemStack(Material.BOW);
                    bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, level / 2 - 11);
                    entity.getEquipment().setItemInMainHand(bow);
                    entity.getEquipment().setItemInMainHandDropChance(0);
                    entity.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                }

                expectedHP = level / 1.3 + (Math.random() * 5) + 7;
                expectedHP *= 5;
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                return level;
            case CREEPER:
                Creeper creeper = (Creeper) entity;
                level = getAveragePlayerLevel(creeper, 250, true);
                if (level > 30 && level < 60){
                    if (Math.random() < level / 100.){
                        creeper.setPowered(true);
                    }
                } else if (level >= 60){
                    creeper.setPowered(true);
                }
                creeper.setMaxFuseTicks((int) (level > 80 ? 2 : 40 - level / 3.));
                return level;
            case ENDERMAN:
                if (entity.getWorld().getEnvironment().equals(World.Environment.NORMAL)){
                    level = (int) (Math.random() * 16 + 40);  // ~ 40 - 55 in overworld
                } else if (entity.getWorld().getEnvironment().equals(World.Environment.NETHER)){
                    level = (int) (Math.random() * 25 + 45);  // ~ 45 - 70 in nether
                } else {
                    Biome biome = entity.getLocation().getBlock().getBiome();
                    if (biome.equals(Biome.THE_END)) { level = (int) (Math.random() * 5 + 58); }
                    else if (biome.equals(Biome.END_HIGHLANDS)) { level = (int) (Math.random() * 5 + 67); }
                    else if (biome.equals(Biome.END_MIDLANDS)) { level = (int) (Math.random() * 5 + 63); }
                    else { level = (int) (Math.random() * 5 + 70); }
                }
                expectedHP = level / 1.1 + (Math.random() * 5) + 15;
                expectedHP *= 5;
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level / 2 + 6);
                sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
                entity.getEquipment().setItemInMainHand(sword);
                entity.getEquipment().setItemInMainHandDropChance(0);
                return level;
            case SHULKER:
                return (int) (Math.random() * 6 + 60);
            case ENDER_DRAGON:
            case WITHER:
            case ELDER_GUARDIAN:
                level = 2;
                int totalPlayerLevels = 0;
                int numPlayers = entity.getWorld().getPlayers().size();
                for (Player p: entity.getWorld().getPlayers()){
                    totalPlayerLevels += p.getLevel();
                }
                if (numPlayers > 0) {
                    level += totalPlayerLevels / numPlayers;
                }
                if (level > 60) {
                    expectedHP = (5 * (level - 60));
                    expectedHP *= 5;
                    entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                    entity.setHealth(expectedHP);
                }
                return level;
            case PILLAGER:
                return (int) (Math.random() * 5 + 25);
            case VINDICATOR:
            case RAVAGER:
                return (int) (Math.random() * 5 + 30);
            case PIG_ZOMBIE:
                level = getAveragePlayerLevel(entity, 250, false) + ((int) (Math.random() * 6));
                expectedHP = level / 1.4 + (Math.random() * 5) + 10;
                expectedHP *= 5;
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                ItemStack goldSword = new ItemStack(Material.GOLDEN_SWORD);
                goldSword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level / 2 + 1);
                entity.getEquipment().setItemInMainHand(goldSword);
                entity.getEquipment().setItemInMainHandDropChance(0);
                return level;
            case MAGMA_CUBE:
                MagmaCube mc = (MagmaCube) entity;
                level = getAveragePlayerLevel(entity, 250, false) + ((int) (Math.random() * 6));
                expectedHP = level / 5. * (Math.random() + 1) * (mc.getSize() /1.5);
                expectedHP *= 5;
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                return level;
            case GHAST:
            case BLAZE:
                level = getAveragePlayerLevel(entity, 250, false) + ((int) (Math.random() * 6));
                expectedHP = level / 1.2 + (Math.random() * 5) + 8;
                expectedHP *= 5;
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                return level;
            case WITHER_SKELETON:
                level = getAveragePlayerLevel(entity, 250, false) + ((int) (Math.random() * 6));
                expectedHP = level / .8 + (Math.random() * 5) + 7;
                expectedHP *= 5;
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
                stoneSword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level / 2 + 9);
                entity.getEquipment().setItemInMainHand(stoneSword);
                entity.getEquipment().setItemInMainHandDropChance(0);
                return level;
            case GUARDIAN:
                level = (int) (Math.random() * 5 + 25);
                return level;
            case SLIME:
                level = getAveragePlayerLevel(entity, 250, true);
                if (level > 25){
                    ItemStack s = new ItemStack(Material.STONE_SWORD);
                    s.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level / 2 - 11);
                    entity.getEquipment().setItemInMainHand(s);
                    entity.getEquipment().setItemInMainHandDropChance(0);
                }
                expectedHP = level / 3. + (Math.random() * 5) + 7;
                expectedHP *= 5;
                if (((Slime) entity).getSize() > 0) { expectedHP *= ((Slime) entity).getSize(); }
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                return level;
            case SILVERFISH:
                level = getAveragePlayerLevel(entity, 250, true);
                if (level > 20){
                    ItemStack s = new ItemStack(Material.STONE_SWORD);
                    s.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level / 2 - 11);
                    entity.getEquipment().setItemInMainHand(s);
                    entity.getEquipment().setItemInMainHandDropChance(0);
                }
                expectedHP = level / 2. + (Math.random() * 5) + 3;
                expectedHP *= 5;
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                return level;
            case WITCH:
            case PHANTOM:
                level = 15;
                expectedHP = Math.random() * 10 + 15 ;
                expectedHP *= 5;
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                return level;
            case CHICKEN:
            case SALMON:
            case RABBIT:
                expectedHP = 30;
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                return 1;
            default:
                expectedHP = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue() * 2.5;
                if (expectedHP > 10000) {
                    System.out.println(ChatColor.RED + "WTF SOMETHING IS OVER 100000");
                    System.out.println(expectedHP);
                    System.out.println(entity);
                    System.out.println(entity.getLocation());
                    expectedHP = 10000;
                }
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
                entity.setHealth(expectedHP);
                return Math.random() < .5 ? 2 : 3;
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event){

        // This can happen?
        if (event.getEntityType().equals(EntityType.ARMOR_STAND)){
            return;
        }

        // We only care about living entities
        if (!(event.getEntity() instanceof LivingEntity)){
            return;
        }

        // We don't care about players
        if (event.getEntity() instanceof Player){
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();

        MobStatistics stats = new MobStatistics(entity, this.calculateEntityLevel(entity), entity.getName());
        this.entityToLevelMap.put(entity, stats);
        this.setEntityNametag(entity);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event){

        // We don't care if the event was cancelled
        if (event.isCancelled()){
            return;
        }

        // We only care about living entities
        if (!(event.getEntity() instanceof  LivingEntity)){
            return;
        }

        // We don't care about Players
        if (event.getEntity() instanceof Player){
            return;
        }

        // Marked
        event.getEntity().setCustomNameVisible(true);

        // Update nametag
        this.setEntityNametag((LivingEntity) event.getEntity(), event.getFinalDamage() * -1);

    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        // Remove the entity from our map if needed.
        this.entityToLevelMap.remove(event.getEntity());
    }

    public int getMobLevel(LivingEntity mob){
        if (mob instanceof Player){
            return ((Player) mob).getLevel();
        }
        try {
            return this.entityToLevelMap.get(mob).getLevel();
        } catch (NullPointerException e){
            MobStatistics stats = getMobStatistics(mob);
            this.entityToLevelMap.put(mob, stats);
            return stats.getLevel();
        }
    }

}
