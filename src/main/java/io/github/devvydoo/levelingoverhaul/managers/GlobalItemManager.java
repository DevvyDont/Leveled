package io.github.devvydoo.levelingoverhaul.managers;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.Rarity;
import io.github.devvydoo.levelingoverhaul.util.LevelRewards;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GlobalItemManager implements Listener {

    LevelingOverhaul plugin;

    public GlobalItemManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    private void fixItem(ItemStack itemStack){

        ItemMeta itemMeta = itemStack.getItemMeta();
        // Unbreakable items are already fixed
        if (itemMeta.isUnbreakable())
            return;
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);

        itemStack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        // Already have a level?
        if (plugin.getEnchantmentManager().getItemLevel(itemStack) > 0)
            return;

        int itemLevel = LevelRewards.getDefaultItemLevelCap(itemStack);
        if (itemLevel > 0)
            plugin.getEnchantmentManager().setItemLevel(itemStack, itemLevel);
        else{
            Rarity rarity = Rarity.getItemRarity(itemStack);
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasDisplayName())
                meta.setDisplayName(rarity.LEVEL_LABEL_COLOR + meta.getDisplayName());
            else
                meta.setDisplayName(rarity.LEVEL_LABEL_COLOR + WordUtils.capitalizeFully(itemStack.getType().toString().replace("_", " ")));
            itemStack.setItemMeta(meta);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        if (event.getCurrentItem() != null){
            fixItem(event.getCurrentItem());
        }
    }

    @EventHandler
    public void onPickupItem(PlayerAttemptPickupItemEvent event){
        fixItem(event.getItem().getItemStack());
    }

}
