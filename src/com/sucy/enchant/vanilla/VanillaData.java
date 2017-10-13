package com.sucy.enchant.vanilla;

import com.sucy.enchant.api.CustomEnchantment;
import com.sucy.enchant.api.ItemSet;
import org.bukkit.enchantments.Enchantment;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.vanilla.VanillaData
 */
public enum VanillaData {

    // ---- Armor ---- //
    BINDING_CURSE(ItemSet.ALL, 1, 1, 25, 25, 0, 8, false),
    DEPTH_STRIDER(ItemSet.BOOTS, 3, 2, 10, 10, 5, 4, true, "stride"),
    FROST_WALKER(ItemSet.BOOTS, 2, 2, 10, 10, 5, 4, false, "stride"),
    OXYGEN(ItemSet.HELMETS, 3, 2, 10, 10, 20, 4, true),
    PROTECTION_ENVIRONMENTAL(ItemSet.ARMOR, 4, 10, 1, 11, 0, 1, true, "protection"),
    PROTECTION_EXPLOSIONS(ItemSet.ARMOR, 4, 2, 5, 8, 0, 4, true, "protection"),
    PROTECTION_FALL(ItemSet.BOOTS, 4, 5, 5, 6, 0, 2, true, "protection"),
    PROTECTION_FIRE(ItemSet.ARMOR, 4, 5, 10, 8, 0, 2, true, "protection"),
    PROTECTION_PROJECTILE(ItemSet.ARMOR, 4, 5, 3, 6, 0, 2, true, "protection"),
    THORNS(ItemSet.CHESTPLATES, 3, 1, 10, 20, 30, 8, true),
    WATER_WORKER(ItemSet.HELMETS, 1, 2, 1, 99, 40, 4, true),

    // ---- Swords ---- //
    DAMAGE_ALL(ItemSet.WEAPONS, 5, 10, 1, 11, 9, 1, true, "damage"),
    DAMAGE_ARTHROPODS(ItemSet.WEAPONS, 5, 5, 5, 8, 12, 2, true, "damage"),
    DAMAGE_UNDEAD(ItemSet.WEAPONS, 5, 5, 5, 8, 12, 2, true, "damage"),
    FIRE_ASPECT(ItemSet.SWORDS, 2, 2, 10, 20, 30, 4, true),
    KNOCKBACK(ItemSet.SWORDS, 2, 5, 5, 20, 30, 2, true),
    LOOT_BONUS_MOBS(ItemSet.SWORDS, 3, 2, 15, 9, 41, 4, true),
    SWEEPING_EDGE(ItemSet.SWORDS, 3, 1, 5, 9, 6, 4, true),

    // --- Tools --- //
    DIG_SPEED(ItemSet.TOOLS, 5, 10, 1, 10, 40, 1, true),
    LOOT_BONUS_BLOCKS(ItemSet.TOOLS, 3, 2, 15, 9, 41, 4, true, "blocks"),
    SILK_TOUCH(ItemSet.TOOLS, 1, 1, 15, 99, 50, 8, true, "blocks"),

    // ---- Bows ---- //
    ARROW_DAMAGE(ItemSet.BOWS, 5, 10, 1, 10, 5, 1, true),
    ARROW_FIRE(ItemSet.BOWS, 1, 2, 20, 99, 30, 4, true),
    ARROW_INFINITE(ItemSet.BOWS, 1, 1, 20, 99, 30, 8, true, "infinite"),
    ARROW_KNOCKBACK(ItemSet.BOWS, 2, 2, 12, 20, 5, 4, true),

    // ---- Fishing ---- //
    LUCK(ItemSet.FISHING, 3, 2, 15, 9, 41, 4, true),
    LURE(ItemSet.FISHING, 3, 2, 15, 9, 41, 4, true),

    // ---- All ---- //
    DURABILITY(ItemSet.DURABILITY, 3, 5, 5, 8, 42, 2, true),
    MENDING(ItemSet.DURABILITY, 1, 2, 25, 25, 0, 4, false, "infinite"),
    VANISHING_CURSE(ItemSet.ALL, 1, 1, 25, 25, 0, 8, false);

    private VanillaEnchantment enchantment;

    public VanillaEnchantment getEnchantment() {
        return enchantment;
    }

    VanillaData(
            final ItemSet itemSet,
            final int maxLevel,
            final int weight,
            final int minEnchantLevel,
            final int enchantLevelScale,
            final int maxBuffer,
            final int costPerLevel,
            final boolean tableEnabled) {
        this(itemSet, maxLevel, weight, minEnchantLevel, enchantLevelScale, maxBuffer, costPerLevel, tableEnabled, CustomEnchantment.DEFAULT_GROUP);
    }

    VanillaData(
            final ItemSet itemSet,
            final int maxLevel,
            final int weight,
            final int minEnchantLevel,
            final int enchantLevelScale,
            final int maxBuffer,
            final int costPerLevel,
            final boolean tableEnabled,
            final String group) {
        enchantment = new VanillaEnchantment(Enchantment.getByName(name()));
        enchantment.addNaturalItems(itemSet.getItems());
        enchantment.setMaxLevel(maxLevel);
        enchantment.setWeight(weight);
        enchantment.setMinEnchantingLevel(minEnchantLevel);
        enchantment.setEnchantLevelScaleFactor(enchantLevelScale);
        enchantment.setEnchantLevelBuffer(maxBuffer);
        enchantment.setCombineCostPerLevel(costPerLevel);
        enchantment.setTableEnabled(tableEnabled);
        enchantment.setGroup(group);
    }
}
