package me.devvy.leveled.mobs;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.mobs.custommobs.CustomLeveledEntity;
import me.devvy.leveled.mobs.custommobs.CustomLeveledEntityType;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MobManager implements Listener {

    // Mobs have this key that determines their level
    public final static NamespacedKey MOB_LEVEL_KEY = new NamespacedKey(Leveled.getPlugin(Leveled.class), "mob-level-key");
    // Mobs have this key that determines what their name is
    public final static NamespacedKey MOB_NAME_KEY = new NamespacedKey(Leveled.getPlugin(Leveled.class), "mob-name-key");
    // Mobs have this key that defines the enum that defines their custom mob status (index of custom mob type enum, -1 if vanilla)
    public final static NamespacedKey MOB_CUSTOM_FLAG_KEY = new NamespacedKey(Leveled.getPlugin(Leveled.class), "mob-custom-key");

    private final int MOB_CLEANUP_DELAY = 20 * 60 * 5;  // TODO: make config option, this is used to keep mem usage down


    private final Map<UUID, LeveledLivingEntity> entityInstanceMap = new HashMap<>();

    /**
     * We need to make our plugin able to recover on a world that already has entities setup.
     *
     * @param worlds - A list of World objects that are currently on the server
     */
    public MobManager(List<World> worlds) {

        Leveled plugin = Leveled.getPlugin(Leveled.class);

        // Loop through all the worlds
        plugin.getLogger().info("Starting mob manager...");
        int times = 0;
        long start = System.currentTimeMillis();
        for (World w : worlds) {
            // Loop through all the entities
            for (LivingEntity e : w.getEntitiesByClass(LivingEntity.class)) {

                if (e instanceof Player || e instanceof ArmorStand)
                    continue;

                times++;

                LeveledLivingEntity entityInstance;

                // If we have a custom entity, we need to instantiate that
                if (e.getPersistentDataContainer().has(MOB_CUSTOM_FLAG_KEY, PersistentDataType.INTEGER) && e.getPersistentDataContainer().get(MOB_CUSTOM_FLAG_KEY, PersistentDataType.INTEGER) != -1) {
                    // Get the type of mob
                    CustomLeveledEntityType type = CustomLeveledEntityType.values()[e.getPersistentDataContainer().get(MOB_CUSTOM_FLAG_KEY, PersistentDataType.INTEGER)];
                    // Attempt to instantiate the custom mob
                    try {
                        entityInstance = type.CLAZZ.getDeclaredConstructor(LivingEntity.class).newInstance(e);
                    } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException error) {
                        error.printStackTrace();
                        e.remove();
                        continue;
                    }
                    // We know it's going to be custom since all classes defined in the enum extend custom mob, set it up
                    ((CustomLeveledEntity) entityInstance).setup();
                } else
                    entityInstance = new LeveledLivingEntity(e, true);

                // Good to insert
                entityInstanceMap.put(e.getUniqueId(), entityInstance);
            }
        }
        plugin.getLogger().info("Finished Mob Manager startup! " + times + " mobs successfully checked: " + (System.currentTimeMillis() - start) + "ms");
    }

    public Map<UUID, LeveledLivingEntity> getEntityInstanceMap() {
        return entityInstanceMap;
    }

    /**
     * Can be used to spawn a mob with a pre-determined level, overriding natural flow for a normal mob spawn
     *
     * @return The entity created, allowing for further customization
     */
    public LeveledLivingEntity spawnLeveledMob(Location location, EntityType entityType, String name, int level){

        if (level < 1)
            throw new IllegalArgumentException("Mob level cannot be less than 1!");

        assert entityType.getEntityClass() != null;

        LivingEntity entity = (LivingEntity) location.getWorld().spawn(location, entityType.getEntityClass());
        LeveledLivingEntity leveledEntityInstance = new LeveledLivingEntity(entity, true);

        leveledEntityInstance.setLevel(level, true);
        leveledEntityInstance.setName(name);
        leveledEntityInstance.update();
        entityInstanceMap.put(entity.getUniqueId(), leveledEntityInstance);

        return leveledEntityInstance;
    }

    public CustomLeveledEntity spawnCustomLeveledMob(CustomLeveledEntityType type, Location location, int level){

        if (level < 1)
            throw new IllegalArgumentException("Mob level cannot be less than 1!");

        assert type.ENTITY_TYPE.getEntityClass() != null;
        LivingEntity entity = (LivingEntity) location.getWorld().spawn(location, type.ENTITY_TYPE.getEntityClass());

        CustomLeveledEntity customLeveledEntity;

        try { customLeveledEntity = type.CLAZZ.getDeclaredConstructor(LivingEntity.class).newInstance(entity); } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {e.printStackTrace();entity.remove();return null;}
        customLeveledEntity.setCustomType(type);
        customLeveledEntity.setLevel(level);  // NOTE: notice how we aren't forcing attributes to update here, this is so we have full control over the entities stats when setting it up
        customLeveledEntity.setName(type.NAME);
        customLeveledEntity.update();
        customLeveledEntity.setup();
        entityInstanceMap.put(entity.getUniqueId(), customLeveledEntity);

        return customLeveledEntity;
    }

    public CustomLeveledEntity spawnCustomLeveledMob(CustomLeveledEntityType type, Location location){
        return spawnCustomLeveledMob(type, location, type.DEFAULT_LEVEL);
    }

    public int getMobLevel(LivingEntity mob) {

        if (mob instanceof Player)
            return ((Player) mob).getLevel();

        return getLeveledEntity(mob).getLevel();
    }

    public LeveledLivingEntity getLeveledEntity(LivingEntity livingEntity) {
        if (entityInstanceMap.containsKey(livingEntity.getUniqueId()))
            return entityInstanceMap.get(livingEntity.getUniqueId());

        LeveledLivingEntity leveledLivingEntity = new LeveledLivingEntity(livingEntity, true);
        leveledLivingEntity.update();
        entityInstanceMap.put(livingEntity.getUniqueId(), leveledLivingEntity);
        return leveledLivingEntity;
    }

    /**
     * Adds a LivingEntity to the map of statistics so we can retrieve things such as level easier
     *
     * @param event EntitySpawnEvent
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(CreatureSpawnEvent event) {

        // If a mob was customly spawned, we probably shouldn't do anything, this may cause issues with other plugins though. Consider making it a config option
        //TODO: maybe turn into config option
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)
            return;

        // We don't care about players or armor stands
        if (event.getEntity() instanceof Player || event.getEntity() instanceof ArmorStand)
            return;

        LeveledLivingEntity leveledLivingEntity = new LeveledLivingEntity(event.getEntity(), true);
        leveledLivingEntity.update();
        entityInstanceMap.put(event.getEntity().getUniqueId(), leveledLivingEntity);
    }

    /**
     * Mainly updates entity nametags when hit
     *
     * @param event EntityDamageEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {

        // We only care about living entities
        if (!(event.getEntity() instanceof LivingEntity))
            return;

        // We don't care about Players
        if (event.getEntity() instanceof Player || event.getEntity() instanceof ArmorStand)
            return;

        getLeveledEntity((LivingEntity) event.getEntity()).update(event.getFinalDamage() * -1);
    }

    @EventHandler
    public void onEntityHealed(EntityRegainHealthEvent event) {

        // We only care about living entities
        if (!(event.getEntity() instanceof LivingEntity))
            return;

        // We don't care about Players
        if (event.getEntity() instanceof Player || event.getEntity() instanceof ArmorStand)
            return;

        getLeveledEntity((LivingEntity) event.getEntity()).update(event.getAmount());

    }

    /**
     * Cleans up entities from memory when they die
     *
     * @param event EntityDeathEvent
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        LeveledLivingEntity entityInstance = entityInstanceMap.get(event.getEntity().getUniqueId());

        if (entityInstance == null)
            return;

        if (entityInstance instanceof CustomLeveledEntity) {
            CustomLeveledEntity mob = (CustomLeveledEntity) entityInstance;
            List<ItemStack> drops = event.getDrops();
            drops.clear();
            drops.addAll(mob.getLootTable().roll());
        }

        // Remove the entity from our map if needed.
        entityInstanceMap.remove(event.getEntity().getUniqueId());
    }

    /**
     * Updates the level of a mob when tamed to the level of the owner
     *
     * @param event The EntityTameEvent we are listening to
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTamed(EntityTameEvent event){

        if (event.getOwner() instanceof Player){
            // ok honestly frick minecraft, after this event fires its going to set the mobs hp to whatever value it wants, so we have to do this later
            new BukkitRunnable() {
                @Override
                public void run() {
                    int newEntitylevel = ((Player)event.getOwner()).getLevel();  // Gets the level of the player who tamed
                    getLeveledEntity(event.getEntity()).setLevel(newEntitylevel, true);
                    getLeveledEntity(event.getEntity()).update();
                    event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .5f, .5f);
                }
            }.runTaskLater(Leveled.getPlugin(Leveled.class), 1);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCustomMobSpawn(CreatureSpawnEvent event){

        // If a mob was custom-ly spawned they cannot recursively call this event
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)
            return;

        Location entityLocation = event.getLocation();
        World.Environment environment = entityLocation.getWorld().getEnvironment();
        Biome biome = entityLocation.getWorld().getBiome(entityLocation.getBlockX(), entityLocation.getBlockY(), entityLocation.getBlockZ());
        int relativeLevel = getMobLevel(event.getEntity());

        switch (environment) {

            case THE_END:

                // What should we do in the end?
                if (event.getEntity().getType() == EntityType.ENDERMAN && Math.random() < CustomLeveledEntityType.CORRUPTED_SKELETON.FREQUENCY)
                    spawnCustomLeveledMob(CustomLeveledEntityType.CORRUPTED_SKELETON, entityLocation, Math.max(relativeLevel, CustomLeveledEntityType.CORRUPTED_SKELETON.DEFAULT_LEVEL));
                break;

            case NETHER:

                // What should we do in the nether?
                if (event.getEntity().getType() == EntityType.WITHER_SKELETON && Math.random() < CustomLeveledEntityType.FIREFOX.FREQUENCY)
                    spawnCustomLeveledMob(CustomLeveledEntityType.FIREFOX, entityLocation);
                break;

        }

    }

}
