package com.sucy.enchant.api;

import com.google.common.collect.ImmutableMap;
import com.sucy.enchant.EnchantmentAPI;
import com.sucy.enchant.TestUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.api.EnchantmentsTest
 */
public class EnchantmentsTest {

    private static final Map<Enchantment, Integer> ENCHANTS = ImmutableMap.of(
            TestUtils.createVanillaEnchantment("TEST"), 1,
            TestUtils.createVanillaEnchantment("SAMPLE"), 3);

    private ItemStack item;

    @Before
    public void setUp() throws Exception {
        item = mock(ItemStack.class);
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.tearDown();
    }

    @Test
    public void getCustomEnchantments() throws Exception {

    }

    @Test
    public void getAllEnchantments() throws Exception {
        when(item.getEnchantments()).thenReturn(ENCHANTS);
        final Map<CustomEnchantment, Integer> result = Enchantments.getAllEnchantments(item);
        final Map<CustomEnchantment, Integer> expected = ImmutableMap.of(
                EnchantmentAPI.getEnchantment("TEST"), 1,
                EnchantmentAPI.getEnchantment("SAMPLE"), 3);
    }

    @Test
    public void hasCustomEnchantment() throws Exception {
    }

    @Test
    public void removeAllEnchantments() throws Exception {
    }

    @Test
    public void removeCustomEnchantments() throws Exception {
    }

    @Test
    public void roll() {
        final Random random = new Random();
        final int runs = 1000000;
        final double increment = 100.0 / runs;
        double chance = 0;
        for (int i = 0; i < 1000000; i++) {
            if (random.nextInt(5) == 0 || random.nextInt(4) == 0) {
                chance += increment;
            }
        }
        System.out.println("Chance: " + chance);
    }
}