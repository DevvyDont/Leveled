package io.github.devvydoo.levelingoverhaul.enchantments.enchants;

import io.github.devvydoo.levelingoverhaul.enchantments.EnchantmentManager;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.CustomEnchantType;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public class CustomEnchantment {

    private EnchantmentManager enchantmentManager;
    private CustomEnchantType type;
    private int level;

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
