package me.devvy.leveled.mobs;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.listeners.monitors.PlayerNametags;
import me.devvy.leveled.mobs.custommobs.CustomLeveledEntityType;
import me.devvy.leveled.util.DamageEquationHelpers;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LeveledLivingEntity {

    // TODO: make config option, sets colors to use before mob names
    public static final String LEVEL_COLOR = ChatColor.GRAY + "" + ChatColor.BOLD;
    public static final String HOSTILE_MOB_COLOR = ChatColor.RED.toString();
    public static final String NEUTRAL_MOB_COLOR = ChatColor.WHITE.toString();
    public static final String TAMED_MOB_COLOR = ChatColor.GREEN.toString();
    public static final String BOSS_MOB_COLOR = ChatColor.DARK_PURPLE.toString();

    protected final LivingEntity entity;

    public static String getEntityNametagColor(LivingEntity entity) {

        if (entity instanceof Boss)
            return BOSS_MOB_COLOR;
        else if (entity instanceof Monster)
            return HOSTILE_MOB_COLOR;
        else if (entity instanceof Tameable && ((Tameable) entity).isTamed())
            return TAMED_MOB_COLOR;
        else
            return NEUTRAL_MOB_COLOR;
    }

    /**
     * Invoke when we are creating a new entity, we calculate the level and such somewhere else
     *
     * @param entity - The LivingEntity that was just created
     */
    public LeveledLivingEntity(LivingEntity entity, boolean updateStats) {
        this.entity = entity;

        // Check if they have the stats required to exist
        if (!entity.getPersistentDataContainer().has(MobManager.MOB_LEVEL_KEY, PersistentDataType.INTEGER))
            setLevel(calculateDefaultEntityLevel(entity), updateStats);

        if (!entity.getPersistentDataContainer().has(MobManager.MOB_NAME_KEY, PersistentDataType.STRING))
            setName(entity.getName());

        update();
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public int getLevel() {
        // Returns the level of the mob, defaults to 1 if they didn't have a level
        return entity.getPersistentDataContainer().getOrDefault(MobManager.MOB_LEVEL_KEY, PersistentDataType.INTEGER, 1);
    }

    public void setLevel(int newLevel) {
        setLevel(newLevel, false);
    }

    public void setLevel(int newLevel, boolean updateStats) {
        entity.getPersistentDataContainer().set(MobManager.MOB_LEVEL_KEY, PersistentDataType.INTEGER, newLevel);
        if (updateStats)
            setEntityAttributes(entity, newLevel);
    }

    public String getName() {
        return entity.getPersistentDataContainer().getOrDefault(MobManager.MOB_NAME_KEY, PersistentDataType.STRING, entity.getName());
    }

    public void setName(String newName) {
        entity.getPersistentDataContainer().set(MobManager.MOB_NAME_KEY, PersistentDataType.STRING, newName);
    }

    public CustomLeveledEntityType getCustomType() {
        int enumIndex = entity.getPersistentDataContainer().getOrDefault(MobManager.MOB_CUSTOM_FLAG_KEY, PersistentDataType.INTEGER, -1);

        if (enumIndex == -1)
            return null;

        return CustomLeveledEntityType.values()[enumIndex];
    }

    public void setCustomType(CustomLeveledEntityType type) {
        entity.getPersistentDataContainer().set(MobManager.MOB_CUSTOM_FLAG_KEY, PersistentDataType.INTEGER, type.ordinal());
    }

    // Updates the entity's nametag, setting things like hp, name, or anything else we should display
    public void update() {
        update(0);
    }

    public void update(double deltaHP) {
        if (!entity.isValid())
            return;

        int hp = Math.max((int) Math.round(entity.getHealth() + deltaHP), 0);
        String hpTextColor = PlayerNametags.getChatColorFromHealth(hp, entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        entity.setCustomName(LEVEL_COLOR + "Lv. " + getLevel() + " " + getEntityNametagColor(entity) + getName() + " " + ChatColor.DARK_RED + "❤" + hpTextColor + hp);
    }



    /**
     * NOTE:
     *
     * This part is very ugly but temporarily here until an actual config system is in place
     */

    public static int getAverageNearbyPlayerLevel(LivingEntity entity, int distance) {

        int numPlayers = 0;
        int numLevels = 0;

        for (Player p : entity.getWorld().getNearbyPlayers(entity.getLocation(), distance)) {
            numPlayers++;
            numLevels += p.getLevel();
        }

        if (numPlayers == 0)
            return 1;

        return numLevels / numPlayers;
    }

    public static int calculateDefaultEntityLevel(LivingEntity entity) {
        // We need to do 2 things, first, calculate what level the entity should be. Then setup their statistics

        switch (entity.getType()) {

            // Early game mobs, stick with player level but cap at 30
            case ZOMBIE:
            case SPIDER:
            case SKELETON:
            case CREEPER:
                return getAverageNearbyPlayerLevel(entity, 250);

            // Caves
            case CAVE_SPIDER:
            case SLIME:
            case WITCH:
            case GLOW_SQUID:
                return 7 + (int)(Math.random() * 8);

            // Desert mobs ~15-20
            case HUSK:
            case STRAY:
                return 15 + (int)(Math.random() * 5);

            // Ocean mobs ~25-30
            case GUARDIAN:
            case DROWNED:
                return 25 + (int)(Math.random() * 5);

            case ELDER_GUARDIAN:
                return 35;

            // Village and pillage ~35-40
            case VILLAGER:
            case PILLAGER:
            case VINDICATOR:
            case VEX:
            case RAVAGER:
            case IRON_GOLEM:
            case ZOMBIE_VILLAGER:
            case ILLUSIONER:
            case EVOKER:
                return 35 + (int)(Math.random() * 5);

            // Stronghold
            case SILVERFISH:
                return 55 + (int)(Math.random() * 5);

            case ENDERMAN:
                switch (entity.getWorld().getEnvironment()){
                    case NORMAL:
                        return (int) (Math.random() * 16 + 50);

                    case NETHER:
                        return (int) (Math.random() * 25 + 45);

                    case THE_END:

                        Biome biome = entity.getLocation().getBlock().getBiome();
                        if (biome.equals(Biome.THE_END))
                            return (int) (Math.random() * 5 + 68);
                        else if (biome.equals(Biome.END_HIGHLANDS))
                            return (int) (Math.random() * 5 + 77);
                        else if (biome.equals(Biome.END_MIDLANDS))
                            return (int) (Math.random() * 5 + 73);
                        else
                            return (int) (Math.random() * 5 + 80);

                    default:
                        Leveled.getPlugin(Leveled.class).getLogger().warning("Could not determine environment for enderman, defaulting to level 1");
                        return 1;
                }

            case SHULKER:
            case ENDERMITE:
                return (int) (Math.random() * 6 + 70);

            case WITHER:  // TODO: Give custom logic
                return 80;

            case ENDER_DRAGON:

                int level =  2;
                int totalPlayerLevels = 0;
                int numPlayers = entity.getWorld().getPlayers().size();

                for (Player p : entity.getWorld().getPlayers())
                    totalPlayerLevels += p.getLevel();

                if (numPlayers > 0 && totalPlayerLevels / numPlayers > 68)
                    return level + totalPlayerLevels / numPlayers;
                return 72;

            // Nether plains
            case ZOMBIFIED_PIGLIN:
            case MAGMA_CUBE:
                return 38 + (int)(Math.random() * 7);

            case GHAST:
                return 42 + (int)(Math.random() * 7);

            // Nether fortress
            case BLAZE:
            case HOGLIN:
            case STRIDER:
            case ZOGLIN:
                return 45 + (int)(Math.random() * 5);

            case WITHER_SKELETON:
                return 50 + (int)(Math.random() * 5);

            // End game nether
            case PIGLIN:
            case PIGLIN_BRUTE:
                return 89 + (int)(Math.random() * 6);

            // Scary warden dude
            case WARDEN:
                return 100;

            case POLAR_BEAR:
            case TRADER_LLAMA:
                return 15;

            case PHANTOM:
                Phantom phantom = (Phantom) entity;
                if (phantom.getSpawningEntity() != null) {
                    Player target = Leveled.getPlugin(Leveled.class).getServer().getPlayer(phantom.getSpawningEntity());

                    if (target != null)
                        return target.getLevel();

                }
                return 10;

            case WOLF:
            case CAT:
            case PARROT:
            case HORSE:
            case SKELETON_HORSE:
            case ZOMBIE_HORSE:
            case LLAMA:
            case MULE:
            case DONKEY:
                Tameable tamedEntity = (Tameable) entity;
                return tamedEntity.getOwner() != null && tamedEntity.getOwner() instanceof Player ? ((Player)tamedEntity.getOwner()).getLevel() : (int)(Math.random() * 5 + 10);

            case BEE:
            case WANDERING_TRADER:
            case FOX:
                return (int)(Math.random() * 5 + 3);

            case PIG:
            case COW:
            case MUSHROOM_COW:
            case GOAT:
            case SHEEP:
            case PANDA:
            case SQUID:
            case DOLPHIN:
                return  Math.random() < .5 ? 2 : 3;

            case CHICKEN:
            case SALMON:
            case RABBIT:
            case COD:
            case AXOLOTL:
            case BAT:
            case OCELOT:
            case SNOWMAN:
            case PUFFERFISH:
            case TROPICAL_FISH:
            case TURTLE:
            case ARMOR_STAND:
                return 1;

            default:
                Leveled.getPlugin(Leveled.class).getLogger().warning("Entity " + entity + " was not defined to have a level in MobManager. Defaulting to level 1");
                return 1;
        }
    }

    /***
     * Sets up entity attributes upon being spawned, or when its stats are first calculated
     *
     * @param entity The entity to calculate stats for
     * @param level The level the entity is/ is supposed to be
     */
    public static void setEntityAttributes(LivingEntity entity, int level){

        switch (entity.getType()){

            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case HUSK:
            case DROWNED:
                if (entity.getEquipment() != null) {
                    if (level >= 20 && level < 30)
                        entity.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
                    else if (level <= 30)
                        entity.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
                    else if (level <= 35)
                        entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                    else if (level <= 40)
                        entity.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                    else {
                        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                        sword.addEnchantment(Enchantment.DURABILITY, 1);
                        entity.getEquipment().setItemInMainHand(sword);
                        entity.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                    }
                    entity.getEquipment().setItemInMainHandDropChance(0);  // NEVER ALLOW IT TO DROP
                }
                break;

            case CAVE_SPIDER:
            case SPIDER:
                if (level > 30) { entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, level / 30)); }
                break;

            case SKELETON:
                if (entity.getEquipment() != null && level > 25) {
                    ItemStack bow = new ItemStack(Material.BOW);
                    bow.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                    entity.getEquipment().setItemInMainHand(bow);
                    entity.getEquipment().setItemInMainHandDropChance(0);
                    entity.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                }
                break;

            case CREEPER:
                Creeper creeper = (Creeper) entity;
                if (level > 30 && level < 60) {
                    if (Math.random() < level / 100.) {
                        creeper.setPowered(true);
                    }
                } else if (level >= 60) {
                    creeper.setPowered(true);
                }
                creeper.setMaxFuseTicks((int) (level > 80 ? 2 : 40 - level / 3.));
                break;

            case ENDERMAN:
                if (entity.getEquipment() != null) {
                    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                    sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
                    entity.getEquipment().setItemInMainHand(sword);
                    entity.getEquipment().setItemInMainHandDropChance(0);
                }
                break;

            case WITHER:  // TODO: Give custom logic
            case ELDER_GUARDIAN: // TODO: Give custom logic
            case ENDER_DRAGON:
                break;

            case ZOMBIFIED_PIGLIN:
                if (entity.getEquipment() != null) {
                    ItemStack goldSword = new ItemStack(Material.GOLDEN_SWORD);
                    goldSword.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                    entity.getEquipment().setItemInMainHand(goldSword);
                    entity.getEquipment().setItemInMainHandDropChance(0);
                }
                break;

            case WITHER_SKELETON:
                if (entity.getEquipment() != null) {
                    ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
                    stoneSword.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                    entity.getEquipment().setItemInMainHand(stoneSword);
                    entity.getEquipment().setItemInMainHandDropChance(0);
                }
                break;

            default:
                Leveled.getPlugin(Leveled.class).getLogger().finest("Entity " + entity + " was not defined to have attributes in MobManager. Defaulting to vanilla stats");
                break;
        }

        double expectedHP = calculateEntityHealth(entity, level);
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
        entity.setHealth(expectedHP);
    }

    /**
     * Calculates how much HP an entity should have based on a level
     *
     * @param entity The entity to calculate HP for
     * @param level The level the entity will be / already is
     * @return A double amount representing max HP
     */
    public static double calculateEntityHealth(LivingEntity entity, int level){

        double baseHP = DamageEquationHelpers.getExpectedEHPAtLevel(level);

        double multiplier;

        switch (entity.getType()) {

            // Passive mobs
            case SHEEP:
            case COW:
            case GOAT:
            case PIG:
            case MULE:
            case MUSHROOM_COW:
            case HORSE:
            case SKELETON_HORSE:
            case SQUID:
            case DONKEY:
            case DOLPHIN:
            case TURTLE:
            case VILLAGER:
            case ZOMBIE_HORSE:
            case TRADER_LLAMA:
            case WANDERING_TRADER:
                multiplier = .75;
                break;

            case OCELOT:
            case PARROT:
            case TROPICAL_FISH:
            case SNOWMAN:
            case CHICKEN:
            case RABBIT:
            case SALMON:
            case BAT:
            case CAT:
            case COD:
            case AXOLOTL:
                multiplier = .4;
                break;

            // Babies
            case SILVERFISH:
            case BEE:
            case VEX:
            case ENDERMITE:
            case PUFFERFISH:
                multiplier = .5;
                break;

            // Small tier
            case CREEPER:
            case EVOKER:
            case SPIDER:
            case CAVE_SPIDER:
            case SHULKER:
            case PHANTOM:
            case GHAST:
            case POLAR_BEAR:
            case PANDA:
            case FOX:
            case WOLF:
            case LLAMA:
                multiplier = .8;
                break;


            // Mid tier
            case HUSK:
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case DROWNED:
            case SKELETON:
            case STRAY:
            case BLAZE:
            case ILLUSIONER:
            case ZOMBIFIED_PIGLIN:
            case PIGLIN:
            case PIGLIN_BRUTE:
            case STRIDER:
            case VINDICATOR:
            case PILLAGER:
            case GUARDIAN:
            case WITCH:
                multiplier = 1;
                break;

            // High tier
            case ENDERMAN:
            case WITHER_SKELETON:
            case RAVAGER:
            case IRON_GOLEM:
            case HOGLIN:
            case ZOGLIN:
            case GLOW_SQUID:
                multiplier = 1.35;
                break;

            // Special cases
            case SLIME:
            case MAGMA_CUBE:
                int size = ((Slime) entity).getSize() + 1;
                multiplier = .2 + size * .2;
                break;

            case ELDER_GUARDIAN:
                multiplier = 100;
                break;

            case ENDER_DRAGON:
            case WITHER:
            case GIANT:
                multiplier = 150;
                for (Player ignored : entity.getLocation().getNearbyPlayers(500))
                    multiplier += Math.random() * 50 + 100;
                break;

            case WARDEN:
                multiplier = 200;
                break;

            default:
                Leveled.getPlugin(Leveled.class).getLogger().warning("Came across unexpected entity for HP calculation: " + entity.getType());
                multiplier = 1;
                break;
        }

        multiplier += ((Math.random() - .5) / 10.);
        return level > 15 ? Math.round(baseHP * multiplier / 10) * 10 : Math.round(baseHP * multiplier);
    }


}