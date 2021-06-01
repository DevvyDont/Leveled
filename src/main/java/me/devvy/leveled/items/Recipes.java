package me.devvy.leveled.items;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.items.recipes.DiamondBowRecipe;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public abstract class Recipes {

    public static void registerRecipes(Leveled plugin) {

        DiamondBowRecipe dbr = new DiamondBowRecipe();
        plugin.getServer().addRecipe(dbr.getShapedRecipe());

        ItemStack chainHelmet = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemStack chainChest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemStack chainLeggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        ItemStack chainBoots = new ItemStack(Material.CHAINMAIL_BOOTS);

        ItemStack witherHead = new ItemStack(Material.WITHER_SKELETON_SKULL);

        ShapedRecipe chainHelmetRecipe = new ShapedRecipe(new NamespacedKey(plugin, "chain_helmet_recipe"), chainHelmet);
        ShapedRecipe chainChestRecipe = new ShapedRecipe(new NamespacedKey(plugin, "chain_chest_recipe"), chainChest);
        ShapedRecipe chainLeggingsRecipe = new ShapedRecipe(new NamespacedKey(plugin, "chain_leggings_recipe"), chainLeggings);
        ShapedRecipe chainBootsRecipe = new ShapedRecipe(new NamespacedKey(plugin, "chain_boots_recipe"), chainBoots);

        ShapedRecipe witherHeadRecipe = new ShapedRecipe(new NamespacedKey(plugin, "wither_head_recipe"), witherHead);

        chainHelmetRecipe.shape("BBB", "BAB");
        chainHelmetRecipe.setIngredient('B', Material.IRON_BARS);
        chainHelmetRecipe.setIngredient('A', Material.AIR);

        chainChestRecipe.shape("BAB", "BBB", "BBB");
        chainChestRecipe.setIngredient('B', Material.IRON_BARS);
        chainChestRecipe.setIngredient('A', Material.AIR);

        chainLeggingsRecipe.shape("BBB", "BAB", "BAB");
        chainLeggingsRecipe.setIngredient('B', Material.IRON_BARS);
        chainLeggingsRecipe.setIngredient('A', Material.AIR);

        chainBootsRecipe.shape("BAB", "BAB");
        chainBootsRecipe.setIngredient('B', Material.IRON_BARS);
        chainBootsRecipe.setIngredient('A', Material.AIR);

        witherHeadRecipe.shape("CCC", "CHC", "OOO");
        witherHeadRecipe.setIngredient('C', Material.COAL_BLOCK);
        witherHeadRecipe.setIngredient('H', Material.SKELETON_SKULL);
        witherHeadRecipe.setIngredient('O', Material.OBSIDIAN);

        plugin.getServer().addRecipe(chainHelmetRecipe);
        plugin.getServer().addRecipe(chainChestRecipe);
        plugin.getServer().addRecipe(chainLeggingsRecipe);
        plugin.getServer().addRecipe(chainBootsRecipe);

        plugin.getServer().addRecipe(witherHeadRecipe);
    }
}
