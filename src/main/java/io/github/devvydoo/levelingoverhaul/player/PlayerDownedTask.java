package io.github.devvydoo.levelingoverhaul.player;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.managers.PartyManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


public class PlayerDownedTask extends BukkitRunnable {

    private final PartyManager partyManager;
    private final Player player;
    private final Entity killer;
    private final int TARGET_TICKS;
    private int currentTick = 0;

    public PlayerDownedTask(final PartyManager partyManager, final Player player, final Entity killer, int seconds){
        this.partyManager = partyManager;
        this.player = player;
        this.killer = killer != null ? killer : player;
        this.TARGET_TICKS = seconds * 20;
    }


    @Override
    public void run() {

        currentTick++;
        LevelingOverhaul.getPlugin(LevelingOverhaul.class).getActionBarManager().dispalyActionBarTextWithExtra(player, ChatColor.DARK_RED + "" + ChatColor.BOLD + "DOWNED! " + ((TARGET_TICKS - currentTick) / 20) + "s");

        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2, -3, false, false, false));

        player.setGliding(true);

        System.out.println("velocity: " + player.getVelocity().length());

        if (player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0)).isPassable())
            player.setVelocity(player.getVelocity().setX(0).setZ(0));
        else if (player.getVelocity().length() > .08)
            player.setVelocity(player.getVelocity().multiply(.5));


        if (currentTick >= TARGET_TICKS) {
            player.setInvulnerable(false);
            player.damage(Integer.MAX_VALUE, killer);
            cancel();
        }


    }

}
