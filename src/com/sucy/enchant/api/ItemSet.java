package com.sucy.enchant.api;

import org.bukkit.Material;

import java.util.Arrays;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.api.ItemSet
 */
public enum ItemSet {

    BOOK_AND_QUILL("BOOK_AND_QUILL", "WRITTEN_BOOK"),
    INK_SACK("INK_SAC", "INK_SACK"),

    AXES("_AXE"),
    BOOTS("BOOTS"),
    BOWS(Material.BOW),
    CHESTPLATES("CHESTPLATE"),
    FISHING(Material.FISHING_ROD),
    GLIDERS(Material.ELYTRA),
    HELMETS("HELMET"),
    HOES("_HOE"),
    LEGGINGS("LEGGINGS"),
    MISCELLANEOUS("SKELETON_SKULL", "SKULL_ITEM", "PUMPKIN"),
    PICKAXES("PICKAXE"),
    SHEARS(Material.SHEARS),
    SHIELDS(Material.SHIELD),
    SHOVELS("SHOVEL", "SPADE"),
    SWORDS("SWORD"),
    TRIDENT("TRIDENT"),
    UTILITY("SHEARS", "FLINT_AND_STEEL", "CARROT_STICK", "CARROT_ON_A_STICK"),

    ARMOR(CHESTPLATES, HELMETS, BOOTS, LEGGINGS),
    WEAPONS(SWORDS, AXES),
    TOOLS(AXES, SHOVELS, PICKAXES),
    DURABILITY(SWORDS, TOOLS, BOWS, FISHING, ARMOR),
    DURABILITY_SECONDARY(UTILITY, HOES, GLIDERS, SHIELDS),
    DURABILITY_ALL(DURABILITY, DURABILITY_SECONDARY),

    VANILLA_ENCHANTABLES(SWORDS, TRIDENT, TOOLS, BOWS, FISHING, ARMOR, UTILITY, GLIDERS, MISCELLANEOUS),

    NONE,
    ALL(DURABILITY, DURABILITY_SECONDARY, MISCELLANEOUS);

    private final Material[] items;

    ItemSet() {
        items = new Material[0];
    }

    ItemSet(final String... suffixes) {
        items = Arrays.stream(Material.values())
                .filter(material -> !material.name().startsWith("LEGACY")
                        && Arrays.stream(suffixes).anyMatch(material.name()::endsWith))
                .toArray(Material[]::new);
    }

    ItemSet(final ItemSet... sets) {
        items = Arrays.stream(sets)
                .map(ItemSet::getItems)
                .flatMap(Arrays::stream)
                .toArray(Material[]::new);
    }

    ItemSet(final Material... items) {
        this.items = items;
    }

    public Material[] getItems() {
        return items;
    }
}
