package me.devvy.leveled.listeners.progression;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.enchantments.enchants.CustomEnchantType;
import me.devvy.leveled.enchantments.enchants.CustomEnchantment;
import me.devvy.leveled.items.Rarity;
import me.devvy.leveled.player.LeveledPlayer;
import me.devvy.leveled.player.PlayerExperience;
import me.devvy.leveled.player.BaseExperience;
import me.devvy.leveled.util.FormattingHelpers;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Listeners in charge of listening for events where we should earn xp. Not to be confused with PlayerExperienceListeners
 * where we are just handling what we should do when we gain experience in general This class is the main source of
 * experience for our players
 */
public class PlayerExperienceGainListeners implements Listener {

    private final Leveled plugin;

    public PlayerExperienceGainListeners(Leveled plugin) {
        this.plugin = plugin;
    }

    /**
     * Helper method that gets double xp chance from the experienced enchant
     *
     * @param player         The player that is earning xp
     * @param doubleXpChance The original xp chance before the enchant modification
     * @return the new double xp chance
     */
    private double getDoubleXpChance(Player player, double doubleXpChance) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!tool.getType().equals(Material.AIR)) {
            for (CustomEnchantment enchantment : plugin.getEnchantmentManager().getCustomEnchantments(tool)) {
                if (enchantment.getType().equals(CustomEnchantType.EXPERIENCED)) {
                    doubleXpChance += enchantment.getLevel() / 33.;
                }
            }
        }
        return doubleXpChance;
    }


    /**
     * Listen for events where an  entity is damaged by another entity, we should check if its a player and if they
     * should be awarded xp for the kill
     *
     * @param event - The EntityDamageByEntityEvent event we are listening to
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKillEntity(EntityDeathEvent event) {

        // Did the entity have a killer?
        if (event.getEntity().getKiller() == null)
            return;

        Player player = event.getEntity().getKiller();
        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        LeveledPlayer leveledPlayer = plugin.getPlayerManager().getLeveledPlayer(player);
        LivingEntity livingEntity = event.getEntity();

        //TODO: maybe add logic for players who pvp or something
        if (livingEntity instanceof Player)
            return;

        int mobLevel = plugin.getMobManager().getMobLevel(livingEntity);

        // At this point a player has killed another entity and we can calculate their xp
        int xp = (int) (500 * mobLevel * mobLevel - (500 * mobLevel) / (10 + Math.pow(mobLevel - 1, 1.25)));

        // Some mobs have multipliers
        xp *= BaseExperience.getMobExperienceMultiplier(livingEntity.getType());

        // 5% chance for double xp :)
        double doubleXpChance = .05;

        // Check the tool in their hand to see if we should give xp
        String bonus = "";
        doubleXpChance = getDoubleXpChance(player, doubleXpChance);

        if (Math.random() < doubleXpChance) {
            bonus = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "BONUS! ";
            xp *= 2;
        }

        if (xp <= 0)
            return;

        leveledPlayer.giveExperience(xp); // Gives player exp
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, (float) .5, 1);
        plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, bonus + ChatColor.YELLOW + "+" + xp + " XP");
    }

    /**
     * Listen for when a block is broken by a player, we should see if they should be awarded xp
     *
     * @param event - The BlockBreakEvent we are listening for
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        // Never ever ever give xp if the block isn't supposed to drop
        if (!event.isDropItems())
            return;

        Player player = event.getPlayer();
        LeveledPlayer leveledPlayer = plugin.getPlayerManager().getLeveledPlayer(player);
        ItemStack tool = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();

        // Never ever ever ever ever give xp if there are no drops
        if (event.getBlock().getDrops(tool).isEmpty())
            return;

        int xpGained = 0;

        Map<CustomEnchantType, CustomEnchantment> customEnchantments = plugin.getEnchantmentManager().getCustomEnchantmentMap(tool);

        // Special case, if we mined iron ore gold ore...
        if (block.getType().equals(Material.GOLD_ORE) || block.getType().equals(Material.IRON_ORE)) {
            // If their tool has smelting touch...
            if (customEnchantments.containsKey(CustomEnchantType.SMELTING_TOUCH)) {
                event.setDropItems(false);
                int numDrop = 1;
                int fortuneLevel = tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                if (fortuneLevel > 0) {
                    numDrop += Math.floor(fortuneLevel / 1.5) + (Math.random() < .5 ? 1 : 0);
                }
                // We know we have either iron or gold, so set type to be equal to the block mined
                Material dropType = block.getType().equals(Material.IRON_ORE) ? Material.IRON_INGOT : Material.GOLD_INGOT;
                ItemStack drop = new ItemStack(dropType, numDrop);
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
                xpGained = block.getType().equals(Material.IRON_ORE) ? 1 : 3;
            }
        } else if (block.getType().equals(Material.STONE)) {
            if (customEnchantments.containsKey(CustomEnchantType.SMELTING_TOUCH)) {
                event.setDropItems(false);
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.STONE));
            }
        } else {
            xpGained = BaseExperience.getBaseExperienceFromBlock(event.getBlock());
        }

        // Did we even gain experience?
        if (xpGained <= 0) {
            return;
        }

        if (customEnchantments.containsKey(CustomEnchantType.GREEDY_MINER) && !player.isDead()) {
            double healBonus = customEnchantments.get(CustomEnchantType.GREEDY_MINER).getLevel() / 10. * player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            player.setHealth(Math.min(player.getHealth() + healBonus, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
        }

        // Never ever ever give someone xp for silk touch breaks
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {
            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
                return;
            }
        }

        // Does the player even need experience?
        if (player.getLevel() >= PlayerExperience.LEVEL_CAP) {
            return;
        }

        String xpMessage = ChatColor.BLUE + "+" + xpGained + " XP";

        // 5% chance for double xp :)
        double doubleXpChance = .05;

        // Check if we should increase chance from the tool
        doubleXpChance = getDoubleXpChance(player, doubleXpChance);

        if (Math.random() <= doubleXpChance) {
            xpGained *= 2;
            xpMessage = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "BONUS! " + ChatColor.BLUE + "+" + xpGained + " XP";
        }

        // Looks good to give them xp
        leveledPlayer.giveExperience(xpGained);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .5f, 1);
        plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, xpMessage);
    }

    /**
     * Run this before we set the exp drop to 0, we do this so we know that the furnace was meant to drop xp before
     * we changed it, we also override the xp ourselves
     *
     * @param event - the FurnaceExtractEvent we are listening for
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onSmeltExtract(FurnaceExtractEvent event) {

        // Was this event meant to drop xp?
        if (event.getExpToDrop() <= 0)
            return;

        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        LeveledPlayer leveledPlayer = plugin.getPlayerManager().getLeveledPlayer(player);

        // Does the player even need xp?
        if (player.getLevel() >= PlayerExperience.LEVEL_CAP) {
            return;
        }

        // We should be good to give xp
        int xpGained = BaseExperience.getBaseExperienceFromSmelt(event.getItemType(), event.getItemAmount());
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .5f, 1);
        leveledPlayer.giveExperience(xpGained);
        plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, ChatColor.GOLD + "+" + xpGained + " XP");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTradeWithVillager(InventoryClickEvent event){

        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
            return;

        // Trading inventory?
        if (event.getInventory().getType() != InventoryType.MERCHANT)
            return;

        // Clicking trading inventory?
        if (event.getClickedInventory() != event.getInventory())
            return;

        // Clicking result slot?
        if (event.getSlotType() != InventoryType.SlotType.RESULT)
            return;

        // Something to take?
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;

        // Don't allow shift clicks, can't award xp properly if we do that
        if (event.isShiftClick()){
            event.setCancelled(true);
            return;
        }

        Player player = (Player) event.getWhoClicked();
        LeveledPlayer leveledPlayer = plugin.getPlayerManager().getLeveledPlayer(player);

        // If the cursor is empty we have nothing to worry about, the trade should be fine
        if (event.getCursor() == null || event.getCursor().getType() == Material.AIR){

//            plugin.getGlobalItemManager().fixItem(event.getCurrentItem());

            // Give them xp based on the rarity of the item
            Rarity itemRarity = Rarity.getItemRarity(event.getCurrentItem());
            int xp = 75000 * (itemRarity.ordinal() + 1);
            leveledPlayer.giveExperience(xp);

            player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, ChatColor.DARK_GREEN + "+" + FormattingHelpers.getFormattedInteger(xp) + "XP");

        } else {

            // Something is in the cursor TODO: do the annoying work to figure out if the item can stack, for now just don't let them do it
            event.setCancelled(true);

        }


    }

    /**
     * Listen for when players earn advancements, and give them xp
     *
     * @param event - The PlayerAdvancementDoneEvent we are listening to
     */
    @EventHandler
    public void onAdvancementEarn(PlayerAdvancementDoneEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        // We don't care about recipe advancements
        if (event.getAdvancement().getKey().toString().startsWith("minecraft:recipes")) {
            return;
        }

        int xpEarned = BaseExperience.getBaseExperienceFromAdvancement(event.getAdvancement());

        // Valid advancement?
        if (xpEarned <= 0) {
            return;
        }

        Player player = event.getPlayer();
        LeveledPlayer leveledPlayer = plugin.getPlayerManager().getLeveledPlayer(player);

        // Smarty pants?
        double multiplier = 1;
        String message = ChatColor.GREEN + "Challenge Completed! " + ChatColor.LIGHT_PURPLE + "+" + xpEarned + " XP";
        try {
            multiplier += plugin.getEnchantmentManager().getEnchantLevel(player.getInventory().getLeggings(), CustomEnchantType.SMARTY_PANTS) * .15;
        } catch (NullPointerException | IllegalArgumentException ignored) {
        }
        if (multiplier > 1) {
            xpEarned *= multiplier;
            message = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "BONUS! " + message;
        }

        // Gib xp
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, .5f, 1);
        leveledPlayer.giveExperience(xpEarned);
        plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, message);
    }
}
