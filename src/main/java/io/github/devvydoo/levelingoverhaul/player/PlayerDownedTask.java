package io.github.devvydoo.levelingoverhaul.player;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.party.PartyManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


public class PlayerDownedTask extends BukkitRunnable {

    private final PartyManager partyManager;
    private final Player player;
    private Player lastReviver = null;
    private final Entity killer;
    private final int TARGET_TICKS;
    private int currentTick = 0;
    private int totalReviveTicks = 0;
    private int temporaryReviveTicks = 0;
    private final int TARGET_REVIVE_TICKS = 120;

    public PlayerDownedTask(final PartyManager partyManager, final Player player, final Entity killer, int seconds){
        this.partyManager = partyManager;
        this.player = player;
        this.killer = killer != null ? killer : player;
        this.TARGET_TICKS = seconds * 20;
    }


    @Override
    public void run() {

        if (temporaryReviveTicks > 0) {
            this.totalReviveTicks++;
            this.temporaryReviveTicks--;
            displayPlayerTitles();
        }
        else {
            currentTick++;
            if (totalReviveTicks > 0){
                totalReviveTicks--;
                displayPlayerTitles();
            }
        }

        LevelingOverhaul.getPlugin(LevelingOverhaul.class).getActionBarManager().dispalyActionBarTextWithExtra(player, ChatColor.DARK_RED + "" + ChatColor.BOLD + "DOWNED! " + getSecondsRemainingWithDecimal() + "s");

        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2, -3, false, false, false));
        player.setGliding(true);

        if (player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0)).isPassable())
            player.setVelocity(player.getVelocity().setX(0).setZ(0));
        else if (player.getVelocity().length() > .078)
            player.setVelocity(player.getVelocity().multiply(.3));


        if (currentTick >= TARGET_TICKS) {
            player.setInvulnerable(false);
            player.damage(Integer.MAX_VALUE);
            cancel();
        } else if (totalReviveTicks >= TARGET_REVIVE_TICKS){
            partyManager.revivePlayer(player);

            if (lastReviver != null && lastReviver != player)
                lastReviver.sendTitle(ChatColor.GREEN + "Revived " + ChatColor.AQUA + player.getDisplayName() + ChatColor.GREEN + "!", ChatColor.GRAY + "100%", 0, 20, 30);
            else if (lastReviver != null)  // If the last reviver isnt null and not the player, then the playe revived themselves
                player.getInventory().removeItem(new ItemStack(Material.TOTEM_OF_UNDYING));

            cancel();
        }


    }

    private void displayPlayerTitles() {
        if (lastReviver != player) {
            lastReviver.sendTitle(ChatColor.GRAY + "Reviving " + ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + "!", ChatColor.GRAY + "" + Math.min(Math.round((totalReviveTicks + 1) / (TARGET_REVIVE_TICKS * 1.) * 100), 100) + "%", 0, 20, 30);
            player.sendTitle(ChatColor.AQUA + lastReviver.getDisplayName() + ChatColor.GRAY + " is reviving you!", ChatColor.GRAY + "" + Math.min(Math.round((totalReviveTicks + 1) / (TARGET_REVIVE_TICKS * 1.) * 100), 100) + "%", 0, 20, 30);
        } else
            player.sendTitle(ChatColor.GREEN + "Using self revive...", ChatColor.GRAY + "" + Math.min(Math.round((totalReviveTicks + 1) / (TARGET_REVIVE_TICKS * 1.) * 100), 100) + "%", 0, 20, 30);
    }

    public void doReviveTick(Player reviver){
        this.lastReviver = reviver;
        this.temporaryReviveTicks = Math.min(this.temporaryReviveTicks + 5, 5);
    }

    public int getSecondsRemaining(){
        return (TARGET_TICKS - currentTick) / 20;
    }

    private String getSecondsRemainingWithDecimal(){
        return String.format("%s.%s", getSecondsRemaining(), 9 - (currentTick % 20 / 2));
    }

}
