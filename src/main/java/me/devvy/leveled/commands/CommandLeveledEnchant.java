package me.devvy.leveled.commands;

import me.devvy.leveled.Leveled;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CommandLeveledEnchant implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        Leveled plugin = Leveled.getPlugin(Leveled.class);

        ArrayList<String> options = new ArrayList<>();

        if (strings.length != 1)
            return options;

        String soFar = strings[0];

        for (Enchantment e : plugin.getEnchantmentManager().getAllRegisteredEnchantments()) {
            if (e.getKey().toString().toUpperCase().contains(soFar.toUpperCase()))
                options.add(e.getKey().toString().toUpperCase());
        }
        return options;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Leveled plugin = Leveled.getPlugin(Leveled.class);

        if (!(commandSender instanceof Player)) {
            System.out.println("Only players can use this!");
            return true;
        }

        if (strings.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "Please provide more args!");
            return true;
        }

        String name = strings[0].toUpperCase();
        int lvl;

        try {
            lvl = Integer.parseInt(strings[1]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(ChatColor.RED + "Please provide a number!");
            return true;
        }

        Enchantment enchantment = null;

        for (Enchantment e : plugin.getEnchantmentManager().getAllRegisteredEnchantments())
            if (e.getKey().toString().equalsIgnoreCase(name))
                enchantment = e;

        if (enchantment == null) {
            commandSender.sendMessage(ChatColor.RED + "Could not find enchantment: " + name);
            return true;
        }

        Player player = (Player) commandSender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getItemMeta() == null) {
            commandSender.sendMessage(ChatColor.RED + "The item in your hand cannot be enchanted!");
            return true;
        }

        itemInHand.addUnsafeEnchantment(enchantment, lvl);
        plugin.getCustomItemManager().updateItemLore(itemInHand);
        commandSender.sendMessage(ChatColor.GREEN + "Added the enchant to your tool!");
        return true;
    }
}
