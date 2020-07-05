package me.devvy.leveled.items;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.player.LevelRewards;
import me.devvy.leveled.player.abilities.CustomAbility;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemManager implements Listener {

    public static final NamespacedKey CUSTOM_ITEM_INDEX_KEY = new NamespacedKey(Leveled.getPlugin(Leveled.class), "custom-item-index");
    public static final NamespacedKey ITEM_LEVEL_KEY = new NamespacedKey(Leveled.getPlugin(Leveled.class), "item-level");
    private final Map<CustomItemType, CustomItem> customItemMap;

    public CustomItemManager() {

        customItemMap = new HashMap<>();

        for (CustomItemType type : CustomItemType.values()){

            // Attempt to create an instance of the custom item's class type, this shouldn't fail unless it's coded incorrectly
            try {
                customItemMap.put(type, type.CLAZZ.getDeclaredConstructor(CustomItemType.class).newInstance(type));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                Leveled.getPlugin(Leveled.class).getPluginLoader().disablePlugin(Leveled.getPlugin(Leveled.class));
                return;
            }

        }

        // Register all the events
        for (CustomItem cu : customItemMap.values())
            Leveled.getPlugin(Leveled.class).getServer().getPluginManager().registerEvents(cu, Leveled.getPlugin(Leveled.class));

    }

    // Simply just tags the item as leveled, provide an arg of 0 if it isn't capped. A cap of 0 will not show up.
    public void setItemLevel(ItemStack item, int level) {

        if (item == null || item.getItemMeta() == null)
            return;


        ItemMeta itemMeta = item.getItemMeta();
        StringBuilder itemName = new StringBuilder();

        // We need to find out if this item is already leveled, if it is it means that we already added a level tag
        if (itemMeta.getPersistentDataContainer().has(ITEM_LEVEL_KEY, PersistentDataType.INTEGER)) {
            String[] components = itemMeta.getDisplayName().split(" ");  // Lv. xx "name"    which means we omit index 0 and 1
            for (int i = 2; i < components.length; i++)
                itemName.append(components[i]).append(" ");
        }
        else
            itemName = new StringBuilder(itemMeta.hasDisplayName() ? ChatColor.stripColor(itemMeta.getDisplayName()) : WordUtils.capitalizeFully(item.getType().toString().replace("_", " ")));

        // Actually set the container to the level
        itemMeta.getPersistentDataContainer().set(ITEM_LEVEL_KEY, PersistentDataType.INTEGER, level);

        // Now we can go ahead and update the display name
        // Attempt to get the custom item that this thing could potentially be
        CustomItemType customItemType = getCustomItemType(item);

        Rarity itemRarity;

        // Is it custom?
        if (customItemType != null)
            itemRarity = customItemType.RARITY;
        else
            itemRarity = Rarity.getItemRarity(item);  // Resort to default rarity if it's not custom

        // If the rarity isn't legendary, and the item has enchants, then the item is enchanted rarity
        if (itemRarity != Rarity.LEGENDARY && !item.getEnchantments().isEmpty())
            itemRarity = Rarity.ENCHANTED;

        // Now actually update the name
        if (level > 0)
            itemMeta.setDisplayName(itemRarity.LEVEL_LABEL_COLOR + "Lv. " + level + " " + itemRarity.NAME_LABEL_COLOR + ChatColor.stripColor(itemName.toString()));
        else
            itemMeta.setDisplayName(itemRarity.LEVEL_LABEL_COLOR + ChatColor.stripColor(itemName.toString()));

        // Other stuff that 'fixes' our items
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(itemMeta);

    }

    public int getItemLevel(ItemStack itemStack) {

        // If the item has the key, then the level of the item is that. If not, assume 0.
        if (itemStack.getItemMeta().getPersistentDataContainer().has(ITEM_LEVEL_KEY, PersistentDataType.INTEGER))
            return itemStack.getItemMeta().getPersistentDataContainer().get(ITEM_LEVEL_KEY, PersistentDataType.INTEGER);

        setItemLevel(itemStack, getItemLevelCap(itemStack));
        return getItemLevelCap(itemStack);
    }

    public void updateItemLore(ItemStack itemStack) {

        List<String> newLore = new ArrayList<>();

        // First we add the damage/defense of the item if applicable.

        // Is the item custom and not utility? This means it has to have some stat to display
        if (isCustomItem(itemStack) && getCustomItemType(itemStack).CATEGORY != CustomItemType.Category.UTILITY) {
            newLore.add("");
            CustomItemType customItemType = getCustomItemType(itemStack);
            // Add the line that says "Defense: 69" to the lore
            newLore.add(CustomItemType.Category.getCategoryStatPrefix(customItemType.CATEGORY) + customItemType.STAT_AMOUNT);
        }
        // If the fallback category is something that we need to pay attention to
        else if (CustomItemType.Category.getFallbackCategory(itemStack.getType()) != null) {
            CustomItemType.Category categoryOfItem = CustomItemType.Category.getFallbackCategory(itemStack.getType());
            if (categoryOfItem != CustomItemType.Category.UTILITY) {
                newLore.add("");
                newLore.add(CustomItemType.Category.getCategoryStatPrefix(categoryOfItem) + CustomItemType.getFallbackStat(itemStack.getType()));
            }
        }

        // Some custom items need lore sections
        if (isCustomItem(itemStack)) {
            newLore.add("");
            newLore.addAll(customItemMap.get(getCustomItemType(itemStack)).getLoreHeader());
        }

        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {

            String enchantName;
            if (enchantment.getKey().toString().contains("minecraft:"))
                enchantName = WordUtils.capitalize(enchantment.getKey().toString().replace("minecraft:", "").replace('_', ' '));
            else
                enchantName = enchantment.getName();

            newLore.add("");
            newLore.add(ChatColor.BLUE + enchantName + " " + itemStack.getEnchantments().get(enchantment));  // Add the name and level of the enchant
            newLore.add(Leveled.getPlugin(Leveled.class).getEnchantmentManager().getEnchantmentDescription(enchantment));  // Add the desc
        }

        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(newLore);
        itemStack.setItemMeta(meta);
    }

    // TODO: refactor full set bonuses to have a more expandable system
    public boolean hasFullSetBonus(Player player, CustomAbility abilityReward) {

        switch (abilityReward) {
            case BOUNDLESS_ROCKETS:
                PlayerInventory playerInventory = player.getInventory();
                if (isCustomItemType(playerInventory.getHelmet(), CustomItemType.DRAGON_HELMET) && isCustomItemType(playerInventory.getChestplate(), CustomItemType.DRAGON_CHESTPLATE) && isCustomItemType(playerInventory.getLeggings(), CustomItemType.DRAGON_LEGGINGS) && isCustomItemType(playerInventory.getBoots(), CustomItemType.DRAGON_BOOTS))
                    return true;
                break;
        }

        return false;
    }

    /**
     * Gets the custom item instance that this item is. If the item is not custom, null is returned.
     *
     * @param item Some itemstack that could potentially be custom
     * @return The CustomItemType enum that this item is, null otherwise
     */
    public CustomItem getCustomItem(ItemStack item) {
        CustomItemType type = getCustomItemType(item);
        return type != null ? customItemMap.get(type) : null;
    }

    /**
     * Gets the type of custom item that this item is. If the item is not custom, null is returned.
     *
     * @param item Some itemstack that could potentially be custom
     * @return The CustomItemType enum that this item is, null otherwise
     */
    public CustomItemType getCustomItemType(ItemStack item) {

        if (item == null || item.getItemMeta() == null)
            return null;

        if (!item.getItemMeta().getPersistentDataContainer().has(CUSTOM_ITEM_INDEX_KEY, PersistentDataType.INTEGER))
            return null;

        Integer index = item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_INDEX_KEY, PersistentDataType.INTEGER);

        if (index == null)
            return null;
        else if (index < 0 || index >= CustomItemType.values().length)
            return null;

        return CustomItemType.values()[index];
    }

    public boolean isCustomItem(ItemStack itemStack) {
        return getCustomItemType(itemStack) != null;
    }

    public boolean isCustomItemType(ItemStack itemStack, CustomItemType type) {
        return getCustomItemType(itemStack) == type;
    }

    public int getItemLevelCap(ItemStack itemStack){
        if (!isCustomItem(itemStack))
            return LevelRewards.getDefaultItemLevelCap(itemStack);

        return getCustomItemType(itemStack).DEFAULT_LEVEL;
    }

    public ItemStack getCustomItem(CustomItemType item){
        return customItemMap.get(item).getItemStackClone();
    }

    public Rarity getCustomItemRarity(ItemStack itemStack){
        return getCustomItemType(itemStack).RARITY;
    }

}
