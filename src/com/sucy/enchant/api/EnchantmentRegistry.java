package com.sucy.enchant.api;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.api.EnchantmentRegistry
 */
public interface EnchantmentRegistry {

    /**
     * Registers enchantments with the API, allowing them to be available
     * in the enchanting tables, commands, and anvils.
     *
     * @param enchantments enchantments to register
     */
    void register(CustomEnchantment... enchantments);
}
