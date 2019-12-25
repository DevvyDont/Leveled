package io.github.devvydoo.levellingoverhaul.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;

public class PlayerToolListeners implements Listener {

    private HashMap<Material, Integer> toolLevelRequirements;

    public PlayerToolListeners(){
        toolLevelRequirements = new HashMap<>();

        toolLevelRequirements.put(Material.WOODEN_PICKAXE, 1);
        toolLevelRequirements.put(Material.WOODEN_HOE, 1);
        toolLevelRequirements.put(Material.WOODEN_AXE, 1);
        toolLevelRequirements.put(Material.WOODEN_SHOVEL, 1);
        toolLevelRequirements.put(Material.WOODEN_SWORD, 1);

        toolLevelRequirements.put(Material.STONE_PICKAXE, 5);
        toolLevelRequirements.put(Material.STONE_HOE, 5);
        toolLevelRequirements.put(Material.STONE_AXE, 5);
        toolLevelRequirements.put(Material.STONE_SHOVEL, 5);
        toolLevelRequirements.put(Material.STONE_SWORD, 5);

        toolLevelRequirements.put(Material.GOLDEN_PICKAXE, 15);
        toolLevelRequirements.put(Material.GOLDEN_HOE, 15);
        toolLevelRequirements.put(Material.GOLDEN_AXE, 15);
        toolLevelRequirements.put(Material.GOLDEN_SHOVEL, 15);
        toolLevelRequirements.put(Material.GOLDEN_SWORD, 15);

        toolLevelRequirements.put(Material.IRON_PICKAXE, 25);
        toolLevelRequirements.put(Material.IRON_HOE, 25);
        toolLevelRequirements.put(Material.IRON_AXE, 25);
        toolLevelRequirements.put(Material.IRON_SHOVEL, 25);
        toolLevelRequirements.put(Material.IRON_SWORD, 25);

        toolLevelRequirements.put(Material.DIAMOND_PICKAXE, 40);
        toolLevelRequirements.put(Material.DIAMOND_HOE, 40);
        toolLevelRequirements.put(Material.DIAMOND_AXE, 40);
        toolLevelRequirements.put(Material.DIAMOND_SHOVEL, 40);
        toolLevelRequirements.put(Material.DIAMOND_SWORD, 40);
    }

    private void cancelEquipmentUse(Player player, int requiredLevel){
        player.sendMessage(ChatColor.RED + "You must be level " + ChatColor.DARK_RED + requiredLevel + ChatColor.RED + " to use that item!");
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, .3f, .7f);
    }

    /**
     *
     *
     * @param event
     */
    @EventHandler
    public void onBlockBreakWithTool(BlockBreakEvent event){

        Player player = event.getPlayer();
        Material toolUsed = player.getInventory().getItemInMainHand().getType();

        if (toolLevelRequirements.containsKey(toolUsed)){
            int requiredLevel = toolLevelRequirements.get(toolUsed);
            if (requiredLevel > player.getLevel()){
                event.setCancelled(true);
                this.cancelEquipmentUse(player, requiredLevel);
            }
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventHandler
    public void onPlayerAttackEntity(EntityDamageByEntityEvent event){

        if (!(event.getDamager() instanceof Player)){
            return;
        }

        Player player = (Player) event.getDamager();
        Material toolUsed = player.getInventory().getItemInMainHand().getType();

        if (toolLevelRequirements.containsKey(toolUsed)){
            int requiredLevel = toolLevelRequirements.get(toolUsed);
            if (requiredLevel > player.getLevel()){
                event.setCancelled(true);
                this.cancelEquipmentUse(player, requiredLevel);
            }
        }

    }

}
