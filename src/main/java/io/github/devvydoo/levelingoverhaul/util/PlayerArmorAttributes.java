package io.github.devvydoo.levelingoverhaul.util;

import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomItemManager;
import io.github.devvydoo.levelingoverhaul.enchantments.EnchantmentManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerArmorAttributes {

    private EnchantmentManager enchantmentManager;
    private CustomItemManager customItemManager;

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

    public PlayerArmorAttributes(EnchantmentManager enchantmentManager, CustomItemManager customItemManager, Player player) {
        this.enchantmentManager = enchantmentManager;
        this.customItemManager = customItemManager;
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
            this.defense += getBaseArmorDefense(this.helmet);
            protectionLevel = this.helmet.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            int armorTier = getArmorTier(this.helmet);
            this.defense += (Math.pow(protectionLevel, 1.25 + .25 * armorTier));
        }
        if (this.chestplate != null) {
            this.defense += getBaseArmorDefense(this.chestplate);
            protectionLevel = this.chestplate.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            int armorTier = getArmorTier(this.chestplate);
            this.defense += (Math.pow(protectionLevel, 1.25 + .25 * armorTier));
        }
        if (this.leggings != null) {
            this.defense += getBaseArmorDefense(this.leggings);
            protectionLevel = this.leggings.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            int armorTier = getArmorTier(this.leggings);
            this.defense += (Math.pow(protectionLevel, 1.25 + .25 * armorTier));
        }
        if (this.boots != null) {
            this.defense += getBaseArmorDefense(this.boots);
            protectionLevel = this.boots.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            int armorTier = getArmorTier(this.boots);
            this.defense += (Math.pow(protectionLevel, 1.25 + .25 * armorTier));
        }

        return this.defense;
    }

    private int getArmorTier(ItemStack armor) {

        if (customItemManager.isDragonHelmet(armor) || customItemManager.isDragonChestplate(armor) || customItemManager.isDragonLeggings(armor) || customItemManager.isDragonBoots(armor)){
            return 6;
        }

        switch (armor.getType()){
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return 5;
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
                return 4;
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:
                return 3;
            case GOLDEN_BOOTS:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_HELMET:
            case GOLDEN_LEGGINGS:
            case ELYTRA:
                return 2;
            case LEATHER_HELMET:
            case LEATHER_BOOTS:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case TURTLE_HELMET:
                return 1;
            default:
                return 0;
        }
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
        AttributeInstance playerMaxHPAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        playerMaxHPAttribute.setBaseValue(totalHealth);
        if (player.getHealth() > playerMaxHPAttribute.getBaseValue())
            player.setHealth(playerMaxHPAttribute.getBaseValue());
        return totalHealth;
    }

    private int calculateSpeed() {
        this.speed = 100;
        return speed;  // TODO: Add armor that modifies speed
    }

    private ArrayList<CustomAbility> calculateAbilities() {
        ArrayList<CustomAbility> list = new ArrayList<>();
        player.setAllowFlight(false);

        if (helmet != null && chestplate != null && leggings != null & boots != null) {
            if (customItemManager.isDragonHelmet(helmet) && customItemManager.isDragonChestplate(chestplate) && customItemManager.isDragonLeggings(leggings) && customItemManager.isDragonBoots(boots)){
                list.add(CustomAbility.DRAGON_FLY);
                if (!player.getAllowFlight()) {
                    player.setAllowFlight(true);
                    player.sendMessage(ChatColor.GREEN + "You can now fly!");
                }
            }
        }

        return list;
    }

    /**
     * All armor in the game has a flat resist value, that is defined here
     *
     * @param armor The ItemStack that the player is wearing
     * @return a dmg amount of resist
     */
    private int getBaseArmorDefense(ItemStack armor) {

        switch (armor.getType()) {

            case DRAGON_HEAD:
                if (customItemManager.isDragonHelmet(armor))
                    return 185;
            case LEATHER_CHESTPLATE:
                if (customItemManager.isDragonChestplate(armor))
                    return 220;
                return 12;
            case LEATHER_LEGGINGS:
                if (customItemManager.isDragonLeggings(armor))
                    return 200;
                return 9;
            case LEATHER_HELMET:
            case TURTLE_HELMET:
                return 7;
            case LEATHER_BOOTS:
                if (customItemManager.isDragonBoots(armor))
                    return 175;
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
