package io.github.devvydoo.levelingoverhaul.managers;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class TipAnnounceManager {

    private final int TIP_TICK_DELAY = 20 * 60 * 10;

    public class AnnounceTipTask extends BukkitRunnable {

        private ArrayList<String> tips;
        Iterator<String> tipIterator;

        public AnnounceTipTask(ArrayList<String> tips) {
            this.tips = tips;
            Collections.shuffle(this.tips);
            tipIterator = this.tips.iterator();
        }

        @Override
        public void run() {
            if (plugin.getServer().getOnlinePlayers().size() < 1) { return; }
            if (tipIterator.hasNext()){
                plugin.getServer().broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Tip" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + tipIterator.next());
            } else {
                tipIterator = tips.iterator();
                run();
            }
        }

    }

    private LevelingOverhaul plugin;

    public TipAnnounceManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
        ArrayList<String> tips = new ArrayList<>();
        tips.add(String.format("Reaching level %sLevel 30 %sgrants access to enchanting! To enchant a piece of gear, you only need half of your level's worth of %sLapis Lazuli%s.", ChatColor.GREEN, ChatColor.GRAY, ChatColor.BLUE, ChatColor.GRAY));
        tips.add(String.format("To increase your luck when %senchanting %syour gear, place bookshelves around the enchanting table. At least %s12 bookshelves %swill enchant with the best possible luck!", ChatColor.LIGHT_PURPLE, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY));
        tips.add(String.format("The power of %senchanting %sis completely based on your player level. %sBookshelves %ssimply just increase your luck!", ChatColor.LIGHT_PURPLE, ChatColor.GRAY, ChatColor.GREEN, ChatColor.GRAY));
        tips.add(String.format("%sGrindstone %scan be used to refund %s66%% Lapis Lazuli %sused to enchant an item!", ChatColor.GREEN, ChatColor.GRAY, ChatColor.BLUE, ChatColor.GRAY));
        tips.add(String.format("%sAnvils %scan be used to refund %shalf of the materials %sused to enchant an item!", ChatColor.GREEN, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY));
        tips.add(String.format("Be cautious when using %sAnvils and Grindstone %sto refund materials from %senchanted gear%s... The item will be gone forever!", ChatColor.GREEN, ChatColor.GRAY, ChatColor.LIGHT_PURPLE, ChatColor.GRAY));
        tips.add(String.format("When %sBoss mobs %sspawn, their health and difficulty will scale based on the %saverage player level %sin the area. Pick your raid group wisely!", ChatColor.DARK_PURPLE, ChatColor.GRAY, ChatColor.GREEN, ChatColor.GRAY));
        tips.add(String.format("As you progress, don't get %stoo comfortable%s. %sHostile mobs %sget stronger with you!", ChatColor.RED, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY));
        tips.add(String.format("Earning %sadvancements %swill award XP upon completion. Equip leggings with the %sSmarty Pants %senchantment to boost this effect!", ChatColor.GREEN, ChatColor.GRAY, ChatColor.LIGHT_PURPLE, ChatColor.GRAY));
        tips.add(String.format("%sArmor %sworks a little different here. Armor will increase your defense stat which yields a %spercent resist %sbased on Armor type, and enchantments.", ChatColor.RED, ChatColor.GRAY, ChatColor.GOLD, ChatColor.GRAY));
        tips.add(String.format("The %sstrength stat %sdetermines how much base damage you deal. You can check your strength using the %s/stats %scommand.", ChatColor.RED, ChatColor.GRAY, ChatColor.AQUA, ChatColor.GRAY));
        tips.add(String.format("Getting struck by a %skilling blow %swith at least 50%% of your max HP will ensure you survive with %s1 HP%s... Yes, this includes fall damage...", ChatColor.RED, ChatColor.GRAY, ChatColor.GREEN, ChatColor.GRAY));
        tips.add(String.format("Damage received by %sthorns %sdepends on the level of the player that has armor with thorns equipped. %sThink twice before picking a fight with higher leveled players%s!", ChatColor.LIGHT_PURPLE, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY));
        tips.add(String.format("Bows have a base %s20%% chance %sto fire a critical shot that inflicts %s2x damage%s. You'll know by seeing a particle effect emit from your bow!", ChatColor.RED, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY));
        tips.add(String.format("Reaching %slevel 80 %swill grant the ability to craft %sWither Skeleton Skulls %susing a Skeleton skull, Obsidian, and coal blocks!", ChatColor.GREEN, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY));
        tips.add(String.format("Reaching %slevel 70 %swill grant the ability to use the %s3x3 crafting grid %swherever you please by Shift-Right clicking with nothing in your hand!", ChatColor.GREEN, ChatColor.GRAY, ChatColor.GOLD, ChatColor.GRAY));
        tips.add(String.format("All items in the game are %sunbreakable%s. Durability is not something that you need to worry about.", ChatColor.GOLD, ChatColor.GRAY));
        tips.add(String.format("%sStone pickaxes %sare able to mine %sGold Ore%s.", ChatColor.GOLD, ChatColor.GRAY, ChatColor.GOLD, ChatColor.GRAY));
        tips.add(String.format("%sGolden pickaxes %sare able to mine %sIron Ore%s.", ChatColor.GOLD, ChatColor.GRAY, ChatColor.GOLD, ChatColor.GRAY));
        tips.add(String.format("%sIron pickaxes %sare able to mine %sObsidian%s. It does take quite a while though!", ChatColor.GOLD, ChatColor.GRAY, ChatColor.BLUE, ChatColor.GRAY));
        tips.add(String.format("The speed of your %shealth regeneration %sis dependent on how %slow your health %sis.", ChatColor.RED, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY));
        tips.add(String.format("The %sInfinity %senchantment has a chance to conserve arrows rather than making them purely infinite. Make sure to %sstock up %son ammo!", ChatColor.LIGHT_PURPLE, ChatColor.GRAY, ChatColor.GOLD, ChatColor.GRAY));
        tips.add(String.format("All methods of earning XP via %sexperience orbs %sare removed. You can earn %sXP %sby defeating mobs, earning advancements, and smelting materials to name a few.", ChatColor.GREEN, ChatColor.GRAY, ChatColor.GREEN, ChatColor.GRAY));
        tips.add(String.format("%sKilling a player %swill grant you all the XP that the player had for their %scurrent level%s.", ChatColor.RED, ChatColor.GRAY, ChatColor.GREEN, ChatColor.GRAY));
        tips.add(String.format("When a player dies, %sall XP progress towards their next level %sis lost. While it is %simpossible to go down in levels%s, it's possible to lose a few minutes worth of progress!", ChatColor.RED, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY));
        tips.add(String.format("Pickaxes with %sSilk Touch %scannot gain experience, however pickaxes with %sSmelting Touch %scan gain experience from Iron Ore and Gold Ore!", ChatColor.LIGHT_PURPLE, ChatColor.GRAY, ChatColor.LIGHT_PURPLE, ChatColor.GRAY));
        tips.add(String.format("Higher level %screepers%s explode much quicker and have a chance to %sspawn charged%s. Be careful!", ChatColor.DARK_GREEN, ChatColor.GRAY, ChatColor.AQUA, ChatColor.GRAY));
        tips.add(String.format("Some mobs spawn in at a fixed level, such as %sEndermen%s. If you are a %slow level%s, it would probably be smart to not mess with the Endermen yet...", ChatColor.DARK_PURPLE, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY));
        tips.add(String.format("%sWant to make a change or see how the plugin works? See the %ssource code %sat:%s https://github.com/DevvyDoo/Leveling-Overhaul", ChatColor.GRAY, ChatColor.GREEN, ChatColor.GRAY, ChatColor.BLUE));
        tips.add(String.format("%sThe %sEnderdragon %sdrops powerful gear with %sunique abilities%s... Can you collect the full set?", ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY, ChatColor.BLUE));
        tips.add(String.format("%sWitches %sdrop %sMagic Mirrors %swhich can be used to instantly teleport to any binded location.", ChatColor.RED, ChatColor.GRAY, ChatColor.AQUA, ChatColor.GRAY));
        tips.add(String.format("%sEndermen %son the %stiny End islands %shave a rare drop...", ChatColor.LIGHT_PURPLE, ChatColor.GRAY, ChatColor.GOLD, ChatColor.GRAY));
        tips.add(String.format("You can invite other players to your party using the %s/party %scommand. This will turn off %sfriendly fire%s, prevent homing arrows from targeting each other, allow member teleportation, and some other fun stuff.", ChatColor.AQUA, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY));
        new AnnounceTipTask(tips).runTaskTimerAsynchronously(plugin, TIP_TICK_DELAY, TIP_TICK_DELAY);
    }


}
