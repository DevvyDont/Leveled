package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.util.ToolTypeHelpers;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class EnchantGrowth extends Enchantment {

    public EnchantGrowth(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Growth";
    }

    @Override
    public int getMaxLevel() {
        return 6;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR;
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
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return ToolTypeHelpers.isArmor(itemStack);
    }
}
