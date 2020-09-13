package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.events.EntityShootArrowEvent;
import me.devvy.leveled.events.PlayerDealtMeleeDamageEvent;
import me.devvy.leveled.util.ToolTypeHelpers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EnchantCriticalStrike extends Enchantment implements Listener {

    public EnchantCriticalStrike(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Critical Strike";
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
        return allowedMats.contains(itemStack.getType());
    }

    @EventHandler
    public void onPlayerCrit(PlayerDealtMeleeDamageEvent event) {

        double critBonusMultiplier = !event.getPlayer().isOnGround() && event.getPlayer().getVelocity().getY() < 0 ? 1.5 : 1;
        if (critBonusMultiplier == 1)
            return;

        critBonusMultiplier += event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(this) * .25;
        critBonusMultiplier += event.getPlayer().getInventory().getItemInOffHand().getEnchantmentLevel(this) * .25;

        event.multiplyDamage(critBonusMultiplier);
    }
}
