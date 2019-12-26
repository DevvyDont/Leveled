package io.github.devvydoo.levellingoverhaul.listeners;

import io.github.devvydoo.levellingoverhaul.util.BaseExperience;
import io.github.devvydoo.levellingoverhaul.util.LevelRewards;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalListeners implements Listener {

    private Location findFreeSpot(Location location){
        if (location.getWorld() == null){
            return location;
        }

        // Search all the blocks in this column at this x, y coord and see if there is an ender portal tp block
        for (int i = 0; i < location.getWorld().getHighestBlockYAt(location); i++){
            if (location.getWorld().getBlockAt(location.getBlockX(), i, location.getBlockZ()).getType().equals(Material.END_PORTAL)){
                double xOffset = Math.random() * 2 - 1;
                double zOffset = Math.random() * 2 - 1;
                return findFreeSpot(location.add(xOffset, 0, zOffset));
            }
        }
        // If we get to this point we are safe to teleport
        return location;
    }

    @EventHandler
    public void onNetherTeleport(PlayerPortalEvent event){

        if (event.getTo() == null){
            return;
        }

        if (event.getTo().getWorld() == null){
            return;
        }

        Player player = event.getPlayer();

        if (event.getTo().getWorld().getEnvironment().equals(World.Environment.NETHER)){

            if (player.getLevel() < LevelRewards.NETHER_UNLOCK){
                event.setCancelled(true);
                BaseExperience.displayActionBarText(event.getPlayer(), ChatColor.RED + "You must be level " + ChatColor.DARK_RED + LevelRewards.NETHER_UNLOCK + ChatColor.RED + " to use this portal!");
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_PORTAL_TRAVEL, .3f, .7f);
            }

        } else if (event.getTo().getWorld().getEnvironment().equals(World.Environment.THE_END)){

            if (player.getLevel() < LevelRewards.THE_END_UNLOCK){
                event.setCancelled(true);
                BaseExperience.displayActionBarText(event.getPlayer(), ChatColor.RED + "You must be level " + ChatColor.DARK_RED + LevelRewards.THE_END_UNLOCK + ChatColor.RED + " to use this portal!");
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, .3f, .7f);
                event.getPlayer().teleport(findFreeSpot(player.getLocation().add(0, 1, 0)));
            }

        }

    }

}
