package me.devvy.leveled.listeners.progression;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ProgressionModifyingListeners implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        // If we are in creative mode, don't do anything
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE) || event.isCancelled())
            return;


        // If we have an Iron pickaxe
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.IRON_PICKAXE)) {
            // If we broke obsidian with our iron pickaxe, drop it
            if (event.getBlock().getType().equals(Material.OBSIDIAN)) {
                event.setDropItems(true);
                Location loc = event.getBlock().getLocation();
                event.getPlayer().getWorld().dropItemNaturally(loc, new ItemStack(Material.OBSIDIAN));
            }
        }


    }
}
