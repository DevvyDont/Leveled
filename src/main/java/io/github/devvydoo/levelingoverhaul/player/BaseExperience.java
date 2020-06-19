package io.github.devvydoo.levelingoverhaul.player;

import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;


public abstract class BaseExperience {

    public static float getMobExperienceMultiplier(EntityType type) {

        switch (type) {

            case IRON_GOLEM:
            case ENDERMAN:
                return 1.3f;

            case RAVAGER:
                return 1.5f;

            case WITHER_SKELETON:
                return 1.1f;

            case PHANTOM:
                return .7f;

            case VILLAGER:
                return .1f;

            case CREEPER:
                return 1.25f;

            case VINDICATOR:
                return 1.15f;

            // Boss logic is handled somewhere else
            case ELDER_GUARDIAN:
            case ENDER_DRAGON:
            case WITHER:
                return 0;

            default:
                return 1f;

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
    public static int getBaseExperienceFromBlock(Block block) {
        switch (block.getType()) {
            case GRASS_BLOCK:
                return Math.random() < .01 ? 1 : 0;
            case COAL_ORE:
                return 150;
            case NETHER_QUARTZ_ORE:
                return 10000;
            case LAPIS_ORE:
                return 3500;
            case REDSTONE_ORE:
                return 3000;
            case DIAMOND_ORE:
                return 20000;
            case EMERALD_ORE:
                return 80000;
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
    public static int getBaseExperienceFromSmelt(Material materialSmelted, int amount) {

        double base;
        switch (materialSmelted) {
            case COOKED_BEEF:
            case COOKED_CHICKEN:
            case COOKED_COD:
            case COOKED_MUTTON:
            case COOKED_PORKCHOP:
            case COOKED_RABBIT:
            case COOKED_SALMON:
                base = 100;
                break;
            case DRIED_KELP:
            case BAKED_POTATO:
                base = 40;
                break;
            case IRON_INGOT:
                base = 250;
                break;
            case GOLD_INGOT:
                base = 500;
                break;
            default:
                base = 0;
                break;
        }

        return (int) (base * amount);
    }

    /**
     * We are going to give xp based on the key of the advancement, which are listed below
     * Note: These don't include any recipe advancements as theyre not really 'achievements'
     * also, the keys start with 'minecraft:' so we are going to remove that for cleanliness purposes
     * The way our progression is forced on the player, we should give balanced amounts of xp for example:
     * The End achievements should be giving decent xp for level 60-75 players,
     * The Nether achievements should be giving decent xp for level 40-55 players
     * <p>
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
     * <p>
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
     * <p>
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
     * <p>
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
     * <p>
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
    public static int getBaseExperienceFromAdvancement(Advancement advancement) {

        String advancementKey = advancement.getKey().toString().replaceAll("minecraft:", "");

        switch (advancementKey) {

            // Early-game
            case "story/mine_stone":
            case "adventure/kill_a_mob":
            case "husbandry/tame_an_animal":
            case "husbandry/breed_an_animal":
            case "husbandry/fishy_business":
            case "husbandry/plant_seed":
            case "adventure/sleep_in_bed":
                return 1000;
            case "husbandry/safely_harvest_honey":
            case "adventure/honey_block_slide":
            case "adventure/trade":
                return 5000;
            case "story/upgrade_tools":  // Stone tools capped at level 5
                return 2500;
            case "husbandry/tactical_fishing":  // Catch fish using a bucket
            case "story/lava_bucket":  // Iron capped at lvl 15
            case "adventure/summon_iron_golem":  // Iron capped at lvl 15
            case "story/form_obsidian":  // Iron capped at lvl 15
            case "story/smelt_iron":  // Iron capped at lvl 15
            case "adventure/voluntary_exile":  // Kill raid captain
            case "story/deflect_arrow":  // Shield capped at lvl 20
                return 80000;
            case "adventure/shoot_arrow":  // Bow capped at lvl 15
            case "adventure/sniper_duel":  // Bow capped at level 15
                return 50000;
            case "story/iron_tools": // Capped at level 25
            case "story/mine_diamond": // Capped at level 25
            case "story/enchant_item":  // Enchanting capped at 30
                return 50000;
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
                return 250000;
            case "husbandry/bred_all_animals":  // Difficult to pull off, should reward a good amount
            case "husbandry/complete_catalogue":  // Difficult to pull off, tame all cat variants
                return 1000000;
            case "husbandry/balanced_diet":  // Eat everything, not easy
                return 1000000;
            case "adventure/adventuring_time":  // Visit all the biomss
                return 2000000;

            // Mid-game
            case "nether/find_fortress":  // Capped at level 40
            case "nether/obtain_blaze_rod":  // Capped at level 40
                return 500000;
            case "nether/brew_potion":  // Brewing capped at 45
                return 600000;
            case "story/enter_the_nether":  // Capped at level 40
                return 500000;
            case "nether/fast_travel":  // Capped at level 40
            case "nether/get_wither_skull":  // Capped at level 40 technically, pretty difficult
            case "nether/return_to_sender":  // Capped at level 40
                return 750000;
            case "husbandry/break_diamond_hoe":  // Diamond tools Capped at level 40
                return 750000;
            case "story/cure_zombie_villager":  // Brewing capped at 45, required to make potions for cure
            case "adventure/kill_all_mobs":  // Capped at level 40 technically
            case "story/shiny_gear":  // Diamond armor, capped at 50
            case "story/follow_ender_eye":  // Ender capped at 60
            case "story/enter_the_end":  // Ender capped at 60
                return 1500000;
            case "nether/create_beacon":  // Pretty difficult
                return 2500000;
            case "nether/all_effects":  // Brewing capped at 45
                return 1500000;
            case "nether/summon_wither":  // Summon wither, technically capped at 40
                return 3000000;
            case "nether/uneasy_alliance":  // Capped at level 40, kill ghast in overworld
                return 1500000;
            case "nether/create_full_beacon":  // Pretty difficult
                return 4000000;

            // End-game
            case "end/enter_end_gateway":
            case "end/find_end_city":
                return 2500000;
            case "end/dragon_breath":
            case "end/kill_dragon":
            case "end/dragon_egg":
                return 2000000;
            case "end/levitate":
            case "end/respawn_dragon":
            case "nether/all_potions":  // have every potion effect in the game, super difficult
                return 4000000;
            case "end/elytra":
                return 5000000;
            default:
                System.out.println("[Achievements]: Came across unexpected achievement: " + advancementKey);
                return 0;
        }
    }
}
