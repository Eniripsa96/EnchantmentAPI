package com.sucy.enchant.util;

import org.bukkit.ChatColor;

import java.util.Objects;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.util.LoreReader
 */
public class LoreReader {

    /**
     * Parses an enchantment name from a bit of text, assuming the format
     * is "{color}{enchantment} {level}"
     *
     * @param text text to parse from
     * @return enchantment name
     */
    public static String parseEnchantmentName(final String text) {
        Objects.requireNonNull(text, "Text cannot be null");
        if (!text.startsWith(ChatColor.GRAY.toString())) return "";

        final int lastSpace = text.lastIndexOf(' ');
        return lastSpace > 2 ? text.substring(2, lastSpace) : "";
    }

    /**
     * Parses a level from a lore line. This expects valid roman numerals to be at the end of the line
     * with no spaces after it, matching the format for enchantments.
     *
     * @param text text to parse the level from
     * @return parsed level or 0 if invalid
     */
    public static int parseEnchantmentLevel(final String text) {
        Objects.requireNonNull(text, "Text cannot be null");
        final int lastSpace = text.lastIndexOf(' ');
        return lastSpace > 0 && lastSpace < text.length() - 1 ? RomanNumerals.fromNumerals(text.substring(lastSpace + 1)) : 0;
    }

    /**
     * Formats an enchantment name for appending to an item's lore.
     *
     * @param name enchantment name
     * @param level level of the enchantment
     * @return lore string for the enchantment
     */
    public static String formatEnchantment(final String name, final int level) {
        return ChatColor.GRAY + name + " " + RomanNumerals.toNumerals(level);
    }

    /**
     * Checks whether or not the lore line is the line for the given enchantment
     *
     * @param line line to check
     * @param enchantmentName name of the enchantment
     * @return true if the line matches, false otherwise
     */
    public static boolean isEnchantment(final String line, final String enchantmentName) {
        if (!line.startsWith(ChatColor.GRAY.toString())) return false;

        final int level = parseEnchantmentLevel(line);
        return level > 0 && line.equals(formatEnchantment(enchantmentName, level));
    }
}
