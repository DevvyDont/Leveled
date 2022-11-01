package me.devvy.leveled.enchantments.customenchants;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.devvy.leveled.util.ToolTypeHelpers;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EnchantGoldenDiet extends Enchantment {

    public EnchantGoldenDiet(NamespacedKey key) {
        super(key);
    }

    @Override
    public @NotNull String getName() {
        return "Golden Diet";
    }


    @Override
    public @NotNull Component displayName(int level) {
        return Component.text(getName());
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return true;
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return EnchantmentRarity.UNCOMMON;
    }

    @Override
    public float getDamageIncrease(int level, @NotNull EntityCategory entityCategory) {
        return 0;
    }

    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        Set<EquipmentSlot> slots = new HashSet<>();
        slots.add(EquipmentSlot.HEAD);
        slots.add(EquipmentSlot.CHEST);
        slots.add(EquipmentSlot.LEGS);
        slots.add(EquipmentSlot.FEET);
        return slots;
    }

    @Override
    public @NotNull String translationKey() {
        return getName();
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
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR_TORSO;
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
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        ArrayList<Material> allowedMats = new ArrayList<>();
        ToolTypeHelpers.addChestplatesToList(allowedMats);
        return allowedMats.contains(itemStack.getType());
    }
}
