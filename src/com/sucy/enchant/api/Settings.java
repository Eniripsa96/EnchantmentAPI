package com.sucy.enchant.api;

import com.rit.sucy.config.parse.DataSection;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.api.Settings
 */
public class Settings extends DataSection {

    private static final String BASE = "-base";
    private static final String SCALE = "-scale";

    /**
     * Sets a scaling configuration setting
     *
     * @param key setting key
     * @param base base value (at enchantment level 1)
     * @param scale value scale (extra per enchantment level)
     */
    public void set(final String key, final double base, final double scale) {
        set(key + BASE, base);
        set(key + SCALE, scale);
    }

    /**
     * Gets a scaling setting based on the provided level
     *
     * @param key setting key
     * @param level enchantment level
     * @return scaled setting value
     */
    public double get(final String key, final int level) {
        return getDouble(key + BASE, 0) + getDouble(key + SCALE, 0) * (level - 1);
    }
}
