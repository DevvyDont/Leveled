package io.github.devvydoo.levelingoverhaul.listeners.monitors;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * A class responsible for handling player joining
 */
public class PlayerJoinListeners implements Listener {

    public PlayerJoinListeners(LevelingOverhaul plugin) {
        for (Player p: plugin.getServer().getOnlinePlayers()){
            if (p.getLevel() < 1) { p.setLevel(1); }  // Sanity check, don't let players be level 0
        }
    }

    /**
     * Listen for first time joiners, if they joined for the first time set their level to 1
     *
     * @param event - The PlayerJoinEvent we are listening for
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFirstJoin(PlayerJoinEvent event) {

        // Is the player a newcomer?  TODO: Make the messages config-able
        if (!event.getPlayer().hasPlayedBefore()) {
            event.getPlayer().setLevel(1);  // Set their level to 1
            event.setJoinMessage(ChatColor.LIGHT_PURPLE + event.getPlayer().getDisplayName() +
                    ChatColor.GOLD + " has joined for the first time!");
            event.getPlayer().setHealth(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            event.getPlayer().setSaturation(20);
            event.getPlayer().setFoodLevel(20);
        } else {
            if (event.getPlayer().getLevel() < 1) { event.getPlayer().setLevel(1); }  // Sanity check, level 0 makes weird things happen
            event.setJoinMessage(ChatColor.LIGHT_PURPLE + event.getPlayer().getDisplayName() +
                    " [Level " + event.getPlayer().getLevel() + "]" + ChatColor.GREEN + " has joined!");
        }
    }

}
