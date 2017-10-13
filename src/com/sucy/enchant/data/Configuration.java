package com.sucy.enchant.data;

import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.parse.DataSection;
import com.sucy.enchant.EnchantmentAPI;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.data.Configuration
 */
public class Configuration {

    private static DataSection data;

    public static void reload(final EnchantmentAPI plugin) {
        final CommentedConfig config = new CommentedConfig(plugin, "config");
        config.saveDefaultConfig();
        config.checkDefaults();
        config.trim();
        config.save();
        data = config.getConfig();
    }

    public static boolean using(final ConfigKey configKey) {
        return data.getBoolean(configKey.getKey());
    }

    public static int amount(final ConfigKey configKey) {
        return data.getInt(configKey.getKey());
    }
}
