package me.devvy.leveled.commands;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.items.CustomItemType;
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

public class CommandGiveCustomItem implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        ArrayList<String> options = new ArrayList<>();

        if (strings.length != 1)
            return options;

        String soFar = strings[0];

        for (CustomItemType customItemType : CustomItemType.values()) {
            if (customItemType.toString().toUpperCase().contains(soFar.toUpperCase()))
                options.add(customItemType.toString());
        }
        return options;


    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Leveled plugin = Leveled.getInstance();

        if (!(commandSender instanceof Player)) {
            System.out.println("Only players can use this!");
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "Please provide an argument! /leveledgive <item> [amount]");
            return true;
        }

        String name = strings[0].toUpperCase();
        int amount;

        try {
            amount = Integer.parseInt(strings[1]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(ChatColor.RED + "Please provide a number!");
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            amount = 1;
        }

        CustomItemType customItemType;

        try {
            customItemType = CustomItemType.valueOf(name);
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(ChatColor.RED + "That is not a valid custom item!");
            return true;
        }

        Player player = (Player) commandSender;
        ItemStack newItem = plugin.getCustomItemManager().getCustomItem(customItemType);
        newItem.setAmount(amount);
        plugin.getCustomItemManager().setItemLevel(newItem, customItemType.DEFAULT_LEVEL);
        player.getInventory().addItem(newItem);
        commandSender.sendMessage(ChatColor.GREEN + "Gave you " + amount + " " + customItemType.toString() + "!");
        return true;

    }


}
