package io.github.devvydoo.levelingoverhaul.items;

import org.bukkit.Material;

public enum CustomItems {

    DRAGON_SWORD("Aspect of the Dragons", "dragon_sword_key", Material.GOLDEN_SWORD, Rarity.LEGENDARY),
    DRAGON_HELMET("Dragon Helmet", "dragon_helmet_key", Material.DRAGON_HEAD, Rarity.LEGENDARY),
    DRAGON_CHESTPLATE("Dragon Wings", "dragon_chestplate_key", Material.ELYTRA, Rarity.LEGENDARY),
    DRAGON_LEGGINGS("Dragon Leggings", "dragon_leggings_key", Material.LEATHER_LEGGINGS, Rarity.LEGENDARY),
    DRAGON_BOOTS("Dragon Boots", "dragon_boots_key", Material.LEATHER_BOOTS, Rarity.LEGENDARY),
    ENDER_BOW("Ender Bow", "ender_bow_key", Material.BOW, Rarity.LEGENDARY),
    MAGIC_MIRROR("Magic Mirror", "magic_mirror_key", Material.COMPASS, Rarity.RARE),
    ;

    public final String name;
    public final String key;
    public final Material type;
    public final Rarity rarity;

    CustomItems(String name, String key, Material type, Rarity rarity) {
        this.name = name;
        this.key = key;
        this.type = type;
        this.rarity = rarity;
    }
}
