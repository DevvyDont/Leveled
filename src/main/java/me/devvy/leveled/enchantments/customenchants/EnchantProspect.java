package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.util.ToolTypeHelpers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EnchantProspect extends Enchantment {

    public EnchantProspect(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Prospect";
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
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
        ToolTypeHelpers.addMeleeWeaponsToList(allowedMats);
        allowedMats.add(Material.CROSSBOW);
        allowedMats.add(Material.BOW);
        return allowedMats.contains(itemStack.getType());
    }
}
