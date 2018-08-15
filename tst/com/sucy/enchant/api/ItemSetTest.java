package com.sucy.enchant.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * EnchantmentAPI © 2018
 * com.sucy.enchant.api.ItemSetTest
 */
public class ItemSetTest {

    @Test
    public void getItems() {
        assertEquals(1, ItemSet.TRIDENT.getItems().length);
    }
}