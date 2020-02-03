package io.github.devvydoo.levelingoverhaul.listeners.progression;

import io.github.devvydoo.levelingoverhaul.util.BaseExperience;
import io.github.devvydoo.levelingoverhaul.util.LevelRewards;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftingListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onCraftAttempt(CraftItemEvent event) {

        // Sanity check
        if (event.getCurrentItem() == null) {
            return;
        }

        // Is a wither skull in the result?
        if (!event.getCurrentItem().getType().equals(Material.WITHER_SKELETON_SKULL)) {
            return;
        }

        // Are we a Player?
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        // Are we a high enough level to craft it?
        if (player.getLevel() < LevelRewards.CRAFT_WITHER_SKULLS_UNLOCK) {
            event.setCancelled(true);
            player.sendActionBar(ChatColor.RED + "You must be level " + ChatColor.DARK_RED + LevelRewards.CRAFT_WITHER_SKULLS_UNLOCK + ChatColor.RED + " to craft this item!");
            player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, .3f, .7f);
        }

    }

}
