package me.devvy.leveled.enchantments.calculator;


import me.devvy.leveled.enchantments.enchants.CustomEnchantType;
import me.devvy.leveled.items.CustomItemManager;
import me.devvy.leveled.enchantments.EnchantmentManager;
import me.devvy.leveled.items.CustomItemType;
import me.devvy.leveled.util.ToolTypeHelpers;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class PotentialEnchantment {

    private final EnchantmentManager enchantmentManager;
    private final CustomItemManager customItemManager;
    private final Object enchantType;
    private final int minPlayerLevelRequired;  // The minimum level required to obtain this enchantment
    private final int maxPlayerLevelRequired;  // The maximum player level required to achieve max enchant level if needed
    private final int maxEnchantLevel;  // The max enchant level this enchantment can be

    public PotentialEnchantment(CustomItemManager customItemManager, EnchantmentManager enchantmentManager, Object enchantType, int minimumLevel, int maximumLevel, int maxEnchantLevel) {
        this.customItemManager = customItemManager;
        this.enchantmentManager = enchantmentManager;
        this.enchantType = enchantType;
        this.minPlayerLevelRequired = minimumLevel;
        this.maxPlayerLevelRequired = maximumLevel;
        this.maxEnchantLevel = maxEnchantLevel;
    }

    public Object getEnchantType() {
        return enchantType;
    }

    public int getMinPlayerLevelRequired() {
        return minPlayerLevelRequired;
    }

    public int getMaxPlayerLevelRequired() {
        return maxPlayerLevelRequired;
    }

    public int getMaxEnchantLevel() {
        return maxEnchantLevel;
    }

    public int getQuality() {

        if (enchantType instanceof Enchantment) {
            return enchantmentManager.getEnchantQuality((Enchantment) enchantType);
        } else if (enchantType instanceof CustomEnchantType) {
            return enchantmentManager.getEnchantQuality((CustomEnchantType) enchantType);
        }
        throw new IllegalStateException("enchantType was not Enchantment or CustomEnchantType!");

    }

    public boolean conflictsWith(PotentialEnchantment otherEnchant) {

        if (otherEnchant.enchantType instanceof CustomEnchantType) {
            CustomEnchantType type = (CustomEnchantType) otherEnchant.enchantType;
            if (this.enchantType instanceof CustomEnchantType) {
                CustomEnchantType query = (CustomEnchantType) this.enchantType;
                return enchantmentManager.getConflictingEnchantTypes(type).contains(query);
            } else if (this.enchantType instanceof Enchantment) {
                Enchantment query = (Enchantment) this.enchantType;
                return enchantmentManager.getConflictingEnchantTypes(type).contains(query.getKey().toString().replace("minecraft:", ""));
            }
        } else if (otherEnchant.enchantType instanceof Enchantment) {
            Enchantment enchantment = (Enchantment) otherEnchant.enchantType;
            if (this.enchantType instanceof CustomEnchantType) {
                CustomEnchantType query = (CustomEnchantType) this.enchantType;
                return enchantmentManager.getConflictingEnchantTypes(enchantment).contains(query);
            } else if (this.enchantType instanceof Enchantment) {
                Enchantment query = (Enchantment) this.enchantType;
                return enchantmentManager.getConflictingEnchantTypes(enchantment).contains(query.getKey().toString().replace("minecraft:", ""));
            }

        }
        throw new IllegalArgumentException("PotentialEnchantment.conflictsWith was passed an invalid Argument!");
    }


    public boolean canBeAppliedTo(ItemStack itemStack) {
        if (enchantType instanceof Enchantment) {

            Enchantment enchant = (Enchantment) enchantType;

            // Various vanilla overrides

            // Override armor to not be able to receive unbreaking. armor in this plugin is unbreakable.
            if (ToolTypeHelpers.isArmor(itemStack) && enchant.getKey().toString().equals("minecraft:unbreaking")) { return false; }

            // Override prot enchants for elytras
            if (itemStack.getType().equals(Material.ELYTRA) && enchant.getKey().toString().contains("protection")) { return true; }
            // Override all helmet enchants for dragon helmet
            if (customItemManager.isCustomItemType(itemStack, CustomItemType.DRAGON_HELMET) && enchant.canEnchantItem(new ItemStack(Material.LEATHER_HELMET))) { return true; }
            return enchant.canEnchantItem(itemStack);

        } else if (enchantType instanceof CustomEnchantType) {
            CustomEnchantType type = (CustomEnchantType) enchantType;
            return enchantmentManager.canEnchantItem(type, itemStack);
        }
        throw new IllegalStateException("enchantType was not of type Enchantment or CustomEnchantType!");
    }
}

