package io.github.devvydoo.levellingoverhaul.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class BaseMobExperience {


    /**
     * A helper method that returns the amount of xp we should receive when killing a mob
     *
     * @param mob - The Entity object that was killed
     * @return the int base amount of XP that we should earn
     */
    public static int getBaseExperienceFromMob(LivingEntity mob){
        switch (mob.getType()){

            case WITHER:
            case ENDER_DRAGON:
                return 100;
            case ELDER_GUARDIAN:
                return 75;
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

}
