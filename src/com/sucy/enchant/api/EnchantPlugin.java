package com.sucy.enchant.api;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.api.EnchantPlugin
 *
 * Interface to be implemented by plugins adding new enchantments to the server.
 * EnchantmentAPI will find any plugins implementing this class and call the
 * registration method on enable.
 */
public interface EnchantPlugin {

    /**
     * This is where you should register your enchantments.
     *
     * @param registry registry to register enchantments with
     */
    void registerEnchantments(EnchantmentRegistry registry);
}
