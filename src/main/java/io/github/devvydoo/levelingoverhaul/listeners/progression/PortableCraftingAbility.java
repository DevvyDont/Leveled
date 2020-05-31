package io.github.devvydoo.levelingoverhaul.listeners.progression;

import io.github.devvydoo.levelingoverhaul.player.LevelRewards;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PortableCraftingAbility implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        // Are we a high enough level?
        if (player.getLevel() < LevelRewards.UNIVERSAL_CRAFTING_ABILITY_UNLOCK)
            return;

        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
            if (player.isSneaking() && player.getInventory().getItemInMainHand().getType().equals(Material.AIR) && player.getInventory().getItemInOffHand().getType().equals(Material.AIR)){
                // Open the crafting menu
                player.openWorkbench(null, true);  // Use player location, and force the menu to open
            }
        }


    }
}
