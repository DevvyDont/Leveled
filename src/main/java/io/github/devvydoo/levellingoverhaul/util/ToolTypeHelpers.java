package io.github.devvydoo.levellingoverhaul.util;

import org.bukkit.Material;

import java.util.ArrayList;

public final class ToolTypeHelpers {

    public static void addAllToolsToList(ArrayList<Material> list) {
        addMeleeWeaponsToList(list);
        addShovelsToList(list);
        addPickaxesToList(list);
        addHoesToList(list);
    }

    public static void addAllArmorToList(ArrayList<Material> list){
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

    public static void addShovelsToList(ArrayList<Material> list){

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

    public static void addHoesToList(ArrayList<Material> list){

        list.add(Material.WOODEN_HOE);
        list.add(Material.STONE_HOE);
        list.add(Material.GOLDEN_HOE);
        list.add(Material.IRON_HOE);
        list.add(Material.DIAMOND_HOE);

    }

    public static void addHelmetsToList(ArrayList<Material> list){
        list.add(Material.LEATHER_HELMET);
        list.add(Material.IRON_HELMET);
        list.add(Material.CHAINMAIL_HELMET);
        list.add(Material.GOLDEN_HELMET);
        list.add(Material.DIAMOND_HELMET);
    }

    public static void addChestplatesToList(ArrayList<Material> list){
        list.add(Material.LEATHER_CHESTPLATE);
        list.add(Material.IRON_CHESTPLATE);
        list.add(Material.CHAINMAIL_CHESTPLATE);
        list.add(Material.GOLDEN_CHESTPLATE);
        list.add(Material.DIAMOND_CHESTPLATE);
    }

    public static void addLeggingsToList(ArrayList<Material> list){
        list.add(Material.LEATHER_LEGGINGS);
        list.add(Material.IRON_LEGGINGS);
        list.add(Material.CHAINMAIL_LEGGINGS);
        list.add(Material.GOLDEN_LEGGINGS);
        list.add(Material.DIAMOND_LEGGINGS);
    }

    public static void addBootsToList(ArrayList<Material> list){
        list.add(Material.LEATHER_BOOTS);
        list.add(Material.IRON_BOOTS);
        list.add(Material.CHAINMAIL_BOOTS);
        list.add(Material.GOLDEN_BOOTS);
        list.add(Material.DIAMOND_BOOTS);
    }


}