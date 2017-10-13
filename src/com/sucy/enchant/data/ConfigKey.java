package com.sucy.enchant.data;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.data.ConfigKey
 */
public enum ConfigKey {
    COLORED_NAMES_IN_ANVILS,
    CUSTOM_FISHING,
    FISHING_ENCHANTING_LEVEL,
    GLOBAL_ANVIL_LEVEL,
    MAX_ENCHANTMENTS,
    MAX_MERGED_ENCHANTMENTS,
    NON_ENCHANTABLES;

    private final String key = name().toLowerCase().replace('_', '-');

    public String getKey() {
        return key;
    }
}
