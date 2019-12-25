package io.github.devvydoo.levellingoverhaul.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnchantmentListeners implements Listener {


    @EventHandler
    public void onEnchantmentTableInteract(PlayerInteractEvent event){

        final int ENCHANT_LEVEL_CAP = 30;

        // Are we right clicking a block
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            return;
        }

        Block blockRightClicked = event.getClickedBlock();

        if (blockRightClicked == null){
            return;
        }

        // Are we right clicking an echantment table
        if (!blockRightClicked.getType().equals(Material.ENCHANTING_TABLE)){
            return;
        }

        // Are we a high enough level
        if (event.getPlayer().getLevel() < ENCHANT_LEVEL_CAP){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You must be level " + ChatColor.DARK_RED + ENCHANT_LEVEL_CAP + ChatColor.RED + " to enchant!");
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, .3f, .7f);
        }
    }

}
