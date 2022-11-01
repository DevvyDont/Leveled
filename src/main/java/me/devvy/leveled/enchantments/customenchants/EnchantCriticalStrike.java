package me.devvy.leveled.enchantments.customenchants;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.devvy.leveled.events.EntityShootArrowEvent;
import me.devvy.leveled.events.PlayerDealtMeleeDamageEvent;
import me.devvy.leveled.util.ToolTypeHelpers;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EnchantCriticalStrike extends Enchantment implements Listener {

    public EnchantCriticalStrike(NamespacedKey key) {
        super(key);
    }

    @Override
    public @NotNull String getName() {
        return "Critical Strike";
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
        slots.add(EquipmentSlot.HAND);
        slots.add(EquipmentSlot.OFF_HAND);
        return slots;
    }

    @Override
    public @NotNull String translationKey() {
        return getName();
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
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
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        ArrayList<Material> allowedMats = new ArrayList<>();
        ToolTypeHelpers.addMeleeWeaponsToList(allowedMats);
        return allowedMats.contains(itemStack.getType());
    }

    @EventHandler
    public void onPlayerCrit(PlayerDealtMeleeDamageEvent event) {

        double critBonusMultiplier = !event.getPlayer().isOnGround() && event.getPlayer().getVelocity().getY() < 0 ? 1.5 : 1;
        if (critBonusMultiplier == 1)
            return;

        critBonusMultiplier += event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(this) * .25;
        critBonusMultiplier += event.getPlayer().getInventory().getItemInOffHand().getEnchantmentLevel(this) * .25;

        event.multiplyDamage(critBonusMultiplier);
    }
}
