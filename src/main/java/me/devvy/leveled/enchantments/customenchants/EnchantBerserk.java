package me.devvy.leveled.enchantments.customenchants;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.devvy.leveled.enchantments.EnchantmentManager;
import me.devvy.leveled.events.PlayerDealtMeleeDamageEvent;
import me.devvy.leveled.util.ToolTypeHelpers;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class EnchantBerserk extends Enchantment implements Listener {

    public EnchantBerserk(NamespacedKey key) {
        super(key);
    }

    @Override
    public @NotNull String getName() {
        return "Berkserk";
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
        return Collections.singleton(EquipmentSlot.CHEST);
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
        return allowedMats.contains(itemStack.getType());
    }

    @EventHandler
    public void onPlayerDealtBerserkerDamage(PlayerDealtMeleeDamageEvent event) {

        int mainHandLevel = event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(EnchantmentManager.BERSERK);
        int offHandLevel = event.getPlayer().getInventory().getItemInOffHand().getEnchantmentLevel(EnchantmentManager.BERSERK);
        if (mainHandLevel != 0 || offHandLevel != 0)
            event.multiplyDamage(2);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTookBerserkerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        int mainHandLevel = player.getInventory().getItemInMainHand().getEnchantmentLevel(EnchantmentManager.BERSERK);
        int offHandLevel = player.getInventory().getItemInOffHand().getEnchantmentLevel(EnchantmentManager.BERSERK);
        if (mainHandLevel != 0 || offHandLevel != 0)
            event.setDamage(event.getDamage() * 3);

    }
}
