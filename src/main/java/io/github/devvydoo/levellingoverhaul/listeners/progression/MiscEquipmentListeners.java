package io.github.devvydoo.levellingoverhaul.listeners.progression;

import io.github.devvydoo.levellingoverhaul.enchantments.CustomEnchantment;
import io.github.devvydoo.levellingoverhaul.enchantments.CustomEnchantments;
import io.github.devvydoo.levellingoverhaul.util.BaseExperience;
import io.github.devvydoo.levellingoverhaul.util.LevelRewards;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class MiscEquipmentListeners implements Listener {

    private HashMap<Material, Integer> equipmentRequirements;

    public MiscEquipmentListeners() {

        this.equipmentRequirements = new HashMap<>();

        this.equipmentRequirements.put(Material.SHEARS, 1);
        this.equipmentRequirements.put(Material.FISHING_ROD, 1);

        this.equipmentRequirements.put(Material.ENDER_PEARL, LevelRewards.PRE_ENDER_EQUIPMENT);
        this.equipmentRequirements.put(Material.ENDER_EYE, LevelRewards.PRE_ENDER_EQUIPMENT);
        this.equipmentRequirements.put(Material.ENDER_CHEST, LevelRewards.POST_ENDER_EQUIPMENT);
        this.equipmentRequirements.put(Material.SHULKER_BOX, LevelRewards.POST_ENDER_EQUIPMENT);

        this.equipmentRequirements.put(Material.BOW, LevelRewards.NORMAL_BOW_UNLOCK);
        this.equipmentRequirements.put(Material.CROSSBOW, LevelRewards.CROSSBOW_UNLOCK);

        this.equipmentRequirements.put(Material.SHIELD, LevelRewards.SHIELD_UNLOCK);
    }

    private boolean isShulker(Material material){
        switch (material){
            case SHULKER_BOX:
            case BLACK_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
                return true;
            default:
                return false;
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onItemInteract(PlayerInteractEvent event) {


        Player player = event.getPlayer();
        Action action = event.getAction();

        // We only need to worry about right click events
        if (!(action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR))) {
            return;
        }

        // Do we even need to perform a level check?
        if (player.getLevel() >= BaseExperience.LEVEL_CAP) {
            return;
        }

        // What's in our hand?
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack itemInOffhand = event.getPlayer().getInventory().getItemInOffHand();

        // Check if any of the items are level capped
        boolean mainHandNeedsChecked = equipmentRequirements.containsKey(itemInHand.getType());
        boolean offhandNeedsChecked = equipmentRequirements.containsKey(itemInOffhand.getType());
        boolean blockClickedNeedsChecked = action.equals(Action.RIGHT_CLICK_BLOCK) &&
                event.getClickedBlock() != null && (equipmentRequirements.containsKey(event.getClickedBlock().getType()) || isShulker(event.getClickedBlock().getType()));
        if (!(mainHandNeedsChecked || offhandNeedsChecked || blockClickedNeedsChecked)) {
            return;
        }

        // We may potentially run into issues, check their main hand first
        if (mainHandNeedsChecked) {
            int levelRequired = Math.max(CustomEnchantments.getItemLevel(itemInHand), equipmentRequirements.get(itemInHand.getType()));
            if (levelRequired > player.getLevel()) {
                event.setCancelled(true);
                BaseExperience.displayActionBarText(player, ChatColor.RED + "You must be level " + ChatColor.DARK_RED + levelRequired + ChatColor.RED + " to use this item!");
                player.playSound(player.getLocation(), this.getSoundFromMaterial(itemInHand.getType()), .3f, .7f);
            }
            // Now check their offhand
        }
        if (offhandNeedsChecked) {
            int levelRequired = Math.max(CustomEnchantments.getItemLevel(itemInOffhand), equipmentRequirements.get(itemInOffhand.getType()));
            if (levelRequired > player.getLevel()) {
                event.setCancelled(true);
                BaseExperience.displayActionBarText(player, ChatColor.RED + "You must be level " + ChatColor.DARK_RED + levelRequired + ChatColor.RED + " to use this item!");
                player.playSound(player.getLocation(), this.getSoundFromMaterial(itemInOffhand.getType()), .3f, .7f);
            }
        } else if (blockClickedNeedsChecked) {
            if (isShulker(event.getClickedBlock().getType())) {
                if (player.getLevel() < equipmentRequirements.get(Material.SHULKER_BOX)) {
                    event.setCancelled(true);
                    BaseExperience.displayActionBarText(player, ChatColor.RED + "You must be level " + ChatColor.DARK_RED + equipmentRequirements.get(Material.SHULKER_BOX) + ChatColor.RED + " to interact with this item!");
                    player.playSound(player.getLocation(), this.getSoundFromMaterial(Material.SHULKER_BOX), .3f, .7f);
                }
            } else {
                if (player.getLevel() < equipmentRequirements.get(event.getClickedBlock().getType())){
                    event.setCancelled(true);
                    BaseExperience.displayActionBarText(player, ChatColor.RED + "You must be level " + ChatColor.DARK_RED + equipmentRequirements.get(event.getClickedBlock().getType()) + ChatColor.RED + " to interact with this item!");
                    player.playSound(player.getLocation(), this.getSoundFromMaterial(event.getClickedBlock().getType()), .3f, .7f);
                }
            }

        }

    }

    private Sound getSoundFromMaterial(Material material) {
        switch (material) {
            case CROSSBOW:
            case BOW:
                return Sound.ITEM_CROSSBOW_SHOOT;
            case SHIELD:
                return Sound.ITEM_SHIELD_BREAK;
            case ENDER_CHEST:
            case ENDER_PEARL:
            case ENDER_EYE:
            case SHULKER_BOX:
                return Sound.ENTITY_ENDERMAN_TELEPORT;
            default:
                return Sound.BLOCK_ANVIL_PLACE;
        }
    }

}
