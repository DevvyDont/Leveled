package me.devvy.leveled.mobs.custommobs;

import me.devvy.leveled.mobs.LeveledLivingEntity;
import org.bukkit.entity.LivingEntity;

public abstract class CustomLeveledEntity extends LeveledLivingEntity {

    protected CustomLeveledEntityLootTable lootTable;

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
