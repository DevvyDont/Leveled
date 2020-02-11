package io.github.devvydoo.levelingoverhaul.enchantments;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public enum Rarity {

    // White
    COMMON("Common", ChatColor.GRAY.toString(), ChatColor.WHITE.toString()),
    // Green
    UNCOMMON("Uncommon", ChatColor.DARK_GREEN.toString(), ChatColor.GREEN.toString()),
    // Blue
    RARE("Rare", ChatColor.BLUE.toString(), ChatColor.AQUA.toString()),
    // Purple
    ENCHANTED("Enchanted", ChatColor.DARK_PURPLE.toString(), ChatColor.LIGHT_PURPLE.toString()),
    // Gold
    LEGENDARY("Legendary", ChatColor.GOLD.toString() + ChatColor.BOLD, ChatColor.YELLOW.toString());

    public final String RARITY_TITLE;
    public final String LEVEL_LABEL_COLOR;
    public final String NAME_LABEL_COLOR;

    private Rarity(String title, String levelColor, String nameColor){
        this.RARITY_TITLE = title;
        this.LEVEL_LABEL_COLOR = levelColor;
        this.NAME_LABEL_COLOR = nameColor;
    }

    public static Rarity getItemRarity(ItemStack itemStack){

        if (itemStack == null) {return null;}

        // Custom item overrides


        // Legendary overrides
        switch (itemStack.getType()){
            case ELYTRA:
                return LEGENDARY;
        }

        // Enchanted overrides
        if (itemStack.getEnchantments().size() > 0)
            return ENCHANTED;

        switch (itemStack.getType()){

            case ELYTRA:
            case TRIDENT:
            case DRAGON_HEAD:
            case ZOMBIE_HEAD:
            case CREEPER_HEAD:
            case PLAYER_HEAD:
            case HEART_OF_THE_SEA:
            case NETHER_STAR:
            case TOTEM_OF_UNDYING:
            case DRAGON_EGG:
            case WITHER_SKELETON_SKULL:
            case SKELETON_SKULL:
            case BEACON:
            case CONDUIT:
                return LEGENDARY;

            case ENCHANTED_GOLDEN_APPLE:
            case SHULKER_BOX:
            case BLACK_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
            case DRAGON_BREATH:
                return ENCHANTED;

            case DIAMOND_PICKAXE:
            case DIAMOND_SWORD:
            case DIAMOND_BOOTS:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_HELMET:
            case DIAMOND_LEGGINGS:
            case DIAMOND_AXE:
            case DIAMOND_HOE:
            case DIAMOND_SHOVEL:
            case DIAMOND_ORE:
            case DIAMOND_BLOCK:
            case DIAMOND:
            case DIAMOND_HORSE_ARMOR:
            case GOLDEN_APPLE:
            case ENDER_CHEST:
            case POTION:
            case LINGERING_POTION:
            case SPLASH_POTION:
            case OBSIDIAN:
            case END_CRYSTAL:
            case NAME_TAG:
            case ENDER_EYE:
            case ENDER_PEARL:
            case ENCHANTING_TABLE:
                return RARE;

            case IRON_PICKAXE:
            case IRON_SWORD:
            case IRON_BOOTS:
            case IRON_CHESTPLATE:
            case IRON_HELMET:
            case IRON_LEGGINGS:
            case IRON_AXE:
            case IRON_HOE:
            case IRON_SHOVEL:
            case IRON_ORE:
            case IRON_BLOCK:
            case IRON_INGOT:
            case IRON_HORSE_ARMOR:

            case GOLDEN_PICKAXE:
            case GOLDEN_SWORD:
            case GOLDEN_BOOTS:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_HELMET:
            case GOLDEN_LEGGINGS:
            case GOLDEN_AXE:
            case GOLDEN_HOE:
            case GOLDEN_SHOVEL:
            case GOLD_ORE:
            case GOLD_BLOCK:
            case GOLD_INGOT:
            case GOLDEN_HORSE_ARMOR:
            case GHAST_TEAR:
            case BLAZE_ROD:

            case BOW:
            case CROSSBOW:
            case SHIELD:
            case COAL:
            case COAL_BLOCK:
            case COAL_ORE:
            case IRON_NUGGET:
            case GOLD_NUGGET:
            case LAPIS_LAZULI:
            case LAPIS_ORE:
            case LAPIS_BLOCK:
            case REDSTONE:
            case REDSTONE_ORE:
            case REDSTONE_BLOCK:
            case GLOWSTONE:
            case GLOWSTONE_DUST:
            case NETHER_QUARTZ_ORE:
            case NETHER_WART:
            case NETHER_WART_BLOCK:
            case QUARTZ:
            case QUARTZ_BLOCK:
            case GRINDSTONE:
            case ANVIL:
            case CHIPPED_ANVIL:
            case DAMAGED_ANVIL:
                return UNCOMMON;

            default:
                return COMMON;
        }

    }

}
