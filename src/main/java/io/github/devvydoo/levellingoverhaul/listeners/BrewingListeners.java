package io.github.devvydoo.levellingoverhaul.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class BrewingListeners implements Listener {

    private final int BREWING_LEVEL_CAP = 45;


    @EventHandler
    public void onBrewingStandInteract(PlayerInteractEvent event){



        Player player = event.getPlayer();
        Action action = event.getAction();

        // Did we right click a block?
        if (!action.equals(Action.RIGHT_CLICK_BLOCK)){
            return;
        }

        if (event.getClickedBlock() == null){
            return;
        }

        // Did we right click a brewing stand?
        if (!event.getClickedBlock().getType().equals(Material.BREWING_STAND)){
            return;
        }

        // Are we a high enough level?
        if (player.getLevel() < BREWING_LEVEL_CAP){
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must be level " + ChatColor.DARK_RED + BREWING_LEVEL_CAP + ChatColor.RED + " to brew potions!");
            player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, .3f, .7f);
        }
    }

    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        ItemStack itemDrank = event.getItem();

        // Are we drinking a potion?
        if (!itemDrank.getType().equals(Material.POTION)){
            return;
        }

        // Are we high enough level?
        if (player.getLevel() < BREWING_LEVEL_CAP){
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must be level " + ChatColor.DARK_RED + BREWING_LEVEL_CAP + ChatColor.RED + " to drink potions!");
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, .3f, .7f);
        }

    }

}
