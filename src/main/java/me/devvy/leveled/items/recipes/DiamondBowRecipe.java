package me.devvy.leveled.items.recipes;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class DiamondBowRecipe {

    public ShapedRecipe getShapedRecipe() {
        ShapedRecipe sr = new ShapedRecipe(new NamespacedKey(Leveled.getPlugin(Leveled.class), this.getClass().getName()), getItemStackResult());
        sr.shape("SD ",
                 "S D",
                 "SD ");
        sr.setIngredient('S', Material.STRING);
        sr.setIngredient('D', Material.DIAMOND);
        return sr;
    }

    public ItemStack getItemStackResult() {
        return Leveled.getPlugin(Leveled.class).getCustomItemManager().getCustomItem(CustomItemType.DIAMOND_BOW);
    }

}
