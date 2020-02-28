package io.github.devvydoo.levelingoverhaul.enchantments.gui;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.enchantments.calculator.EnchantmentCalculator;
import io.github.devvydoo.levelingoverhaul.enchantments.calculator.PotentialEnchantment;
import io.github.devvydoo.levelingoverhaul.util.BaseExperience;
import io.github.devvydoo.levelingoverhaul.util.LevelRewards;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bukkit.event.EventPriority.HIGH;
import static org.bukkit.event.EventPriority.HIGHEST;

public class EnchantingInterface implements Listener {

    private LevelingOverhaul plugin;
    private String INTERFACE_NAME = ChatColor.LIGHT_PURPLE + "Enchanting";
    private int LAPIS_SLOT = 38;
    private int BUTTON_SLOT = 42;
    private int EQUIPMENT_SLOT = 13;
    private HashMap<Player, Long> interfaceInteractCooldownPlayers = new HashMap<>();
    private ArrayList<Player> inventoryRefreshCooldownPlayers = new ArrayList<>();

    public EnchantingInterface(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    // Helper Methods

    private boolean playerOnCooldown(Player player) {
        if (inventoryRefreshCooldownPlayers.contains(player)) {
            player.sendMessage(ChatColor.RED + "Slow down!");
            return true;
        }
        if (interfaceInteractCooldownPlayers.containsKey(player)) {
            if (interfaceInteractCooldownPlayers.get(player) > System.currentTimeMillis()) {
                player.sendMessage(ChatColor.RED + "Slow down!");
                return true;
            }
            interfaceInteractCooldownPlayers.remove(player);
            return false;
        }
        interfaceInteractCooldownPlayers.put(player, System.currentTimeMillis() + 100);
        return false;
    }

    private void doUpdateInventoryTask(Player player) {
        inventoryRefreshCooldownPlayers.add(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.updateInventory();
                inventoryRefreshCooldownPlayers.remove(player);
            }
        }.runTaskLater(plugin, 5);
    }

    private ItemStack setupButton(Inventory gui, ItemStack itemStack, int level, boolean active) {

        String colorOne = active ? ChatColor.GREEN.toString() : ChatColor.RED.toString();
        String colorTwo = active ? ChatColor.DARK_GREEN.toString() : ChatColor.DARK_RED.toString();
        ItemStack paneDeco = active ? new ItemStack(Material.GREEN_STAINED_GLASS_PANE) : new ItemStack(Material.RED_STAINED_GLASS_PANE);

        gui.setItem(42 + 1, paneDeco);
        gui.setItem(42 + 9, paneDeco);
        gui.setItem(42 + 10, paneDeco);

        ItemMeta enchantButtonMeta = itemStack.getItemMeta();
        enchantButtonMeta.setDisplayName(colorOne + "Level " + colorTwo + level + colorOne + " enchant");
        ArrayList<String> buttonLore = new ArrayList<>();
        buttonLore.add("");
        buttonLore.add(ChatColor.BLUE + "Add " + colorOne + getLapisRequired(level) + ChatColor.BLUE + " Lapis Lazuli to enchant!");
        enchantButtonMeta.setLore(buttonLore);
        enchantButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(enchantButtonMeta);
        itemStack.setAmount(level);
        return itemStack;
    }


    private int getLapisRequired(int level) {
        return level / 2;
    }

    private boolean canBeEnchanted(ItemStack itemStack) {
        // If it's already enchanted we can't do anything
        if (plugin.getEnchantmentManager().getCustomEnchantments(itemStack).size() > 0 || itemStack.getEnchantments().size() > 0)
            return false;

        // Check if the material type is allowed to be enchanted
        switch (itemStack.getType()) {
            case WOODEN_SHOVEL:
            case WOODEN_AXE:
            case WOODEN_SWORD:
            case WOODEN_PICKAXE:
            case WOODEN_HOE:
            case GOLDEN_SHOVEL:
            case GOLDEN_AXE:
            case GOLDEN_PICKAXE:
            case GOLDEN_SWORD:
            case GOLDEN_HOE:
            case IRON_SHOVEL:
            case IRON_AXE:
            case IRON_SWORD:
            case IRON_PICKAXE:
            case IRON_HOE:
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
            case DIAMOND_SWORD:
            case DIAMOND_HOE:
            case DIAMOND_SHOVEL:
            case BOW:
            case CROSSBOW:
            case TRIDENT:
            case CHAINMAIL_BOOTS:
            case IRON_BOOTS:
            case GOLDEN_BOOTS:
            case LEATHER_BOOTS:
            case DIAMOND_BOOTS:
            case CHAINMAIL_LEGGINGS:
            case IRON_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case LEATHER_LEGGINGS:
            case IRON_CHESTPLATE:
            case LEATHER_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case DIAMOND_HELMET:
            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
            case GOLDEN_HELMET:
            case IRON_HELMET:
            case TURTLE_HELMET:
            case FISHING_ROD:
            case ELYTRA:
            case SHEARS:
                return true;
            default:
                return false;
        }
    }

    private ItemStack enchantItem(Player player, Block enchantTable, ItemStack item) {
        int qualityFactor = 1;

        // We need to perform a cubic search around the enchantment table to see if we have bookshelves
        masterLoop: for (int xOffset = -2; xOffset <= 2; xOffset++) {
            for (int yOffset = -2; yOffset <= 2; yOffset++) {
                for (int zOffset = -2; zOffset <= 2; zOffset++) {

                    Block block = enchantTable.getWorld().getBlockAt(enchantTable.getX() + xOffset, enchantTable.getY() + yOffset, enchantTable.getZ() + zOffset);
                    if (block.getLocation().distance(enchantTable.getLocation()) <= 1)
                        continue;

                    if (block.getType().equals(Material.BOOKSHELF)) {
                        qualityFactor++;
                        if (qualityFactor >= plugin.getEnchantmentManager().MAX_ENCHANT_QUALITY_FACTOR)
                            break masterLoop;
                    }

                }
            }
        }

        if (qualityFactor > plugin.getEnchantmentManager().MAX_ENCHANT_QUALITY_FACTOR)
            qualityFactor = plugin.getEnchantmentManager().MAX_ENCHANT_QUALITY_FACTOR;

        EnchantmentCalculator calculator = new EnchantmentCalculator(plugin.getCustomItemManager(), plugin.getEnchantmentManager(), player.getLevel(), qualityFactor, item);
        ArrayList<PotentialEnchantment> potentialEnchantments = calculator.calculateEnchantmentTypes();
        HashMap<PotentialEnchantment, Integer> enchantLevels = calculator.calculateEnchantmentLevels(potentialEnchantments);

        if (enchantLevels.isEmpty())
            System.out.println(ChatColor.RED + "[Enchanting] ERROR: Enchanting interface attempted to enchant item: " + item.getType() + " and didn't roll any enchantments!");

        for (PotentialEnchantment enchantment : enchantLevels.keySet()) {

            if (enchantment.getEnchantType() instanceof Enchantment)
                plugin.getEnchantmentManager().addEnchant(item, (Enchantment) enchantment.getEnchantType(), enchantLevels.get(enchantment));
             else if (enchantment.getEnchantType() instanceof CustomEnchantType)
                plugin.getEnchantmentManager().addEnchant(item, (CustomEnchantType) enchantment.getEnchantType(), enchantLevels.get(enchantment));
             else
                throw new IllegalStateException("PotentialEnchantment enchantType was not Enchantment or CustomEnchantType!");

        }
        return item;
    }

    private void openEnchantingInterface(Player player) {

        Block blockLookingAt = player.getTargetBlockExact(6);
        if (blockLookingAt == null || !blockLookingAt.getType().equals(Material.ENCHANTING_TABLE))
            return;

        // Create the GUI
        Inventory gui = plugin.getServer().createInventory(player, 54, INTERFACE_NAME);
        gui.setMaxStackSize(BaseExperience.LEVEL_CAP);  // Hopefully this works, didn't work in 1.8 xd
        // Make slots be black stained glass
        for (int i = 0; i < 54; i++) {
            switch (i) {
                case 38 - 1:
                case 38 + 8:
                case 38 + 9:
                    gui.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                    break;

                case 42 + 1:
                case 42 + 9:
                case 42 + 10:
                    gui.setItem(i, new ItemStack(Material.RED_STAINED_GLASS_PANE));
                    break;

                case 13 - 8:
                case 13 - 9:
                case 13 - 10:
                case 13 - 1:
                case 13 + 1:
                    gui.setItem(i, new ItemStack(Material.PURPLE_STAINED_GLASS_PANE));
                    break;

                case 13 + 9:
                    gui.setItem(i, new ItemStack(Material.ENCHANTING_TABLE));
                    break;
                default:
                    gui.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }
        }

        // Initialize our elements, we need lapis input, and the actual enchant button
        gui.setItem(EQUIPMENT_SLOT, new ItemStack(Material.AIR));  // This is our input
        gui.setItem(LAPIS_SLOT, new ItemStack(Material.AIR));  // This is our input
        ItemStack enchantButton = new ItemStack(Material.CLAY_BALL);
        gui.setItem(BUTTON_SLOT, setupButton(gui, enchantButton, player.getLevel(), false));
        player.openInventory(gui);
    }

    @EventHandler(priority = HIGH)
    private void onEnchantInterfaceClick(InventoryClickEvent event) {

        // Ignore if its not our interface
        if (!event.getView().getTitle().equals(INTERFACE_NAME))
            return;

        // Only care about players
        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();

        // On cooldown?
        if (playerOnCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        // We only allow certain clicks.
        if (!clickType.equals(ClickType.LEFT) && !clickType.equals(ClickType.RIGHT)) {
            event.setCancelled(true);
            return;
        }

        // If we don't have an inventory don't do anything
        if (event.getClickedInventory() == null)
            return;

        // Let players do whatever in their own inventories
        if (event.getClickedInventory() instanceof PlayerInventory)
            return;

        event.setCancelled(true);

        Inventory gui = event.getClickedInventory();
        ItemStack notEnoughButton = new ItemStack(Material.CLAY_BALL);
        ItemStack readyButton = new ItemStack(Material.EXPERIENCE_BOTTLE);

        int lapisBeforeClick = gui.getItem(LAPIS_SLOT) != null ? gui.getItem(LAPIS_SLOT).getAmount() : 0;

        if (event.getSlot() == LAPIS_SLOT) {

            // Allow the event if we are holding lapis
            if (event.getCursor() != null && event.getCursor().getType().equals(Material.LAPIS_LAZULI)) {

                event.setCancelled(false);
                // Check the equipment slot to see if we should update the button or not
                if (gui.getItem(EQUIPMENT_SLOT) != null && !gui.getItem(EQUIPMENT_SLOT).getType().equals(Material.AIR)) {

                    if (lapisBeforeClick + event.getCursor().getAmount() >= getLapisRequired(player.getLevel()))
                        gui.setItem(BUTTON_SLOT, setupButton(gui, readyButton, player.getLevel(), true));
                    else
                        gui.setItem(BUTTON_SLOT, setupButton(gui, notEnoughButton, player.getLevel(), false));

                }

                // Allow the event if we are holding nothing
            } else if (event.getCursor() == null || event.getCursor().getType().equals(Material.AIR)) {
                event.setCancelled(false);
                gui.setItem(BUTTON_SLOT, setupButton(gui, notEnoughButton, player.getLevel(), false));
            }

            doUpdateInventoryTask(player);

        } else if (event.getSlot() == EQUIPMENT_SLOT) {

            // Allow the event if we are holding an item that can be enchanted
            if (event.getCursor() != null && canBeEnchanted(event.getCursor())) {
                event.setCancelled(false);

                // Check the lapis to see if we should allow the enchant
                if (lapisBeforeClick >= getLapisRequired(player.getLevel()))
                    gui.setItem(BUTTON_SLOT, setupButton(gui, readyButton, player.getLevel(), true));
                else
                    gui.setItem(BUTTON_SLOT, setupButton(gui, notEnoughButton, player.getLevel(), false));

                // Allow the event if we aren't holding anything
            } else if (event.getCursor() == null || event.getCursor().getType().equals(Material.AIR)) {
                event.setCancelled(false);
                gui.setItem(BUTTON_SLOT, setupButton(gui, notEnoughButton, player.getLevel(), false));
            }

            doUpdateInventoryTask(player);

        } else {

            // In here we are clicking a button, do any necessary logic if so
            if (event.getSlot() == BUTTON_SLOT) {
                // Do we have things in the slots?
                if (gui.getItem(EQUIPMENT_SLOT) != null && gui.getItem(LAPIS_SLOT) != null && gui.getItem(LAPIS_SLOT).getType().equals(Material.LAPIS_LAZULI)) {
                    // Do we have enough lapis?
                    if (gui.getItem(LAPIS_SLOT).getAmount() >= getLapisRequired(player.getLevel())) {
                        // Do we have an enchantable item?
                        if (canBeEnchanted(gui.getItem(EQUIPMENT_SLOT))) {
                            ItemStack newItem = enchantItem(player, player.getTargetBlockExact(10), gui.getItem(EQUIPMENT_SLOT)); // Enchant
                            if (newItem.getEnchantments().isEmpty() && plugin.getEnchantmentManager().getCustomEnchantments(newItem).isEmpty()){ return; }
                            plugin.getEnchantmentManager().setItemLevel(newItem, player.getLevel());
                            if (newItem.getEnchantmentLevel(Enchantment.DURABILITY) < 1) { newItem.addEnchantment(Enchantment.DURABILITY, 1); }
                            gui.setItem(EQUIPMENT_SLOT, newItem);

                            if (lapisBeforeClick <= getLapisRequired(player.getLevel()))
                                gui.setItem(LAPIS_SLOT, new ItemStack(Material.AIR));
                             else
                                gui.getItem(LAPIS_SLOT).setAmount(lapisBeforeClick - getLapisRequired(player.getLevel()));

                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                            doUpdateInventoryTask(player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = HIGHEST)
    public void onEnchantTableInteract(PlayerInteractEvent event) {

        // Are we right clicking a block?
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        Block block = event.getClickedBlock();

        // Are we right clicking an enchanting table?
        if (block == null || !block.getType().equals(Material.ENCHANTING_TABLE))
            return;

        // Is enchanting allowed?
        if (event.getPlayer().getLevel() < LevelRewards.ENCHANTING_UNLOCK)
            return;

        // Give them enchanting advancement
        event.getPlayer().getAdvancementProgress(plugin.getEnchantAdvancement()).awardCriteria("enchanted_item");

        // Open the GUI
        event.setCancelled(true);
        openEnchantingInterface(event.getPlayer());
    }

    @EventHandler
    public void onEnchantTableClose(InventoryCloseEvent event) {

        // Was our enchant GUI closed?
        if (!event.getView().getTitle().equals(INTERFACE_NAME))
            return;

        Inventory gui = event.getView().getTopInventory();

        ItemStack lapis = gui.getItem(LAPIS_SLOT);
        ItemStack equipment = gui.getItem(EQUIPMENT_SLOT);

        ArrayList<ItemStack> overflow = new ArrayList<>();

        if (lapis != null)
            overflow.addAll(event.getPlayer().getInventory().addItem(lapis).values());

        if (equipment != null)
            overflow.addAll(event.getPlayer().getInventory().addItem(equipment).values());

        if (overflow.size() <= 0)
            return;

        for (ItemStack item : overflow)
            event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), item);
    }

}
