package io.github.devvydoo.levellingoverhaul.listeners;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;


/**
 * A class responsible for how we should handle experience gain for players. In general, we want to be completely in
 * charge of player experience, so we should cancel all vanilla experience gains
 */
public class PlayerExperienceListeners implements Listener {

    private LevellingOverhaul plugin;

    public PlayerExperienceListeners(LevellingOverhaul plugin){
        this.plugin = plugin;
    }

    /**
     * Every time an entity is spawned, if it's an exp orb we should cancel it
     *
     * @param event - The EntitySpawnEvent we are listening for
     */
    @EventHandler
    public void onExpOrbSpawn(EntitySpawnEvent event){

        // Is the entity being spawned an exp orb?
        if (event.getEntity() instanceof ExperienceOrb){
            event.setCancelled(true);  // Cancel it
        }
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event){

        // If we are gaining exp normally don't do anything TODO: Make it so action bar displays xp gained
        if (event.getAmount() > 0){
            return;
        }

        // At this point we are losing exp, we don't want that set it to 0
        event.setAmount(0);
    }

    /**
     * Every time a player's level changes, we want to make sure
     *
     * @param event - the PlayerLevelChangeEvent we are listening to
     */
    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event){

        // First we want to make sure we are never going down in levels for now, our xp system doesn't go backwards
        if (event.getNewLevel() < event.getOldLevel()){
            event.getPlayer().setLevel(event.getOldLevel());  // Simply sets their level back to what it was
            event.getPlayer().setExp(0);  // This sets their progress to their current level to 0%
            return;
        }

        // At this point we are levelling up, let's tell the server
        this.plugin.getServer().broadcastMessage(ChatColor.RED + event.getPlayer().getDisplayName() + ChatColor.GOLD + " is now level " + ChatColor.RED + event.getNewLevel());
    }
}
