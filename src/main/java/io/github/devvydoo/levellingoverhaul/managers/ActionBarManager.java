package io.github.devvydoo.levellingoverhaul.managers;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import io.github.devvydoo.levellingoverhaul.util.BaseExperience;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class ActionBarManager implements Listener {

    private LevellingOverhaul plugin;

    public ActionBarManager(LevellingOverhaul plugin) {
        this.plugin = plugin;
        for (Player p: plugin.getServer().getOnlinePlayers()){
            displayActionBarText(p, (int)p.getHealth(), (int)p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), "");
        }
    }

    private double getTotalExpAccumulated(double percentCompleted, double totalXpInLevel){
        return percentCompleted * totalXpInLevel;
    }

    /**
     * Displays default action bar text
     *
     * @param player The player the send the action bar to
     */
    private void displayActionBarText(Player player, int currentHP, int maxHP, String extra){
        int xpTotal = player.getExpToLevel();
        int xpProgress = (int)  getTotalExpAccumulated(player.getExp(), xpTotal);
        String xpPortion = ChatColor.GREEN + "" + xpProgress + ChatColor.GRAY + "/" + ChatColor.DARK_GREEN + "" + xpTotal + ChatColor.GRAY + " XP   ";
        if (player.getLevel() == BaseExperience.LEVEL_CAP) { xpPortion = ChatColor.GREEN + "" + ChatColor.BOLD + "MAXED" + ChatColor.GRAY + " XP   "; }
        String message = ChatColor.RED + "" + currentHP + "/" + maxHP + ChatColor.DARK_RED + " ❤   " + xpPortion + extra;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,  TextComponent.fromLegacyText(message));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerGotHit(EntityDamageEvent event){

        if (event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            int hpToDisplay = (int) (player.getHealth() - event.getFinalDamage());
            int maxHP = (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (hpToDisplay < 0) { hpToDisplay = 0; }
            else if (hpToDisplay > maxHP) {hpToDisplay = maxHP; }
            displayActionBarText(player, hpToDisplay, maxHP, "");
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHealed(EntityRegainHealthEvent event){

        if (event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            int hpToDisplay = (int) (player.getHealth() + event.getAmount());
            int maxHP = (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (hpToDisplay > maxHP) { hpToDisplay = maxHP; }
            displayActionBarText(player, hpToDisplay, maxHP, "");
        }

    }

    /**
     * Called from other places in the plugin we should add some extra text on the bar, when we earn exp etc
     *
     * @param player The player to send the message to
     * @param message The extra text to tack onto the action bar text
     */
    public void dispalyActionBarTextWithExtra(Player player, String message){
        displayActionBarText(player, (int)player.getHealth(), (int)player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), message);
    }

}
