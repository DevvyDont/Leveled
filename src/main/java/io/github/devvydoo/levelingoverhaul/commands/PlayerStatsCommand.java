package io.github.devvydoo.levelingoverhaul.commands;

import com.google.common.base.Strings;
import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.player.LeveledPlayer;
import io.github.devvydoo.levelingoverhaul.util.FormattingHelpers;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerStatsCommand implements CommandExecutor {

    private LevelingOverhaul plugin;

    public PlayerStatsCommand(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            System.out.println("Only players can use this!");
            return true;
        }

        Player player = (Player) sender;
        LeveledPlayer leveledPlayer = plugin.getPlayerManager().getLeveledPlayer(player);
        int envDef = leveledPlayer.getDefense();
        int fireDef = leveledPlayer.getFireDefense();
        int explDef = leveledPlayer.getExplosionDefense();
        int projDef = leveledPlayer.getProjectileDefense();

        String header = ChatColor.GRAY + "=====- " + ChatColor.DARK_GREEN + "Lv. " + player.getLevel() + " " + ChatColor.GREEN +  player.getName() + ChatColor.GRAY + " -=====";
        player.sendMessage(header);
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + FormattingHelpers.getFormattedInteger(leveledPlayer.getExperience().getTotalExperienceRequiredForNextLevel() - leveledPlayer.getExperience().getAccumulatedExperienceToNextLevel()) + ChatColor.GRAY + " XP required to level " + ChatColor.GREEN + ChatColor.BOLD + (player.getLevel() + 1));
        player.sendMessage();
        player.sendMessage(ChatColor.DARK_RED + "❤ " + ChatColor.GREEN + "" + FormattingHelpers.getFormattedInteger((int) player.getHealth()) + ChatColor.GRAY + "/" + ChatColor.DARK_GREEN + FormattingHelpers.getFormattedInteger((int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) + ChatColor.GRAY + " | " + ChatColor.DARK_RED + "✦ " + ChatColor.RED + leveledPlayer.getStrength());
        player.sendMessage();
        player.sendMessage(ChatColor.BLUE + "♠ " + getDefenseColor(envDef) + envDef + ChatColor.GRAY + " | " + ChatColor.GOLD + "♨ " + getDefenseColor(fireDef) + fireDef);
        player.sendMessage(ChatColor.DARK_GRAY + "☀ " + getDefenseColor(explDef) + explDef + ChatColor.GRAY + " | " + ChatColor.WHITE + "➹ " + getDefenseColor(projDef) + projDef);
        player.sendMessage(ChatColor.GRAY + Strings.repeat("=", ChatColor.stripColor(header).length()));
        return true;
    }

    private ChatColor getDefenseColor(int amount){

        if (amount <= 0)
            return ChatColor.RED;
        else if (amount < 50)
            return ChatColor.GOLD;
        else if (amount < 100)
            return ChatColor.YELLOW;
        else if (amount < 175)
            return ChatColor.DARK_GREEN;
        else if (amount < 250)
            return ChatColor.GREEN;
        else if (amount < 400)
            return ChatColor.AQUA;
        else
            return ChatColor.LIGHT_PURPLE;

    }
}
