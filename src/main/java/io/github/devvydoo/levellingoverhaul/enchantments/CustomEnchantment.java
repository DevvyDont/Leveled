package io.github.devvydoo.levellingoverhaul.enchantments;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public class CustomEnchantment {

    private CustomEnchantType type;
    private int level;

    public static String getName(CustomEnchantType type){
        return WordUtils.capitalize(type.toString().replace('_', ' ').toLowerCase());
    }

    public static String getLoreContent(CustomEnchantType type, int level){
        return String.format(ChatColor.BLUE + "%s %d", getName(type), level);
    }

    public CustomEnchantment(CustomEnchantType type, int level){
        this.type = type;
        this.level = level;
    }

    public int getLevel() { return this.level; }

    public CustomEnchantType getType(){
        return type;
    }

    public String getName(){
        return getName(this.type);
    }

    public String getDescription(){
        return CustomEnchantments.getEnchantmentDescription(this.getType());
    }

    public String getLoreContent() {
        return getLoreContent(this.getType(), this.getLevel());
    }

}
