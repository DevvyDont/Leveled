package me.devvy.leveled.items;

import me.devvy.leveled.Leveled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class GlobalItemManager implements Listener {

    public void fixItem(ItemStack itemStack){
        if (itemStack == null || itemStack.getItemMeta() == null)
            return;
        Leveled.getInstance().getCustomItemManager().getItemLevel(itemStack);
        Leveled.getInstance().getCustomItemManager().updateItemLore(itemStack);
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
    public void ItemSpawnedIn(InventoryCreativeEvent event) {
        fixItem(event.getCursor());
        fixItem(event.getCurrentItem());
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event){
        fixItem(event.getResult());
    }

}
