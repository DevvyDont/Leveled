package io.github.devvydoo.levelingoverhaul.player;

import com.google.common.base.Strings;
import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.party.PartyManager;
import io.github.devvydoo.levelingoverhaul.util.FormattingHelpers;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
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

    private final ArmorStand playerLabel;

    public PlayerDownedTask(final PartyManager partyManager, final Player player, final Entity killer, int seconds){
        this.partyManager = partyManager;
        this.player = player;
        this.killer = killer != null ? killer : player;
        this.TARGET_TICKS = seconds * 20;

        this.playerLabel = player.getLocation().getWorld().spawn(
                player.getLocation().add(0, 1, 0),
                ArmorStand.class,
                (ArmorStand a) -> {
                    a.setVisible(false);
                    a.setMarker(true);
                    a.setCustomName("This is a label.");
                    a.setCustomNameVisible(true);
                });
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

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1, 3, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1, 5, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1, -3, false, false, false));
        player.setGliding(true);

        // If the player is being revived they can't move
        if (temporaryReviveTicks > 0)
            player.setVelocity(player.getVelocity().zero());

        // If the block below them is something they can fall through, don't let them move on the xz plane and just let them fall
        else if (!player.isOnGround()) {
            player.setVelocity(player.getVelocity().setX(0).setZ(0));
            player.setGliding(false);
        }

        // Restrict their movement speed, don't count their vertical velocity however
        if (temporaryReviveTicks <= 0 && player.getVelocity().setY(0).length() > .078)
            player.setVelocity(player.getVelocity().multiply(.3).setY(player.getVelocity().getY()));

        updateArmorStand();

        if (currentTick >= TARGET_TICKS) {
            player.setInvulnerable(false);
            player.setHealth(0);
            cancel();
        } else if (totalReviveTicks >= TARGET_REVIVE_TICKS){
            partyManager.revivePlayer(player);

            if (lastReviver != null && lastReviver != player)
                lastReviver.sendTitle("", ChatColor.GREEN + "Revived " + ChatColor.AQUA + player.getDisplayName() + ChatColor.GREEN + "!", 0, 20, 30);
            else if (lastReviver != null)  // If the last reviver isnt null and not the player, then the playe revived themselves
                player.getInventory().removeItem(new ItemStack(Material.TOTEM_OF_UNDYING));

            cancel();
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        this.playerLabel.remove();
        super.cancel();
    }


    public void doReviveTick(Player reviver){
        this.lastReviver = reviver;
        this.temporaryReviveTicks = Math.min(this.temporaryReviveTicks + 5, 5);
    }

    public int getSecondsRemaining(){
        return (TARGET_TICKS - currentTick) / 20;
    }

    private void displayPlayerTitles() {
        if (lastReviver != player) {
            lastReviver.sendTitle("", ChatColor.GRAY + "Reviving " + ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + "! " + getReviveProgressText(), 0, 20, 30);
            player.sendTitle("", ChatColor.AQUA + lastReviver.getDisplayName() + ChatColor.GRAY + " is reviving you! " + getReviveProgressText(), 0, 20, 30);
        } else
            player.sendTitle("", ChatColor.AQUA + "Using self revive... " + getReviveProgressText(), 0, 20, 30);
    }

    private String getSecondsRemainingWithDecimal(){
        return String.format("%s.%s", getSecondsRemaining(), 9 - (currentTick % 20 / 2));
    }

    private void updateArmorStand(){
        double yOffset = Math.sin(currentTick / 5.) / 4. + 1.5;
        this.playerLabel.teleport(this.player.getLocation().add(0, yOffset, 0));
        this.playerLabel.setCustomName(ChatColor.GREEN.toString() + ChatColor.BOLD + "REVIVE " + ChatColor.DARK_GRAY + "☠" + (getSecondsRemaining() % 2 == 0 ? ChatColor.RED : ChatColor.DARK_RED) + getSecondsRemainingWithDecimal() + "s" + getReviveProgressText());
    }

    private String getReviveProgressText() {
        String progress = " ";
        if (totalReviveTicks > 0) {
            progress = ChatColor.DARK_GRAY + " [";
            int greenChars = (int) (totalReviveTicks / (float)TARGET_REVIVE_TICKS * 20);
            progress += ChatColor.GREEN + Strings.repeat("=", greenChars);
            progress += ChatColor.GRAY + Strings.repeat("=",20 - greenChars);
            progress += ChatColor.DARK_GRAY + "]";
        }
        return progress;
    }

}
