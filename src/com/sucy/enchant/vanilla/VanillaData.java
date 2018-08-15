package com.sucy.enchant.vanilla;

import com.sucy.enchant.api.ItemSet;
import org.bukkit.enchantments.Enchantment;

import static com.sucy.enchant.api.CustomEnchantment.DEFAULT_GROUP;
import static com.sucy.enchant.api.ItemSet.ALL;
import static com.sucy.enchant.api.ItemSet.ARMOR;
import static com.sucy.enchant.api.ItemSet.AXES;
import static com.sucy.enchant.api.ItemSet.BOOTS;
import static com.sucy.enchant.api.ItemSet.BOWS;
import static com.sucy.enchant.api.ItemSet.CHESTPLATES;
import static com.sucy.enchant.api.ItemSet.DURABILITY_ALL;
import static com.sucy.enchant.api.ItemSet.DURABILITY_SECONDARY;
import static com.sucy.enchant.api.ItemSet.FISHING;
import static com.sucy.enchant.api.ItemSet.HELMETS;
import static com.sucy.enchant.api.ItemSet.NONE;
import static com.sucy.enchant.api.ItemSet.SHEARS;
import static com.sucy.enchant.api.ItemSet.SWORDS;
import static com.sucy.enchant.api.ItemSet.TOOLS;
import static com.sucy.enchant.api.ItemSet.TRIDENT;
import static com.sucy.enchant.api.ItemSet.WEAPONS;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.vanilla.VanillaData
 */
public enum VanillaData {

    // ---- Armor ---- //
    BINDING_CURSE(ALL, 1, 1, 25, 0, 50, 8, false),
    DEPTH_STRIDER(BOOTS, NONE, 3, 2, 10, 10, 5, 4, true, "stride"),
    FROST_WALKER(BOOTS, NONE, 2, 2, 10, 10, 5, 4, false, "stride"),
    OXYGEN(HELMETS, 3, 2, 10, 10, 20, 4, true),
    PROTECTION_ENVIRONMENTAL(ARMOR, NONE, 4, 10, 1, 11, 0, 1, true, "protection"),
    PROTECTION_EXPLOSIONS(ARMOR, NONE, 4, 2, 5, 8, 0, 4, true, "protection"),
    PROTECTION_FALL(BOOTS, NONE, 4, 5, 5, 6, 0, 2, true, "protection"),
    PROTECTION_FIRE(ARMOR, NONE, 4, 5, 10, 8, 0, 2, true, "protection"),
    PROTECTION_PROJECTILE(ARMOR, NONE, 4, 5, 3, 6, 0, 2, true, "protection"),
    THORNS(CHESTPLATES, ARMOR, 3, 1, -10, 20, 30, 8, true, DEFAULT_GROUP),
    WATER_WORKER(HELMETS, 1, 2, 1, 0, 40, 4, true),

    // ---- Swords ---- //
    DAMAGE_ALL(WEAPONS, AXES, 5, 10, 1, 11, 9, 1, true, "damage"),
    DAMAGE_ARTHROPODS(WEAPONS, AXES, 5, 5, 5, 8, 12, 2, true, "damage"),
    DAMAGE_UNDEAD(WEAPONS, AXES, 5, 5, 5, 8, 12, 2, true, "damage"),
    FIRE_ASPECT(SWORDS, 2, 2, 10, 20, 30, 4, true),
    KNOCKBACK(SWORDS, 2, 5, 5, 20, 30, 2, true),
    LOOT_BONUS_MOBS(SWORDS, 3, 2, 15, 9, 41, 4, true),
    SWEEPING_EDGE(SWORDS, 3, 1, 5, 9, 6, 4, true),

    // --- Tools --- //
    DIG_SPEED(TOOLS, SHEARS, 5, 10, 1, 10, 40, 1, true, DEFAULT_GROUP),
    LOOT_BONUS_BLOCKS(TOOLS, NONE, 3, 2, 15, 9, 41, 4, true, "blocks"),
    SILK_TOUCH(TOOLS, NONE, 1, 1, 15, 0, 50, 8, true, "blocks"),

    // ---- Bows ---- //
    ARROW_DAMAGE(BOWS, 5, 10, 1, 10, 5, 1, true),
    ARROW_FIRE(BOWS, 1, 2, 20, 0, 50, 4, true),
    ARROW_INFINITE(BOWS, NONE, 1, 1, 20, 0, 50, 8, true, "infinite"),
    ARROW_KNOCKBACK(BOWS, 2, 2, 12, 20, 5, 4, true),

    // ---- Fishing ---- //
    LUCK(FISHING, 3, 2, 15, 9, 41, 4, true),
    LURE(FISHING, 3, 2, 15, 9, 41, 4, true),

    // ---- Trident ---- //
    LOYALTY(TRIDENT, 3, 5, 5, 7, 43, 2, true),
    IMPALING(TRIDENT, 5, 2, 1, 8, 12, 4, true),
    RIPTIDE(TRIDENT, 3, 2, 10, 7, 43, 4, true),
    CHANNELING(TRIDENT, 1, 1, 25, 0, 50, 8, true),

    // ---- All ---- //
    DURABILITY(ItemSet.DURABILITY, DURABILITY_SECONDARY, 3, 5, 5, 8, 42, 2, true, DEFAULT_GROUP),
    MENDING(DURABILITY_ALL, NONE, 1, 2, 0, 25, 25, 4, false, "infinite"),
    VANISHING_CURSE(ALL, 1, 1, 25, 0, 50, 8, false);

    private VanillaEnchantment enchantment;

    public VanillaEnchantment getEnchantment() {
        return enchantment;
    }

    public boolean doesExist() {
        return enchantment != null;
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
        this(itemSet, NONE, maxLevel, weight, minEnchantLevel, enchantLevelScale, maxBuffer, costPerLevel, tableEnabled, DEFAULT_GROUP);
    }

    VanillaData(
            final ItemSet itemSet,
            final ItemSet secondary,
            final int maxLevel,
            final int weight,
            final int minEnchantLevel,
            final int enchantLevelScale,
            final int maxBuffer,
            final int costPerLevel,
            final boolean tableEnabled,
            final String group) {

        final Enchantment enchant = Enchantment.getByName(name());
        if (enchant == null) return;

        enchantment = new VanillaEnchantment(enchant);
        enchantment.addNaturalItems(itemSet.getItems());
        enchantment.addAnvilItems(secondary.getItems());
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
