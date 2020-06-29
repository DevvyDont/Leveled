package me.devvy.leveled.enchantments.customenchants;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class EnchantExplosiveTouch extends Enchantment {

    public EnchantExplosiveTouch(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Explosive Touch";
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return enchantment == LOOT_BONUS_BLOCKS || enchantment == SILK_TOUCH;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        switch (itemStack.getType()) {
            case DIAMOND_SHOVEL:
            case GOLDEN_SHOVEL:
            case IRON_SHOVEL:
            case WOODEN_SHOVEL:
            case STONE_SHOVEL:
                return true;
            default:
                return false;
        }
    }
}
