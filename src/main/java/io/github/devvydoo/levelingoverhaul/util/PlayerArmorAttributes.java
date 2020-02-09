package io.github.devvydoo.levelingoverhaul.util;

import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.enchantments.EnchantmentManager;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerArmorAttributes {

    private EnchantmentManager enchantmentManager;

    private Player player;

    private int defense;
    private int fireDefense;
    private int explosionDefense;
    private int projectileDefense;
    private double bonusHealth;
    private int speed;
    private ArrayList<CustomAbility> abilities;

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    public PlayerArmorAttributes(EnchantmentManager enchantmentManager, Player player) {
        this.enchantmentManager = enchantmentManager;
        this.player = player;
        updateAttributes();
    }

    public Player getPlayer() {
        return player;
    }

    public int getDefense() {
        return defense;
    }

    public int getFireDefense() {
        return fireDefense;
    }

    public int getExplosionDefense() {
        return explosionDefense;
    }

    public int getProjectileDefense() {
        return projectileDefense;
    }

    public double getBonusHealth() {
        return bonusHealth;
    }

    public int getSpeed() {
        return speed;
    }

    public ArrayList<CustomAbility> getAbilities() {
        return abilities;
    }

    public void updateAttributes() {
        this.helmet = player.getInventory().getHelmet();
        this.chestplate = player.getInventory().getChestplate();
        this.leggings = player.getInventory().getLeggings();
        this.boots = player.getInventory().getBoots();

        this.defense = calculateDefense();
        this.fireDefense = calculateFireDefense();
        this.explosionDefense = calculateExplosionDefense();
        this.projectileDefense = calculateProjectileDefense();

        this.bonusHealth = calculateBonusHealth();
        calculateTotalHealth();
        this.speed = calculateSpeed();
        this.abilities = calculateAbilities();
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(128);
    }

    private int calculateDefense() {
        this.defense = 0;
        int protectionLevel = 0;
        if (this.helmet != null) {
            this.defense += getBaseArmorDefense(this.helmet.getType());
            protectionLevel += this.helmet.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        }
        if (this.chestplate != null) {
            this.defense += getBaseArmorDefense(this.chestplate.getType());
            protectionLevel += this.chestplate.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        }
        if (this.leggings != null) {
            this.defense += getBaseArmorDefense(this.leggings.getType());
            protectionLevel += this.leggings.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        }
        if (this.boots != null) {
            this.defense += getBaseArmorDefense(this.boots.getType());
            protectionLevel += this.boots.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        }

        this.defense += (Math.pow(protectionLevel, 2));
        return this.defense;
    }

    private int calculateFireDefense() {
        this.fireDefense = 0;
        int protectionLevel = 0;
        if (this.helmet != null)
            protectionLevel += this.helmet.getEnchantmentLevel(Enchantment.PROTECTION_FIRE);
        if (this.chestplate != null)
            protectionLevel += this.chestplate.getEnchantmentLevel(Enchantment.PROTECTION_FIRE);
        if (this.leggings != null)
            protectionLevel += this.leggings.getEnchantmentLevel(Enchantment.PROTECTION_FIRE);
        if (this.boots != null)
            protectionLevel += this.boots.getEnchantmentLevel(Enchantment.PROTECTION_FIRE);
        this.fireDefense += (Math.pow(protectionLevel, 2));
        return this.fireDefense;
    }

    private int calculateExplosionDefense() {
        this.explosionDefense = 0;
        int protectionLevel = 0;
        if (this.helmet != null)
            protectionLevel += this.helmet.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS);
        if (this.chestplate != null)
            protectionLevel += this.chestplate.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS);
        if (this.leggings != null)
            protectionLevel += this.leggings.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS);
        if (this.boots != null)
            protectionLevel += this.boots.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS);
        this.explosionDefense += (Math.pow(protectionLevel, 2));
        return this.explosionDefense;
    }

    private int calculateProjectileDefense() {
        this.projectileDefense = 0;
        int protectionLevel = 0;
        if (this.helmet != null)
            protectionLevel += this.helmet.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE);
        if (this.chestplate != null)
            protectionLevel += this.chestplate.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE);
        if (this.leggings != null)
            protectionLevel += this.leggings.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE);
        if (this.boots != null)
            protectionLevel += this.boots.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE);
        this.projectileDefense += (Math.pow(protectionLevel, 2));
        return this.projectileDefense;
    }

    public double calculateBaseHealth() {
        if (player.getLevel() == 1) { return 100; }
        return 100 + (Math.floor(Math.pow(player.getLevel(), 1.845142)));  // 100 + 12x where x is level - 1
    }

    private double calculateBonusHealth() {
        double growthFactor = 0;
        // Attempt to grab the Growth enchant level for all gear, if we get a nullptr, they don't have a helmet, if we get an illegalarg, they dont have growth
        try {
            growthFactor += enchantmentManager.getEnchantLevel(this.helmet, CustomEnchantType.GROWTH);
        } catch (IllegalArgumentException | NullPointerException ignored) {
        }
        try {
            growthFactor += enchantmentManager.getEnchantLevel(this.chestplate, CustomEnchantType.GROWTH);
        } catch (IllegalArgumentException | NullPointerException ignored) {
        }
        try {
            growthFactor += enchantmentManager.getEnchantLevel(this.leggings, CustomEnchantType.GROWTH);
        } catch (IllegalArgumentException | NullPointerException ignored) {
        }
        try {
            growthFactor += enchantmentManager.getEnchantLevel(this.boots, CustomEnchantType.GROWTH);
        } catch (IllegalArgumentException | NullPointerException ignored) {
        }

        // Best growth currently is Growth %5 x 20, so best HP we can have is +100% HP
        player.setHealthScale(20 + growthFactor);
        this.bonusHealth = growthFactor * .05 * calculateBaseHealth();
        return this.bonusHealth;
    }

    private double calculateTotalHealth(){
        double totalHealth = calculateBonusHealth() + calculateBaseHealth();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(totalHealth);
        return totalHealth;
    }

    private int calculateSpeed() {
        this.speed = 100;
        return speed;  // TODO: Add armor that modifies speed
    }

    private ArrayList<CustomAbility> calculateAbilities() {
        return new ArrayList<>();
    }

    /**
     * All armor in the game has a flat resist value, that is defined here
     *
     * @param armor The ItemStack that the player is wearing
     * @return a dmg amount of resist
     */
    private int getBaseArmorDefense(Material armor) {
        switch (armor) {

            case LEATHER_CHESTPLATE:
                return 12;
            case LEATHER_LEGGINGS:
                return 9;
            case LEATHER_HELMET:
            case TURTLE_HELMET:
                return 7;
            case LEATHER_BOOTS:
                return 5;

            case GOLDEN_CHESTPLATE:
                return 15;
            case GOLDEN_LEGGINGS:
                return 12;
            case GOLDEN_HELMET:
                return 10;
            case GOLDEN_BOOTS:
                return 8;

            case CHAINMAIL_CHESTPLATE:
                return 35;
            case CHAINMAIL_LEGGINGS:
                return 28;
            case CHAINMAIL_HELMET:
                return 25;
            case CHAINMAIL_BOOTS:
                return 21;

            case IRON_CHESTPLATE:
                return 60;
            case IRON_LEGGINGS:
                return 49;
            case IRON_HELMET:
                return 45;
            case IRON_BOOTS:
                return 40;

            case DIAMOND_CHESTPLATE:
                return 80;
            case DIAMOND_LEGGINGS:
                return 70;
            case DIAMOND_HELMET:
                return 64;
            case DIAMOND_BOOTS:
                return 60;

            case ELYTRA:
                return 20;

            default:
                return 0;
        }
    }


}
