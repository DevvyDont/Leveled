package io.github.devvydoo.levellingoverhaul.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

public abstract class BaseExperience {

    public static final int LEVEL_CAP = 100;


    /**
     * A helper method that returns the amount of xp we should receive when killing a mob
     *
     * @param mob - The Entity object that was killed
     * @return the int base amount of XP that we should earn
     */
    public static int getBaseExperienceFromMob(LivingEntity mob){
        switch (mob.getType()){

            case WITHER_SKELETON:
                return 25;
            case GIANT:
                return 20;
            case ENDER_CRYSTAL:
            case GHAST:
            case RAVAGER:
            case ILLUSIONER:
                return 10;
            case EVOKER:
            case VINDICATOR:
            case ENDERMAN:
            case PILLAGER:
            case SHULKER:
            case GUARDIAN:
                return 7;
            case PIG_ZOMBIE:
            case BLAZE:
                return 5;
            case ZOMBIE_VILLAGER:
            case CREEPER:
            case ENDERMITE:
                return 4;
            case ZOMBIE:
            case SKELETON:
            case HUSK:
            case STRAY:
            case WITCH:
            case DROWNED:
            case CAVE_SPIDER:
            case IRON_GOLEM:
                return 3;
            case SPIDER:
            case SLIME:
            case MAGMA_CUBE:
            case PHANTOM:
            case VEX:
            case SILVERFISH:
            case SKELETON_HORSE:
            case ZOMBIE_HORSE:
                return 2;
            case CHICKEN:
            case DOLPHIN:
            case BAT:
            case BEE:
            case CAT:
            case COD:
            case COW:
            case FOX:
            case PIG:
            case MULE:
            case HORSE:
            case LLAMA:
            case PANDA:
            case DONKEY:
            case SQUID:
            case WOLF:
            case SNOWMAN:
            case VILLAGER:
            case OCELOT:
            case PARROT:
            case RABBIT:
            case SHEEP:
            case MUSHROOM_COW:
            case TURTLE:
            case POLAR_BEAR:
                return 1;
            default:
                return 0;

        }
    }

    /**
     * A helper method used to calculate the amount of xp we should earn from mining a block, NOTE we should not award
     * xp if a player breaks a block with silk touch, however we should award xp if we have auto smelt and we mine
     * either Iron or Gold
     *
     * @param block - The Block we mined
     * @return the int amount of xp to give
     */
    public static int getBaseExperienceFromBlock(Block block){
        switch (block.getType()){

            case COAL_ORE:
            case IRON_ORE:
                return 1;
            case GOLD_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
            case NETHER_QUARTZ_ORE:
                return 3;
            case DIAMOND_ORE:
                return 7;
            case EMERALD_ORE:
                return 10;
            default:
                return 0;
        }
    }

    /**
     * The XP we should give a player for smelting something in a furnace
     *
     * @param itemStack - The items smelted
     * @return the int amount of xp we should give
     */
    public static int getBaseExperienceFromSmelt(Material materialSmelted, int amount){

        int base;
        switch (materialSmelted){
            case COOKED_BEEF:
            case COOKED_CHICKEN:
            case COOKED_COD:
            case COOKED_MUTTON:
            case COOKED_PORKCHOP:
            case COOKED_RABBIT:
            case COOKED_SALMON:
            case IRON_INGOT:
                base =  1;
                break;
            case GOLD_INGOT:
                base = 3;
                break;
            default:
                base = 0;
                break;
        }

        return base * amount;
    }
}
