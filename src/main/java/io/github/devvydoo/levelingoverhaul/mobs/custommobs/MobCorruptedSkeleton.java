package io.github.devvydoo.levelingoverhaul.mobs.custommobs;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class MobCorruptedSkeleton extends CustomMob {

    public MobCorruptedSkeleton(LivingEntity entity) {
        super(entity);
    }

    @Override
    public CustomMobType getCustomMobType() {
        return CustomMobType.CORRUPTED_SKELETON;
    }

    @Override
    public void setup() {
        entity.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_AXE));
        entity.getEquipment().getItemInMainHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
    }
}
