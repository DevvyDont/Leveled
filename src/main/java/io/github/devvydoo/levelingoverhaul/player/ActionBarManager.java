package io.github.devvydoo.levelingoverhaul.player;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.player.LeveledPlayer;
import io.github.devvydoo.levelingoverhaul.player.PlayerExperience;
import io.github.devvydoo.levelingoverhaul.util.FormattingHelpers;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class ActionBarManager implements Listener {

    private LevelingOverhaul plugin;

    public ActionBarManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            displayActionBarText(p, (int) p.getHealth(), (int) p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), "");
        }
    }

    /**
     * Displays default action bar text
     *
     * @param player The player the send the action bar to
     */
    private void displayActionBarText(Player player, int currentHP, int maxHP, String extra) {

        LeveledPlayer leveledPlayer = plugin.getPlayerManager().getLeveledPlayer(player);
        String accumulated = FormattingHelpers.getFormattedInteger(leveledPlayer.getExperience().getAccumulatedExperienceToNextLevel());
        String totalNeeded = FormattingHelpers.getFormattedInteger(leveledPlayer.getExperience().getTotalExperienceRequiredForNextLevel());

        String xpPortion = ChatColor.GREEN + "" + accumulated + ChatColor.GRAY + "/" + ChatColor.DARK_GREEN + "" + totalNeeded + ChatColor.GRAY + " XP   ";

        if (player.getLevel() == PlayerExperience.LEVEL_CAP)
            xpPortion = ChatColor.GREEN + "" + ChatColor.BOLD + "MAXED" + ChatColor.GRAY + " XP   ";

        String message = ChatColor.RED + "" + FormattingHelpers.getFormattedInteger(currentHP) + "/" + FormattingHelpers.getFormattedInteger(maxHP) + ChatColor.DARK_RED + " ❤   " + xpPortion + extra;
        player.sendActionBar(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerGotHit(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            int hpToDisplay = (int) (player.getHealth() - event.getFinalDamage());
            int maxHP = (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (hpToDisplay < 0) {
                hpToDisplay = 0;
            } else if (hpToDisplay > maxHP) {
                hpToDisplay = maxHP;
            }
            displayActionBarText(player, hpToDisplay, maxHP, "");
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerHealed(EntityRegainHealthEvent event) {

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            int hpToDisplay = (int) (player.getHealth() + event.getAmount());
            int maxHP = (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (hpToDisplay > maxHP) {
                hpToDisplay = maxHP;
            }
            displayActionBarText(player, hpToDisplay, maxHP, "");
        }

    }

    /**
     * Called from other places in the plugin we should add some extra text on the bar, when we earn exp etc
     *
     * @param player  The player to send the message to
     * @param message The extra text to tack onto the action bar text
     */
    public void dispalyActionBarTextWithExtra(Player player, String message) {
        displayActionBarText(player, (int) player.getHealth(), (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), message);
    }

}
