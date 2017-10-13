package com.sucy.enchant.skillapi;

import com.google.common.collect.ImmutableList;
import com.rit.sucy.config.CommentedConfig;
import com.sucy.enchant.EnchantmentAPI;
import com.sucy.enchant.api.CustomEnchantment;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.List;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.skillapi.SkillAPIHook
 */
public class SkillAPIHook {

    public static List<CustomEnchantment> getEnchantments(final EnchantmentAPI plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("SkillAPI")) {
            return loadAll(plugin);
        }
        plugin.getLogger().info("SkillAPI not enabled, skipping skill-based enchantments");
        return ImmutableList.of();
    }

    private static List<CustomEnchantment> loadAll(final EnchantmentAPI plugin) {
        final String path = plugin.getDataFolder().getAbsolutePath() + "/" + SkillEnchantment.SAVE_FOLDER;
        final File directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                plugin.getLogger().severe("Unable to create folder at: " + path);
            }
        }

        final File[] files = directory.listFiles();
        if (files == null) {
            plugin.getLogger().warning("Folder \"" + path + "\" does not exist");
            return ImmutableList.of();
        }

        final ImmutableList.Builder<CustomEnchantment> result = ImmutableList.builder();
        for (final File file : files) {
            if (!file.getName().endsWith(".yml")) {
                plugin.getLogger().warning(file.getName() + " is not a .yml file but is in the skill enchantments folder");
                continue;
            }

            try {
                final String fileName = file.getName().replace(".yml", "");
                final CommentedConfig config = new CommentedConfig(plugin, SkillEnchantment.SAVE_FOLDER + fileName);
                result.add(new SkillEnchantment(fileName, config.getConfig()));
            } catch (final Exception ex) {
                // Do nothing
                plugin.getLogger().warning("Failed to load " + file.getName() + ", make sure it points to a valid skill");
                ex.printStackTrace();
            }
        }
        return result.build();
    }
}
