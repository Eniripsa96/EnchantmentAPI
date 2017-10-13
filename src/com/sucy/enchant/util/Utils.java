package com.sucy.enchant.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.util.Utils
 */
public class Utils {

    /**
     * @param item item to check
     * @return true if isPresent, false otherwise
     */
    public static boolean isPresent(final ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }
}
