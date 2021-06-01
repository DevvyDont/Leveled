package me.devvy.leveled.listeners.progression;

import me.devvy.leveled.Leveled;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listeners that involve armor
 */
public class PlayerArmorListeners implements Listener {

    /**
     * Because there may be exploits that allow players to equip over leveled armor, check the player to make sure
     * their armor is legal
     *
     * @param player The spigot player to check
     */
    private void equippedArmorSanityCheck(Player player){

        ItemStack[] armor = new ItemStack[]{player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(), player.getInventory().getBoots()};

        for (ItemStack armorItem : armor){

            if (armorItem == null || armorItem.getType() == Material.AIR)
                continue;

            int level = Leveled.getInstance().getCustomItemManager().getItemLevel(armorItem);

            if (level > player.getLevel()){
                player.getWorld().dropItemNaturally(player.getLocation(), armorItem.clone());
                armorItem.setAmount(0);
            }
        }

    }

    private boolean isWearable(Material material){

        switch (material){
            case CHAINMAIL_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case IRON_CHESTPLATE:
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
            case DIAMOND_HELMET:
            case GOLDEN_HELMET:
            case IRON_HELMET:
            case TURTLE_HELMET:
            case LEATHER_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case IRON_LEGGINGS:
            case CHAINMAIL_BOOTS:
            case DIAMOND_BOOTS:
            case GOLDEN_BOOTS:
            case IRON_BOOTS:
            case LEATHER_BOOTS:
            case ELYTRA:
            case DRAGON_HEAD:
            case CREEPER_HEAD:
            case PLAYER_HEAD:
            case ZOMBIE_HEAD:
                return true;
            default:return false;
        }
    }

    private void cancelArmorEvent(Cancellable event, Player player, int neededLevel){
        event.setCancelled(true);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, .3f, .7f);
        player.sendActionBar( ChatColor.RED + "You must be level " + ChatColor.DARK_RED + neededLevel + ChatColor.RED + " to equip that item!");
    }

    /**
     * Listen to all right click events, and check if we should disallow a player to equip armor
     *
     * @param event - The PlayerInteractEvent we are listening to
     */
    @EventHandler(ignoreCancelled = true)
    public void onArmorRightClick(PlayerInteractEvent event) {

        // Was it a right click?
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            return;

        // Is our player holding armor?
        if (event.getItem() == null || !isWearable(event.getItem().getType()))
            return;

        // Does our player have the required level to equip this?
        int requiredLevel = Leveled.getInstance().getCustomItemManager().getItemLevel(event.getItem());

        if (event.getPlayer().getLevel() < requiredLevel)
            cancelArmorEvent(event, event.getPlayer(), requiredLevel);
    }

    /**
     * Listen to inventory click events and cancel them if a player is trying to equip an item they are not high
     * enough level for
     *
     * @param event - The InventoryClickEvent we are listening to
     */
    @EventHandler(ignoreCancelled = true)
    public void onArmorInteract(InventoryClickEvent event) {

        // Do we have a player?
        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();

        // Did they click an armor slot?
        if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {

            ItemStack itemHeld = event.getCursor();

            // Sanity check, do we have an item?
            if (itemHeld == null || !isWearable(itemHeld.getType()))
                return;

            int requiredLevel = Leveled.getInstance().getCustomItemManager().getItemLevel(itemHeld);

            // Does our player have the required level to interact with this item?
            if (player.getLevel() < requiredLevel) {
                player.sendActionBar( ChatColor.RED + "You must be level " + ChatColor.DARK_RED + requiredLevel + ChatColor.RED + " to equip that item!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, .3f, .7f);
                event.setCancelled(true);
            }

            // Did they shift click?
        } else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {

            ItemStack itemClicked = event.getCurrentItem();

            // Did they shift click something?
            if (itemClicked == null || isWearable(itemClicked.getType()))
                return;

            // Are they a high enough level to equip it?
            int requiredLevel = Leveled.getInstance().getCustomItemManager().getItemLevel(itemClicked);

            // Does our player have the required level to interact with this item?
            if (player.getLevel() < requiredLevel)
                cancelArmorEvent(event, player, requiredLevel);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerHit(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            equippedArmorSanityCheck((Player)event.getEntity());
        }
    }

}
