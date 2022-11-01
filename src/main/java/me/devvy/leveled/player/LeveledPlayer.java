package me.devvy.leveled.player;

import me.devvy.leveled.items.CustomItemManager;
import me.devvy.leveled.enchantments.EnchantmentManager;
import me.devvy.leveled.items.CustomItemType;
import me.devvy.leveled.player.abilities.CustomAbility;
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

    private final CustomItemManager customItemManager;

    private final Player spigotPlayer;

    private final PlayerExperience experience;
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

    public LeveledPlayer(CustomItemManager customItemManager, Player player) {
        this.customItemManager = customItemManager;
        this.spigotPlayer = player;
        this.experience = new PlayerExperience(this);
        updateAttributes();
        this.spigotPlayer.setInvulnerable(false);
        this.spigotPlayer.setGlowing(false);
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
        return 1 + percent;
    }

    public int getDefense() {
        return defense;
    }

    public double getEnvResist(){
        PotionEffect resistPot = spigotPlayer.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        int bonusResist = 0;
        if (resistPot != null)
            bonusResist += (resistPot.getAmplifier() * 150);
        int resistStat = defense + bonusResist;
        return Math.pow(.5, resistStat/100.);
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

        this.strength = 10 + spigotPlayer.getLevel() * 2;

        calculateDefense();
        calculateFireDefense();
        calculateExplosionDefense();
        calculateProjectileDefense();

        this.bonusHealth = calculateBonusHealth();
        calculateTotalHealth();
        this.speed = calculateSpeed();
        this.abilities = calculateAbilities();
        spigotPlayer.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(128);
    }

    private void calculateDefense() {
        this.defense = 10 + (6 * spigotPlayer.getLevel() / 5);
        int protectionLevel;

        ItemStack[] armorPieces = new ItemStack[]{this.helmet, this.chestplate, this.leggings, this.boots};

        for (ItemStack armor : armorPieces){
            if (armor == null)
                continue;

            int armorDef = getBaseArmorDefense(armor);
            protectionLevel = armor.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            armorDef += 1 + (protectionLevel / 10.);
            this.defense += armorDef;
        }
    }

    private void calculateFireDefense() {
        this.fireDefense = 0;
        int protectionLevel = 0;

        ItemStack[] armorPieces = new ItemStack[]{this.helmet, this.chestplate, this.leggings, this.boots};
        for (ItemStack armor : armorPieces)
            if (armor != null)
                protectionLevel += armor.getEnchantmentLevel(Enchantment.PROTECTION_FIRE);

        this.fireDefense += (Math.pow(protectionLevel, 2));
    }

    private void calculateExplosionDefense() {
        this.explosionDefense = 0;
        int protectionLevel = 0;

        ItemStack[] armorPieces = new ItemStack[]{this.helmet, this.chestplate, this.leggings, this.boots};
        for (ItemStack armor : armorPieces)
            if (armor != null)
                protectionLevel += armor.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS);

        this.explosionDefense += (Math.pow(protectionLevel, 2));
    }

    private void calculateProjectileDefense() {
        this.projectileDefense = 0;
        int protectionLevel = 0;

        ItemStack[] armorPieces = new ItemStack[]{this.helmet, this.chestplate, this.leggings, this.boots};
        for (ItemStack armor : armorPieces)
            if (armor != null)
                protectionLevel += armor.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE);

        this.projectileDefense += (Math.pow(protectionLevel, 2));
    }

    public double calculateBaseHealth() {
        return 100 + ((2 * spigotPlayer.getLevel()/5.0) - 1) * 10;
    }

    private double calculateBonusHealth() {
        double growthFactor = 0;
        // Attempt to grab the Growth enchant level for all gear, if we get a nullptr, they don't have a helmet, if we get an illegalarg, they dont have growth
        ItemStack[] armorPieces = new ItemStack[]{this.helmet, this.chestplate, this.leggings, this.boots};
        for (ItemStack armor : armorPieces){
            if (armor != null && armor.getItemMeta() != null)
                growthFactor += armor.getEnchantmentLevel(EnchantmentManager.GROWTH);
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
        if (boots != null && boots.getItemMeta() != null)
            speedsterLevel += boots.getEnchantmentLevel(EnchantmentManager.SPEEDSTER);
        if (speedsterLevel > 0)
            speed += (speedsterLevel * .03);
        spigotPlayer.setWalkSpeed((float) speed);
        return speed;  // TODO: Add armor that modifies speed
    }

    private ArrayList<CustomAbility> calculateAbilities() {
        ArrayList<CustomAbility> list = new ArrayList<>();

        if (helmet != null && chestplate != null && leggings != null & boots != null) {
            if (customItemManager.hasFullSetBonus(spigotPlayer, CustomAbility.BOUNDLESS_ROCKETS)){
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

        // Try to get a custom defense value
        CustomItemType type = customItemManager.getCustomItemType(armor);
        if (type != null && type.CATEGORY == CustomItemType.Category.ARMOR)
            return type.STAT_AMOUNT;

        CustomItemType.Category fallbackCategory = CustomItemType.Category.getFallbackCategory(armor.getType());
        if (fallbackCategory == CustomItemType.Category.ARMOR)
            return CustomItemType.getFallbackStat(armor.getType());

        return 0;
    }


}
