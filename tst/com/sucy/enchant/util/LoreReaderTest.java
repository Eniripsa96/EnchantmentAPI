package com.sucy.enchant.util;

import com.sucy.enchant.TestUtils;
import com.sucy.enchant.api.CustomEnchantment;
import org.bukkit.ChatColor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.util.LoreReaderTest
 */
@RunWith(MockitoJUnitRunner.class)
public class LoreReaderTest {

    @Mock
    private CustomEnchantment enchantment;

    @Test
    public void parseEnchantmentName() {
        assertEquals("Test", LoreReader.parseEnchantmentName(ChatColor.GRAY + "Test IX"));
        assertEquals("With Spaces", LoreReader.parseEnchantmentName(ChatColor.GRAY + "With Spaces MLCD"));
        assertEquals("No Numerals Here", LoreReader.parseEnchantmentName(ChatColor.GRAY + "No Numerals Here"));
        assertEquals("NoSpace", LoreReader.parseEnchantmentName(ChatColor.GRAY + "NoSpace"));
        assertEquals("A Test", LoreReader.parseEnchantmentName(ChatColor.GRAY + "A Test"));
        assertEquals(" FrontSpace", LoreReader.parseEnchantmentName(ChatColor.GRAY + " FrontSpace"));
        assertEquals("EndSpace ", LoreReader.parseEnchantmentName(ChatColor.GRAY + "EndSpace "));
        assertEquals("", LoreReader.parseEnchantmentName("Test IX"));
        assertEquals("", LoreReader.parseEnchantmentName(ChatColor.GRAY.toString()));
        assertEquals("", LoreReader.parseEnchantmentName(""));
    }

    @Test
    public void parseEnchantmentLevel() {
        assertEquals(5, LoreReader.parseEnchantmentLevel("Test V"));
        assertEquals(9, LoreReader.parseEnchantmentLevel("With Spaces IX"));
        assertEquals(1, LoreReader.parseEnchantmentLevel("No Numerals"));
        assertEquals(1, LoreReader.parseEnchantmentLevel("A Test"));
        assertEquals(1, LoreReader.parseEnchantmentLevel(" FrontSpace"));
        assertEquals(1, LoreReader.parseEnchantmentLevel("EndSpace "));
        assertEquals(1, LoreReader.parseEnchantmentLevel(""));
    }

    @Test
    public void formatEnchantmentWithLevel() {
        when(enchantment.getName()).thenReturn("Test");
        when(enchantment.getMaxLevel()).thenReturn(10);
        assertEquals(ChatColor.GRAY + "Test IX", LoreReader.formatEnchantment(enchantment, 9));
    }

    @Test
    public void formatEnchantmentWithoutLevel() {
        when(enchantment.getName()).thenReturn("Test");
        when(enchantment.getMaxLevel()).thenReturn(1);
        assertEquals(ChatColor.GRAY + "Test", LoreReader.formatEnchantment(enchantment, 9));
    }

    @Test
    public void isEnchantment() {
        TestUtils.createEnchantment("Test");
        assertTrue(LoreReader.isEnchantment(ChatColor.GRAY + "Test IX"));
        assertTrue(LoreReader.isEnchantment(ChatColor.GRAY + "Test"));
        assertFalse(LoreReader.isEnchantment("Test IX"));
        assertFalse(LoreReader.isEnchantment(ChatColor.GRAY + "Different IX"));
        assertFalse(LoreReader.isEnchantment(""));
        assertFalse(LoreReader.isEnchantment(""));
    }
}