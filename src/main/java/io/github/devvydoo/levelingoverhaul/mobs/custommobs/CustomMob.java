package io.github.devvydoo.levelingoverhaul.mobs.custommobs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class CustomMob {

    protected final CustomMobLootTable lootTable;
    protected LivingEntity entity;

    public CustomMob(LivingEntity entity, CustomMobLootTable lootTable) {
        this.lootTable = lootTable;
        this.entity = entity;
        setup();
    }

    public CustomMob(LivingEntity entity) {
        this(entity, new CustomMobLootTable());
    }

    public CustomMobLootTable getLootTable() {
        return lootTable;
    }

    public abstract CustomMobType getCustomMobType();

    /**
     * Any custom logic that we should run on the mob
     */
    public abstract void setup();
}
