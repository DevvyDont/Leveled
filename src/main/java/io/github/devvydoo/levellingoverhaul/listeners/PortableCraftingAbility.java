package io.github.devvydoo.levellingoverhaul.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PortableCraftingAbility implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){

        final int PORTABLE_CRAFTING_ABILITY_LEVEL_UNLOCK = 70;

        Player player = event.getPlayer();
        // Are we a high enough level?
        if (player.getLevel() < PORTABLE_CRAFTING_ABILITY_LEVEL_UNLOCK){
            return;
        }

        Action action = event.getAction();

        // Did we right click?
        if (!(action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR))){
            return;
        }

        // Are we sneaking?
        if (!player.isSneaking()){
            return;
        }

        // Are we holding nothing in our hand?
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
            return;
        }

        // Open the crafting menu
        player.openWorkbench(null, true);  // Use player location, and force the menu to open
    }
}
