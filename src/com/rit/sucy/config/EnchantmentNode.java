package com.rit.sucy.config;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.service.ConfigNode;

import java.util.Collections;

/**
 * @author Diemex
 */
public enum EnchantmentNode implements ConfigNode
{
    /**
     * Whether this enchantment is enabled and can be used
     */
    ENABLED ("Enabled", VarType.BOOLEAN, true),

    /**
     * Whether or not the enchantment can be obtained in the table
     */
    TABLE ("Table", VarType.BOOLEAN, true),

    /**
     * Weight determines the commonness of the enchantment, higher weight = more common
     */
    WEIGHT ("Weight", VarType.INTEGER, 5),

    /**
     * Determines what enchantments conflict with others
     */
    GROUP ("Group", VarType.STRING, CustomEnchantment.DEFAULT_GROUP),

    /**
     * The maximum level of the enchantment
     */
    MAX ("Max Level", VarType.INTEGER, 1),

    /**
     * The base value for calculating enchantment levels
     */
    BASE ("Base", VarType.DOUBLE, 1),

    /**
     * The interval for calculating enchantment level
     */
    INTERVAL ("Interval", VarType.DOUBLE, 10),

    /**
     * The items on which this enchantment can be obtained through the enchanting table
     */
    ITEMS ("Items", VarType.LIST, Collections.emptyList()),
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
    private EnchantmentNode(String path, VarType type, Object def)
    {
        this.path = path;
        this.type = type;
        this.defaultValue = def;
    }

    private EnchantmentNode(String path, VarType type, SubType subType, Object def)
    {
        this.path = path;
        this.type = type;
        this.defaultValue = def;
        this.subType = subType;
    }

    /**
     * Returns the path with a dot before
     *
     * @return
     */
    @Override
    public String getPath()
    {
        return "." + path;
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
    public SubType getSubType()
    {
        return subType;
    }
}
