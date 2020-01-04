package io.github.devvydoo.levellingoverhaul.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class BaseExperience {

    public static final int LEVEL_CAP = 100;

    /**
     * Because of how sanity checked xp gains are, we need a 'safe level' that we can set a player to to use as a
     * case where we ignore logic. This will be useful if we ever implement commands that allow us to set a user's
     * level, otherwise the command won't function properly since LevelChange events will still be fired
     */
    public static final int DEBUG_LEVEL = 77777777;

    /**
     * Helper method to display stuff on the action bar, mainly when xp is gained
     *
     * @param player
     * @param message
     */
    public static void displayActionBarText(Player player, String message){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }


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
            case PLAYER:
                // In the event a player kills another player, we are just going to give their xp to them
                Player deadPlayer = (Player) mob;
                // Maxed players don't drop xp
                if (deadPlayer.getLevel() >= BaseExperience.LEVEL_CAP){
                    return 0;
                }
                // Super edge case where if we killed a player that had a full xp bar
                if (deadPlayer.getExp() == 1){
                        deadPlayer.setExp(.9999f);
                }
                // Math yay
                return (int) (deadPlayer.getExpToLevel() * deadPlayer.getExp());
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

                if (Math.random() < .5){
                    return 1;
                } else {
                    return 0;
                }

            case LAPIS_ORE:
            case REDSTONE_ORE:
                return 3;
            case DIAMOND_ORE:
                return 7;
            case EMERALD_ORE:
            case NETHER_QUARTZ_ORE:
                return 10;
            default:
                return 0;
        }
    }

    /**
     * The XP we should give a player for smelting something in a furnace
     *
     * @param materialSmelted - The items smelted
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
            case DRIED_KELP:
                base =  1;
                break;
            case IRON_INGOT:
                base = 2;
                break;
            case GOLD_INGOT:
                base = 4;
                break;
            default:
                base = 0;
                break;
        }

        return base * amount;
    }

    /**
     * We are going to give xp based on the key of the advancement, which are listed below
     * Note: These don't include any recipe advancements as theyre not really 'achievements'
     * also, the keys start with 'minecraft:' so we are going to remove that for cleanliness purposes
     * The way our progression is forced on the player, we should give balanced amounts of xp for example:
     * The End achievements should be giving decent xp for level 60-75 players,
     * The Nether achievements should be giving decent xp for level 40-55 players
     *
     * story/obtain_armor
     * story/lava_bucket
     * story/deflect_arrow
     * story/iron_tools
     * story/mine_stone
     * story/enter_the_nether
     * story/upgrade_tools
     * story/cure_zombie_villager
     * story/form_obsidian
     * story/smelt_iron
     * story/shiny_gear
     * story/enchant_item
     * story/follow_ender_eye
     * story/mine_diamond
     * story/root
     * story/enter_the_end
     *
     * husbandry/tame_an_animal
     * husbandry/fishy_business
     * husbandry/bred_all_animals
     * husbandry/tactical_fishing
     * husbandry/silk_touch_nest
     * husbandry/break_diamond_hoe
     * husbandry/plant_seed
     * husbandry/balanced_diet
     * husbandry/root
     * husbandry/safely_harvest_honey
     * husbandry/breed_an_animal
     * husbandry/complete_catalogue
     *
     * adventure/very_very_frightening
     * adventure/sniper_duel
     * adventure/two_birds_one_arrow
     * adventure/whos_the_pillager_now
     * adventure/shoot_arrow
     * adventure/arbalistic
     * adventure/summon_iron_golem
     * adventure/sleep_in_bed
     * adventure/root
     * adventure/kill_all_mobs
     * adventure/voluntary_exile
     * adventure/totem_of_undying
     * adventure/kill_a_mob
     * adventure/adventuring_time
     * adventure/hero_of_the_village
     * adventure/trade
     * adventure/throw_trident
     * adventure/honey_block_slide
     * adventure/ol_betsy
     *
     * nether/all_potions
     * nether/create_beacon
     * nether/brew_potion
     * nether/root
     * nether/all_effects
     * nether/get_wither_skull
     * nether/obtain_blaze_rod
     * nether/return_to_sender
     * nether/create_full_beacon
     * nether/summon_wither
     * nether/fast_travel
     * nether/uneasy_alliance
     * nether/find_fortress
     *
     * end/kill_dragon
     * end/dragon_egg
     * end/levitate
     * end/find_end_city
     * end/enter_end_gateway
     * end/respawn_dragon
     * end/elytra
     * end/dragon_breath
     * end/root
     *
     * @param advancement - the Advancement object the player earned
     * @return an int representing the amount of xp earned
     */
    public static int getBaseExperienceFromAdvancement(Advancement advancement){

        String advancementKey = advancement.getKey().toString().replaceAll("minecraft:", "");

        switch (advancementKey){

            // Early-game
            case "story/mine_stone":
            case "adventure/kill_a_mob":
            case "husbandry/tame_an_animal":
            case "husbandry/breed_an_animal":
            case "husbandry/fishy_business":
            case "husbandry/plant_seed":
            case "adventure/sleep_in_bed":
                return 5;
            case "husbandry/safely_harvest_honey":
            case "adventure/honey_block_slide":
            case "adventure/trade":
                return 7;
            case "husbandry/tactical_fishing":  // Catch fish using a bucket
            case "story/upgrade_tools":  // Stone tools capped at level 5
            case "story/lava_bucket":  // Iron capped at lvl 15
            case "adventure/summon_iron_golem":  // Iron capped at lvl 15
            case "story/form_obsidian":  // Iron capped at lvl 15
            case "story/smelt_iron":  // Iron capped at lvl 15
            case "adventure/voluntary_exile":  // Kill raid captain
            case "story/deflect_arrow":  // Shield capped at lvl 20
                return 10;
            case "adventure/shoot_arrow":  // Bow capped at lvl 15
            case "adventure/sniper_duel":  // Bow capped at level 15
                return 20;
            case "story/iron_tools": // Capped at level 25
            case "story/mine_diamond": // Capped at level 25
            case "story/enchant_item":  // Enchanting capped at 30
                return 25;
            case "adventure/arbalistic":  // Shoot a crossbow i think?
            case "adventure/ol_betsy":  // Shoot a crossbow i think?
            case "adventure/totem_of_undying":  // Cheat death
            case "adventure/two_birds_one_arrow":  // Crossbow capped at 30, kill two phantoms with piercing
            case "adventure/whos_the_pillager_now":  // Crossbow capped at 30, kill illager with it
            case "adventure/hero_of_the_village":  // Raid
            case "adventure/very_very_frightening":  // STrike a villager with lightning
            case "adventure/throw_trident":  // Throw a trident
            case "husbandry/silk_touch_nest":  // Silk touch bee nest with 3 bees inside
            case "story/obtain_armor":  // Clarification: Equip iron armor, capped at lvl 35
                return 30;
            case "husbandry/bred_all_animals":  // Difficult to pull off, should reward a good amount
            case "husbandry/complete_catalogue":  // Difficult to pull off, tame all cat variants
                return 50;
            case "husbandry/balanced_diet":  // Eat everything, not easy
            case "adventure/adventuring_time":  // Visit all the biomss
                return 100;

            // Mid-game
            case "nether/find_fortress":  // Capped at level 40
            case "nether/uneasy_alliance":  // Capped at level 40, kill ghast in overworld
            case "nether/obtain_blaze_rod":  // Capped at level 40
            case "nether/return_to_sender":  // Capped at level 40
                return 20;
            case "nether/brew_potion":  // Brewing capped at 45
                return 25;
            case "story/enter_the_nether":  // Capped at level 40
                return 30;
            case "nether/fast_travel":  // Capped at level 40
            case "nether/get_wither_skull":  // Capped at level 40 technically, pretty difficult
                return 50;
            case "husbandry/break_diamond_hoe":  // Diamond tools Capped at level 40
                return 75;
            case "story/cure_zombie_villager":  // Brewing capped at 45, required to make potions for cure
            case "adventure/kill_all_mobs":  // Capped at level 40 technically
            case "story/shiny_gear":  // Diamond armor, capped at 50
            case "story/follow_ender_eye":  // Ender capped at 60
            case "story/enter_the_end":  // Ender capped at 60
                return 100;
            case "nether/create_beacon":  // Pretty difficult
                return 120;
            case "nether/all_effects":  // Brewing capped at 45
                return 150;
            case "nether/summon_wither":  // Summon wither, technically capped at 40
                return 175;
            case "nether/create_full_beacon":  // Pretty difficult
                return 225;

            // End-game
            case "end/enter_end_gateway":
            case "end/find_end_city":
                return 200;
            case "end/dragon_breath":
            case "end/kill_dragon":
            case "end/dragon_egg":
                return 300;
            case "end/levitate":
            case "end/respawn_dragon":
            case "nether/all_potions":  // have every potion effect in the game, super difficult
                return 500;
            case "end/elytra":
                return 750;
            default:
                System.out.println("[Achievements]: Came across unexpected achievement: " + advancementKey);
                return 0;
        }
    }
}
