package me.devvy.leveled.mobs.custommobs;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.items.CustomItemType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomLeveledEntityZombieMiner extends CustomLeveledEntity {

    public CustomLeveledEntityZombieMiner(LivingEntity entity) {
        super(entity);
    }

    @Override
    public CustomLeveledEntityType getCustomMobType() {
        return CustomLeveledEntityType.LOST_MINER;
    }

    @Override
    public void setup() {

        Leveled plugin = Leveled.getInstance();

        ItemStack[] items = new ItemStack[]{
                                plugin.getCustomItemManager().getCustomItem(CustomItemType.MINER_PICKAXE),
                                plugin.getCustomItemManager().getCustomItem(CustomItemType.MINER_HELMET),
                                plugin.getCustomItemManager().getCustomItem(CustomItemType.MINER_CHESTPLATE),
                                plugin.getCustomItemManager().getCustomItem(CustomItemType.MINER_LEGGINGS),
                                plugin.getCustomItemManager().getCustomItem(CustomItemType.MINER_BOOTS)
        };

        for (ItemStack gear : items)
            if (Math.random() < .5f)
                plugin.getEnchantmentManager().doCalculatorEnchant(gear, 30, 12, 15);

        entity.getEquipment().setItemInMainHand(items[0]);
        entity.getEquipment().setHelmet(items[1]);
        entity.getEquipment().setChestplate(items[2]);
        entity.getEquipment().setLeggings(items[3]);
        entity.getEquipment().setBoots(items[4]);

        entity.getEquipment().setHelmetDropChance(0);
        entity.getEquipment().setChestplateDropChance(0);
        entity.getEquipment().setLeggingsDropChance(0);
        entity.getEquipment().setBootsDropChance(0);

        lootTable = new CustomLeveledEntityLootTable();
        List<CustomLeveledEntityLootTableItem> lootDrops = new ArrayList<>();
        for (ItemStack gear : items)
            lootDrops.add(new CustomLeveledEntityLootTableItem(gear, .05f));
        lootTable.setPossibleItems(lootDrops);
    }
}
