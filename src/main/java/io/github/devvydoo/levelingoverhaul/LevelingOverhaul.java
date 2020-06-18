package io.github.devvydoo.levelingoverhaul;

import io.github.devvydoo.levelingoverhaul.commands.*;
import io.github.devvydoo.levelingoverhaul.items.CustomItemManager;
import io.github.devvydoo.levelingoverhaul.enchantments.EnchantmentManager;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.ExplosiveTouchEnchantment;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.FoodEnchantments;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.HomingArrows;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.Infinity;
import io.github.devvydoo.levelingoverhaul.enchantments.gui.AnvilInterface;
import io.github.devvydoo.levelingoverhaul.enchantments.gui.EnchantingInterface;
import io.github.devvydoo.levelingoverhaul.items.GlobalItemManager;
import io.github.devvydoo.levelingoverhaul.listeners.monitors.PlayerChatListener;
import io.github.devvydoo.levelingoverhaul.listeners.monitors.PlayerJoinListeners;
import io.github.devvydoo.levelingoverhaul.listeners.monitors.PlayerNametags;
import io.github.devvydoo.levelingoverhaul.listeners.progression.*;
import io.github.devvydoo.levelingoverhaul.managers.*;
import io.github.devvydoo.levelingoverhaul.mobs.BossManager;
import io.github.devvydoo.levelingoverhaul.mobs.MobManager;
import io.github.devvydoo.levelingoverhaul.party.PartyManager;
import io.github.devvydoo.levelingoverhaul.player.ActionBarManager;
import io.github.devvydoo.levelingoverhaul.player.LeveledPlayerManager;
import io.github.devvydoo.levelingoverhaul.player.ScoreboardManager;
import io.github.devvydoo.levelingoverhaul.items.Recipes;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;


public final class LevelingOverhaul extends JavaPlugin {

    // TODO: Make this not hacky
    private NamespacedKey NAMETAG_KEY;

    public NamespacedKey getNametagKey() {
        return NAMETAG_KEY;
    }

    private CustomItemManager customItemManager;
    private EnchantmentManager enchantmentManager;
    private GlobalItemManager globalItemManager;

    private MobManager mobManager;
    private BossManager bossManager;
    private LeveledPlayerManager playerManager;
    private GlobalDamageManager damageManager;

    private ActionBarManager actionBarManager;
    private PartyManager partyManager;
    private ScoreboardManager scoreboardManager;

    private Advancement enchantAdvancement;

    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }

    public GlobalItemManager getGlobalItemManager() {
        return globalItemManager;
    }

    public EnchantmentManager getEnchantmentManager() {
        return enchantmentManager;
    }

    public MobManager getMobManager() {
        return this.mobManager;
    }

    public LeveledPlayerManager getPlayerManager() {
        return playerManager;
    }

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public BossManager getBossManager() {
        return bossManager;
    }

    public Advancement getEnchantAdvancement() {
        return enchantAdvancement;
    }

    @Override
    public void onEnable() {

        // First do some checks to make sure the server is setup right...
        // Can HP be really high?
        if (getServer().spigot().getSpigotConfig().getDouble("settings.attribute.maxHealth.max") < 2000000000){
            getLogger().severe("Your spigot server is not setup correctly for this plugin! You must set max health attribute in spigot.yml to at least 2 billion or higher! (2000000000)\nThe current setting is: " + Math.round(getServer().spigot().getSpigotConfig().getDouble("settings.attribute.maxHealth.max")));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Can attack damage be really high?
        if (getServer().spigot().getSpigotConfig().getDouble("settings.attribute.attackDamage.max") < 10000000){
            getLogger().severe("Your spigot server is not setup correctly for this plugin! You must set max attack damage attribute in spigot.yml to at least 10 million or higher! (10000000)\nThe current setting is: " + Math.round(getServer().spigot().getSpigotConfig().getDouble("settings.attribute.attackDamage.max")));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Is flight enabled?
        if (!getServer().getAllowFlight())
            getLogger().warning("The server does not have flight enabled, which may cause some issues in the late game. It is recommended that flight be enabled using this plugin.");

        // keep inventory should be true, items are balanced to be like an mmo TODO: make config option
        for (World w : getServer().getWorlds())
            w.setGameRule(GameRule.KEEP_INVENTORY, true);

        // Plugin startup logic
        NAMETAG_KEY = new NamespacedKey(this, "name");
        // We need this key for later, not sure if there's a better way to do this because i don't really understand NamespacedKeys :(
        Iterator<Advancement> advanceIterator = getServer().advancementIterator();
        while (advanceIterator.hasNext()) {
            Advancement advancement = advanceIterator.next();
            if (advancement.getKey().toString().equals("minecraft:story/enchant_item")) {
                enchantAdvancement = advancement;
                break;
            }
        }

        enchantmentManager = new EnchantmentManager(this);
        customItemManager = new CustomItemManager();
        globalItemManager = new GlobalItemManager(this);
        playerManager = new LeveledPlayerManager(this);
        damageManager = new GlobalDamageManager(this);
        actionBarManager = new ActionBarManager(this);
        partyManager = new PartyManager();
        scoreboardManager = new ScoreboardManager(this);
        bossManager = new BossManager(this);
        new TipAnnounceManager(this);

        // Listeners that change how natural progression works
        getServer().getPluginManager().registerEvents(new ProgressionModifyingListeners(), this);
        getServer().getPluginManager().registerEvents(playerManager, this);
        getServer().getPluginManager().registerEvents(damageManager, this);
        getServer().getPluginManager().registerEvents(globalItemManager, this);

        // Register listeners regarding experience
        getServer().getPluginManager().registerEvents(new PlayerJoinListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeveledUpListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerExperienceGainListeners(this), this);
        getServer().getPluginManager().registerEvents(new VanillaExperienceCancellingListeners(this), this);

        // Listeners involving level capped gear
        getServer().getPluginManager().registerEvents(new PlayerArmorListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerToolListeners(this), this);
        getServer().getPluginManager().registerEvents(new EnchantmentListeners(), this);
        getServer().getPluginManager().registerEvents(new BrewingListeners(), this);
        getServer().getPluginManager().registerEvents(new MiscEquipmentListeners(this), this);

        // Listeners involving custom enchantments
        getServer().getPluginManager().registerEvents(new EnchantingInterface(this), this);
        getServer().getPluginManager().registerEvents(new AnvilInterface(this), this);
        getServer().getPluginManager().registerEvents(new ExplosiveTouchEnchantment(this), this);
        getServer().getPluginManager().registerEvents(new FoodEnchantments(this), this);
        getServer().getPluginManager().registerEvents(new Infinity(), this);
        getServer().getPluginManager().registerEvents(new HomingArrows(this), this);
        getServer().getPluginManager().registerEvents(customItemManager, this);

        // Listeners involving chat
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);

        // Listeners involving the scoreboard
        getServer().getPluginManager().registerEvents(new PlayerNametags(this), this);
        getServer().getPluginManager().registerEvents(actionBarManager, this);
        getServer().getPluginManager().registerEvents(partyManager, this);
        getServer().getPluginManager().registerEvents(scoreboardManager, this);

        // Register custom recipes
        Recipes.registerRecipes(this);

        // Listeners involving mobs
        mobManager = new MobManager(getServer().getWorlds());  // Initialize all worlds.
        getServer().getPluginManager().registerEvents(mobManager, this);
        getServer().getPluginManager().registerEvents(new DamagePopupManager(this), this);
        getServer().getPluginManager().registerEvents(bossManager, this);

        // Register commands
        PartyCommand partyCommand = new PartyCommand(this);
        getCommand("adminmob").setExecutor(new TestMobCommand());
        getCommand("stats").setExecutor(new PlayerStatsCommand(this));
        getCommand("party").setExecutor(partyCommand);
        getCommand("party").setTabCompleter(partyCommand);
        getCommand("adminlevel").setExecutor(new DebugLevelSetter(this));
        getCommand("adminenchant").setExecutor(new DebugEnchant(this));
        getCommand("nametag").setExecutor(new NametagCommand(this));
    }

    @Override
    public void onDisable() {
        getServer().resetRecipes();  // Reset the recipes TODO: Currently this wont support other plugins if we are unloading, figure out a way to make this work
    }
}
