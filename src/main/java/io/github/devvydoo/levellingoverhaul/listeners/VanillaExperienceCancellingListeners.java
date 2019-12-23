package io.github.devvydoo.levellingoverhaul.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * This class is specifically designed to cancel all events in vanilla Minecraft that spawn EXP orbs, this is because
 * we cannot prevent them from spawning
 */
public class VanillaExperienceCancellingListeners implements Listener {

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

}
