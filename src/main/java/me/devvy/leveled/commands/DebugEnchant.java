package me.devvy.leveled.commands;

import me.devvy.leveled.Leveled;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class DebugEnchant implements CommandExecutor {

    private final Leveled plugin;

    public DebugEnchant(Leveled plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            System.out.println("only players can use");
            return true;
        }

        Player player = (Player) sender;
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Provide at least 2 args please, level enchant and quality of enchant");
            return false;
        }

        int levelToEnchant;
        int qualityOfEnchant;
        int levelToSetItem;

        try {
            levelToEnchant = Integer.parseInt(args[0]);
            qualityOfEnchant = Integer.parseInt(args[1]);
            if (args.length > 2)
                levelToSetItem = Integer.parseInt(args[2]);
            else
                levelToSetItem = levelToEnchant;
        } catch (NumberFormatException ignored) {
            player.sendMessage(ChatColor.RED + "You must provide integers as arguments." + ChatColor.DARK_RED + " /adminenchant <level> <quality> [item level]");
            return true;
        }

        if (levelToEnchant < 0 || levelToEnchant > 150)
            player.sendMessage(ChatColor.YELLOW + "It is not recommended to do enchants outside of 0-150. Expect odd behavior.");

        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You can't enchant your hand silly!!!");
            return true;
        }

        plugin.getEnchantmentManager().doCalculatorEnchant(hand, levelToEnchant, qualityOfEnchant, levelToSetItem);
        player.sendMessage(ChatColor.GREEN + "Enchanted your item!");
        return true;
    }
}
