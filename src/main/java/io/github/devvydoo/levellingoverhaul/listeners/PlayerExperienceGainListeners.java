package io.github.devvydoo.levellingoverhaul.listeners;

import io.github.devvydoo.levellingoverhaul.util.BaseExperience;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
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

/**
 * Listeners in charge of listening for events where we should earn xp. Not to be confused with PlayerExperienceListeners
 * where we are just handling what we should do when we gain experience in general This class is the main source of
 * experience for our players
 */
public class PlayerExperienceGainListeners implements Listener {


    /**
     * Listen for events where an  entity is damaged by another entity, we should check if its a player and if they
     * should be awarded xp for the kill
     *
     * @param event - The EntityDamageByEntityEvent event we are listening to
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKillEntity(EntityDamageByEntityEvent event){

        // Was the event cancelled?
        if (event.isCancelled()){
            return;
        }

        // Was the entity living?
        if (!(event.getEntity() instanceof LivingEntity)){
            return;
        }

        LivingEntity livingEntity = (LivingEntity) event.getEntity();

        // Is the entity going to die?
        if (livingEntity.getHealth() - event.getFinalDamage() > 0){
            return;
        }

        // Are they dying to a player or an arrow?
        if (!(event.getDamager() instanceof Player || event.getDamager() instanceof Arrow)){
            return;
        }
        Player player;
        // Are they dying to a player shooting an arrow?
        if (event.getDamager() instanceof Arrow){
            Arrow arrow = (Arrow) event.getDamager();

            // If a player didn't shoot the arrow, we don't care
            if (!(arrow.getShooter() instanceof Player)){
                return;
            }

            player = (Player) arrow.getShooter();
        } else {
            // We know that we are dying straight from a player.
            player = (Player) event.getDamager();
        }


        // Does the player even need xp? i.e. are they max level
        if (player.getLevel() >= BaseExperience.LEVEL_CAP){
            player.setLevel(BaseExperience.LEVEL_CAP);
            player.setExp((float) .9999);
            return;
        }

        // At this point a player has killed another entity and we can calculate their xp
        int xp = BaseExperience.getBaseExperienceFromMob(livingEntity);
        // If we have no xp to give don't do anything
        if (xp <= 0){
            return;
        }
        player.giveExp(xp); // Gives player exp
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, (float) .5, 1);

        BaseExperience.displayActionBarText(player, ChatColor.YELLOW + "+" + xp + " XP");
    }

    /**
     * Several cases where we handle when a boss is killed by a player
     *
     * @param event - The EntityDeathEvent we are listening to
     */
    @EventHandler
    public void onBossDeath(EntityDeathEvent event){
        EntityType enemy = event.getEntity().getType();

        switch (enemy){
            case ENDER_DRAGON:
                // We are going to give all players in the end a bonus
                for (Player p: event.getEntity().getWorld().getPlayers()){
                    p.giveExp(200);
                    p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Ender Dragon" + ChatColor.YELLOW + "! +200XP");
                }
                break;
            case WITHER:
                // All players within 100 block radius from the wither get credit
                for (Player p: event.getEntity().getWorld().getPlayers()){
                    if (p.getLocation().distance(event.getEntity().getLocation()) < 100){
                        p.giveExp(175);
                        p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Wither" + ChatColor.YELLOW + "! +175XP");
                    }
                }
                break;
            case ELDER_GUARDIAN:
                // All players within 100 block radius from the guardian get credit
                for (Player p: event.getEntity().getWorld().getPlayers()){
                    if (p.getLocation().distance(event.getEntity().getLocation()) < 100){
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
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        Player player = event.getPlayer();
        int xpGained = BaseExperience.getBaseExperienceFromBlock(event.getBlock());

        // Never ever ever give someone xp for silk touch breaks
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {
            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)){
                return;
            }
        }

        // Did we even gain experience?
        if (xpGained <= 0){
            return;
        }

        // Does the player even need experience?
        if (player.getLevel() >= BaseExperience.LEVEL_CAP){
            return;
        }

        // Looks good to give them xp
        player.giveExp(xpGained);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .5f, 1);
        BaseExperience.displayActionBarText(player, ChatColor.BLUE + "+" + xpGained + " XP");
    }

    /**
     * Run this before we set the exp drop to 0, we do this so we know that the furnace was meant to drop xp before
     * we changed it, we also override the xp ourselves
     *
     * @param event - the FurnaceExtractEvent we are listening for
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onSmeltExtract(FurnaceExtractEvent event){

        // Was this event meant to drop xp?
        if (event.getExpToDrop() <= 0){
            return;
        }

        Player player = event.getPlayer();

        // Does the player even need xp?
        if (player.getLevel() >= BaseExperience.LEVEL_CAP){
            return;
        }

        // We should be good to give xp
        int xpGained = BaseExperience.getBaseExperienceFromSmelt(event.getItemType(), event.getItemAmount());
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .5f, 1);
        BaseExperience.displayActionBarText(player, ChatColor.GOLD + "+" + xpGained + " XP");
        player.giveExp(xpGained);
    }
}
