package com.sucy.enchant.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.util.RomanNumeralsTest
 */
public class RomanNumeralsTest {
    @Test
    public void toNumerals() throws Exception {
        assertEquals("I", RomanNumerals.toNumerals(1));
        assertEquals("III", RomanNumerals.toNumerals(3));
        assertEquals("IV", RomanNumerals.toNumerals(4));
        assertEquals("V", RomanNumerals.toNumerals(5));
        assertEquals("IX", RomanNumerals.toNumerals(9));
        assertEquals("X", RomanNumerals.toNumerals(10));
        assertEquals("XXXVII", RomanNumerals.toNumerals(37));
        assertEquals("XL", RomanNumerals.toNumerals(40));
        assertEquals("L", RomanNumerals.toNumerals(50));
        assertEquals("XCII", RomanNumerals.toNumerals(92));
        assertEquals("CIV", RomanNumerals.toNumerals(104));
        assertEquals("CD", RomanNumerals.toNumerals(400));
        assertEquals("DXXXI", RomanNumerals.toNumerals(531));
        assertEquals("CM", RomanNumerals.toNumerals(900));
        assertEquals("MCCXXXIV", RomanNumerals.toNumerals(1234));

    }

    @Test
    public void fromNumerals() throws Exception {
        assertEquals(1, RomanNumerals.fromNumerals("I"));
        assertEquals(3, RomanNumerals.fromNumerals("III"));
        assertEquals(4, RomanNumerals.fromNumerals("IV"));
        assertEquals(5, RomanNumerals.fromNumerals("V"));
        assertEquals(9, RomanNumerals.fromNumerals("IX"));
        assertEquals(10, RomanNumerals.fromNumerals("X"));
        assertEquals(37, RomanNumerals.fromNumerals("XXXVII"));
        assertEquals(40, RomanNumerals.fromNumerals("XL"));
        assertEquals(50, RomanNumerals.fromNumerals("L"));
        assertEquals(92, RomanNumerals.fromNumerals("XCII"));
        assertEquals(104, RomanNumerals.fromNumerals("CIV"));
        assertEquals(400, RomanNumerals.fromNumerals("CD"));
        assertEquals(531, RomanNumerals.fromNumerals("DXXXI"));
        assertEquals(900, RomanNumerals.fromNumerals("CM"));
        assertEquals(1234, RomanNumerals.fromNumerals("MCCXXXIV"));
    }
}