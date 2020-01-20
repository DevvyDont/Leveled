package io.github.devvydoo.levellingoverhaul.listeners.progression;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ProgressionModifyingListeners implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        // If we are in creative mode, don't do anything
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        // If we have a stone pickaxe
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STONE_PICKAXE)) {
            // If we broke gold ore with our stone pickaxe, drop it
            if (event.getBlock().getType().equals(Material.GOLD_ORE)) {
                event.setDropItems(true);
                Location loc = event.getBlock().getLocation();
                event.getPlayer().getWorld().dropItemNaturally(loc, new ItemStack(Material.GOLD_ORE));
            }
        }

        // If we have an Iron pickaxe
        else if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.IRON_PICKAXE)) {
            // If we broke obsidian with our iron pickaxe, drop it
            if (event.getBlock().getType().equals(Material.OBSIDIAN)) {
                event.setDropItems(true);
                Location loc = event.getBlock().getLocation();
                event.getPlayer().getWorld().dropItemNaturally(loc, new ItemStack(Material.OBSIDIAN));
            }
        }

        // If we have a gold pickaxe
        else if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_PICKAXE)) {
            // If we broke iron with our golden pick, drop it
            if (event.getBlock().getType().equals(Material.IRON_ORE)) {
                event.setDropItems(true);
                Location loc = event.getBlock().getLocation();
                event.getPlayer().getWorld().dropItemNaturally(loc, new ItemStack(Material.IRON_ORE));
                // If we broke gold ore with our gold pick, drop it
            } else if (event.getBlock().getType().equals(Material.GOLD_ORE)) {
                event.setDropItems(true);
                Location loc = event.getBlock().getLocation();
                event.getPlayer().getWorld().dropItemNaturally(loc, new ItemStack(Material.GOLD_ORE));
            }
        }
    }
}
