package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.enchantments.EnchantmentManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantExplosiveTouch extends Enchantment implements Listener {

    public EnchantExplosiveTouch(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Explosive Touch";
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
    public EnchantmentTarget getItemTarget() {
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
    public boolean conflictsWith(Enchantment enchantment) {
        return enchantment == LOOT_BONUS_BLOCKS || enchantment == SILK_TOUCH;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        switch (itemStack.getType()) {
            case DIAMOND_SHOVEL:
            case GOLDEN_SHOVEL:
            case IRON_SHOVEL:
            case WOODEN_SHOVEL:
            case STONE_SHOVEL:
                return true;
            default:
                return false;
        }
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
