package io.github.devvydoo.levelingoverhaul.listeners.progression;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.player.LevelRewards;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PlayerToolListeners implements Listener {

    LevelingOverhaul plugin;

    public PlayerToolListeners(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    private void cancelEquipmentUse(Cancellable event, Player player, int requiredLevel) {
        event.setCancelled(true);
        player.sendActionBar( ChatColor.RED + "You must be level " + ChatColor.DARK_RED + requiredLevel + ChatColor.RED + " to use that item!");
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, .3f, .7f);
    }

    /**
     * Listen for events where a player breaks a block and make sure they are high enough level to use it
     *
     * @param event - The BlockBreakEvent we are listening to
     */
    @EventHandler
    public void onBlockBreakWithTool(BlockBreakEvent event) {

        Player player = event.getPlayer();
        ItemStack toolUsed = player.getInventory().getItemInMainHand();

        if (toolUsed.getType() == Material.AIR)
            return;

        int toolLevel = plugin.getCustomItemManager().getItemLevel(toolUsed);

        if (toolLevel > player.getLevel())
            cancelEquipmentUse(event, player, toolLevel);
    }

    /**
     * Listen for events where a player attacks something with a tool and make sure they are high enough level to use it
     *
     * @param event - The EntityDamageByEntityEvent we are listening to
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttackEntity(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getDamager();
        ItemStack toolUsed = player.getInventory().getItemInMainHand();

        if (toolUsed.getType() == Material.AIR)
            return;

        int toolLevel = plugin.getCustomItemManager().getItemLevel(toolUsed);

        if (toolLevel > player.getLevel())
            cancelEquipmentUse(event, player, toolLevel);
    }

}
