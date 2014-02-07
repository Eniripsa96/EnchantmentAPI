package com.rit.sucy.service;

/**
 * All known permission nodes.
 */
public enum PermissionNode
{
    /**
     * Access to commands that modify enchantments
     */
    ADMIN("admin"),
    /**
     * Access to a basic list command
     */
    LIST("list"),
    /**
     * Access to getting enchantment description books
     */
    BOOK("book"),
    /**
     * Access to custom item name generation
     */
    NAMES("names"),
    /**
     * Access to using the enchantment table
     */
    TABLE("table"),
    /**
     * Access to using all enchantments
     */
    ENCHANT ("enchant"),
    /**
     * Access to all vanilla enchantments
     */
    ENCHANT_VANILLA ("enchant.vanilla");
    /**
     * Prefix for all permission nodes.
     */
    private static final String PREFIX = "EnchantmentAPI.";

    /**
     * Resulting permission node path.
     */
    private final String node;

    /**
     * Constructor.
     *
     * @param subperm - specific permission path.
     */
    private PermissionNode(String subperm)
    {
        this.node = PREFIX + subperm;
    }

    /**
     * Get the full permission node path.
     *
     * @return Permission node path.
     */
    public String getNode()
    {
        return node;
    }
}