package com.rit.sucy.enchanting;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.service.ItemSets;
import com.rit.sucy.service.SuffixGroups;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

/**
 * Holds information about Enchantments in the vanilla game
 * like their weight, and the exp levels required to get a specific lvl of an enchantment
 */
public enum VanillaData
{
    /**
     * ARMOR
     */
    PROTECTION_ENVIRONMENTAL (Enchantment.PROTECTION_ENVIRONMENTAL, ItemSets.ARMOR.getItems(), "Damage_Reduction",
            new String[] { SuffixGroups.DEFENSE.getKey() }, 10, 1, 11, 4),
    PROTECTION_FALL(Enchantment.PROTECTION_FALL, ItemSets.BOOTS.getItems(),
            new String[] { SuffixGroups.DEFENSE.getKey(), SuffixGroups.FALL.getKey() }, 5, 5, 6, 4),
    PROTECTION_FIRE(Enchantment.PROTECTION_FIRE, ItemSets.ARMOR.getItems(), "Damage_Reduction",
            new String[] { SuffixGroups.DEFENSE.getKey(), SuffixGroups.FIRE.getKey() }, 5, 10, 8, 4),
    PROTECTION_PROJECTILE(Enchantment.PROTECTION_PROJECTILE, ItemSets.ARMOR.getItems(), "Damage_Reduction",
            new String[] { SuffixGroups.DEFENSE.getKey(), SuffixGroups.PROJECTILE.getKey() }, 5, 3, 6, 4),
    WATER_WORKER(Enchantment.WATER_WORKER, ItemSets.HELMETS.getItems(),
            new String[] { SuffixGroups.DIGGING.getKey(), SuffixGroups.BREATHING.getKey() }, 2,  1, 20, 1),
    PROTECTION_EXPLOSIONS(Enchantment.PROTECTION_EXPLOSIONS, ItemSets.ARMOR.getItems(), "Damage_Reduction",
            new String[] { SuffixGroups.DEFENSE.getKey(), SuffixGroups.EXPLOSIONS.getKey() }, 2,  5, 8, 4),
    OXYGEN(Enchantment.OXYGEN, ItemSets.HELMETS.getItems(),
            new String[] { SuffixGroups.BREATHING.getKey() }, 2,  10, 10, 3),
    THORNS(Enchantment.THORNS, ItemSets.CHESTPLATES.getItems(),
            new String[] { SuffixGroups.STRENGTH.getKey() }, 1,  10, 20, 3),

    /**
     * WEAPONS
     */
    DAMAGE_ALL(Enchantment.DAMAGE_ALL, ItemSets.SWORDS.getItems(), "Bonus_Damage",
            new String[] { SuffixGroups.STRENGTH.getKey() }, 10, 1, 11, 5),
    DAMAGE_ARTHROPODS(Enchantment.DAMAGE_ARTHROPODS, ItemSets.SWORDS.getItems(), "Bonus_Damage",
            new String[] { SuffixGroups.STRENGTH.getKey() }, 4, 5, 8, 5),
    KNOCKBACK(Enchantment.KNOCKBACK, ItemSets.SWORDS.getItems(),
            new String[] { SuffixGroups.FORCE.getKey() }, 8, 4, 20, 2),
    DAMAGE_UNDEAD(Enchantment.DAMAGE_UNDEAD, ItemSets.SWORDS.getItems(), "Bonus_Damage",
            new String[] { SuffixGroups.STRENGTH.getKey() }, 4, 5, 8, 5),
    FIRE_ASPECT(Enchantment.FIRE_ASPECT, ItemSets.SWORDS.getItems(),
            new String[] { SuffixGroups.FIRE.getKey() }, 2, 9, 20, 2),
    LOOT_BONUS_MOBS(Enchantment.LOOT_BONUS_MOBS, ItemSets.SWORDS.getItems(),
            new String[] { SuffixGroups.LOOT.getKey() }, 2, 14, 9, 3),

    /**
     * TOOLS
     */
    DIG_SPEED(Enchantment.DIG_SPEED, ItemSets.TOOLS.getItems(),
            new String[] { SuffixGroups.DIGGING.getKey() }, 10, 1, 10, 5),
    DURABILITY(Enchantment.DURABILITY, ItemSets.TOOLS.getItems(),
            new String[] { SuffixGroups.DURABILITY.getKey() }, 5, 5, 8, 3),
    LOOT_BONUS_BLOCKS(Enchantment.LOOT_BONUS_BLOCKS, ItemSets.TOOLS.getItems(), "Block_Modifier",
            new String[] { SuffixGroups.DIGGING.getKey(), SuffixGroups.LOOT.getKey() }, 2, 15, 9, 3),
    SILK_TOUCH(Enchantment.SILK_TOUCH, ItemSets.TOOLS.getItems(), "Block_Modifier",
            new String[] { SuffixGroups.DIGGING.getKey() }, 1, 15, 20, 1),

    /**
     * BOW
     */
    ARROW_DAMAGE(Enchantment.ARROW_DAMAGE, ItemSets.BOW.getItems(),
            new String[] { SuffixGroups.STRENGTH.getKey() }, 10, 1, 10, 5),
    ARROW_FIRE(Enchantment.ARROW_FIRE, ItemSets.BOW.getItems(),
            new String[] { SuffixGroups.FIRE.getKey() }, 2, 20, 20, 1),
    ARROW_KNOCKBACK(Enchantment.ARROW_KNOCKBACK, ItemSets.BOW.getItems(),
            new String[] { SuffixGroups.FORCE.getKey() }, 2, 12, 20, 2),
    ARROW_INFINITE(Enchantment.ARROW_INFINITE, ItemSets.BOW.getItems(),
            new String[] { SuffixGroups.PROJECTILE.getKey(), SuffixGroups.DURABILITY.getKey() }, 1, 20, 20, 1),
    ;
    /**
     * The Enchantment id in the vanilla game
     */
    private final Enchantment enchantment;

    /**
     * The conflict group of the enchantment
     */
    private final String group;

    /**
     * The weight this enchantment has when enchants are choosen
     */
    private final int enchantWeight;

    /**
     * Base value for calculating enchantment level
     */
    private final double base;

    /**
     * Max level of the enchantment
     */
    private final int maxLevel;

    /**
     * Interval value for calculating enchantment level
     */
    private final double interval;

    /**
     * Natural items for the enchantment
     */
    private final Material[] items;

    /**
     * Group for suffixes
     */
    private final String[] suffixGroup;

    /**
     * Private constructor for this enum
     *
     * @param enchantment   vanilla enchantment
     * @param items         natural items
     * @param enchantWeight enchantment weight
     * @param base          base level value
     * @param interval      interval level value
     * @param max           max enchantment level
     */
    private VanillaData(Enchantment enchantment, Material[] items, String[] suffixGroup, int enchantWeight, double base, double interval, int max){
        this(enchantment, items, CustomEnchantment.DEFAULT_GROUP, suffixGroup, enchantWeight, base, interval, max);
    }

    /**
     * Private constructor for this enum
     *
     * @param enchantment   vanilla enchantment
     * @param items         natural items
     * @param group         conflict group
     * @param enchantWeight enchantment weight
     * @param base          base level value
     * @param interval      interval level value
     * @param max           max enchantment level
     */
    private VanillaData(Enchantment enchantment, Material[] items, String group, String[] suffixGroup, int enchantWeight, double base, double interval, int max) {
        this.enchantment = enchantment;
        this.group = group;
        this.enchantWeight = enchantWeight;
        this.base = base;
        this.maxLevel = max;
        this.interval = interval;
        this.items = items;
        this.suffixGroup = suffixGroup;
    }

    /**
     * The groups the enchantment is in
     *
     * @return suffix group list
     */
    public String[] getSuffixGroup() {
        return suffixGroup;
    }

    /**
     * @return the natural items for the enchantment
     */
    public Material[] getItems() {
        return items;
    }

    /**
     * @return the base value for calculating enchantment levels
     */
    public double getBase() {
        return base;
    }

    /**
     * @return the max level for the enchantment
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * @return gets the interval value for calculating enchantment levels
     */
    public double getInterval() {
        return interval;
    }

    /**
     * Get the weight of the enchantment (used when choosing which enchant we should choose)
     *
     * @return the weight of the enchant
     */
    public int getEnchantWeight() {
        return enchantWeight;
    }

    /**
     * Get the group of the enchantment
     *
     * @return the group of the enchant
     */
    public String getGroup() {
        return group;
    }

    /**
     * Get the actual Enchantment id from the Vanilla Enchantment enum
     *
     * @return the vanilla enchantment
     */
    public Enchantment getEnchantment() {
        return enchantment;
    }
}
