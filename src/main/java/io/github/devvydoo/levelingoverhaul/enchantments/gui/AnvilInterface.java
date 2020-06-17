package io.github.devvydoo.levelingoverhaul.enchantments.gui;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class AnvilInterface implements Listener {

    private final LevelingOverhaul plugin;
    private final String ANVIL_INTERFACE_NAME = ChatColor.DARK_GRAY + "Anvil";
    private final String GRINDSTONE_INTERFACE_NAME = ChatColor.DARK_GRAY + "Grindstone";
    private final int ANVIL_INPUT_SLOT = 20;
    private final int ANVIL_OUTPUT_SLOT = 24;

    public AnvilInterface(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    private void forceUpdateInventory(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.updateInventory();
            }
        }.runTaskLater(plugin, 2);
    }

    private ItemStack setItemLore(ItemStack itemStack, String itemTitle) {

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            throw new IllegalArgumentException("Item Meta was null");
        }
        meta.setDisplayName(itemTitle);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private int getMaterialRefundAmount(ItemStack item, String viewTitle) throws IllegalArgumentException {
        if (viewTitle.equals(GRINDSTONE_INTERFACE_NAME)) {
            int itemLevel = plugin.getEnchantmentManager().getItemLevel(item);
            return itemLevel / 3;
        }
        switch (item.getType()) {
            case DIAMOND_CHESTPLATE:
            case IRON_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case LEATHER_CHESTPLATE:
                return 5;

            case DIAMOND_LEGGINGS:
            case IRON_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case LEATHER_LEGGINGS:
                return 4;

            case DIAMOND_BOOTS:
            case IRON_BOOTS:
            case GOLDEN_BOOTS:
            case CHAINMAIL_BOOTS:
            case LEATHER_BOOTS:

            case DIAMOND_HELMET:
            case IRON_HELMET:
            case GOLDEN_HELMET:
            case CHAINMAIL_HELMET:
            case LEATHER_HELMET:

            case DIAMOND_PICKAXE:
            case IRON_PICKAXE:
            case GOLDEN_PICKAXE:
            case STONE_PICKAXE:
            case WOODEN_PICKAXE:
            case IRON_AXE:
            case DIAMOND_AXE:
            case GOLDEN_AXE:
            case STONE_AXE:
            case WOODEN_AXE:
                return 2;

            case DIAMOND_HOE:
            case IRON_HOE:
            case GOLDEN_HOE:
            case STONE_HOE:
            case WOODEN_HOE:
            case IRON_SWORD:
            case GOLDEN_SWORD:
            case STONE_SWORD:
            case WOODEN_SWORD:
            case DIAMOND_SWORD:

            case DIAMOND_SHOVEL:
            case IRON_SHOVEL:
            case GOLDEN_SHOVEL:
            case STONE_SHOVEL:
            case WOODEN_SHOVEL:
                return 1;
        }
        throw new IllegalArgumentException("Invalid material " + item.getType());
    }

    private Material getMaterialRefundType(ItemStack item, String viewTitle) throws IllegalArgumentException {
        if (viewTitle.equals(GRINDSTONE_INTERFACE_NAME)) {
            return Material.LAPIS_LAZULI;
        }
        switch (item.getType()) {
            case DIAMOND_CHESTPLATE:
            case DIAMOND_HELMET:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
            case DIAMOND_HOE:
            case DIAMOND_SWORD:
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
            case DIAMOND_SHOVEL:
                return Material.DIAMOND;
            case IRON_CHESTPLATE:
            case IRON_HELMET:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case IRON_HOE:
            case IRON_SWORD:
            case IRON_PICKAXE:
            case IRON_AXE:
            case IRON_SHOVEL:
                return Material.IRON_INGOT;
            case GOLDEN_CHESTPLATE:
            case GOLDEN_HELMET:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
            case GOLDEN_HOE:
            case GOLDEN_SWORD:
            case GOLDEN_PICKAXE:
            case GOLDEN_AXE:
            case GOLDEN_SHOVEL:
                return Material.GOLD_INGOT;
            case STONE_HOE:
            case STONE_SWORD:
            case STONE_PICKAXE:
            case STONE_AXE:
            case STONE_SHOVEL:
                return Material.COBBLESTONE;
            case WOODEN_HOE:
            case WOODEN_SWORD:
            case WOODEN_PICKAXE:
            case WOODEN_AXE:
            case WOODEN_SHOVEL:
                return Material.OAK_PLANKS;
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
                return Material.IRON_BARS;
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return Material.LEATHER;

        }
        throw new IllegalArgumentException("Invalid material " + item.getType());
    }

    private void openAnvilInterface(Player player) {
        Inventory gui = plugin.getServer().createInventory(player, 54, ANVIL_INTERFACE_NAME);
        for (int i = 0; i < 54; i++) {
            switch (i) {
                case 10:
                case 11:
                case 19:
                    gui.setItem(i, setItemLore(new ItemStack(Material.RED_STAINED_GLASS_PANE), " "));
                    break;
                case 15:
                case 16:
                case 25:
                    gui.setItem(i, setItemLore(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), " "));
                    break;
                default:
                    gui.setItem(i, setItemLore(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
            }
        }
        gui.setItem(ANVIL_INPUT_SLOT, new ItemStack(Material.AIR));
        gui.setItem(ANVIL_INPUT_SLOT + 9, setItemLore(new ItemStack(Material.ANVIL), ChatColor.RED + "Scrap Equipment"));
        gui.setItem(ANVIL_OUTPUT_SLOT, setItemLore(new ItemStack(Material.CLAY_BALL), ChatColor.RED + "Put an enchanted tool in the left slot to refund materials!"));
        gui.setItem(ANVIL_OUTPUT_SLOT + 9, setItemLore(new ItemStack(Material.CRAFTING_TABLE), ChatColor.GREEN + "Materials Refunded"));
        player.openInventory(gui);
    }

    private void openGrindstoneInterface(Player player) {
        Inventory gui = plugin.getServer().createInventory(player, 54, GRINDSTONE_INTERFACE_NAME);
        for (int i = 0; i < 54; i++) {
            switch (i) {
                case 10:
                case 11:
                case 19:
                    gui.setItem(i, setItemLore(new ItemStack(Material.RED_STAINED_GLASS_PANE), " "));
                    break;
                case 15:
                case 16:
                case 25:
                    gui.setItem(i, setItemLore(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), " "));
                    break;
                default:
                    gui.setItem(i, setItemLore(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
            }
        }
        gui.setItem(ANVIL_INPUT_SLOT, new ItemStack(Material.AIR));
        gui.setItem(ANVIL_INPUT_SLOT + 9, setItemLore(new ItemStack(Material.GRINDSTONE), ChatColor.RED + "Scrap Equipment"));
        gui.setItem(ANVIL_OUTPUT_SLOT, setItemLore(new ItemStack(Material.CLAY_BALL), ChatColor.RED + "Put an enchanted tool in the left slot to refund materials!"));
        gui.setItem(ANVIL_OUTPUT_SLOT + 9, setItemLore(new ItemStack(Material.CRAFTING_TABLE), ChatColor.GREEN + "Materials Refunded"));
        player.openInventory(gui);
    }


    @EventHandler()
    public void onAnvilInteract(PlayerInteractEvent event) {

        // Did the player right click?
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Block block = event.getClickedBlock();

        // Did the player right click an anvil?
        if (!block.getType().equals(Material.ANVIL) && !block.getType().equals(Material.CHIPPED_ANVIL) && !block.getType().equals(Material.DAMAGED_ANVIL) && !block.getType().equals(Material.GRINDSTONE)) {
            return;
        }

        event.setCancelled(true);

        // Open the interface
        if (block.getType().equals(Material.ANVIL) || block.getType().equals(Material.CHIPPED_ANVIL) || block.getType().equals(Material.DAMAGED_ANVIL)) {
            openAnvilInterface(event.getPlayer());
        }
        if (block.getType().equals(Material.GRINDSTONE)) {
            openGrindstoneInterface(event.getPlayer());
        }
    }

    @EventHandler()
    public void onAnvilInterfaceClick(InventoryClickEvent event) {

        // Do we have the anvil interface open?
        if (!event.getView().getTitle().equals(ANVIL_INTERFACE_NAME) && !event.getView().getTitle().equals(GRINDSTONE_INTERFACE_NAME)) {
            return;
        }

        // Never allow shift clicks in custom guis
        if (event.getClick().isShiftClick()){
            event.setCancelled(true);
            return;
        }

        // Don't allow clay to be clicked
        if (event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.CLAY_BALL)){
            forceUpdateInventory((Player)event.getWhoClicked());
            event.setCancelled(true);
            return;
        }

        // Let them interact with their own inventory as they please
        if (event.getClickedInventory() instanceof PlayerInventory)
            return;

        // Cancel the event no matter what
        event.setCancelled(true);

        // Double clicks do weird things
        if (event.getClick().equals(ClickType.DOUBLE_CLICK))
            return;

        // If the output was clicked, and there's nothing in the input, we shouldn't do anything
        if (event.getSlot() == ANVIL_OUTPUT_SLOT && (event.getView().getItem(ANVIL_INPUT_SLOT) == null || event.getView().getItem(ANVIL_INPUT_SLOT).getType().equals(Material.AIR)))
            return;

        // In the case that we clicked the input slot, don't do anything
        if (!(event.getClickedInventory() instanceof PlayerInventory) && event.getSlot() == ANVIL_INPUT_SLOT) {
            // Check if we should update the output
            if (!event.getCursor().getType().equals(Material.AIR)) {
                ItemStack cursor = event.getCursor();
                if (cursor.getEnchantments().size() != 0 || plugin.getEnchantmentManager().getCustomEnchantments(cursor).size() != 0) {
                    try {
                        String viewTitle = event.getView().getTitle();
                        ItemStack item = new ItemStack(getMaterialRefundType(cursor, viewTitle), getMaterialRefundAmount(cursor, viewTitle));
                        plugin.getGlobalItemManager().fixItem(item);
                        event.getClickedInventory().setItem(ANVIL_OUTPUT_SLOT, item);
                    } catch (IllegalArgumentException e) {
                        // we were given an illegal item, no need to do anything
                        System.out.println(ChatColor.YELLOW + "[Anvils] Illegal Argument " + cursor.getType() + ", this may be an error as it was enchanted.");
                    }
                }
                // If there was nothing in the cursor just clear the output
            } else {
                ItemStack na = setItemLore(new ItemStack(Material.CLAY_BALL), ChatColor.RED + "Put an enchanted tool in the left slot to refund materials!");
                event.getClickedInventory().setItem(ANVIL_OUTPUT_SLOT, na);
            }
            event.setCancelled(false);
            forceUpdateInventory((Player) event.getWhoClicked());
            return;
        }
        // In the case that we clicked the output slot, check if we have an item, let them grab it, and clear the input
        else if (!(event.getClickedInventory() instanceof PlayerInventory) && event.getSlot() == ANVIL_OUTPUT_SLOT) {
            if (event.getCursor() == null || event.getCursor().getType().equals(Material.AIR)) {
                if (event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)) {
                    event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, .8f, 1);
                    event.setCancelled(false);
                    event.getClickedInventory().setItem(ANVIL_INPUT_SLOT, new ItemStack(Material.AIR));
                    forceUpdateInventory((Player) event.getWhoClicked());
                    return;
                }
            }
        }
        // In the case that we click our inventory and it wasn't a shift click, don't cancel it
        else if (!event.getClick().equals(ClickType.SHIFT_LEFT) && !event.getClick().equals(ClickType.SHIFT_RIGHT) && event.getClickedInventory() instanceof PlayerInventory) {
            event.setCancelled(false);
        }

    }

    @EventHandler
    public void onAnvilInterfaceClose(InventoryCloseEvent event) {

        // Was our anvil interface open?
        if (!event.getView().getTitle().equals(ANVIL_INTERFACE_NAME) && !event.getView().getTitle().equals(GRINDSTONE_INTERFACE_NAME)) {
            return;
        }

        // Is there an item in the input slot?
        if (event.getInventory().getItem(ANVIL_INPUT_SLOT) != null && !event.getInventory().getItem(ANVIL_INPUT_SLOT).getType().equals(Material.AIR)) {
            // Give them the item, just in case their inventory was full keep track of the overflow
            Collection<ItemStack> overflow = event.getPlayer().getInventory().addItem(event.getInventory().getItem(ANVIL_INPUT_SLOT)).values();
            // If there was overflow, drop the item
            if (overflow.size() != 0) {
                overflow.forEach(itemStack -> event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), itemStack));
            }
        }

    }
}
