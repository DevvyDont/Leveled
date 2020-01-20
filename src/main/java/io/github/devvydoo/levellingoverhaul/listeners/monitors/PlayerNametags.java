package io.github.devvydoo.levellingoverhaul.listeners.monitors;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class PlayerNametags implements Listener {

    private static String HEALTHY_HP_COLOR = ChatColor.GREEN.toString();
    private static String DAMAGED_HP_COLOR = ChatColor.YELLOW.toString();
    private static String HURT_HP_COLOR = ChatColor.GOLD.toString();
    private static String CRITICAL_HP_COLOR = ChatColor.RED.toString();
    private static String DEAD_HP_COLOR = ChatColor.DARK_GRAY.toString();
    private LevellingOverhaul plugin;

    public PlayerNametags(LevellingOverhaul plugin) {
        this.plugin = plugin;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            updatePlayerScoreboard(p, p.getLevel(), p.getHealth());
        }
    }

    public static String getChatColorFromHealth(double hp) {
        if (hp <= 0) {
            return DEAD_HP_COLOR;
        } else if (hp <= 5) {
            return CRITICAL_HP_COLOR;
        } else if (hp <= 10) {
            return HURT_HP_COLOR;
        } else if (hp <= 15) {
            return DAMAGED_HP_COLOR;
        } else {
            return HEALTHY_HP_COLOR;
        }
    }

    private String getNametagString(String name, int level, double hp) {
        return ChatColor.GRAY + "" + ChatColor.BOLD + "Lv. " + level + " " + ChatColor.DARK_GREEN + name + ChatColor.DARK_RED + " ❤" + getChatColorFromHealth(hp) + (int) hp;
    }

    private void updatePlayerScoreboard(Player player, int level, double hp) {
        player.setPlayerListName(getNametagString(player.getDisplayName(), level, hp >= 0 ? hp : 0));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayerScoreboard(event.getPlayer(), event.getPlayer().getLevel(), event.getPlayer().getHealth());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            updatePlayerScoreboard(player, player.getLevel(), player.getHealth() - event.getFinalDamage());

            // If the player is dead, try again in 5 seconds, otherwise update the scoreboard
            if (player.getHealth() - event.getFinalDamage() <= 0) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.getHealth() >= 0) {
                            updatePlayerScoreboard(player, player.getLevel(), player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                        }
                    }
                }.runTaskLater(this.plugin, 20 * 5);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLevelChange(PlayerLevelChangeEvent event) {
        if (event.getNewLevel() < event.getOldLevel()) {
            return;
        }
        updatePlayerScoreboard(event.getPlayer(), event.getNewLevel(), event.getPlayer().getHealth());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHealthRegen(EntityRegainHealthEvent event) {

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            double hpToDisplay = player.getHealth() + event.getAmount();
            if (hpToDisplay >= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                hpToDisplay = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            }
            updatePlayerScoreboard(player, player.getLevel(), hpToDisplay);
        }
    }

}
