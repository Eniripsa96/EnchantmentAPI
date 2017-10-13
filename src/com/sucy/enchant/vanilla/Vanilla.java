package com.sucy.enchant.vanilla;

import com.sucy.enchant.EnchantmentAPI;
import com.sucy.enchant.api.CustomEnchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.vanilla.Vanilla
 */
public class Vanilla {

    public static final Optional<CustomEnchantment> getEnchantment(final String name) {
        final String conversion = ENCHANTS.getOrDefault(name.toLowerCase(), name).replace(" ", "_");
        return Optional.ofNullable(EnchantmentAPI.getEnchantment(conversion));
    }

    private static final Map<String,String> ENCHANTS = new HashMap<String,String>() {{
        put("knockback",             "KNOCKBACK");
        put("looting",               "LOOT_BONUS_MOBS");
        put("sharpness",             "DAMAGE_ALL");
        put("smite",                 "DAMAGE_UNDEAD");
        put("bane of arthropods",    "DAMAGE_ARTHROPODS");
        put("fire aspect",           "FIRE_ASPECT");
        put("infinity",              "ARROW_INFINITE");
        put("flame",                 "ARROW_FIRE");
        put("punch",                 "ARROW_KNOCKBACK");
        put("power",                 "ARROW_DAMAGE");
        put("respiration",           "OXYGEN");
        put("aqua affinity",         "WATER_WORKER");
        put("feather falling",       "PROTECTION_FALL");
        put("thorns",                "THORNS");
        put("protection",            "PROTECTION_ENVIRONMENTAL");
        put("fire protection",       "PROTECTION_FIRE");
        put("blast protection",      "PROTECTION_EXPLOSIONS");
        put("projectile protection", "PROTECTION_PROJECTILE");
        put("efficiency",            "DIG_SPEED");
        put("unbreaking",            "DURABILITY");
        put("fortune",               "LOOT_BONUS_BLOCKS");
        put("silk touch",            "SILK_TOUCH");
    }};
}
