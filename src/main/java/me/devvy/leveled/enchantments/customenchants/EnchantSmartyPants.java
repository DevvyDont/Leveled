package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.util.ToolTypeHelpers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantSmartyPants extends Enchantment {

    public EnchantSmartyPants(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Smarty Pants";
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR_LEGS;
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
        ArrayList<Material> allowedMats = new ArrayList<>();
        ToolTypeHelpers.addLeggingsToList(allowedMats);
        return allowedMats.contains(itemStack.getType());
    }
}
