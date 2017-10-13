package com.sucy.enchant.util;

import org.bukkit.ChatColor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.util.LoreReaderTest
 */
public class LoreReaderTest {

    @Test
    public void parseEnchantmentName() throws Exception {
        assertEquals("Test", LoreReader.parseEnchantmentName(ChatColor.GRAY + "Test IX"));
        assertEquals("With Spaces", LoreReader.parseEnchantmentName(ChatColor.GRAY + "With Spaces MLCD"));
        assertEquals("No Numerals", LoreReader.parseEnchantmentName(ChatColor.GRAY + "No Numerals Here"));
        assertEquals("", LoreReader.parseEnchantmentName(ChatColor.GRAY + "NoSpace"));
        assertEquals("A", LoreReader.parseEnchantmentName(ChatColor.GRAY + "A Test"));
        assertEquals("", LoreReader.parseEnchantmentName(ChatColor.GRAY + " FrontSpace"));
        assertEquals("EndSpace", LoreReader.parseEnchantmentName(ChatColor.GRAY + "EndSpace "));
        assertEquals("", LoreReader.parseEnchantmentName("Test IX"));
        assertEquals("", LoreReader.parseEnchantmentName(ChatColor.GRAY.toString()));
        assertEquals("", LoreReader.parseEnchantmentName(""));
    }

    @Test
    public void parseEnchantmentLevel() throws Exception {
        assertEquals(5, LoreReader.parseEnchantmentLevel("Test V"));
        assertEquals(9, LoreReader.parseEnchantmentLevel("With Spaces IX"));
        assertEquals(0, LoreReader.parseEnchantmentLevel("No Numerals"));
        assertEquals(0, LoreReader.parseEnchantmentLevel("A Test"));
        assertEquals(0, LoreReader.parseEnchantmentLevel(" FrontSpace"));
        assertEquals(0, LoreReader.parseEnchantmentLevel("EndSpace "));
        assertEquals(0, LoreReader.parseEnchantmentLevel(""));
    }

    @Test
    public void formatEnchantment() throws Exception {
        assertEquals(ChatColor.GRAY + "Test IX", LoreReader.formatEnchantment("Test", 9));
    }

    @Test
    public void isEnchantment() throws Exception {
        assertTrue(LoreReader.isEnchantment(ChatColor.GRAY + "Test IX", "Test"));
        assertFalse(LoreReader.isEnchantment(ChatColor.GRAY + "Test IX", "Other"));
        assertFalse(LoreReader.isEnchantment(ChatColor.GRAY + "Test", "Test"));
        assertFalse(LoreReader.isEnchantment("Test IX", "Test"));
        assertFalse(LoreReader.isEnchantment("", "Test"));
        assertFalse(LoreReader.isEnchantment("", "Test"));
    }
}