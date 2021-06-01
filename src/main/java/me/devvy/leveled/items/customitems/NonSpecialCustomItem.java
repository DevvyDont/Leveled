package me.devvy.leveled.items.customitems;

import me.devvy.leveled.items.CustomItem;
import me.devvy.leveled.items.CustomItemType;

/**
 * Used when you don't want to make an entire class for an item that isn't special
 */
public class NonSpecialCustomItem extends CustomItem {

    public NonSpecialCustomItem(CustomItemType type) {
        super(type);
    }
}
