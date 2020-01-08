package io.github.devvydoo.levellingoverhaul.listeners;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerNametags implements Listener {

    private static String HEALTHY_HP_COLOR = ChatColor.GREEN.toString();
    private static String DAMAGED_HP_COLOR = ChatColor.YELLOW.toString();
    private static String HURT_HP_COLOR = ChatColor.GOLD.toString();
    private static String CRITICAL_HP_COLOR = ChatColor.RED.toString();
    private static String DEAD_HP_COLOR = ChatColor.DARK_GRAY.toString();

    public static String getChatColorFromHealth(double hp){
        if (hp <= 0){ return DEAD_HP_COLOR; }
        else if (hp <= 5) {  return CRITICAL_HP_COLOR; }
        else if (hp <= 10) {  return HURT_HP_COLOR; }
        else if (hp <= 15) {  return DAMAGED_HP_COLOR; }
        else {  return HEALTHY_HP_COLOR; }
    }

    public PlayerNametags(LevellingOverhaul plugin){
        for (Player p: plugin.getServer().getOnlinePlayers()){
            updatePlayerScoreboard(p, p.getLevel(), p.getHealth());
        }
    }

    private String getNametagString(String name, int level, double hp){
        return ChatColor.GRAY + "" + ChatColor.BOLD + "Lv. " + level + " " +  ChatColor.DARK_GREEN + name +  ChatColor.DARK_RED + " ❤" + getChatColorFromHealth(hp) + (int) hp;
    }

    private void updatePlayerScoreboard(Player player, int level, double hp){
        player.setPlayerListName(getNametagString(player.getDisplayName(), level, hp >= 0 ? hp : 0));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent event){

        if (event.isCancelled()){
            return;
        }

        if (event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            updatePlayerScoreboard(player, player.getLevel(), player.getHealth() - event.getFinalDamage());
        }
    }

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event){
        if (event.getNewLevel() < event.getOldLevel()){
            return;
        }
        updatePlayerScoreboard(event.getPlayer(), event.getNewLevel(), event.getPlayer().getHealth());
    }

}
