package io.github.devvydoo.levellingoverhaul.enchantments;


import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class PotentialEnchantment  {

    private Object enchantType;
    private int minPlayerLevelRequired;  // The minimum level required to obtain this enchantment
    private int maxPlayerLevelRequired;  // The maximum player level required to achieve max enchant level if needed
    private int maxEnchantLevel;  // The max enchant level this enchantment can be

    public PotentialEnchantment(Object enchantType, int minimumLevel, int maximumLevel, int maxEnchantLevel) {
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

    public int getQuality(){

        if (enchantType instanceof Enchantment){
            return CustomEnchantments.getEnchantQuality((Enchantment) enchantType);
        } else if (enchantType instanceof CustomEnchantType){
            return CustomEnchantments.getEnchantQuality((CustomEnchantType) enchantType);
        }
        throw new IllegalStateException("enchantType was not Enchantment or CustomEnchantType!");

    }

    public boolean conflictsWith(PotentialEnchantment otherEnchant) {

        if (otherEnchant.enchantType instanceof CustomEnchantType) {
            CustomEnchantType type = (CustomEnchantType) otherEnchant.enchantType;
            if (this.enchantType instanceof CustomEnchantType) {
                CustomEnchantType query = (CustomEnchantType) this.enchantType;
                return CustomEnchantments.getConflictingEnchantTypes(type).contains(query);
            } else if (this.enchantType instanceof Enchantment) {
                Enchantment query = (Enchantment) this.enchantType;
                return CustomEnchantments.getConflictingEnchantTypes(type).contains(query.getKey().toString().replace("minecraft:", ""));
            }
        } else if (otherEnchant.enchantType instanceof Enchantment) {
            Enchantment enchantment = (Enchantment) otherEnchant.enchantType;
            if (this.enchantType instanceof CustomEnchantType) {
                CustomEnchantType query = (CustomEnchantType) this.enchantType;
                return CustomEnchantments.getConflictingEnchantTypes(enchantment).contains(query);
            } else if (this.enchantType instanceof Enchantment) {
                Enchantment query = (Enchantment) this.enchantType;
                return CustomEnchantments.getConflictingEnchantTypes(enchantment).contains(query.getKey().toString().replace("minecraft:", ""));
            }

        }
        throw new IllegalArgumentException("PotentialEnchantment.conflictsWith was passed an invalid Argument!");
    }


    public boolean canBeAppliedTo(ItemStack itemStack){
        if (enchantType instanceof Enchantment){
            Enchantment enchant = (Enchantment) enchantType;
            return enchant.canEnchantItem(itemStack);
        } else if (enchantType instanceof CustomEnchantType){
            CustomEnchantType type = (CustomEnchantType) enchantType;
            return CustomEnchantments.canEnchantItem(type, itemStack);
        }
        throw new IllegalStateException("enchantType was not of type Enchantment or CustomEnchantType!");
    }
}

