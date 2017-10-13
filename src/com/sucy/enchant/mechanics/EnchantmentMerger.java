package com.sucy.enchant.mechanics;

import com.sucy.enchant.api.CustomEnchantment;
import com.sucy.enchant.api.Enchantments;
import com.sucy.enchant.api.GlowEffects;
import com.sucy.enchant.data.ConfigKey;
import com.sucy.enchant.data.Configuration;
import com.sucy.enchant.vanilla.VanillaEnchantment;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.mechanics.EnchantmentMerger
 */
public class EnchantmentMerger {

    private final Map<CustomEnchantment, Integer> enchants;

    private int customCost;
    private int vanillaCost;
    private int repairCost = 0;

    private final int maxEnchants = Configuration.amount(ConfigKey.MAX_MERGED_ENCHANTMENTS);
    private final int maxLevel = Configuration.amount(ConfigKey.GLOBAL_ANVIL_LEVEL);

    public EnchantmentMerger() {
        enchants = new HashMap<>();
    }

    public int getCost() {
        return repairCost + customCost + vanillaCost;
    }

    public int getVanillaCost() {
        return vanillaCost;
    }

    public int getCustomCost() {
        return customCost;
    }

    public EnchantmentMerger merge(final ItemStack item, final boolean addCost) {
        if (item.getDurability() < item.getType().getMaxDurability()) {
            repairCost = 2;
        }

        Enchantments.getAllEnchantments(item).forEach(
                (enchant, level) -> this.merge(enchant, level, addCost, item.getType() == Material.ENCHANTED_BOOK));
        return this;
    }

    public void merge(final CustomEnchantment enchantment, final int level, final boolean addCost, final boolean book) {

        // Ignore conflicting enchantments
        if (enchants.keySet().stream().anyMatch(enchant -> enchant.conflictsWith(enchantment, false))
                || (enchants.containsKey(enchantment) && enchants.size() == maxEnchants)) {
            if (addCost) {
                vanillaCost++;
            }
            return;
        }

        final int primaryLevel = enchants.getOrDefault(enchantment, 0);
        final int combinedLevel = primaryLevel == level
                ? Math.min(Math.max(maxLevel, enchantment.getMaxLevel()), level + 1)
                : Math.max(primaryLevel, level);
        enchants.put(enchantment, combinedLevel);

        if (addCost) {
            final int costPerLevel = Math.max(1, enchantment.getCombineCostPerLevel() >> (book ? 1 : 0));
            if (enchantment instanceof VanillaEnchantment) {
                final int vanillaMax = ((VanillaEnchantment) enchantment).getEnchantment().getMaxLevel();
                vanillaCost += Math.min(vanillaMax, combinedLevel) * costPerLevel;
                customCost += Math.max(0, combinedLevel - vanillaMax) * costPerLevel;
            }
            else {
                customCost += combinedLevel * costPerLevel;
            }
        }
    }

    public ItemStack apply(final ItemStack item) {
        final ItemStack result = Enchantments.removeAllEnchantments(item);
        enchants.entrySet().stream()
                .filter(e -> e.getKey().canEnchantOnto(item))
                .forEach(e -> e.getKey().addToItem(result, e.getValue()));
        return GlowEffects.finalize(result);
    }
}
