package me.devvy.leveled.player;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.devvy.leveled.Leveled;
import me.devvy.leveled.events.EntityDamagedByMiscEvent;
import me.devvy.leveled.player.abilities.AbilityBoundlessRockets;
import me.devvy.leveled.player.abilities.AbilityExpertCrafter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * This manager is in charge for any stats and abilities for players
 */
public class LeveledPlayerManager implements Listener {

    private final Leveled plugin;
    private final HashMap<Player, LeveledPlayer> playerArmorAttributesMap;

    public LeveledPlayerManager(Leveled plugin) {
        this.plugin = plugin;
        playerArmorAttributesMap = new HashMap<>();
        for (Player player : plugin.getServer().getOnlinePlayers())
            playerArmorAttributesMap.put(player, new LeveledPlayer(plugin.getCustomItemManager(), player));

        // Register ability classes
        plugin.getServer().getPluginManager().registerEvents(new AbilityExpertCrafter(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new AbilityBoundlessRockets(), plugin);
    }

    public void updateLeveledPlayerAttributes(Player player){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!playerArmorAttributesMap.containsKey(player))
                    playerArmorAttributesMap.put(player, new LeveledPlayer(plugin.getCustomItemManager(), player));
                else
                    playerArmorAttributesMap.get(player).updateAttributes();
            }
        }.runTaskLater(plugin, 1);
    }

    public LeveledPlayer getLeveledPlayer(Player player){
        if (playerArmorAttributesMap.containsKey(player))
            return playerArmorAttributesMap.get(player);
        return playerArmorAttributesMap.put(player, new LeveledPlayer(plugin.getCustomItemManager(), player));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArmorRightClick(PlayerArmorChangeEvent event){
        updateLeveledPlayerAttributes(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        playerArmorAttributesMap.remove(event.getPlayer());
        playerArmorAttributesMap.put(event.getPlayer(), new LeveledPlayer(plugin.getCustomItemManager(), event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        playerArmorAttributesMap.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerHitByMiscSource(EntityDamagedByMiscEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;

        LeveledPlayer player = getLeveledPlayer((Player)event.getEntity());

        // Consider certain cases where we take damage and multiply damage accordingly
        switch (event.getCause()) {


            case FIRE_TICK:
            case LAVA:
            case LIGHTNING:
            case FIRE:
            case HOT_FLOOR:
                event.multiplyDamage((float) player.getFireResist());
                break;

            case PROJECTILE:
                event.multiplyDamage((float)player.getEnvResist());
                event.multiplyDamage((float)player.getProjResist());
                break;

            case BLOCK_EXPLOSION:
                event.multiplyDamage((float)player.getExplosionResist());
                break;
        }

    }
}
