package io.github.devvydoo.levellingoverhaul.enchantments.enchants;

import io.github.devvydoo.levellingoverhaul.enchantments.CustomEnchantType;
import io.github.devvydoo.levellingoverhaul.enchantments.CustomEnchantments;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A class responsible for listening for events for the EXPLOSIVE_TOUCH enchantment
 * <p>
 * This test enchantment gives whatever is in your hand explosive touch upon breaking a block, pretty useless
 */
public class ExplosiveTouchEnchantment implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        // Is the event cancelled already?
        if (event.isCancelled()) {
            return;
        }

        // Are we even holding a tool?
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            return;
        }

        // Is the tool enchanted?
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (!CustomEnchantments.hasEnchant(tool, CustomEnchantType.EXPLOSIVE_TOUCH)) {
            return;
        }

        // Cool, how big should our explosion be?
        int explosionPower = 3 * CustomEnchantments.getEnchantLevel(tool, CustomEnchantType.EXPLOSIVE_TOUCH);

        // Boom
        event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), explosionPower);
    }

}
