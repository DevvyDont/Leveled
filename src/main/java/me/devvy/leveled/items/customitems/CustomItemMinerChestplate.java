package me.devvy.leveled.items.customitems;

import me.devvy.leveled.items.CustomItem;
import me.devvy.leveled.items.CustomItemType;
import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class CustomItemMinerChestplate extends CustomItem {

    public CustomItemMinerChestplate(CustomItemType type) {
        super(type);
    }

    @Override
    public void setupItemStack() {
        super.setupItemStack();
        if (itemStack.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(Color.BLUE);
            itemStack.setItemMeta(meta);
        }
    }
}
