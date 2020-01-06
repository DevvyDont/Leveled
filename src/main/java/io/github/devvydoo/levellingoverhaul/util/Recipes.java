package io.github.devvydoo.levellingoverhaul.util;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public abstract class Recipes {

    public static void registerRecipes(LevellingOverhaul plugin){

        ItemStack chainHelmet = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemStack chainChest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemStack chainLeggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        ItemStack chainBoots = new ItemStack(Material.CHAINMAIL_BOOTS);

        ShapedRecipe chainHelmetRecipe = new ShapedRecipe(new NamespacedKey(plugin, "chain_helmet_recipe"), chainHelmet);
        ShapedRecipe chainChestRecipe = new ShapedRecipe(new NamespacedKey(plugin, "chain_chest_recipe"), chainChest);
        ShapedRecipe chainLeggingsRecipe = new ShapedRecipe(new NamespacedKey(plugin, "chain_leggings_recipe"), chainLeggings);
        ShapedRecipe chainBootsRecipe = new ShapedRecipe(new NamespacedKey(plugin, "chain_boots_recipe"), chainBoots);

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

        plugin.getServer().addRecipe(chainHelmetRecipe);
        plugin.getServer().addRecipe(chainChestRecipe);
        plugin.getServer().addRecipe(chainLeggingsRecipe);
        plugin.getServer().addRecipe(chainBootsRecipe);
    }
}
