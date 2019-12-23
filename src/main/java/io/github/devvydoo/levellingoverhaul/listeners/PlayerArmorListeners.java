package io.github.devvydoo.levellingoverhaul.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Listeners that involve armor
 */
public class PlayerArmorListeners implements Listener {

    // A map from item type, to its level cap
    private HashMap<Material, Integer> materialLevelCaps;

    public PlayerArmorListeners(){

        materialLevelCaps = new HashMap<>();

        materialLevelCaps.put(Material.LEATHER_HELMET, 10);
        materialLevelCaps.put(Material.LEATHER_CHESTPLATE, 10);
        materialLevelCaps.put(Material.LEATHER_LEGGINGS, 10);
        materialLevelCaps.put(Material.LEATHER_BOOTS, 10);

        materialLevelCaps.put(Material.GOLDEN_HELMET, 20);
        materialLevelCaps.put(Material.GOLDEN_CHESTPLATE, 20);
        materialLevelCaps.put(Material.GOLDEN_LEGGINGS, 20);
        materialLevelCaps.put(Material.GOLDEN_BOOTS, 20);

        materialLevelCaps.put(Material.CHAINMAIL_HELMET, 30);
        materialLevelCaps.put(Material.CHAINMAIL_CHESTPLATE, 30);
        materialLevelCaps.put(Material.CHAINMAIL_LEGGINGS, 30);
        materialLevelCaps.put(Material.CHAINMAIL_BOOTS, 30);

        materialLevelCaps.put(Material.IRON_HELMET, 35);
        materialLevelCaps.put(Material.IRON_CHESTPLATE, 35);
        materialLevelCaps.put(Material.IRON_LEGGINGS, 35);
        materialLevelCaps.put(Material.IRON_BOOTS, 35);

        materialLevelCaps.put(Material.DIAMOND_HELMET, 50);
        materialLevelCaps.put(Material.DIAMOND_CHESTPLATE, 50);
        materialLevelCaps.put(Material.DIAMOND_LEGGINGS, 50);
        materialLevelCaps.put(Material.DIAMOND_BOOTS, 50);
    }

    /**
     * Listen to all right click events, and check if we should disallow a player to equip armor
     *
     * @param event - The PlayerInteractEvent we are listening to
     */
    @EventHandler
    public void onArmorRightClick(PlayerInteractEvent event){


        // Was it a right click?
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            return;
        }

        // Is our player holding armor?
        if (event.getItem() == null){
            return;
        }

        if (!materialLevelCaps.containsKey(event.getItem().getType())){
            return;
        }

        // Does our player have the required level to equip this?
        int requiredLevel = materialLevelCaps.get(event.getItem().getType());
        if (event.getPlayer().getLevel() < requiredLevel ){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You must be level " + ChatColor.DARK_RED + requiredLevel + ChatColor.RED + " to equip that item!");
        }

    }

    /**
     * Listen to inventory click events and cancel them if a player is trying to equip an item they are not high
     * enough level for
     *
     * @param event - The InventoryClickEvent we are listening to
     */
    @EventHandler
    public void onArmorInteract(InventoryClickEvent event){

        // Did they click an armor slot?
        if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {

            ItemStack itemClicked = event.getCurrentItem();
            ItemStack itemHeld = event.getCursor();

            // Sanity check, do they somehow have armor already equipped theyre not supposed to?
            if (itemClicked != null) {
                if (materialLevelCaps.containsKey(itemClicked.getType())) {
                    if (event.getWhoClicked() instanceof Player) {
                        if (((Player) event.getWhoClicked()).getLevel() < materialLevelCaps.get(itemClicked.getType())) {
                            event.getWhoClicked().getInventory().setItem(event.getSlot(), null);
                        }
                    }
                }
            }

            // Sanity check, do we have an item?
            if (itemHeld == null) {
                return;
            }

            // Is the item they clicked armor?
            if (!this.materialLevelCaps.containsKey(itemHeld.getType())) {
                return;
            }

            int requiredLevel = materialLevelCaps.get(itemHeld.getType());
            Player player = (Player) event.getWhoClicked();

            // Does our player have the required level to interact with this item?
            if (player.getLevel() < requiredLevel) {
                player.sendMessage(ChatColor.RED + "You must be level " + ChatColor.DARK_RED + requiredLevel + ChatColor.RED + " to equip that item!");
                event.setCancelled(true);
            }

        // Did they shift click?
        } else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)){

            ItemStack itemClicked = event.getCurrentItem();

            // Did they shift click something?
            if (itemClicked == null){
                return;
            }

            // Did they shift click armor?
            if (!materialLevelCaps.containsKey(itemClicked.getType())){
                return;
            }

            // Are they a high enough level to equip it?
            int requiredLevel = materialLevelCaps.get(itemClicked.getType());
            Player player = (Player) event.getWhoClicked();

            // Does our player have the required level to interact with this item?
            if (player.getLevel() < requiredLevel) {
                player.sendMessage(ChatColor.RED + "You must be level " + ChatColor.DARK_RED + requiredLevel + ChatColor.RED + " to equip that item!");
                event.setCancelled(true);
            }
        }
    }

}
