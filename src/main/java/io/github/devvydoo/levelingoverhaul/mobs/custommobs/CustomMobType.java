package io.github.devvydoo.levelingoverhaul.mobs.custommobs;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public enum CustomMobType {

    CORRUPTED_SKELETON(MobCorruptedSkeleton.class, EntityType.STRAY, "Corrupted Skeleton", 70, .2f),
    NETHER_FOX(MobNetherFox.class, EntityType.FOX,  ChatColor.RED + "Nether Fox", 50, .25f);

    public final Class<? extends CustomMob> CLAZZ;
    public final EntityType ENTITY_TYPE;
    public final String NAME;
    public final int DEFAULT_LEVEL;
    public final float FREQUENCY;  // percentage chance that this mob has to spawn with a 'relative'

    CustomMobType(Class<? extends CustomMob> clazz, EntityType entityType, String name, int defaultLevel, float frequency){
        this.CLAZZ = clazz;
        this.ENTITY_TYPE = entityType;
        this.NAME = name;
        this.DEFAULT_LEVEL = defaultLevel;
        this.FREQUENCY = frequency;
    }

}
