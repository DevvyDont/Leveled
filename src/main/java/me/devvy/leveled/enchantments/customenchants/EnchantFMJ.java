package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.events.EntityHitByProjectileEvent;
import me.devvy.leveled.events.EntityShootArrowEvent;
import me.devvy.leveled.managers.GlobalDamageManager;
import me.devvy.leveled.util.ToolTypeHelpers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Boss;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EnchantFMJ extends Enchantment implements Listener {

    public EnchantFMJ(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "FMJ";
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        ArrayList<Material> allowedMats = new ArrayList<>();
        ToolTypeHelpers.addMeleeWeaponsToList(allowedMats);
        allowedMats.add(Material.CROSSBOW);
        allowedMats.add(Material.BOW);
        return allowedMats.contains(itemStack.getType());
    }

    private boolean isBoss(LivingEntity entity) {
        return entity instanceof Boss || entity instanceof EnderDragonPart;
    }

    @EventHandler
    public void onEntityShotFMJArrow(EntityShootArrowEvent event) {

        assert event.getBow() != null;
        int fmjLevel = event.getBow().getEnchantmentLevel(this);

        if (fmjLevel == 0)
            return;


        event.applyProjectileFlag(GlobalDamageManager.ARROW_FMJ_ENCHANT_METANAME, fmjLevel);
    }

    @EventHandler
    public void onBossHitByFMJArrow(EntityHitByProjectileEvent event) {

        if (!(event.getHitEntity() instanceof LivingEntity))
            return;

        if (!isBoss((LivingEntity) event.getHitEntity()))
            return;

        int fmjLevel = event.getProjectileFlag(GlobalDamageManager.ARROW_FMJ_ENCHANT_METANAME);
        if (fmjLevel == 0)
            return;

        event.multiplyDamage(1 + fmjLevel * .2f);
    }
}
