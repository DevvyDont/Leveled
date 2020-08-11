package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.events.EntityHitByProjectileEvent;
import me.devvy.leveled.events.EntityShootArrowEvent;
import me.devvy.leveled.managers.GlobalDamageManager;
import me.devvy.leveled.util.ToolTypeHelpers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EnchantExecutioner extends Enchantment implements Listener {

    public EnchantExecutioner(NamespacedKey key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Executioner";
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
        allowedMats.add(Material.BOW);
        allowedMats.add(Material.CROSSBOW);
        return allowedMats.contains(itemStack.getType());
    }

    @EventHandler
    public void onEntityShotEXEArrow(EntityShootArrowEvent event) {

        assert event.getBow() != null;
        int exeLevel = event.getBow().getEnchantmentLevel(this);

        if (exeLevel == 0)
            return;

        event.applyProjectileFlag(GlobalDamageManager.ARROW_EXECUTE_ENCHANT_METANAME, exeLevel);
    }

    @EventHandler
    public void onEntityHitByEXEArrow(EntityHitByProjectileEvent event) {

        if (event.getHitEntity() == null || !(event.getHitEntity() instanceof LivingEntity))
            return;

        int exeLevel = event.getProjectileFlag(GlobalDamageManager.ARROW_EXECUTE_ENCHANT_METANAME);
        if (exeLevel == 0)
            return;

        double hpPercent = ((LivingEntity) event.getHitEntity()).getHealth() / ((LivingEntity) event.getHitEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        System.out.println(hpPercent);

        if (hpPercent > .25)
            return;

        event.multiplyDamage(1 + exeLevel * .15f);
    }
}
