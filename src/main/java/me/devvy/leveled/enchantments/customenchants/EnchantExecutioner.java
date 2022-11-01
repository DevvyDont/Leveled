package me.devvy.leveled.enchantments.customenchants;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.devvy.leveled.events.EntityHitByProjectileEvent;
import me.devvy.leveled.events.EntityShootArrowEvent;
import me.devvy.leveled.managers.GlobalDamageManager;
import me.devvy.leveled.util.ToolTypeHelpers;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
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

public class EnchantExecutioner extends Enchantment implements Listener {

    public EnchantExecutioner(NamespacedKey key) {
        super(key);
    }

    @Override
    public @NotNull String getName() {
        return "Executioner";
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
        allowedMats.add(Material.BOW);
        allowedMats.add(Material.CROSSBOW);
        return allowedMats.contains(itemStack.getType());
    }

    @EventHandler
    public void onEntityShotEXEArrow(EntityShootArrowEvent event) {

        assert event.getBow() != null;
        int exeLevel = event.getBow().getEnchantmentLevel(this);

        if (exeLevel == 0)
            return;

        event.applyProjectileFlag(GlobalDamageManager.ARROW_EXECUTE_ENCHANT_METANAME, exeLevel);
    }

    @EventHandler
    public void onEntityHitByEXEArrow(EntityHitByProjectileEvent event) {

        if (event.getHitEntity() == null || !(event.getHitEntity() instanceof LivingEntity))
            return;

        int exeLevel = event.getProjectileFlag(GlobalDamageManager.ARROW_EXECUTE_ENCHANT_METANAME);
        if (exeLevel == 0)
            return;

        double hpPercent = ((LivingEntity) event.getHitEntity()).getHealth() / ((LivingEntity) event.getHitEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        if (hpPercent > .25)
            return;

        event.multiplyDamage(1 + exeLevel * .15f);
    }
}
