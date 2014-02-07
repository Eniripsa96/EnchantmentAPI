package com.rit.sucy.config;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.enchanting.VanillaEnchantment;
import com.rit.sucy.service.MaterialsParser;
import com.rit.sucy.service.ModularConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Configuration handler for the root config.yml file.
 */
public class RootConfig extends ModularConfig
{
    public static String baseNode = "EnchantmentAPI.";
    private String customNode = "Custom Enchantments.";
    private String vanillaNode = "Vanilla Enchantments.";

    /**
     * Constructor. You no say?
     *
     * @param plugin - plugin instance.
     */
    public RootConfig(EnchantmentAPI plugin)
    {
        super(plugin);
    }

    @Override
    public void starting()
    {
        //DO NOTHING WE HAVE TO WAIT FOR THE ENCHANTMENTS TO BE LOADED
    }

    @Override
    public void closing()
    {
        plugin.reloadConfig();
        plugin.saveConfig();
    }

    @Override
    public void save()
    {
        plugin.saveConfig();
    }

    @Override
    public void set(String path, Object value)
    {
        final ConfigurationSection config = plugin.getConfig();
        config.set(path, value);
        plugin.saveConfig();
    }

    @Override
    public void reload()
    {
        plugin.reloadConfig();
        loadDefaults(plugin.getConfig());
        loadSettings(plugin.getConfig());
        boundsCheck();
        loadEnchantments(plugin.getConfig());
        loadLanguage(plugin.getConfig());
        writeConfig();
    }

    @Override
    public void loadSettings(ConfigurationSection config)
    {
        for (final RootNode node : RootNode.values())
        {
            updateOption(node, config);
        }
    }

    @Override
    public void loadDefaults(ConfigurationSection config)
    {
        for (RootNode node : RootNode.values())
        {
            if (!config.contains(node.getPath()))
            {
                config.set(node.getPath(), node.getDefaultValue());
            }
        }
    }

    public void loadLanguage(ConfigurationSection config) {
        for (LanguageNode node : LanguageNode.values()) {
            if (!config.contains(node.getFullPath()) || config.getStringList(node.getFullPath()).size() == 0) {
                config.set(node.getFullPath(), node.getDefaultValue());
            }
            else if ((node == LanguageNode.TABLE_ENCHANTABLE || node == LanguageNode.TABLE_UNENCHANTABLE)
                && config.getStringList(node.getFullPath()).size() != 2) {
                config.set(node.getFullPath(), node.getDefaultValue());
            }
        }
    }

    /**
     * Loads settings from the config and updates the enchantments in memory
     */
    public void loadEnchantments (ConfigurationSection config)
    {
        Collection<CustomEnchantment> enchantments = EnchantmentAPI.getEnchantments();
        for (CustomEnchantment enchantment : enchantments)
        {
            String section = enchantment instanceof VanillaEnchantment ? vanillaNode : customNode;
            for (EnchantmentNode node : EnchantmentNode.values())
            {
                String path = baseNode + section + enchantment.name() + node.getPath();
                if (config.contains(path))
                {
                    Object obj = config.get(path);
                    switch(node)
                    {
                        case ENABLED:
                            if (obj instanceof Boolean)
                                enchantment.setEnabled((Boolean) obj);
                            break;
                        case TABLE:
                            if (obj instanceof Boolean)
                                enchantment.setTableEnabled((Boolean)obj);
                            break;
                        case ITEMS:
                            if (obj instanceof List)
                            {
                                @SuppressWarnings("unchecked")
                                List<String> stringList = (List<String>) obj;
                                Material[] materials = MaterialsParser.toMaterial(stringList.toArray(new String[stringList.size()]));
                                enchantment.setNaturalMaterials(materials);
                            }
                            break;
                        case WEIGHT:
                            if (obj instanceof Integer)
                                enchantment.setWeight((Integer) obj);
                            break;
                        case GROUP:
                            if (obj instanceof String)
                                enchantment.setGroup(((String) obj));
                            break;
                        case MAX:
                            if (obj instanceof Integer)
                                enchantment.setMaxLevel((Integer)obj);
                            break;
                        case BASE:
                            if (obj instanceof Integer)
                                enchantment.setBase((Integer)obj);
                            else if (obj instanceof Double)
                                enchantment.setBase((Double)obj);
                            break;
                        case INTERVAL:
                            if (obj instanceof Double)
                                enchantment.setInterval((Double)obj);
                            else if (obj instanceof Integer)
                                enchantment.setInterval((Integer)obj);
                            break;
                        default:
                            throw new UnsupportedOperationException("The node " + node.name() + " hasn't been configured yet");
                    }
                }
            }
        }
    }

    /**
     * Writes the config to file.
     * First the settings.
     * then the custom enchantment settings.
     * then the vanilla enchantment settings.
     * finally the language settings.
     */
    public void writeConfig ()
    {
        FileConfiguration config = plugin.getConfig();
        YamlConfiguration out = new YamlConfiguration();
        //Normal Settings
        if (RootNode.values().length > 0)
        {
            for (RootNode node : RootNode.values())
            {
                out.set(node.getPath(), config.get(node.getPath()));
            }
        }

        //Separate vanilla from custom enchants
        List<CustomEnchantment> customEnchantments = new ArrayList<CustomEnchantment>(EnchantmentAPI.getEnchantments());
        List<CustomEnchantment> vanillaEnchantments = new ArrayList<CustomEnchantment>();
        Iterator<CustomEnchantment> iter = customEnchantments.iterator();
        while (iter.hasNext())
        {
            CustomEnchantment customEnchant = iter.next();
            if (customEnchant instanceof VanillaEnchantment)
            {
                vanillaEnchantments.add(customEnchant);
                iter.remove();
            }
        }

        // Save the enchantments
        Collections.sort(customEnchantments);
        for (CustomEnchantment enchant : customEnchantments)
        {
            String path = baseNode + customNode + enchant.name();
            // Enabled
            if (getBoolean(RootNode.CUSTOM_ENABLED))
                out.set(path + EnchantmentNode.ENABLED.getPath(), enchant.isEnabled());
            // Table enabled
            if (getBoolean(RootNode.CUSTOM_TABLE))
                out.set(path + EnchantmentNode.TABLE.getPath(), enchant.isTableEnabled());
            // Weight
            if (getBoolean(RootNode.CUSTOM_WEIGHT))
                out.set(path + EnchantmentNode.WEIGHT.getPath(), enchant.getWeight());
            // Group
            if (getBoolean(RootNode.CUSTOM_GROUPS))
                out.set(path + EnchantmentNode.GROUP.getPath(), enchant.getGroup());
            // Max Level
            if (getBoolean(RootNode.CUSTOM_MAX))
                out.set(path + EnchantmentNode.MAX.getPath(), enchant.getMaxLevel());
            // Base
            if (getBoolean(RootNode.CUSTOM_BASE))
                out.set(path + EnchantmentNode.BASE.getPath(), enchant.getBase());
            // Interval
            if (getBoolean(RootNode.CUSTOM_INTERVAL))
                out.set(path + EnchantmentNode.INTERVAL.getPath(), enchant.getInterval());
            // Items
            if (getBoolean(RootNode.CUSTOM_ITEMS))
                out.set(path + EnchantmentNode.ITEMS.getPath(), MaterialsParser.toStringArray(enchant.getNaturalMaterials()));
        }

        Collections.sort(vanillaEnchantments);
        for (CustomEnchantment enchant : vanillaEnchantments)
        {
            String path = baseNode + vanillaNode + enchant.name();
            // Enabled
            if (getBoolean(RootNode.VANILLA_ENABLED))
                out.set(path + EnchantmentNode.ENABLED.getPath(), enchant.isEnabled());
            // Table Enabled
            if (getBoolean(RootNode.VANILLA_TABLE))
                out.set(path + EnchantmentNode.TABLE.getPath(), enchant.isTableEnabled());
            // Weight
            if (getBoolean(RootNode.VANILLA_WEIGHT))
                out.set(path + EnchantmentNode.WEIGHT.getPath(), enchant.getWeight());
            // Group
            if (getBoolean(RootNode.VANILLA_GROUPS))
                out.set(path + EnchantmentNode.GROUP.getPath(), enchant.getGroup());
            // Max Level
            if (getBoolean(RootNode.VANILLA_MAX))
                out.set(path + EnchantmentNode.MAX.getPath(), enchant.getMaxLevel());
            // Base
            if (getBoolean(RootNode.VANILLA_BASE))
                out.set(path + EnchantmentNode.BASE.getPath(), enchant.getBase());
            // Interval
            if (getBoolean(RootNode.VANILLA_INTERVAL))
                out.set(path + EnchantmentNode.INTERVAL.getPath(), enchant.getInterval());
            // Items
            if (getBoolean(RootNode.VANILLA_ITEMS))
                out.set(path + EnchantmentNode.ITEMS.getPath(), MaterialsParser.toStringArray(enchant.getNaturalMaterials()));
        }

        for (LanguageNode node : LanguageNode.values()) {
            String path = node.getFullPath();
            out.set(path, config.get(path));
        }

        try {
            String path = plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml"; //so we can see the var in debugger
            out.save(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void boundsCheck() {

    }
}