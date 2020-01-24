package io.github.devvydoo.levelingoverhaul.listeners.progression;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantment;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantments;
import io.github.devvydoo.levelingoverhaul.util.BaseExperience;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listeners in charge of listening for events where we should earn xp. Not to be confused with PlayerExperienceListeners
 * where we are just handling what we should do when we gain experience in general This class is the main source of
 * experience for our players
 */
public class PlayerExperienceGainListeners implements Listener {

    private LevelingOverhaul plugin;

    public PlayerExperienceGainListeners(LevelingOverhaul plugin) {
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
            for (CustomEnchantment enchantment : CustomEnchantments.getCustomEnchantments(tool)) {
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
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKillEntity(EntityDeathEvent event) {

        // Did the entity have a killer?
        if (event.getEntity().getKiller() == null){ return; }

        Player player = event.getEntity().getKiller();
        LivingEntity livingEntity = event.getEntity();

        // Does the player even need xp? i.e. are they max level
        if (player.getLevel() >= BaseExperience.LEVEL_CAP) {
            player.setLevel(BaseExperience.LEVEL_CAP);
            player.setExp((float) .9999);
            return;
        }

        // At this point a player has killed another entity and we can calculate their xp
        int xp = BaseExperience.getBaseExperienceFromMob(livingEntity);

        double extraLevelXp = 0;
        if (!(livingEntity instanceof Player)) {
            extraLevelXp = this.plugin.getMobManager().getMobLevel(livingEntity) / (Math.random() * 20 + 11);
        }
        xp += (int) extraLevelXp;

        // 5% chance for double xp :)
        double doubleXpChance = .05;

        // Check the tool in their hand to see if we should give xp
        String bonus = "";
        doubleXpChance = getDoubleXpChance(player, doubleXpChance);

        if (Math.random() < doubleXpChance) {
            bonus = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "BONUS! ";
            xp *= 2;
        }

        // If we have no xp to give don't do anything
        if (!(livingEntity instanceof Player)) {
            int mobLevel = plugin.getMobManager().getMobLevel(livingEntity);
            if (mobLevel < player.getLevel()) {
                xp *= (1 - .04 * (player.getLevel() - mobLevel));
            }
        }

        if (xp <= 0) {
            return;
        }
        player.giveExp(xp); // Gives player exp
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, (float) .5, 1);

        plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, bonus + ChatColor.YELLOW + "+" + xp + " XP");
    }

    /**
     * Several cases where we handle when a boss is killed by a player
     *
     * @param event - The EntityDeathEvent we are listening to
     */
    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        EntityType enemy = event.getEntity().getType();

        switch (enemy) {
            case ENDER_DRAGON:
                // We are going to give all players in the end a bonus
                for (Player p : event.getEntity().getWorld().getPlayers()) {
                    if (p.getLevel() == BaseExperience.LEVEL_CAP) {
                        continue;
                    }
                    p.giveExp(200);
                    p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Ender Dragon" + ChatColor.YELLOW + "! +200XP");
                }
                break;
            case WITHER:
                // All players within 100 block radius from the wither get credit
                for (Player p : event.getEntity().getWorld().getPlayers()) {
                    if (p.getLocation().distance(event.getEntity().getLocation()) < 100) {
                        if (p.getLevel() == BaseExperience.LEVEL_CAP) {
                            continue;
                        }
                        p.giveExp(175);
                        p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Wither" + ChatColor.YELLOW + "! +175XP");
                    }
                }
                break;
            case ELDER_GUARDIAN:
                // All players within 100 block radius from the guardian get credit
                for (Player p : event.getEntity().getWorld().getPlayers()) {
                    if (p.getLocation().distance(event.getEntity().getLocation()) < 100) {
                        if (p.getLevel() == BaseExperience.LEVEL_CAP) {
                            continue;
                        }
                        p.giveExp(120);
                        p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Elder Guardian" + ChatColor.YELLOW + "! +120XP");
                    }
                }
                break;
        }
    }

    /**
     * Listen for when a block is broken by a player, we should see if they should be awarded xp
     *
     * @param event - The BlockBreakEvent we are listening for
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        // Never ever ever give xp if the block isn't supposed to drop
        if (!event.isDropItems()) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        ItemStack tool = player.getInventory().getItemInMainHand();
        int xpGained = 0;

        // Special case, if we mined iron ore gold ore...
        if (block.getType().equals(Material.GOLD_ORE) || block.getType().equals(Material.IRON_ORE)) {
            // If their tool has smelting touch...
            if (CustomEnchantments.hasEnchant(tool, CustomEnchantType.SMELTING_TOUCH)) {
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
            if (CustomEnchantments.hasEnchant(tool, CustomEnchantType.SMELTING_TOUCH)) {
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

        // Never ever ever give someone xp for silk touch breaks
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {
            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
                return;
            }
        }

        // Does the player even need experience?
        if (player.getLevel() >= BaseExperience.LEVEL_CAP) {
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
        player.giveExp(xpGained);
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
        if (event.getExpToDrop() <= 0) {
            return;
        }

        Player player = event.getPlayer();

        // Does the player even need xp?
        if (player.getLevel() >= BaseExperience.LEVEL_CAP) {
            return;
        }

        // We should be good to give xp
        int xpGained = BaseExperience.getBaseExperienceFromSmelt(event.getItemType(), event.getItemAmount());
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .5f, 1);
        player.giveExp(xpGained);
        plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, ChatColor.GOLD + "+" + xpGained + " XP");
    }

    /**
     * Listen for when players earn advancements, and give them xp
     *
     * @param event - The PlayerAdvancementDoneEvent we are listening to
     */
    @EventHandler
    public void onAdvancementEarn(PlayerAdvancementDoneEvent event) {

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

        // Is the player at the level cap?
        if (player.getLevel() >= BaseExperience.LEVEL_CAP) {
            return;
        }

        // Smarty pants?
        double multiplier = 1;
        String message = ChatColor.GREEN + "Challenge Completed! " + ChatColor.LIGHT_PURPLE + "+" + xpEarned + " XP";
        try {
            multiplier += CustomEnchantments.getEnchantLevel(player.getInventory().getLeggings(), CustomEnchantType.SMARTY_PANTS) * .15;
        } catch (NullPointerException | IllegalArgumentException ignored) {
        }
        if (multiplier > 1) {
            xpEarned *= multiplier;
            message = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "BONUS! " + message;
        }

        // Gib xp
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, .5f, 1);
        player.giveExp(xpEarned);
        plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, message);
    }
}
