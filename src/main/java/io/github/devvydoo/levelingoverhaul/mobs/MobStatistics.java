package io.github.devvydoo.levelingoverhaul.mobs;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

public class MobStatistics {


    private LivingEntity entity;
    private int level;
    private String name;


    /**
     * Invoke when we are creating a new entity, we calculate the level and such somewhere else
     *
     * @param entity - The LivingEntity that was just created
     * @param level  - The level that the entity will be considered to be
     * @param name   - The name of the entity if it is a 'custom' entity
     */
    public MobStatistics(LivingEntity entity, int level, String name) {
        this.entity = entity;
        this.level = level;
        this.name = name;
    }

    /**
     * Invoke when the entity already exists and we need to parse it.
     *
     * @param entity - The LivingEntity we are parsing the nametag of
     */
    public MobStatistics(LivingEntity entity) throws NullPointerException, NumberFormatException, ArrayIndexOutOfBoundsException {
        this.entity = entity;

        // If anything goes wrong here, the mob doesn't have a proper name tag and should be ignored
        String cleanNametag = ChatColor.stripColor(entity.getCustomName());  // "Level 15 Creeper [20 HP]"
        String[] tagComponents = cleanNametag.split(" ");
        this.level = Integer.parseInt(tagComponents[1]);
        this.name = tagComponents[2];
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level){
        this.level = level;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public String getName() {
        return name;
    }
}