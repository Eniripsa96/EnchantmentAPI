/*
 * This file is part of
 * ExtraHardMode Server Plugin for Minecraft
 *
 * Copyright (C) 2012 Ryan Hamshire
 * Copyright (C) 2013 Diemex
 *
 * ExtraHardMode is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ExtraHardMode is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero Public License
 * along with ExtraHardMode.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.rit.sucy.config;

import com.rit.sucy.service.ConfigNode;

/**
* Configuration options of the root config.yml file.
*/
public enum RootNode implements ConfigNode
{
    ITEM_LORE
            ("Generate Unique Name", VarType.BOOLEAN, true),
    MAX_ENCHANTS
            ("Max Enchantments", VarType.INTEGER, 5),
    VANILLA_ENABLED
            (getVisibleSettingsNode() + getVanillaNode() + "Enabled", VarType.BOOLEAN, true),
    VANILLA_TABLE
            (getVisibleSettingsNode() + getVanillaNode() + "Table", VarType.BOOLEAN, true),
    VANILLA_WEIGHT
            (getVisibleSettingsNode() + getVanillaNode() + "Weight", VarType.BOOLEAN, true),
    VANILLA_ITEMS
            (getVisibleSettingsNode() + getVanillaNode() + "Items", VarType.BOOLEAN, true),
    VANILLA_GROUPS
            (getVisibleSettingsNode() + getVanillaNode() + "Groups", VarType.BOOLEAN, true),
    VANILLA_MAX
            (getVisibleSettingsNode() + getVanillaNode() + "Max Level", VarType.BOOLEAN, true),
    VANILLA_BASE
            (getVisibleSettingsNode() + getVanillaNode() + "Base", VarType.BOOLEAN, true),
    VANILLA_INTERVAL
            (getVisibleSettingsNode() + getVanillaNode() + "Interval", VarType.BOOLEAN, true),
    CUSTOM_ENABLED
            (getVisibleSettingsNode() + getCustomNode() + "Enabled", VarType.BOOLEAN, true),
    CUSTOM_TABLE
            (getVisibleSettingsNode() + getCustomNode() + "Table", VarType.BOOLEAN, true),
    CUSTOM_WEIGHT
            (getVisibleSettingsNode() + getCustomNode() + "Weight", VarType.BOOLEAN, true),
    CUSTOM_ITEMS
            (getVisibleSettingsNode() + getCustomNode() + "Items", VarType.BOOLEAN, true),
    CUSTOM_GROUPS
            (getVisibleSettingsNode() + getCustomNode() + "Groups", VarType.BOOLEAN, true),
    CUSTOM_MAX
            (getVisibleSettingsNode() + getCustomNode() + "Max Level", VarType.BOOLEAN, true),
    CUSTOM_BASE
            (getVisibleSettingsNode() + getCustomNode() + "Base", VarType.BOOLEAN, true),
    CUSTOM_INTERVAL
            (getVisibleSettingsNode() + getCustomNode() + "Interval", VarType.BOOLEAN, true),

    ;

    /**
     * Path.
     */
    private final String path;
    /**
     * Variable type.
     */
    private final VarType type;
    /**
     * Subtype like percentage, y-value, health
     */
    private SubType subType = null;
    /**
     * Default value.
     */
    private final Object defaultValue;

    /**
     * Constructor.
     *
     * @param path - Configuration path.
     * @param type - Variable type.
     * @param def  - Default value.
     */
    private RootNode(String path, VarType type, Object def)
    {
        this.path = path;
        this.type = type;
        this.defaultValue = def;
    }

    private RootNode(String path, VarType type, SubType subType, Object def)
    {
        this.path = path;
        this.type = type;
        this.defaultValue = def;
        this.subType = subType;
    }

    @Override
    public String getPath()
    {
        return RootConfig.baseNode + getNode() + path;
    }

    @Override
    public VarType getVarType()
    {
        return type;
    }

    @Override
    public Object getDefaultValue()
    {
        return defaultValue;
    }

    @Override
    public SubType getSubType() {
        return subType;
    }

    public static String getNode() {
        return "Settings.";
    }

    public static String getVisibleSettingsNode() {
        return "Visible Settings.";
    }

    public static String getCustomNode() {
        return "Custom Enchantments.";
    }

    public static String getVanillaNode() {
        return "Vanilla Enchantments.";
    }
}
