package me.devvy.leveled.commands;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.party.Party;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class PartyCommand implements CommandExecutor, TabCompleter {

    private final HashMap<Player, PartyRequest> playerPartyRequests;

    private class PartyRequest {

        private final Player pendingMember;
        private final Player invitee;
        private final long expireeTimestamp;

        public PartyRequest(Player pendingMember, Player invitee, long expireeTimestamp) {
            this.pendingMember = pendingMember;
            this.invitee = invitee;
            this.expireeTimestamp = expireeTimestamp;
            pendingMember.sendMessage(ChatColor.AQUA + invitee.getDisplayName() + ChatColor.GOLD + " has sent you a party invite! type " + ChatColor.LIGHT_PURPLE + "/party accept " + ChatColor.GOLD + "to join!");
        }

        public long getExpireeTimestamp() {
            return expireeTimestamp;
        }

        public void completeRequest(){
            // Make sure the other player is party-less
            if (Leveled.getInstance().getPartyManager().getParty(pendingMember) != null){
                invitee.sendMessage(ChatColor.RED + "The player " + ChatColor.DARK_RED + pendingMember.getDisplayName() + ChatColor.RED + " is already in a party!");
            }

            // Is the player in a party?
            Party party = Leveled.getInstance().getPartyManager().getParty(invitee);

            // Logic for if the player is in a party
            if (party != null){

                // Make sure it isn't full
                if (party.isFull()){
                    invitee.sendMessage(ChatColor.RED + "Your party is full!");
                    return;
                }

                // Should be good to invite them
                Leveled.getInstance().getPartyManager().addPlayerToParty(party.getOwner(), pendingMember);
                return;
            }

            // Logic for when the player is party-less and is making a new party
            Leveled.getInstance().getPartyManager().makeNewParty(invitee);
            Leveled.getInstance().getPartyManager().addPlayerToParty(invitee, pendingMember);
        }
    }

    public PartyCommand() {
        playerPartyRequests = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Please specify an argument!");
            return false;
        }

        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Is the player trying to leave their party?
        if (args[0].equalsIgnoreCase("leave")){

            handleLeaveSubCommand(player);
            return true;

        }
        else if (args[0].equalsIgnoreCase("accept")){

            handleAcceptSubCommand(player);
            return true;

        }
        else if (args[0].equalsIgnoreCase("list")){

            handleListSubCommand(player);
            return true;
        }
        else if (args[0].equalsIgnoreCase("tp")){
            if (args.length < 2){
                player.sendMessage(ChatColor.RED + "Please specify a player");
                return true;
            }
            handleTpSubCommand(player, args);
            return true;
        } else if (args[0].equalsIgnoreCase("invite") && args.length > 1){
            handleInviteSubCommand(player, args[1]);
            return true;
        }

        handleInviteSubCommand(player, args[0]);
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String[] subs = {"tp", "invite", "list", "leave", "accept"};
        if (command.getName().equalsIgnoreCase("party")){
            if (args.length == 1){
                ArrayList<String> options = new ArrayList<>();

                if (!args[0].equalsIgnoreCase("")){
                    for (String sub : subs) {
                        if (sub.startsWith(args[0]))
                            options.add(sub);
                    }
                    Collections.sort(options);
                    return options;

                } else {
                    return Arrays.asList(subs);
                }

            }
        }
        return null;
    }

    private void handleLeaveSubCommand(Player player){
        // Is the player in a party?
        Party party = Leveled.getInstance().getPartyManager().getParty(player);
        if (party == null){
            player.sendMessage(ChatColor.RED + "You aren't in a party!");
            return;
        }
        Leveled.getInstance().getPartyManager().removePlayerFromParty(player);
        party.sendPartyMessage(ChatColor.DARK_RED + player.getDisplayName() +  ChatColor.RED + " has left the party.");
        player.sendMessage(ChatColor.RED + "You have left the party.");
    }

    private void handleAcceptSubCommand(Player player){
        // Do we have a request?
        if (!playerPartyRequests.containsKey(player) || playerPartyRequests.get(player).getExpireeTimestamp() < System.currentTimeMillis()){
            player.sendMessage(ChatColor.RED + "You don't have any party requests");
            return;
        }
        playerPartyRequests.get(player).completeRequest();
        playerPartyRequests.remove(player);
    }

    private void handleListSubCommand(Player player){
        for (Party party: Leveled.getInstance().getPartyManager().getParties()){
            player.sendMessage("Party owned by " + party.getOwner().getDisplayName());
            for (Player m: party.getMembers()){
                player.sendMessage("Member: " + m.getDisplayName());
            }
            player.sendMessage();
        }
    }

    private void handleTpSubCommand(Player player, String[] args) {

        // Is the player in a party?
        if (!Leveled.getInstance().getPartyManager().isPlayerInParty(player)){
            player.sendMessage(ChatColor.RED + "You must be in a party to use that command!");
            return;
        }

        // Was a valid player given?
        Player friend = Leveled.getInstance().getServer().getPlayerExact(args[1]);
        if (friend == null){
            player.sendMessage(ChatColor.RED + "Could not find player " + ChatColor.DARK_RED +  args[1]);
            return;
        }

        // Are the players in the same party?
        if (!Leveled.getInstance().getPartyManager().inSameParty(player, friend)){
            player.sendMessage(ChatColor.RED + "You must be in the same party as someone to tp to them!");
            return;
        }

        // Is either player down?
        if (Leveled.getInstance().getPartyManager().isDown(player)) {
            player.sendMessage(ChatColor.RED + "You cannot tp when you are downed!");
            return;
        } else if (Leveled.getInstance().getPartyManager().isDown(friend)) {
            player.sendMessage(ChatColor.RED + "You cannot tp to a party member that is downed!");
            return;
        }

        // Same worlds?
        if (player.getWorld() != friend.getWorld()) {
            player.sendMessage(ChatColor.RED + "You cannot tp to a party member in a different dimension!");
            return;
        }

        // All good to tp
        player.teleport(friend.getLocation());
        player.sendTitle("", ChatColor.GREEN + "Teleporting to " + friend.getDisplayName(), 20, 20, 10);
        friend.sendTitle("", ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + " teleported to you", 20, 15, 10);
        friend.getWorld().playSound(friend.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }

    private void handleInviteSubCommand(Player player, String potentialMember){
        // Attempt to get the player specified
        Player member = Leveled.getInstance().getServer().getPlayerExact(potentialMember);

        if (member == null){
            player.sendMessage(ChatColor.RED + "Could not find the player: " + ChatColor.DARK_RED + potentialMember);
            return;
        }

        // lol
        if (member.equals(player)){
            player.sendMessage(ChatColor.RED + "You can't be in a party with yourself!");
            return;
        }

        // Make sure the other player is party-less
        if (Leveled.getInstance().getPartyManager().getParty(member) != null){
            player.sendMessage(ChatColor.RED + "The player " + ChatColor.DARK_RED + member.getDisplayName() + ChatColor.RED + " is already in a party!");
            return;
        }

        // Make a request
        playerPartyRequests.put(member, new PartyRequest(member, player, System.currentTimeMillis() + 120 * 1000));
        player.sendMessage(ChatColor.GREEN + "Sent a party invite to " + ChatColor.DARK_GREEN +  member.getDisplayName() + ChatColor.GREEN + "!");
    }
}
