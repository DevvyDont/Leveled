package io.github.devvydoo.levelingoverhaul.managers;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantments;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerHealthManager implements Listener {

    private LevelingOverhaul plugin;
    private double baseHP = 100;

    public PlayerHealthManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            double expectedHP = calculatePlayerExpectedHealth(p);
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
            p.setHealth(expectedHP);
        }
    }

    public double getBaseHP() {
        return baseHP;
    }

    public double calculateGrowthFactor(Player player) {
        double growthFactor = 0;
        PlayerInventory inventory = player.getInventory();
        // Attempt to grab the Growth enchant level for all gear, if we get a nullptr, they don't have a helmet, if we get an illegalarg, they dont have growth
        try {
            growthFactor += CustomEnchantments.getEnchantLevel(inventory.getHelmet(), CustomEnchantType.GROWTH);
        } catch (IllegalArgumentException | NullPointerException ignored) {
        }
        try {
            growthFactor += CustomEnchantments.getEnchantLevel(inventory.getChestplate(), CustomEnchantType.GROWTH);
        } catch (IllegalArgumentException | NullPointerException ignored) {
        }
        try {
            growthFactor += CustomEnchantments.getEnchantLevel(inventory.getLeggings(), CustomEnchantType.GROWTH);
        } catch (IllegalArgumentException | NullPointerException ignored) {
        }
        try {
            growthFactor += CustomEnchantments.getEnchantLevel(inventory.getBoots(), CustomEnchantType.GROWTH);
        } catch (IllegalArgumentException | NullPointerException ignored) {
        }
        return growthFactor;
    }

    /**
     * Method used to calculate any bonus HP a player should have (Growth enchant etc.)
     *
     * @param player The player we are calculating bonus hp for
     * @return a double representing bonus HP
     */
    private double getBonusHealth(Player player) {
        double growthFactor = calculateGrowthFactor(player);
        // Best growth currently is Growth 5 x 4, so best HP we can have is 20 * 15 which equals 300 extra HP
        player.setHealthScale(20 + growthFactor);
        return growthFactor * .005 * calculateBaseHealth(player.getLevel());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // Figure out what the player should have and set that
        event.getPlayer().setHealthScale(20);
        event.getPlayer().setHealthScaled(false);
        double percentBeforeJoin = event.getPlayer().getHealth() / event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double hpToSet = event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * percentBeforeJoin;
        event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(calculatePlayerExpectedHealth(event.getPlayer()));
        event.getPlayer().setHealth(hpToSet);
        // Always no matter what display 10 hearts no matter the HP
        new BukkitRunnable() {
            public void run() {
                event.getPlayer().setHealthScale(20 + calculateGrowthFactor(event.getPlayer()));
            }

        }.runTaskLater(plugin, 10);

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRegen(EntityRegainHealthEvent event) {

        if (event.getEntity() instanceof LivingEntity){
            if (((LivingEntity) event.getEntity()).getHealth() < 1) { event.setCancelled(true); return; }
        }

        // This is basically just natural regen
        if (event.getEntity() instanceof Player && event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            Player player = (Player) event.getEntity();
            double maxHP = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            double halfHeartAmount = maxHP / 20.;
            double amountToRegen;
            if (maxHP / 2 > player.getHealth()) {amountToRegen = halfHeartAmount * 1.33; }
            else { amountToRegen = halfHeartAmount * .35; }
            event.setAmount(amountToRegen);
        }
    }

    /**
     * Pass in a player and calculate how much health the player should have based on level AND armor worn and
     * whatever other hp modifying things we have
     *
     * @param player - The Player we are calculating hp for
     * @return a double amount representing how much hp the player should have
     */
    public double calculatePlayerExpectedHealth(Player player) {
        return calculatePlayerExpectedHealth(player, player.getLevel());
    }

    /**
     * Same as the first method, but will calculate using a manual level value. Usually used when we level up
     * The reason we still need the player, is we need to calculate hp gained from the growth enchantment
     *
     * @param player        The player we are calculating hp for
     * @param levelOverride The level to calculate hp for
     * @return a double amount representing how much hp the player should have
     */
    public double calculatePlayerExpectedHealth(Player player, int levelOverride) {
        double extraHP = getBonusHealth(player);
        return calculateBaseHealth(levelOverride) + extraHP;
    }

    /**
     * Pass in a level and calculate how much base hp a player should have for a certain level
     *
     * @param level The int level to calculate hp for
     * @return a double amount representing how much base hp a level should have
     */
    public double calculateBaseHealth(int level) {
        if (level == 1) { return 100; }
        return baseHP + (Math.floor(Math.pow(level, 1.845142)));  // 100 + 12x where x is level - 1
    }
}
