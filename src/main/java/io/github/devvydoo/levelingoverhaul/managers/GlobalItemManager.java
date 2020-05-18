package io.github.devvydoo.levelingoverhaul.managers;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.Rarity;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GlobalItemManager implements Listener {

    LevelingOverhaul plugin;

    public GlobalItemManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    public void fixItem(ItemStack itemStack){

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;
        // Unbreakable items are already fixed
        if (itemMeta.isUnbreakable())
            return;
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);

        itemStack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        // Already have a level?
        if (plugin.getEnchantmentManager().getItemLevel(itemStack) > 0)
            return;

        int itemLevel = plugin.getCustomItemManager().getItemLevelCap(itemStack);
        if (itemLevel > 0)
            plugin.getEnchantmentManager().setItemLevel(itemStack, itemLevel);
        else{
            Rarity rarity;
            if (plugin.getCustomItemManager().isCustomItem(itemStack))
                rarity = plugin.getCustomItemManager().getCustomItemRarity(itemStack);
            else
                rarity = Rarity.getItemRarity(itemStack);
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasDisplayName())
                meta.setDisplayName(rarity.LEVEL_LABEL_COLOR + ChatColor.stripColor(meta.getDisplayName()));
            else
                meta.setDisplayName(rarity.LEVEL_LABEL_COLOR + WordUtils.capitalizeFully(itemStack.getType().toString().replace("_", " ")));
            itemStack.setItemMeta(meta);
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event){

        if (event.getInventory().getResult() != null)
            fixItem(event.getInventory().getResult());

    }

    @EventHandler
    public void onPickupItem(PlayerAttemptPickupItemEvent event){
        fixItem(event.getItem().getItemStack());
    }

    @EventHandler
    public void onPickupEntity(PlayerPickupArrowEvent event){
        fixItem(event.getItem().getItemStack());
    }

    @EventHandler
    public void onProjectileShoot(EntityShootBowEvent event){
        fixItem(event.getArrowItem());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event){
        for (ItemStack item : event.getInventory().getContents()){
            if (item == null)
                continue;
            fixItem(item);
        }
    }

    @EventHandler
    public void onMerchantInteract(PlayerInteractEntityEvent event){

        List<MerchantRecipe> newRecipes = new ArrayList<>();

        // Fix all the items in the trades
        if (event.getRightClicked() instanceof Merchant){
            Merchant merchant = (Merchant) event.getRightClicked();

            for (MerchantRecipe recipe : merchant.getRecipes()){

                ItemStack result = new ItemStack(recipe.getResult().getType(), recipe.getResult().getAmount());
                result.setItemMeta(recipe.getResult().getItemMeta());
                fixItem(result);

                MerchantRecipe newRec = new MerchantRecipe(result, recipe.getUses(), recipe.getMaxUses(), recipe.hasExperienceReward());

                List<ItemStack> fixedIngredients = new ArrayList<>();

                for (ItemStack oldIngredient : recipe.getIngredients()){
                    ItemStack newIngredient = new ItemStack(oldIngredient.getType(), oldIngredient.getAmount());
                    newIngredient.setItemMeta(oldIngredient.getItemMeta());
                    fixItem(newIngredient);
                    fixedIngredients.add(newIngredient);
                }

                newRec.setIngredients(fixedIngredients);

                newRecipes.add(newRec);

            }

            merchant.setRecipes(newRecipes);
        }



    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event){
        fixItem(event.getResult());
    }

}
