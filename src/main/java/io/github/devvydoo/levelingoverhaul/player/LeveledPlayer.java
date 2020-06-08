package io.github.devvydoo.levelingoverhaul.player;

import io.github.devvydoo.levelingoverhaul.enchantments.enchants.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.items.CustomItemManager;
import io.github.devvydoo.levelingoverhaul.enchantments.EnchantmentManager;
import io.github.devvydoo.levelingoverhaul.player.abilities.CustomAbility;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class LeveledPlayer {

    private EnchantmentManager enchantmentManager;
    private CustomItemManager customItemManager;

    private Player spigotPlayer;

    private PlayerExperience experience;
    private int strength;
    private int defense;
    private int fireDefense;
    private int explosionDefense;
    private int projectileDefense;
    private double bonusHealth;
    private double speed;
    private ArrayList<CustomAbility> abilities;

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    public LeveledPlayer(EnchantmentManager enchantmentManager, CustomItemManager customItemManager, Player player) {
        this.enchantmentManager = enchantmentManager;
        this.customItemManager = customItemManager;
        this.spigotPlayer = player;
        this.experience = new PlayerExperience(this);
        updateAttributes();
    }

    public Player getSpigotPlayer() {
        return spigotPlayer;
    }

    public PlayerExperience getExperience() {
        return experience;
    }

    public void giveExperience(int amount){
        experience.giveExperience(amount);
    }

    public int getStrength() {
        return strength;
    }

    public double getStrengthBonus(){
        double percent = strength / 100.;
        for (PotionEffect potionEffect : spigotPlayer.getActivePotionEffects()){
            if (potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE))
                percent += (1.3 * potionEffect.getAmplifier());
        }
        return percent;
    }

    public int getDefense() {
        return defense;
    }

    public double getEnvResist(){
        PotionEffect resistPot = spigotPlayer.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        int bonusResist = 0;
        if (resistPot != null)
            bonusResist += (resistPot.getAmplifier() * 150);
        return 100. / (defense + bonusResist + 100.);
    }

    public int getFireDefense() {
        return fireDefense;
    }

    public double getFireResist(){
        PotionEffect resistPot = spigotPlayer.getPotionEffect(PotionEffectType.FIRE_RESISTANCE);
        int bonusResist = 0;
        if (resistPot != null)
            bonusResist += (resistPot.getAmplifier() * 150);
        return 100. / (fireDefense + bonusResist + 100.);
    }

    public int getExplosionDefense() {
        return explosionDefense;
    }

    public double getExplosionResist(){
        return 100. / (explosionDefense + 100.);
    }

    public int getProjectileDefense() {
        return projectileDefense;
    }

    public double getProjResist(){
        return 100. / (projectileDefense + 100.);
    }

    public double getBonusHealth() {
        return bonusHealth;
    }

    public double getSpeed() {
        return speed;
    }

    public ArrayList<CustomAbility> getAbilities() {
        return abilities;
    }

    public void updateAttributes() {
        this.helmet = spigotPlayer.getInventory().getHelmet();
        this.chestplate = spigotPlayer.getInventory().getChestplate();
        this.leggings = spigotPlayer.getInventory().getLeggings();
        this.boots = spigotPlayer.getInventory().getBoots();

        this.strength = 2 * spigotPlayer.getLevel() + 98;
        this.defense = calculateDefense();
        this.fireDefense = calculateFireDefense();
        this.explosionDefense = calculateExplosionDefense();
        this.projectileDefense = calculateProjectileDefense();

        this.bonusHealth = calculateBonusHealth();
        calculateTotalHealth();
        this.speed = calculateSpeed();
        this.abilities = calculateAbilities();
        spigotPlayer.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(128);
    }

    private int calculateDefense() {
        this.defense = 0;
        int protectionLevel;

        ItemStack[] armorPieces = new ItemStack[]{this.helmet, this.chestplate, this.leggings, this.boots};

        for (ItemStack armor : armorPieces){
            if (armor == null)
                continue;
            this.defense += getBaseArmorDefense(armor);
            protectionLevel = armor.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            int armorTier = getArmorTier(armor);
            this.defense += (Math.pow(protectionLevel, 1.05 + .25 * armorTier) / 2);
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

        ItemStack[] armorPieces = new ItemStack[]{this.helmet, this.chestplate, this.leggings, this.boots};
        for (ItemStack armor : armorPieces)
            if (armor != null)
                protectionLevel += armor.getEnchantmentLevel(Enchantment.PROTECTION_FIRE);

        this.fireDefense += (Math.pow(protectionLevel, 2));
        return this.fireDefense;
    }

    private int calculateExplosionDefense() {
        this.explosionDefense = 0;
        int protectionLevel = 0;

        ItemStack[] armorPieces = new ItemStack[]{this.helmet, this.chestplate, this.leggings, this.boots};
        for (ItemStack armor : armorPieces)
            if (armor != null)
                protectionLevel += armor.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS);

        this.explosionDefense += (Math.pow(protectionLevel, 2));
        return this.explosionDefense;
    }

    private int calculateProjectileDefense() {
        this.projectileDefense = 0;
        int protectionLevel = 0;

        ItemStack[] armorPieces = new ItemStack[]{this.helmet, this.chestplate, this.leggings, this.boots};
        for (ItemStack armor : armorPieces)
            if (armor != null)
                protectionLevel += armor.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE);

        this.projectileDefense += (Math.pow(protectionLevel, 2));
        return this.projectileDefense;
    }

    public double calculateBaseHealth() {
        if (spigotPlayer.getLevel() <= 1)
            return 100;
        return 100 + Math.floor(Math.pow(spigotPlayer.getLevel() + 5, 2) / 2.);
    }

    private double calculateBonusHealth() {
        double growthFactor = 0;
        // Attempt to grab the Growth enchant level for all gear, if we get a nullptr, they don't have a helmet, if we get an illegalarg, they dont have growth
        ItemStack[] armorPieces = new ItemStack[]{this.helmet, this.chestplate, this.leggings, this.boots};
        for (ItemStack armor : armorPieces){
            try {
                growthFactor += enchantmentManager.getEnchantLevel(armor, CustomEnchantType.GROWTH);
            } catch (IllegalArgumentException | NullPointerException ignored) {
            }
        }

        // Best growth currently is Growth %5 x 20, so best HP we can have is +100% HP
        spigotPlayer.setHealthScale(Math.min(20 + growthFactor, 40));
        this.bonusHealth = growthFactor * .05 * calculateBaseHealth();
        return this.bonusHealth;
    }

    private void calculateTotalHealth(){
        double totalHealth = calculateBonusHealth() + calculateBaseHealth();
        AttributeInstance playerMaxHPAttribute = spigotPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        playerMaxHPAttribute.setBaseValue(totalHealth);
        if (spigotPlayer.getHealth() > playerMaxHPAttribute.getBaseValue())
            spigotPlayer.setHealth(playerMaxHPAttribute.getBaseValue());
    }

    private double calculateSpeed() {
        speed = 0.19982229;
        int speedsterLevel = 0;
        try {
            speedsterLevel = enchantmentManager.getEnchantLevel(boots, CustomEnchantType.SPEEDSTER);
        } catch (IllegalArgumentException | NullPointerException ignored){}
        if (speedsterLevel > 0)
            speed += (speedsterLevel * .03);
        spigotPlayer.setWalkSpeed((float) speed);
        return speed;  // TODO: Add armor that modifies speed
    }

    private ArrayList<CustomAbility> calculateAbilities() {
        ArrayList<CustomAbility> list = new ArrayList<>();

        if (helmet != null && chestplate != null && leggings != null & boots != null) {
            if (customItemManager.isDragonHelmet(helmet) && customItemManager.isDragonChestplate(chestplate) && customItemManager.isDragonLeggings(leggings) && customItemManager.isDragonBoots(boots)){
                list.add(CustomAbility.BOUNDLESS_ROCKETS);
                if (abilities != null && !abilities.contains(CustomAbility.BOUNDLESS_ROCKETS))
                    spigotPlayer.sendTitle(ChatColor.GOLD.toString() + ChatColor.BOLD + "Ability " + ChatColor.WHITE + "Boundless Rockets", ChatColor.GRAY + "Fireworks have a 50% to not consume when Elytra boosting!", 10, 60, 30);
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
                    return 130;
            case LEATHER_CHESTPLATE:
                return 10;
            case LEATHER_LEGGINGS:
                if (customItemManager.isDragonLeggings(armor))
                    return 150;
                return 8;
            case LEATHER_HELMET:
            case TURTLE_HELMET:
                return 5;
            case LEATHER_BOOTS:
                if (customItemManager.isDragonBoots(armor))
                    return 110;
                return 4;

            case GOLDEN_CHESTPLATE:
                return 15;
            case GOLDEN_LEGGINGS:
                return 12;
            case GOLDEN_HELMET:
                return 10;
            case GOLDEN_BOOTS:
                return 8;

            case CHAINMAIL_CHESTPLATE:
                return 30;
            case CHAINMAIL_LEGGINGS:
                return 24;
            case CHAINMAIL_HELMET:
                return 20;
            case CHAINMAIL_BOOTS:
                return 18;

            case IRON_CHESTPLATE:
                return 40;
            case IRON_LEGGINGS:
                return 35;
            case IRON_HELMET:
                return 30;
            case IRON_BOOTS:
                return 28;

            case DIAMOND_CHESTPLATE:
                return 60;
            case DIAMOND_LEGGINGS:
                return 50;
            case DIAMOND_HELMET:
                return 45;
            case DIAMOND_BOOTS:
                return 40;

            case ELYTRA:
                if (customItemManager.isDragonChestplate(armor))
                    return 170;
                return 20;

            default:
                return 0;
        }
    }


}
