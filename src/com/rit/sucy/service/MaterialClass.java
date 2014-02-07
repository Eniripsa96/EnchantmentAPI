package com.rit.sucy.service;

import org.bukkit.Material;

/**
 * Determine to which type of Material this item belongs stone/iron/diamond/chain...
 * and hold default values about enchantability
 * @author Diemex
 */
public enum MaterialClass
{
    /**
     * Wood Tools/Weapons
     */
    WOOD (15),
    /**
     * Stone Tools/Weapons
     */
    STONE (5),
    /**
     * Iron Armor/Tools/Weapons
     */
    IRON (14),
    /**
     * Gold Armor/Tools/Weapons
     */
    GOLD (25),
    /**
     * Diamond Armor/Tools/Weapons
     */
    DIAMOND (10),
    /**
     * Leather Armor
     */
    LEATHER (15),
    /**
     * Chain_Mail Armor
     */
    CHAIN (12),
    /**
     * When nothing can be said about this particular item
     */
    DEFAULT (10);

    private final int enchantability;

    /**
     * Constructor
     *
     * @param enchantabilty     enchantability of this MaterialClass
     */
    private MaterialClass (int enchantabilty)
    {
        this.enchantability = enchantabilty;
    }

    /**
     * Get the enchantability of this item
     *
     * @return enchantability
     */
    public int getEnchantability()
    {
        return enchantability;
    }

    /**
     * Get the enchantability for a certain item type
     *
     * @param material  for which to get the enchantability
     * @return          the enchantibily for the Material or the default value if not found (10)
     */
    public static int getEnchantabilityFor (Material material)
    {
        int enchantability = MaterialClass.DEFAULT.getEnchantability();

        for (MaterialClass materialClass : MaterialClass.values())
        {
            if (material.name().contains (materialClass.name() + "_"))
            {
                enchantability = materialClass.getEnchantability();
                break;
            }
        }
        return enchantability;
    }
}
