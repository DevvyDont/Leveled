package io.github.devvydoo.levellingoverhaul.enchantments;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A class responsible for listening for events for the EXPLOSIVE_TOUCH enchantment
 *
 * This test enchantment gives whatever is in your hand explosive touch upon breaking a block, pretty useless
 */
public class ExplosiveTouch implements Listener {

    private int count = 1;

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

        // Is the tool enchanted?
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (!CustomEnchantments.hasEnchant(tool, CustomEnchantType.EXPLOSIVE_TOUCH)){
            return;
        }

        // Cool, how big should our explosion be?
        int explosionPower = 3 * CustomEnchantments.getEnchantLevel(tool, CustomEnchantType.EXPLOSIVE_TOUCH);

        // Boom
        event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), explosionPower);
    }

//    // Debug event that gives an item with our custom enchantment
//    @EventHandler
//    public void onCraftingTableInteract(PlayerInteractEvent event){
//
//        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
//            if (event.getClickedBlock().getType().equals(Material.CRAFTING_TABLE)){
//                ItemStack explosiveShovel = new ItemStack(Material.WOODEN_SHOVEL);
//                CustomEnchantments.addEnchant(explosiveShovel, CustomEnchantType.EXPLOSIVE_TOUCH, count);
//                count++;
//                ItemMeta shovelMeta = explosiveShovel.getItemMeta();
//                shovelMeta.setDisplayName(ChatColor.RED + "Explosive Shovel");
//                explosiveShovel.setItemMeta(shovelMeta);
//                event.getPlayer().getInventory().addItem(explosiveShovel);
//            }
//        }
//
//    }

}
