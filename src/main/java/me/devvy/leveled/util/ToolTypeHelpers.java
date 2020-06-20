package me.devvy.leveled.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public final class ToolTypeHelpers {

    public static void addAllToolsToList(ArrayList<Material> list) {
        addMeleeWeaponsToList(list);
        addShovelsToList(list);
        addPickaxesToList(list);
        addHoesToList(list);
    }

    public static void addAllArmorToList(ArrayList<Material> list) {
        addHelmetsToList(list);
        addChestplatesToList(list);
        addLeggingsToList(list);
        addBootsToList(list);
    }

    public static void addMeleeWeaponsToList(ArrayList<Material> list) {

        list.add(Material.WOODEN_SWORD);
        list.add(Material.STONE_SWORD);
        list.add(Material.GOLDEN_SWORD);
        list.add(Material.IRON_SWORD);
        list.add(Material.DIAMOND_SWORD);

        list.add(Material.WOODEN_AXE);
        list.add(Material.STONE_AXE);
        list.add(Material.GOLDEN_AXE);
        list.add(Material.IRON_AXE);
        list.add(Material.DIAMOND_AXE);

    }

    public static void addShovelsToList(ArrayList<Material> list) {

        list.add(Material.WOODEN_SHOVEL);
        list.add(Material.STONE_SHOVEL);
        list.add(Material.GOLDEN_SHOVEL);
        list.add(Material.IRON_SHOVEL);
        list.add(Material.DIAMOND_SHOVEL);

    }

    public static void addPickaxesToList(ArrayList<Material> list) {

        list.add(Material.GOLDEN_PICKAXE);
        list.add(Material.WOODEN_PICKAXE);
        list.add(Material.IRON_PICKAXE);
        list.add(Material.DIAMOND_PICKAXE);
        list.add(Material.STONE_PICKAXE);

    }

    public static void addHoesToList(ArrayList<Material> list) {

        list.add(Material.WOODEN_HOE);
        list.add(Material.STONE_HOE);
        list.add(Material.GOLDEN_HOE);
        list.add(Material.IRON_HOE);
        list.add(Material.DIAMOND_HOE);

    }

    public static void addHelmetsToList(ArrayList<Material> list) {
        list.add(Material.LEATHER_HELMET);
        list.add(Material.IRON_HELMET);
        list.add(Material.CHAINMAIL_HELMET);
        list.add(Material.GOLDEN_HELMET);
        list.add(Material.DIAMOND_HELMET);
        list.add(Material.TURTLE_HELMET);
        list.add(Material.DRAGON_HEAD);
    }

    public static void addChestplatesToList(ArrayList<Material> list) {
        list.add(Material.LEATHER_CHESTPLATE);
        list.add(Material.IRON_CHESTPLATE);
        list.add(Material.CHAINMAIL_CHESTPLATE);
        list.add(Material.GOLDEN_CHESTPLATE);
        list.add(Material.DIAMOND_CHESTPLATE);
        list.add(Material.ELYTRA);
    }

    public static void addLeggingsToList(ArrayList<Material> list) {
        list.add(Material.LEATHER_LEGGINGS);
        list.add(Material.IRON_LEGGINGS);
        list.add(Material.CHAINMAIL_LEGGINGS);
        list.add(Material.GOLDEN_LEGGINGS);
        list.add(Material.DIAMOND_LEGGINGS);
    }

    public static void addBootsToList(ArrayList<Material> list) {
        list.add(Material.LEATHER_BOOTS);
        list.add(Material.IRON_BOOTS);
        list.add(Material.CHAINMAIL_BOOTS);
        list.add(Material.GOLDEN_BOOTS);
        list.add(Material.DIAMOND_BOOTS);
    }

    public static boolean isArmor(ItemStack itemStack){

        switch (itemStack.getType()){
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_HELMET:
            case DIAMOND_BOOTS:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_HELMET:
            case IRON_BOOTS:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_BOOTS:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_HELMET:
            case GOLDEN_BOOTS:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_HELMET:
            case TURTLE_HELMET:
            case LEATHER_BOOTS:
            case ELYTRA:
                return true;
            default:
                return false;
        }

    }

    public static boolean isAxe(ItemStack itemStack){
        switch (itemStack.getType()){
            case DIAMOND_AXE:
            case GOLDEN_AXE:
            case IRON_AXE:
            case WOODEN_AXE:
            case STONE_AXE:
                return true;
        }
        return false;
    }


}
