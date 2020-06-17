package io.github.devvydoo.levelingoverhaul.mobs.custommobs;

import io.github.devvydoo.levelingoverhaul.mobs.LeveledLivingEntity;
import org.bukkit.entity.LivingEntity;

public abstract class CustomLeveledEntity extends LeveledLivingEntity {

    protected final CustomLeveledEntityLootTable lootTable;

    public CustomLeveledEntity(LivingEntity entity, CustomLeveledEntityLootTable lootTable) {
        super(entity, false);
        this.lootTable = lootTable;
        setup();
    }

    public CustomLeveledEntity(LivingEntity entity) {
        this(entity, new CustomLeveledEntityLootTable());
    }

    public CustomLeveledEntityLootTable getLootTable() {
        return lootTable;
    }

    public abstract CustomLeveledEntityType getCustomMobType();

    /**
     * Any custom logic that we should run on the mob
     */
    public abstract void setup();
}
