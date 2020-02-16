package io.github.devvydoo.levelingoverhaul.listeners.progression;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.util.LevelRewards;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PlayerToolListeners implements Listener {

    LevelingOverhaul plugin;
    private HashMap<Material, Integer> toolLevelRequirements;

    public PlayerToolListeners(LevelingOverhaul plugin) {

        this.plugin = plugin;

        toolLevelRequirements = new HashMap<>();

        toolLevelRequirements.put(Material.WOODEN_PICKAXE, 1);
        toolLevelRequirements.put(Material.WOODEN_HOE, 1);
        toolLevelRequirements.put(Material.WOODEN_AXE, 1);
        toolLevelRequirements.put(Material.WOODEN_SHOVEL, 1);
        toolLevelRequirements.put(Material.WOODEN_SWORD, 1);
        toolLevelRequirements.put(Material.SHEARS, 1);
        toolLevelRequirements.put(Material.FISHING_ROD, 1);

        toolLevelRequirements.put(Material.STONE_PICKAXE, LevelRewards.STONE_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.STONE_HOE, LevelRewards.STONE_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.STONE_AXE, LevelRewards.STONE_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.STONE_SHOVEL, LevelRewards.STONE_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.STONE_SWORD, LevelRewards.STONE_TOOLS_UNLOCK);

        toolLevelRequirements.put(Material.GOLDEN_PICKAXE, LevelRewards.GOLDEN_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.GOLDEN_HOE, LevelRewards.GOLDEN_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.GOLDEN_AXE, LevelRewards.GOLDEN_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.GOLDEN_SHOVEL, LevelRewards.GOLDEN_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.GOLDEN_SWORD, LevelRewards.GOLDEN_TOOLS_UNLOCK);

        toolLevelRequirements.put(Material.IRON_PICKAXE, LevelRewards.IRON_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.IRON_HOE, LevelRewards.IRON_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.IRON_AXE, LevelRewards.IRON_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.IRON_SHOVEL, LevelRewards.IRON_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.IRON_SWORD, LevelRewards.IRON_TOOLS_UNLOCK);

        toolLevelRequirements.put(Material.DIAMOND_PICKAXE, LevelRewards.DIAMOND_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.DIAMOND_HOE, LevelRewards.DIAMOND_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.DIAMOND_AXE, LevelRewards.DIAMOND_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.DIAMOND_SHOVEL, LevelRewards.DIAMOND_TOOLS_UNLOCK);
        toolLevelRequirements.put(Material.DIAMOND_SWORD, LevelRewards.DIAMOND_TOOLS_UNLOCK);
    }

    private void cancelEquipmentUse(Player player, int requiredLevel) {
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

        if (toolLevelRequirements.containsKey(toolUsed.getType())) {
            int requiredLevel = plugin.getEnchantmentManager().getItemLevel(toolUsed);
            if (requiredLevel < toolLevelRequirements.get(toolUsed.getType())) {
                requiredLevel = toolLevelRequirements.get(toolUsed.getType());
            }
            if (requiredLevel > player.getLevel()) {
                event.setCancelled(true);
                this.cancelEquipmentUse(player, requiredLevel);
            }
        }
    }

    /**
     * Listen for events where a player attacks something with a tool and make sure they are high enough level to use it
     *
     * @param event - The EntityDamageByEntityEvent we are listening to
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttackEntity(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        ItemStack toolUsed = player.getInventory().getItemInMainHand();

        if (toolLevelRequirements.containsKey(toolUsed.getType())) {
            int requiredLevel = plugin.getEnchantmentManager().getItemLevel(toolUsed);
            if (requiredLevel < toolLevelRequirements.get(toolUsed.getType())) {
                requiredLevel = toolLevelRequirements.get(toolUsed.getType());
            }
            if (requiredLevel > player.getLevel()) {
                event.setCancelled(true);
                this.cancelEquipmentUse(player, requiredLevel);
            }
        }

    }

}
