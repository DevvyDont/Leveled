package io.github.devvydoo.levellingoverhaul.managers;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerHealthManager implements Listener {

    private double baseHP = 100;

    public double getBaseHP(){
        return baseHP;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        // Figure out what the player should have and set that
        event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(calculatePlayerExpectedHealth(event.getPlayer()));
        // Always no matter what display 10 hearts no matter the HP
        event.getPlayer().setHealthScale(20);
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
