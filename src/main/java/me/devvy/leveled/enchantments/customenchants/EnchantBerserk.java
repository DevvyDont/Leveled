package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.enchantments.EnchantmentManager;
import me.devvy.leveled.events.PlayerDealtMeleeDamageEvent;
import me.devvy.leveled.util.ToolTypeHelpers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EnchantBerserk extends Enchantment implements Listener {

    public EnchantBerserk(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Berkserk";
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
        return allowedMats.contains(itemStack.getType());
    }

    @EventHandler
    public void onPlayerDealtBerserkerDamage(PlayerDealtMeleeDamageEvent event) {

        int mainHandLevel = event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(EnchantmentManager.BERSERK);
        int offHandLevel = event.getPlayer().getInventory().getItemInOffHand().getEnchantmentLevel(EnchantmentManager.BERSERK);
        if (mainHandLevel != 0 || offHandLevel != 0)
            event.multiplyDamage(2);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTookBerserkerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        int mainHandLevel = player.getInventory().getItemInMainHand().getEnchantmentLevel(EnchantmentManager.BERSERK);
        int offHandLevel = player.getInventory().getItemInOffHand().getEnchantmentLevel(EnchantmentManager.BERSERK);
        if (mainHandLevel != 0 || offHandLevel != 0)
            event.setDamage(event.getDamage() * 3);

    }
}
