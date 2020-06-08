package io.github.devvydoo.levelingoverhaul.mobs.custommobs;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class MobCorruptedSkeleton extends CustomMob {

    public MobCorruptedSkeleton(EntityType actualMobType) {
        super(actualMobType);
    }

    @Override
    public void setup(LivingEntity entity) {
        entity.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_AXE));
        entity.getEquipment().getItemInMainHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
    }
}
