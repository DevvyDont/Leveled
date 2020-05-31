package io.github.devvydoo.levelingoverhaul.party;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Party {

    private ArrayList<Player> members;
    private Player owner;
    private int MEMBER_CAP = 4;

    public Party(Player owner) {
        this.owner = owner;
        members = new ArrayList<>();
        members.add(owner);
    }

    public ArrayList<Player> getMembers() {
        return members;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void addPlayer(Player player){
        if (size() < MEMBER_CAP)
            members.add(player);
            sendPartyMessage(ChatColor.AQUA + player.getDisplayName() + ChatColor.GOLD + " has joined the party!");
    }

    public void removePlayer(Player player){
        members.remove(player);
    }

    public void sendPartyMessage(String message){
        for (Player member: members){
            member.sendMessage(message);
        }
    }

    public int size(){
        return members.size();
    }

    public boolean isFull(){
        return size() >= MEMBER_CAP;
    }
}
