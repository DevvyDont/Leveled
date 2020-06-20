package me.devvy.leveled.enchantments.enchants;

import me.devvy.leveled.enchantments.EnchantmentManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public class CustomEnchantment {

    private final EnchantmentManager enchantmentManager;
    private final CustomEnchantType type;
    private final int level;

    public CustomEnchantment(EnchantmentManager enchantmentManager, CustomEnchantType type, int level) {
        this.enchantmentManager = enchantmentManager;
        this.type = type;
        this.level = level;
    }

    public static String getName(CustomEnchantType type) {
        return WordUtils.capitalize(type.toString().replace('_', ' ').toLowerCase());
    }

    public static String getLoreContent(CustomEnchantType type, int level) {
        return String.format(ChatColor.BLUE + "%s %d", getName(type), level);
    }

    public int getLevel() {
        return this.level;
    }

    public CustomEnchantType getType() {
        return type;
    }

    public String getName() {
        return getName(this.type);
    }

    public String getDescription() {
        return enchantmentManager.getEnchantmentDescription(this.getType());
    }

    public String getLoreContent() {
        return getLoreContent(this.getType(), this.getLevel());
    }

}
