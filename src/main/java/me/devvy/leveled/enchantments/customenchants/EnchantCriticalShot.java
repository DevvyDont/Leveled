package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.events.EntityShootArrowEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class EnchantCriticalShot extends Enchantment implements Listener {

    public EnchantCriticalShot(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Critical Shot";
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

    @EventHandler
    public void onBowShot(EntityShootArrowEvent event) {

        // 20% chance to crit for double damage
        double critPercent = .2;

        // 15% boost per level of critical shot
        assert event.getBow() != null;  // Bow can't be null, we make sure it's not when this event is called
        critPercent += event.getBow().getEnchantmentLevel(this) * .15;

        // Test for crit, 1.5x damage
        if (Math.random() < critPercent) {
            event.multiplyDamage(1.5f);
            event.getEntity().getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0, 1.6, 0), 50);
            event.getEntity().getWorld().playSound(event.getEntity().getLocation().add(0, 1.6, 0), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, .3f, .6f);
        }
    }
}
