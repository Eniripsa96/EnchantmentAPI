package com.rit.sucy.lore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

public class LoreConfig {

    private final String fileName;
    private final JavaPlugin plugin;

    private File configFile;
    private FileConfiguration fileConfiguration;

    public LoreConfig(JavaPlugin plugin, String type) {
        this.plugin = plugin;
        this.fileName = type + ".yml";
        this.configFile = new File(plugin.getDataFolder(), fileName);
        saveDefaultConfig();
    }

    public Hashtable<String, List<String>> getLists() {
        Hashtable<String, List<String>> table = new Hashtable<String, List<String>>();
        for (String key : getConfig().getKeys(false))
            table.put(key, getConfig().getStringList(key));
        return table;
    }

    private void reloadConfig() {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    private FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reloadConfig();
        }
        return fileConfiguration;
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            this.plugin.saveResource(fileName, false);
        }
    }
}
