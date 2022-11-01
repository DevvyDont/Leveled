package me.devvy.leveled.enchantments.customenchants;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.devvy.leveled.enchantments.EnchantmentManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class EnchantExplosiveTouch extends Enchantment implements Listener {

    public EnchantExplosiveTouch(NamespacedKey key) {
        super(key);
    }

    @Override
    public @NotNull String getName() {
        return "Explosive Touch";
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
        return 10;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
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
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return enchantment == LOOT_BONUS_BLOCKS || enchantment == SILK_TOUCH;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return switch (itemStack.getType()) {
            case DIAMOND_SHOVEL, GOLDEN_SHOVEL, IRON_SHOVEL, WOODEN_SHOVEL, STONE_SHOVEL -> true;
            default -> false;
        };
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event){

        // Is the event cancelled already?
        if (event.isCancelled()){
            return;
        }

        // Are we even holding a tool?
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)){
            return;
        }

        Player player = event.getPlayer();

        // Is the tool enchanted?
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (!(player.getInventory().getItemInMainHand().containsEnchantment(EnchantmentManager.EXPLOSIVE_TOUCH))){
            return;
        }

        // Cool, how big should our explosion be?
        int explosionPower = 3 * player.getInventory().getItemInMainHand().getEnchantmentLevel(EnchantmentManager.EXPLOSIVE_TOUCH);

        // Boom
        event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), explosionPower);
    }
}
