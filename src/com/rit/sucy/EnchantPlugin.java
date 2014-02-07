package com.rit.sucy;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Base class for plugins that use this API
 */
public class EnchantPlugin extends JavaPlugin {

    /**
     * Register each of your custom enchantments with this method
     * This needs to be overridden
     */
    public void registerEnchantments(){}
}
