package io.github.devvydoo.levelingoverhaul.managers;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.util.PlayerArmorAttributes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * This manager is in charge for any stats and abilities for armor
 */
public class PlayerArmorManager implements Listener {

    private LevelingOverhaul plugin;
    private HashMap<Player, PlayerArmorAttributes> playerArmorAttributesMap;

    public PlayerArmorManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
        playerArmorAttributesMap = new HashMap<>();
        for (Player player : plugin.getServer().getOnlinePlayers())
            playerArmorAttributesMap.put(player, new PlayerArmorAttributes(plugin.getEnchantmentManager(), plugin.getCustomItemManager(), player));
    }

    public void updatePlayerArmorAttributes(Player player){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!playerArmorAttributesMap.containsKey(player))
                    playerArmorAttributesMap.put(player, new PlayerArmorAttributes(plugin.getEnchantmentManager(), plugin.getCustomItemManager(), player));
                else
                    playerArmorAttributesMap.get(player).updateAttributes();
            }
        }.runTaskLater(plugin, 1);
    }

    public PlayerArmorAttributes getPlayerArmorAttributes(Player player){
        if (playerArmorAttributesMap.containsKey(player))
            return playerArmorAttributesMap.get(player);
        return playerArmorAttributesMap.put(player, new PlayerArmorAttributes(plugin.getEnchantmentManager(), plugin.getCustomItemManager(), player));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArmorRightClick(PlayerArmorChangeEvent event){
        updatePlayerArmorAttributes(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        playerArmorAttributesMap.remove(event.getPlayer());
        playerArmorAttributesMap.put(event.getPlayer(), new PlayerArmorAttributes(plugin.getEnchantmentManager(), plugin.getCustomItemManager(), event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        playerArmorAttributesMap.remove(event.getPlayer());
    }
}
