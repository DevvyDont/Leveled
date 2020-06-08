package io.github.devvydoo.levelingoverhaul.mobs.custommobs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class CustomMob {

    protected final EntityType actualMobType;
    protected final CustomMobLootTable lootTable;
    protected LivingEntity entity = null;

    public CustomMob(EntityType actualMobType, CustomMobLootTable lootTable) {
        this.actualMobType = actualMobType;
        this.lootTable = lootTable;
    }

    public CustomMob(EntityType actualMobType) {
        this(actualMobType, new CustomMobLootTable());
    }

    public EntityType getActualMobType() {
        return actualMobType;
    }

    public CustomMobLootTable getLootTable() {
        return lootTable;
    }

    /**
     * Any custom logic that we should run on the mob
     */
    public abstract void setup(LivingEntity entity);
}
