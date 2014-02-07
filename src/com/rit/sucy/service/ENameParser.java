package com.rit.sucy.service;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Parses lore names into enchantment names and levels
 */
public class ENameParser {

    /**
     * Parses an enchantment name from a lore string
     *
     * @param lore the lore string to parse
     * @return     enchantment name
     */
    public static String parseName(String lore) {
        if (!lore.contains(" ")) return null;

        String[] pieces = lore.split(" ");

        String name = "";
        for (int i = 0; i < pieces.length - 1; i++) {
            name += pieces[i] + (i < pieces.length - 2 ? " " : "");
        }
        name = ChatColor.stripColor(name);
        return name;
    }

    /**
     * Parses an enchantment level from a lore string
     * @param lore the lore string to parse
     * @return     enchantment name
     */
    public static int parseLevel(String lore) {
        if (!lore.contains(" ")) return 0;

        String[] pieces = lore.split(" ");
        return ERomanNumeral.getValueOf(pieces[pieces.length - 1]);
    }

    /**
     * Gets the vanilla name of the item
     *
     * @param item item to get the name of
     * @return     normal display name
     */
    public static String getName(ItemStack item) {
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasEnchants()) return null;
            if (item.getItemMeta().hasDisplayName()) return null;
        }
        return getName(item.getType());
    }

    /**
     * Gets the vanilla name of the item
     *
     * @param item item type
     * @return     normal display name
     */
    public static String getName(Material item) {
        String name = item.name().toLowerCase();
        String[] pieces = name.split("_");
        name = "";
        for (int i = 0; i < pieces.length; i++) {
            name += pieces[i].substring(0, 1).toUpperCase() + pieces[i].substring(1);
            if (i < pieces.length - 1) name += " ";
        }
        if (differentNames.containsKey(name))
            return ChatColor.AQUA + differentNames.get(name);
        return ChatColor.AQUA + name;
    }

    /**
     * Gets the vanilla name of the enchantment
     *
     * @param enchant enchantment
     * @return        vanilla name
     */
    public static String getVanillaName(Enchantment enchant) {
        for (Map.Entry<String, String> entry : eNames.entrySet()) {
            if (entry.getValue().equals(enchant.getName())) {
                if (entry.getKey().contains(" ")) {
                    String[] parts = entry.getKey().split(" ");
                    String result = parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1);
                    for (int i = 1; i < parts.length; i++)
                        result += " " + parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
                    return result;
                }
                else return entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
            }
        }
        return enchant.getName();
    }

    /**
     * Gets the bukkit name of the vanilla name string
     *
     * @param name vanilla enchantment name
     * @return     bukkit enchantment name
     */
    public static String getBukkitName(String name) {
        if (eNames.containsKey(name.toLowerCase()))
            return eNames.get(name.toLowerCase());
        else return name;
    }

    private static final Map<String,String> eNames = new HashMap<String,String>(){{
        put("knockback",             "KNOCKBACK");
        put("looting",               "LOOT_BONUS_MOBS");
        put("sharpness",             "DAMAGE_ALL");
        put("smite",                 "DAMAGE_UNDEAD");
        put("bane of arthropods",    "DAMAGE_ARTHROPODS");
        put("fire aspect",           "FIRE_ASPECT");
        put("infinity",              "ARROW_INFINITE");
        put("flame",                 "ARROW_FIRE");
        put("punch",                 "ARROW_KNOCKBACK");
        put("power",                 "ARROW_DAMAGE");
        put("respiration",           "OXYGEN");
        put("aqua affinity",         "WATER_WORKER");
        put("feather falling",       "PROTECTION_FALL");
        put("thorns",                "THORNS");
        put("protection",            "PROTECTION_ENVIRONMENTAL");
        put("fire protection",       "PROTECTION_FIRE");
        put("blast protection",      "PROTECTION_EXPLOSIONS");
        put("projectile protection", "PROTECTION_PROJECTILE");
        put("efficiency",            "DIG_SPEED");
        put("unbreaking",            "DURABILITY");
        put("fortune",               "LOOT_BONUS_BLOCKS");
        put("silk touch",            "SILK_TOUCH");
    }};

    private static final Hashtable<String, String> differentNames = new Hashtable<String, String>(){{
        put("Birch Wood Stairs",  "Wooden Stairs");
        put("Book And Quill",     "Book and Quill");
        put("Cobble Wall",        "Cobblestone Wall");
        put("Command",            "Command Block");
        put("Cooked Beef",        "Steak");
        put("Daylight Detector",  "Daylight Sensor");
        put("Dead Bush",          "Tall Grass");
        put("Diamond Spade",      "Diamond Shovel");
        put("Diode",              "Redstone Repeater");
        put("Exp Bottle",         "Bottle of Enchanting");
        put("Explosive Minecart", "TNT Minecart");
        put("Gold Spade",         "Gold Shovel");
        put("Gold Record",        "Music Disk");
        put("Green Record",       "Music Disk");
        put("Grilled Pork",       "Cooked Porkchop");
        put("Iron Spade",         "Iron Shovel");
        put("Jack O Lantern",     "Jack-O-Lantern");
        put("Jungle Wood Stairs", "Wooden Stairs");
        put("Lapis Block",        "Lapis Lazuli Block");
        put("Lapis Ore",          "Lapis Lazuli Ore");
        put("Leather Chestplate", "Leather Tunic");
        put("Leather Helmet",     "Leather Cap");
        put("Leather Leggings",   "Leather Pants");
        put("Long Grass",         "Tall Grass");
        put("Mycel",              "Mycelium");
        put("Nether Fence",       "Nether Brick Fence");
        put("Nether Warts",       "Nether Wart");
        put("Pork",               "Raw Porkchop");
        put("Record 3",           "Music Disk");
        put("Record 4",           "Music Disk");
        put("Record 5",           "Music Disk");
        put("Record 6",           "Music Disk");
        put("Record 7",           "Music Disk");
        put("Record 8",           "Music Disk");
        put("Record 9",           "Music Disk");
        put("Record 10",          "Music Disk");
        put("Record 11",          "Music Disk");
        put("Record 12",          "Music Disk");
        put("Red Rose",           "Rose");
        put("Smooth Stairs",      "Stone Brick Stairs");
        put("Speckled Melon",     "Glistering Melon");
        put("Sprue Wood Stairs",  "Wooden Stairs");
        put("Stone Plate",        "Stone Pressure Plate");
        put("Stone Spade",        "Stone Shovel");
        put("Sulphur",            "Gunpowder");
        put("Thin Glass",         "Glass Pane");
        put("Tnt",                "TNT");
        put("Water Lily",         "Lily Pad");
        put("Wood",               "Wooden Plank");
        put("Wood Axe",           "Wooden Axe");
        put("Wood Button",        "Button");
        put("Wood Double Step",   "Wooden Slab");
        put("Wood Hoe",           "Wooden Hoe");
        put("Wood Pickaxe",       "Wooden Pickaxe");
        put("Wood Plate",         "Wooden Pressure Plate");
        put("Wood Spade",         "Wooden Shovel");
        put("Wood Stairs",        "Wooden Stairs");
        put("Wood Sword",         "Wooden Sword");
        put("Yellow Flower",      "Dandelion");
    }};
}
