package io.github.devvydoo.levelingoverhaul.items.customitems;

import io.github.devvydoo.levelingoverhaul.items.CustomItem;
import io.github.devvydoo.levelingoverhaul.items.CustomItemType;
import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class CustomItemDragonHelmet extends CustomItem {

    public CustomItemDragonHelmet(CustomItemType type) {
        super(type);
    }

    @Override
    public void setupItemStack() {
        super.setupItemStack();
        if (itemStack.getItemMeta() instanceof LeatherArmorMeta){
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            leatherArmorMeta.setColor(Color.PURPLE);
            itemStack.setItemMeta(leatherArmorMeta);
        }
    }
}
