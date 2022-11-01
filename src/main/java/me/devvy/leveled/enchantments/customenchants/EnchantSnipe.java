package me.devvy.leveled.enchantments.customenchants;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.devvy.leveled.events.EntityHitByProjectileEvent;
import me.devvy.leveled.events.EntityShootArrowEvent;
import me.devvy.leveled.managers.GlobalDamageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class EnchantSnipe extends Enchantment {

    public EnchantSnipe(NamespacedKey key) {
        super(key);
    }

    @Override
    public @NotNull String getName() {
        return "Snipe";
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

    private boolean isBoss(Entity entity) {
        return entity instanceof EnderDragon || entity instanceof EnderDragonPart || entity instanceof Wither;
    }

    private float getDistanceDamageMultiplier(double distance, int level) {

        float mult = 1.0f;

        // Cap at 100
        double distanceToUse = Math.min(distance, 100);
        mult += distanceToUse / 100;
        mult *= 1 + (level * .2);
        return mult;

    }

    @EventHandler
    public void onEntityShotFMJArrow(EntityShootArrowEvent event) {

        assert event.getBow() != null;
        int snipeLevel = event.getBow().getEnchantmentLevel(this);

        if (snipeLevel == 0)
            return;

        event.applyProjectileFlag(GlobalDamageManager.ARROW_SNIPE_ENCHANT_METANAME, snipeLevel);
    }

    @EventHandler
    public void onBossHitByFMJArrow(EntityHitByProjectileEvent event) {

        if (event.getHitEntity() == null)
            return;

        if (!isBoss(event.getHitEntity()))
            return;

        int snipeLevel = event.getProjectileFlag(GlobalDamageManager.ARROW_SNIPE_ENCHANT_METANAME);
        if (snipeLevel == 0)
            return;

        if (!(event.getEntity().getShooter() instanceof LivingEntity))
            return;

        double distance = ((LivingEntity) event.getEntity().getShooter()).getLocation().distance(event.getHitEntity().getLocation());
        event.multiplyDamage(1 + getDistanceDamageMultiplier(distance, snipeLevel));
    }
}
