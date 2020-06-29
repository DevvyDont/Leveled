package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.enchantments.EnchantmentManager;
import me.devvy.leveled.util.ToolTypeHelpers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EnchantSmeltingTouch extends Enchantment {

    public EnchantSmeltingTouch(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Smelting Touch";
    }

    @Override
    public int getMaxLevel() {
        return 1;
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
        return enchantment == SILK_TOUCH || enchantment == EnchantmentManager.EXPLOSIVE_TOUCH;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        ArrayList<Material> allowedMats = new ArrayList<>();
        ToolTypeHelpers.addPickaxesToList(allowedMats);
        return allowedMats.contains(itemStack.getType());
    }
}
