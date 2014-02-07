package com.rit.sucy.service;

/**
 * Represents a configuration node.
 */
public interface ConfigNode
{

    /**
     * Get the config path.
     *
     * @return Config path.
     */
    public String getPath();

    /**
     * Get the variable type.
     *
     * @return Variable type.
     */
    public VarType getVarType();

    /**
     * Get the SubType
     * Used especially for verfification
     *
     * @return SubType
     */
    public SubType getSubType();

    /**
     * Get the default value.
     *
     * @return Default value.
     */
    public Object getDefaultValue();

    /**
     * Variable Types.
     */
    public enum VarType
    {
        STRING,
        INTEGER,
        DOUBLE,
        BOOLEAN,
        LIST
    }

    /**
     * SubTypes, like percentage, y-value, custom etc
     */
    public enum SubType
    {
        PERCENTAGE,
        Y_VALUE,
        HEALTH,
        NATURAL_NUMBER
    }
}
