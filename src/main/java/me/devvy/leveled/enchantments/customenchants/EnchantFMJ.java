package me.devvy.leveled.enchantments.customenchants;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.devvy.leveled.events.EntityHitByProjectileEvent;
import me.devvy.leveled.events.EntityShootArrowEvent;
import me.devvy.leveled.managers.GlobalDamageManager;
import me.devvy.leveled.util.ToolTypeHelpers;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Boss;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EnchantFMJ extends Enchantment implements Listener {

    public EnchantFMJ(NamespacedKey key) {
        super(key);
    }

    @Override
    public @NotNull String getName() {
        return "FMJ";
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
        return 3;
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
        allowedMats.add(Material.CROSSBOW);
        allowedMats.add(Material.BOW);
        return allowedMats.contains(itemStack.getType());
    }

    private boolean isBoss(LivingEntity entity) {
        return entity instanceof Boss || entity instanceof EnderDragonPart;
    }

    @EventHandler
    public void onEntityShotFMJArrow(EntityShootArrowEvent event) {

        assert event.getBow() != null;
        int fmjLevel = event.getBow().getEnchantmentLevel(this);

        if (fmjLevel == 0)
            return;


        event.applyProjectileFlag(GlobalDamageManager.ARROW_FMJ_ENCHANT_METANAME, fmjLevel);
    }

    @EventHandler
    public void onBossHitByFMJArrow(EntityHitByProjectileEvent event) {

        if (!(event.getHitEntity() instanceof LivingEntity))
            return;

        if (!isBoss((LivingEntity) event.getHitEntity()))
            return;

        int fmjLevel = event.getProjectileFlag(GlobalDamageManager.ARROW_FMJ_ENCHANT_METANAME);
        if (fmjLevel == 0)
            return;

        event.multiplyDamage(1 + fmjLevel * .2f);
    }
}
