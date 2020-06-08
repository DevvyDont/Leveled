package io.github.devvydoo.levelingoverhaul.mobs.custommobs;

import org.bukkit.inventory.ItemStack;

public class CustomMobLootTableItem {

    private final ItemStack item;
    private float chance;

    public CustomMobLootTableItem(ItemStack item, float chance) {
        this.item = item;
        this.chance = chance;
    }

    public float getChance() {
        return chance;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public ItemStack getItem() {
        return item;
    }
}
