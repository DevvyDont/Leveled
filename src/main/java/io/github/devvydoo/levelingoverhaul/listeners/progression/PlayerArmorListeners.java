package io.github.devvydoo.levelingoverhaul.listeners.progression;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.util.BaseExperience;
import io.github.devvydoo.levelingoverhaul.util.LevelRewards;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

/**
 * Listeners that involve armor
 */
public class PlayerArmorListeners implements Listener {

    private LevelingOverhaul plugin;

    // A map from item type, to its level cap
    private HashMap<Material, Integer> materialLevelCaps;

    public PlayerArmorListeners(LevelingOverhaul plugin) {

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

        materialLevelCaps.put(Material.WITHER_SKELETON_SKULL, 1);
        materialLevelCaps.put(Material.CREEPER_HEAD, 1);
        materialLevelCaps.put(Material.PLAYER_HEAD, 1);
        materialLevelCaps.put(Material.ZOMBIE_HEAD, 1);
        materialLevelCaps.put(Material.SKELETON_SKULL, 1);
        materialLevelCaps.put(Material.DRAGON_HEAD, 1);
    }

    private void equippedArmorSanityCheck(Player player){

        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        if (helmet != null && !helmet.getType().equals(Material.AIR) && Math.max(plugin.getEnchantmentManager().getItemLevel(helmet), materialLevelCaps.get(helmet.getType())) > player.getLevel()){
            player.getWorld().dropItemNaturally(player.getLocation(), helmet);
            player.getInventory().setHelmet(new ItemStack(Material.AIR));
        }

        if (chestplate != null && !chestplate.getType().equals(Material.AIR) && Math.max(plugin.getEnchantmentManager().getItemLevel(chestplate), materialLevelCaps.get(chestplate.getType())) > player.getLevel()){
            player.getWorld().dropItemNaturally(player.getLocation(), chestplate);
            player.getInventory().setChestplate(new ItemStack(Material.AIR));
        }

        if (leggings != null && !leggings.getType().equals(Material.AIR) && Math.max(plugin.getEnchantmentManager().getItemLevel(leggings), materialLevelCaps.get(leggings.getType())) > player.getLevel()){
            player.getWorld().dropItemNaturally(player.getLocation(), leggings);
            player.getInventory().setLeggings(new ItemStack(Material.AIR));
        }

        if (boots != null && !boots.getType().equals(Material.AIR) && Math.max(plugin.getEnchantmentManager().getItemLevel(boots), materialLevelCaps.get(boots.getType())) > player.getLevel()){
            player.getWorld().dropItemNaturally(player.getLocation(), boots);
            player.getInventory().setBoots(new ItemStack(Material.AIR));
        }

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

        int requiredLevel = Math.max(plugin.getEnchantmentManager().getItemLevel(event.getItem()), materialLevelCaps.get(event.getItem().getType()));

        if (event.getPlayer().getLevel() < requiredLevel) {
            event.setCancelled(true);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_PLACE, .3f, .7f);
            event.getPlayer().sendActionBar( ChatColor.RED + "You must be level " + ChatColor.DARK_RED + requiredLevel + ChatColor.RED + " to equip that item!");
        }
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

        // Do we even need to check them?
        if (player.getLevel() >= BaseExperience.LEVEL_CAP) {
            return;
        }

        // Did they click an armor slot?
        if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {

            ItemStack itemClicked = event.getCurrentItem();
            ItemStack itemHeld = event.getCursor();

            // Sanity check, do they somehow have armor already equipped theyre not supposed to?
            if (itemClicked != null && !itemClicked.getType().equals(Material.AIR)) {
                if (materialLevelCaps.containsKey(itemClicked.getType())) {
                    if (event.getWhoClicked() instanceof Player) {
                        int cap = Math.max(materialLevelCaps.get(itemClicked.getType()), plugin.getEnchantmentManager().getItemLevel(itemClicked));
                        if (((Player) event.getWhoClicked()).getLevel() < cap) {
                            event.getWhoClicked().getWorld().dropItemNaturally(event.getWhoClicked().getLocation(), itemClicked);
                            event.getWhoClicked().getInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
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

            int requiredLevel = plugin.getEnchantmentManager().getItemLevel(itemHeld);
            if (requiredLevel < materialLevelCaps.get(itemHeld.getType())) {
                requiredLevel = materialLevelCaps.get(itemHeld.getType());
            }

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
            if (itemClicked == null) {
                return;
            }

            // Did they shift click armor?
            if (!materialLevelCaps.containsKey(itemClicked.getType())) {
                return;
            }

            // Are they a high enough level to equip it?
            int requiredLevel = plugin.getEnchantmentManager().getItemLevel(itemClicked);
            if (requiredLevel < materialLevelCaps.get(itemClicked.getType())) {
                requiredLevel = materialLevelCaps.get(itemClicked.getType());
            }

            // Does our player have the required level to interact with this item?
            if (player.getLevel() < requiredLevel) {
                player.sendActionBar( ChatColor.RED + "You must be level " + ChatColor.DARK_RED + requiredLevel + ChatColor.RED + " to equip that item!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, .3f, .7f);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler()
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerHit(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            equippedArmorSanityCheck((Player)event.getEntity());
        }
    }

}
