package com.sucy.enchant.api;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.api.ItemSet
 */
public enum ItemSet {

    AXES(Material.WOOD_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLD_AXE,
            Material.DIAMOND_AXE),

    BOOTS(Material.LEATHER_BOOTS,
            Material.CHAINMAIL_BOOTS,
            Material.IRON_BOOTS,
            Material.GOLD_BOOTS,
            Material.DIAMOND_BOOTS),

    BOWS(Material.BOW),

    CHESTPLATES(Material.LEATHER_CHESTPLATE,
            Material.CHAINMAIL_CHESTPLATE,
            Material.IRON_CHESTPLATE,
            Material.GOLD_CHESTPLATE,
            Material.DIAMOND_CHESTPLATE),

    FISHING(Material.FISHING_ROD),

    GLIDERS(Material.ELYTRA),

    HELMETS(Material.LEATHER_HELMET,
            Material.CHAINMAIL_HELMET,
            Material.IRON_HELMET,
            Material.GOLD_HELMET,
            Material.DIAMOND_HELMET),

    HOES(Material.WOOD_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLD_HOE,
            Material.DIAMOND_HOE),

    LEGGINGS(Material.LEATHER_LEGGINGS,
            Material.CHAINMAIL_LEGGINGS,
            Material.IRON_LEGGINGS,
            Material.GOLD_LEGGINGS,
            Material.DIAMOND_LEGGINGS),

    MISCELLANEOUS(Material.SKULL_ITEM,
            Material.PUMPKIN),

    PICKAXES(Material.WOOD_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLD_PICKAXE,
            Material.DIAMOND_PICKAXE),

    SHIELDS(Material.SHIELD),

    SHOVELS(Material.WOOD_SPADE,
            Material.STONE_SPADE,
            Material.IRON_SPADE,
            Material.GOLD_SPADE,
            Material.DIAMOND_SPADE),

    SWORDS(Material.WOOD_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLD_SWORD,
            Material.DIAMOND_SWORD),

    UTILITY(Material.SHEARS,
            Material.FLINT_AND_STEEL,
            Material.CARROT_STICK),

    ARMOR(CHESTPLATES, HELMETS, BOOTS, LEGGINGS),

    WEAPONS(SWORDS, AXES),

    TOOLS(AXES, SHOVELS, PICKAXES),

    DURABILITY(SWORDS, TOOLS, BOWS, FISHING, ARMOR, UTILITY, HOES, GLIDERS, SHIELDS),

    ALL(DURABILITY, MISCELLANEOUS);

    private final Material[] items;

    ItemSet(final ItemSet... sets) {
        final List<Material> list = Arrays.stream(sets)
                .map(ItemSet::getItems)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
        items = list.toArray(new Material[list.size()]);
    }

    ItemSet(final Material... items) {
        this.items = items;
    }

    public Material[] getItems() {
        return items;
    }
}
