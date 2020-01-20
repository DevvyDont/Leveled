package io.github.devvydoo.levellingoverhaul.listeners.progression;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import io.github.devvydoo.levellingoverhaul.enchantments.CustomEnchantType;
import io.github.devvydoo.levellingoverhaul.enchantments.CustomEnchantments;
import io.github.devvydoo.levellingoverhaul.util.BaseExperience;
import io.github.devvydoo.levellingoverhaul.util.LevelRewards;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Listeners that involve armor
 */
public class PlayerArmorListeners implements Listener {

    private LevellingOverhaul plugin;

    // A map from item type, to its level cap
    private HashMap<Material, Integer> materialLevelCaps;

    public PlayerArmorListeners(LevellingOverhaul plugin) {

        this.plugin = plugin;

        materialLevelCaps = new HashMap<>();

        materialLevelCaps.put(Material.LEATHER_HELMET, LevelRewards.LEATHER_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.LEATHER_CHESTPLATE, LevelRewards.LEATHER_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.LEATHER_LEGGINGS, LevelRewards.LEATHER_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.LEATHER_BOOTS, LevelRewards.LEATHER_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.TURTLE_HELMET, LevelRewards.LEATHER_ARMOR_UNLOCK);

        materialLevelCaps.put(Material.GOLDEN_HELMET, LevelRewards.GOLDEN_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.GOLDEN_CHESTPLATE, LevelRewards.GOLDEN_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.GOLDEN_LEGGINGS, LevelRewards.GOLDEN_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.GOLDEN_BOOTS, LevelRewards.GOLDEN_ARMOR_UNLOCK);

        materialLevelCaps.put(Material.CHAINMAIL_HELMET, LevelRewards.CHAINMAIL_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.CHAINMAIL_CHESTPLATE, LevelRewards.CHAINMAIL_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.CHAINMAIL_LEGGINGS, LevelRewards.CHAINMAIL_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.CHAINMAIL_BOOTS, LevelRewards.CHAINMAIL_ARMOR_UNLOCK);

        materialLevelCaps.put(Material.IRON_HELMET, LevelRewards.IRON_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.IRON_CHESTPLATE, LevelRewards.IRON_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.IRON_LEGGINGS, LevelRewards.IRON_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.IRON_BOOTS, LevelRewards.IRON_ARMOR_UNLOCK);

        materialLevelCaps.put(Material.DIAMOND_HELMET, LevelRewards.DIAMOND_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.DIAMOND_CHESTPLATE, LevelRewards.DIAMOND_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.DIAMOND_LEGGINGS, LevelRewards.DIAMOND_ARMOR_UNLOCK);
        materialLevelCaps.put(Material.DIAMOND_BOOTS, LevelRewards.DIAMOND_ARMOR_UNLOCK);


        materialLevelCaps.put(Material.ELYTRA, LevelRewards.POST_ENDER_EQUIPMENT);
    }

    /**
     * Because we don't have a 'post event' system we will just have to correct a players health later if armor has growth
     *
     * @param player The player to correct health for
     */
    private void correctPlayerHealthLater(Player player) {
        new BukkitRunnable() {

            public void run() {
                double HP = plugin.getHpManager().calculatePlayerExpectedHealth(player);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(HP);
                if (player.getHealth() > HP) {
                    player.setHealth(HP);
                }
                plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, "");
            }

        }.runTaskLater(plugin, 10);
    }

    /**
     * Listen to all right click events, and check if we should disallow a player to equip armor
     *
     * @param event - The PlayerInteractEvent we are listening to
     */
    @EventHandler(ignoreCancelled = true)
    public void onArmorRightClick(PlayerInteractEvent event) {


        // Was it a right click?
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        // Is our player holding armor?
        if (event.getItem() == null) {
            return;
        }

        if (!materialLevelCaps.containsKey(event.getItem().getType())) {
            return;
        }

        // Does our player have the required level to equip this?
        int requiredLevel = CustomEnchantments.getItemLevel(event.getItem());
        if (requiredLevel < materialLevelCaps.get(event.getItem().getType())) {
            requiredLevel = materialLevelCaps.get(event.getItem().getType());
        }


        if (event.getPlayer().getLevel() < requiredLevel) {
            event.setCancelled(true);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_PLACE, .3f, .7f);
            BaseExperience.displayActionBarText(event.getPlayer(), ChatColor.RED + "You must be level " + ChatColor.DARK_RED + requiredLevel + ChatColor.RED + " to equip that item!");
        }

        // See if we have growth, if not return else correct the players hp later
        try {
            CustomEnchantments.getEnchantLevel(event.getItem(), CustomEnchantType.GROWTH);
        } catch (IllegalArgumentException ignored) {
            return;
        }
        correctPlayerHealthLater(event.getPlayer());

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
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        correctPlayerHealthLater(player);

        // Do we even need to check them?
        if (player.getLevel() >= BaseExperience.LEVEL_CAP) {
            return;
        }

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

            int requiredLevel = CustomEnchantments.getItemLevel(itemHeld);
            if (requiredLevel < materialLevelCaps.get(itemHeld.getType())) {
                requiredLevel = materialLevelCaps.get(itemHeld.getType());
            }

            // Does our player have the required level to interact with this item?
            if (player.getLevel() < requiredLevel) {
                BaseExperience.displayActionBarText(player, ChatColor.RED + "You must be level " + ChatColor.DARK_RED + requiredLevel + ChatColor.RED + " to equip that item!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, .3f, .7f);
                event.setCancelled(true);
            }

            // Did they shift click?
        } else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {

            ItemStack itemClicked = event.getCurrentItem();

            // Did they shift click something?
            if (itemClicked == null) {
                return;
            }

            // Did they shift click armor?
            if (!materialLevelCaps.containsKey(itemClicked.getType())) {
                return;
            }

            // Are they a high enough level to equip it?
            int requiredLevel = CustomEnchantments.getItemLevel(itemClicked);
            if (requiredLevel < materialLevelCaps.get(itemClicked.getType())) {
                requiredLevel = materialLevelCaps.get(itemClicked.getType());
            }

            // Does our player have the required level to interact with this item?
            if (player.getLevel() < requiredLevel) {
                BaseExperience.displayActionBarText(player, ChatColor.RED + "You must be level " + ChatColor.DARK_RED + requiredLevel + ChatColor.RED + " to equip that item!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, .3f, .7f);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorClick(InventoryClickEvent event) {

        // Was armor clicked? Armor in our plugin is unbreakable so make sure it can't break
        if (event.getCurrentItem() != null && materialLevelCaps.containsKey(event.getCurrentItem().getType())) {
            ItemMeta meta = event.getCurrentItem().getItemMeta();
            if (meta == null) {
                return;
            }
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            event.getCurrentItem().setItemMeta(meta);
        }

    }

}
