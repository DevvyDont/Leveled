package io.github.devvydoo.levelingoverhaul.mobs.custommobs;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CustomMobLootTable {

    List<CustomMobLootTableItem> possibleItems;
    private boolean oneShot;

    public CustomMobLootTable(CustomMobLootTableItem... lootTableItems){
        this(false, lootTableItems);
    }

    public CustomMobLootTable(boolean oneShot, CustomMobLootTableItem... lootTableItems){
        this.oneShot = oneShot;
        possibleItems = new ArrayList<>();
        possibleItems.addAll(Arrays.asList(lootTableItems));
    }

    /**
     * Get a mutable list of possible items to drop
     *
     * @return
     */
    public List<CustomMobLootTableItem> getPossibleItems() {
        return possibleItems;
    }

    public void setPossibleItems(List<CustomMobLootTableItem> possibleItems) {
        this.possibleItems = possibleItems;
    }

    /**
     * A one shot loot table means that one item will always be dropped if there are items to drop, but ONLY one
     *
     * @return Whether this loot table is one shot
     */
    public boolean isOneShot() {
        return oneShot;
    }

    public void setOneShot(boolean oneShot) {
        this.oneShot = oneShot;
    }

    public Collection<ItemStack> roll(float luckBoost) {

        List<ItemStack> buffer = new ArrayList<>();

        if (possibleItems.isEmpty())
            return buffer;

        // If we are doing a one shot, shuffle the possible items and return the first one, otherwise roll %s
        if (oneShot) {
            Collections.shuffle(possibleItems);
            buffer.add(possibleItems.get(0).getItem());
        } else {
            for (CustomMobLootTableItem lootTableItem : possibleItems)
                if (Math.random() + luckBoost < lootTableItem.getChance())
                    buffer.add(lootTableItem.getItem());
        }

        return buffer;

    }

    public Collection<ItemStack> roll() {
        return roll(0);
    }
}
