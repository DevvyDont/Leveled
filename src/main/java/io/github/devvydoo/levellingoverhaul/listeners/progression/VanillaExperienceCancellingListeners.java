package io.github.devvydoo.levellingoverhaul.listeners.progression;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import io.github.devvydoo.levellingoverhaul.tasks.XPOrbKillerTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * This class is specifically designed to cancel all events in vanilla Minecraft that spawn EXP orbs, this is because
 * we cannot prevent them from spawning
 */
public class VanillaExperienceCancellingListeners implements Listener {

    private LevellingOverhaul plugin;
    private XPOrbKillerTask xpOrbKillerTask = null;

    public VanillaExperienceCancellingListeners(LevellingOverhaul plugin){
        this.plugin = plugin;
    }

    /**
     * Listen for all events where an entity dies and don't let them drop xp
     *
     * @param event - The EntityDeathEvent
     */
    @EventHandler
    public void onMobDeath(EntityDeathEvent event){
        event.setDroppedExp(0);  // Always make sure we drop 0 exp
    }

    /**
     * Listen for all events where a block is broken and make sure no exp drops
     *
     * @param event - The BlockBreakEvent we are listening to
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        event.setExpToDrop(0);  // Always make sure we drop 0 exp
    }

    /**
     * Listen for events where we are extracting something from a furnace
     *
     * @param event - The FurnaceExtractEvent we are listening to
     */
    @EventHandler
    public void onCook(FurnaceExtractEvent event){
        event.setExpToDrop(0);  // Always make sure we drop 0 exp
    }

    /**
     * Listen for events where we are breeding animals
     *
     * @param event - The EntityBreedEvent we are listening to
     */
    @EventHandler
    public void onBreed(EntityBreedEvent event){
        event.setExperience(0);  // Always make sure we drop 0 exp
    }

    /**
     * Listen for events where we fish
     *
     * @param event - The PlayerFishEvent to listen to
     */
    @EventHandler
    public void onFishCatch(PlayerFishEvent event){
        event.setExpToDrop(0);
    }

    @EventHandler
    public void onTrade(InventoryClickEvent event){

        if (!(event.getClickedInventory() instanceof MerchantInventory)){
            return;
        }

        if (!(event.getSlotType().equals(InventoryType.SlotType.RESULT))){
            return;
        }

        if (event.getCurrentItem() == null){
            return;
        }

        if (event.getCurrentItem().getType().equals(Material.AIR)){
            return;
        }

        XPOrbKillerTask task = new XPOrbKillerTask(event.getWhoClicked().getWorld(), 10);
        task.runTaskTimer(this.plugin, 1, 1);
    }

    /**
     * A hacky way to not make the enderdragon drop xp orbs, there is literally no other way to do this for now
     * We are going to make a task that checks all the entities every 1/2 second and thanos snaps XP orbs for 30 sec
     * @param event - the EntityDeathEvent we are listening to
     */
    @EventHandler
    public void EnderDragonXPKiller(EntityDeathEvent event){

        // Did an ender dragon die?
        if (!event.getEntity().getType().equals(EntityType.ENDER_DRAGON)){
            return;
        }

        // Get the world
        World dragonsWorld = event.getEntity().getWorld();
        // For now we are only going to worry about the end
        if (!dragonsWorld.getEnvironment().equals(World.Environment.THE_END)){
            return;
        }

        // Kill xp orbs
        xpOrbKillerTask = new XPOrbKillerTask(dragonsWorld, 170);
        xpOrbKillerTask.runTaskTimer(this.plugin, 1, 3);
    }

}