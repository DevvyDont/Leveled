package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.events.EntityHitByProjectileEvent;
import me.devvy.leveled.events.EntityShootArrowEvent;
import me.devvy.leveled.managers.GlobalDamageManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class EnchantSnipe extends Enchantment {

    public EnchantSnipe(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Snipe";
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.BOW;
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
        return itemStack.getType() == Material.BOW || itemStack.getType() == Material.CROSSBOW;
    }

    private boolean isBoss(Entity entity) {
        return entity instanceof EnderDragon || entity instanceof EnderDragonPart || entity instanceof Wither;
    }

    private float getDistanceDamageMultiplier(double distance, int level) {

        float mult = 1.0f;

        // Cap at 100
        double distanceToUse = Math.min(distance, 100);
        mult += distanceToUse / 100;
        mult *= 1 + (level * .2);
        return mult;

    }

    @EventHandler
    public void onEntityShotFMJArrow(EntityShootArrowEvent event) {

        assert event.getBow() != null;
        int snipeLevel = event.getBow().getEnchantmentLevel(this);

        if (snipeLevel == 0)
            return;

        event.applyProjectileFlag(GlobalDamageManager.ARROW_SNIPE_ENCHANT_METANAME, snipeLevel);
    }

    @EventHandler
    public void onBossHitByFMJArrow(EntityHitByProjectileEvent event) {

        if (event.getHitEntity() == null)
            return;

        if (!isBoss(event.getHitEntity()))
            return;

        int snipeLevel = event.getProjectileFlag(GlobalDamageManager.ARROW_SNIPE_ENCHANT_METANAME);
        if (snipeLevel == 0)
            return;

        if (!(event.getEntity().getShooter() instanceof LivingEntity))
            return;

        double distance = ((LivingEntity) event.getEntity().getShooter()).getLocation().distance(event.getHitEntity().getLocation());
        event.multiplyDamage(1 + getDistanceDamageMultiplier(distance, snipeLevel));
    }
}
