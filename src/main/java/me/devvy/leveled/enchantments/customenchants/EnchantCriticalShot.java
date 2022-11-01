package me.devvy.leveled.enchantments.customenchants;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.devvy.leveled.events.EntityShootArrowEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EnchantCriticalShot extends Enchantment implements Listener {

    public EnchantCriticalShot(NamespacedKey key) {
        super(key);
    }

    @Override
    public @NotNull String getName() {
        return "Critical Shot";
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
        return EnchantmentTarget.BOW;
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
        return itemStack.getType() == Material.BOW || itemStack.getType() == Material.CROSSBOW;
    }

    @EventHandler
    public void onBowShot(EntityShootArrowEvent event) {

        // 20% chance to crit for double damage
        double critPercent = .2;

        // 15% boost per level of critical shot
        assert event.getBow() != null;  // Bow can't be null, we make sure it's not when this event is called
        critPercent += event.getBow().getEnchantmentLevel(this) * .15;

        // Test for crit, 1.5x damage
        if (Math.random() < critPercent) {
            event.multiplyDamage(1.5f);
            event.getEntity().getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0, 1.6, 0), 50);
            event.getEntity().getWorld().playSound(event.getEntity().getLocation().add(0, 1.6, 0), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, .3f, .6f);
        }
    }
}
