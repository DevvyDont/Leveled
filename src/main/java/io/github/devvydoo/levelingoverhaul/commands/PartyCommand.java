package io.github.devvydoo.levelingoverhaul.commands;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.util.Party;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PartyCommand implements CommandExecutor {

    private LevelingOverhaul plugin;
    private HashMap<Player, PartyRequest> playerPartyRequests;

    private class PartyRequest {

        private Player pendingMember;
        private Player invitee;
        private long expireeTimestamp;

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
            if (plugin.getPartyManager().getParty(pendingMember) != null){
                invitee.sendMessage(ChatColor.RED + "The player " + ChatColor.DARK_RED + pendingMember.getDisplayName() + ChatColor.RED + " is already in a party!");
            }

            // Is the player in a party?
            Party party = plugin.getPartyManager().getParty(invitee);

            // Logic for if the player is in a party
            if (party != null){

                // Make sure it isn't full
                if (party.isFull()){
                    invitee.sendMessage(ChatColor.RED + "Your party is full!");
                    return;
                }

                // Should be good to invite them
                plugin.getPartyManager().addPlayerToParty(party.getOwner(), pendingMember);
                return;
            }

            // Logic for when the player is party-less and is making a new party
            plugin.getPartyManager().makeNewParty(invitee);
            plugin.getPartyManager().addPlayerToParty(invitee, pendingMember);
        }
    }

    public PartyCommand(LevelingOverhaul plugin) {
        this.plugin = plugin;
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

        } else if (args[0].equalsIgnoreCase("accept")){

            handleAcceptSubCommand(player);
            return true;

        } else if (args[0].equalsIgnoreCase("list")){

            handleListSubCommand(player);
            return true;
        }

        // Attempt to get the player specified
        Player member = plugin.getServer().getPlayerExact(args[0]);

        if (member == null){
            sender.sendMessage(ChatColor.RED + "Could not find the player: " + ChatColor.DARK_RED + args[0]);
            return true;
        }

        // lol
        if (member.equals(player)){
            sender.sendMessage(ChatColor.RED + "You can't be in a party with yourself!");
            return true;
        }

        // Make sure the other player is party-less
        if (plugin.getPartyManager().getParty(member) != null){
            sender.sendMessage(ChatColor.RED + "The player " + ChatColor.DARK_RED + member.getDisplayName() + ChatColor.RED + " is already in a party!");
            return true;
        }

        // Make a request
        playerPartyRequests.put(member, new PartyRequest(member, player, System.currentTimeMillis() + 120 * 1000));
        player.sendMessage(ChatColor.GREEN + "Sent a party invite to " + ChatColor.DARK_GREEN +  member.getDisplayName() + ChatColor.GREEN + "!");
        return true;
    }

    public void handleLeaveSubCommand(Player player){
        // Is the player in a party?
        Party party = plugin.getPartyManager().getParty(player);
        if (party == null){
            player.sendMessage(ChatColor.RED + "You aren't in a party!");
            return;
        }
        plugin.getPartyManager().removePlayerFromParty(player);
        party.sendPartyMessage(ChatColor.DARK_RED + player.getDisplayName() +  ChatColor.RED + " has left the party.");
        player.sendMessage(ChatColor.RED + "You have left the party.");
    }

    public void handleAcceptSubCommand(Player player){
        // Do we have a request?
        if (!playerPartyRequests.containsKey(player) || playerPartyRequests.get(player).getExpireeTimestamp() < System.currentTimeMillis()){
            player.sendMessage(ChatColor.RED + "You don't have any party requests");
            return;
        }
        playerPartyRequests.get(player).completeRequest();
        playerPartyRequests.remove(player);
    }

    public void handleListSubCommand(Player player){
        for (Party party: plugin.getPartyManager().getParties()){
            player.sendMessage("Party owned by " + party.getOwner().getDisplayName());
            for (Player m: party.getMembers()){
                player.sendMessage("Member: " + m.getDisplayName());
            }
            player.sendMessage();
        }
    }
}
