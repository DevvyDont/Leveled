package io.github.devvydoo.levellingoverhaul.managers;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerHealthManager implements Listener {

    private LevellingOverhaul plugin;
    private double baseHP = 100;

    public double getBaseHP(){
        return baseHP;
    }

    public PlayerHealthManager(LevellingOverhaul plugin) {
        this.plugin = plugin;
        for (Player p : plugin.getServer().getOnlinePlayers()){
            double expectedHP = calculatePlayerExpectedHealth(p);
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(expectedHP);
            p.setHealth(expectedHP);
            p.setHealthScale(20);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        // Figure out what the player should have and set that
        double percentBeforeJoin = event.getPlayer().getHealth() / event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double hpToSet = event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * percentBeforeJoin;
        event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(calculatePlayerExpectedHealth(event.getPlayer()));
        event.getPlayer().setHealth(hpToSet);
        // Always no matter what display 10 hearts no matter the HP
        event.getPlayer().setHealthScale(20);
    }

    @EventHandler
    public void onPlayerRegen(EntityRegainHealthEvent event){

        // This is basically just natural regen
        if (event.getEntity() instanceof Player && event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)){
            Player player = (Player) event.getEntity();
            double halfHeartAmount = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() / 20.;
            event.setAmount(halfHeartAmount);
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
        return calculateBaseHealth(player.getLevel());
    }

    /**
     * Same as the first method, but will calculate using a manual level value. Usually used when we level up
     * The reason we still need the player, is we need to calculate hp gained from the growth enchantment
     *
     * @param player The player we are calculating hp for
     * @param levelOverride The level to calculate hp for
     * @return a double amount representing how much hp the player should have
     */
    public double calculatePlayerExpectedHealth(Player player, int levelOverride){
        // TODO: Implementation for any other hp modifying factors
        return calculateBaseHealth(levelOverride);
    }

    /**
     * Pass in a level and calculate how much base hp a player should have for a certain level
     *
     * @param level The int level to calculate hp for
     * @return a double amount representing how much base hp a level should have
     */
    public double calculateBaseHealth(int level){
        return baseHP + ((level - 1) * 12);  // 100 + 12x where x is level - 1
    }
}
