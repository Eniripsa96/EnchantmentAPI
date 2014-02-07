package com.rit.sucy.service;

import com.rit.sucy.EnchantmentAPI;

/**
 * ExtraHardMode module.
 */
public abstract class EAPIModule implements IModule
{
    /**
     * Plugin reference.
     */
    protected final EnchantmentAPI plugin;

    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    protected EAPIModule(EnchantmentAPI plugin)
    {
        this.plugin = plugin;
    }
}
