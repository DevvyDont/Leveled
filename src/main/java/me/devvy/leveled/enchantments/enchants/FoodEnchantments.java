package me.devvy.leveled.enchantments.enchants;

import me.devvy.leveled.Leveled;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class FoodEnchantments implements Listener {

    private final Leveled plugin;

    public FoodEnchantments(Leveled plugin) {
        this.plugin = plugin;
    }

    private int getNutrition(ItemStack itemStack) {
        switch (itemStack.getType()) {
            case RABBIT_STEW:
                return 10;
            case COOKED_PORKCHOP:
            case COOKED_BEEF:
            case PUMPKIN_PIE:
                return 8;
            case BEETROOT_SOUP:
            case COOKED_CHICKEN:
            case COOKED_MUTTON:
            case COOKED_SALMON:
            case GOLDEN_CARROT:
            case HONEY_BOTTLE:
            case SUSPICIOUS_STEW:
                return 6;
            case BAKED_POTATO:
            case BREAD:
            case COOKED_COD:
            case COOKED_RABBIT:
                return 5;
            case APPLE:
            case CHORUS_FRUIT:
            case ENCHANTED_GOLDEN_APPLE:
            case GOLDEN_APPLE:
            case ROTTEN_FLESH:
                return 4;
            case CARROT:
            case BEEF:
            case PORKCHOP:
            case RABBIT:
                return 3;
            case COOKIE:
            case MELON_SLICE:
            case POISONOUS_POTATO:
            case CHICKEN:
            case MUTTON:
            case SALMON:
            case SPIDER_EYE:
            case SWEET_BERRIES:
                return 2;
            case BEETROOT:
            case DRIED_KELP:
            case POTATO:
            case PUFFERFISH:
            case TROPICAL_FISH:
                return 1;
            default:
                return 0;
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onFoodEat(PlayerItemConsumeEvent event) {

        if (event.getPlayer().isDead())
            return;

        // Does the player have a helmet?
        ItemStack helmet = event.getPlayer().getInventory().getHelmet();
        if (helmet != null && !helmet.getType().equals(Material.AIR)) {

            // Is the helmet enchanted with Saturation?
            for (CustomEnchantment enchantment : plugin.getEnchantmentManager().getCustomEnchantments(helmet)) {
                if (enchantment.getType().equals(CustomEnchantType.SATURATION)) {
                    event.getPlayer().setSaturation(event.getPlayer().getSaturation() + enchantment.getLevel() * 3);
                    event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() + enchantment.getLevel());
                }
            }
        }

        // Does the player have a chestplate? also don't do golden diet stuff if they are basically starving
        ItemStack chestplate = event.getPlayer().getInventory().getChestplate();
        if (event.getPlayer().getFoodLevel() > 10 && chestplate != null && !chestplate.getType().equals(Material.AIR)) {

            // Golden Diet?
            if (plugin.getEnchantmentManager().hasEnchant(chestplate, CustomEnchantType.GOLDEN_DIET)) {
                // "take away" half of the hunger bois that the food is going to give
                int hungerTaken = getNutrition(event.getItem()) / 2;
                event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() - hungerTaken);
                try {
                    event.getPlayer().setHealth(event.getPlayer().getHealth() + (100 * hungerTaken));
                } catch (IllegalArgumentException ignored) {
                    event.getPlayer().setHealth(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                }

            }

        }


    }

}
