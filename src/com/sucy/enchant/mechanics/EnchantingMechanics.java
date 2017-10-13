package com.sucy.enchant.mechanics;

import com.sucy.enchant.EnchantmentAPI;
import com.sucy.enchant.api.CustomEnchantment;
import com.sucy.enchant.data.ConfigKey;
import com.sucy.enchant.data.Configuration;
import com.sucy.enchant.data.Enchantability;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.mechanics.EnchantingMechanics
 */
public class EnchantingMechanics {

    private final int maxEnchants = Configuration.amount(ConfigKey.MAX_ENCHANTMENTS);

    /**
     * Generates an enchantment result for the given item
     *
     * @param enchanter player enchanting the item (use null to bypass permissions)
     * @param item item to enchant
     * @param enchantLevel enchantment level (1-30 from the table)
     * @param table whether or not it is a table enchantment. If not a table enchantment maxLevel is used over maxTableLevel
     * @param seed enchanter's random seed for enchanting
     * @return enchanting result
     */
    public EnchantResult generateEnchantments(
            final Player enchanter, final ItemStack item, final int enchantLevel, final boolean table, final long seed) {

        final Random random = new Random(seed);

        final int modifier = getModifier(Enchantability.determine(item.getType()), random);
        final int adjustedLevel = modifyLevel(enchantLevel, modifier, random);
        final EnchantResult result = new EnchantResult(adjustedLevel, modifier);

        List<CustomEnchantment> available = getAllValidEnchants(item, enchanter, adjustedLevel, table);
        if (available.isEmpty()) {
            return result;
        }

        CustomEnchantment toAdd = weightedRandom(available, item.getType(), random);
        result.addEnchantment(toAdd, toAdd.computeLevel(adjustedLevel));
        int repeatFactor = adjustedLevel;
        while (random.nextInt(50) <= repeatFactor && result.getEnchantments().size() < maxEnchants) {
            final CustomEnchantment previous = toAdd;
            available = available.stream()
                    .filter(enchant -> !previous.conflictsWith(enchant, true))
                    .collect(Collectors.toList());
            if (available.isEmpty()) {
                break;
            }

            toAdd = weightedRandom(available, item.getType(), random);
            result.addEnchantment(toAdd, toAdd.computeLevel(adjustedLevel));
            repeatFactor >>= 1;
        }

        return result;
    }

    private CustomEnchantment weightedRandom(
            final Collection<CustomEnchantment> enchantments, final Material material, final Random random) {

        final double totalWeight = enchantments.stream()
                .mapToDouble(enchant -> enchant.getWeight(material))
                .sum();

        double roll = random.nextDouble() * totalWeight;
        for (final CustomEnchantment enchantment : enchantments) {
            roll -= enchantment.getWeight(material);
            if (roll < 0) {
                return enchantment;
            }
        }
        throw new IllegalStateException("Should not call weighted random without any entries");
    }

    private int getModifier(final int enchantability, final Random random) {
        final int modifiedEnchantability = enchantability / 4 + 1;
        return random.nextInt(modifiedEnchantability) + random.nextInt(modifiedEnchantability);
    }

    private int modifyLevel(final int level, final int modifier, final Random random) {
        final int modified = level + 1 + modifier;
        final float percentage = 0.85f + (random.nextFloat() + random.nextFloat()) * 0.15f;
        return (int)Math.max(1, modified * percentage + 0.5f);
    }

    /**
     * Get all enchantments which can be applied to the given ItemStack
     *
     * @param item  item to check for applicable enchantments
     * @param level modified enchantment level
     * @return      List of all applicable enchantments
     */
    public List<CustomEnchantment> getAllValidEnchants(final ItemStack item, final Permissible enchanter, final int level, final boolean table) {
        return EnchantmentAPI.getRegisteredEnchantments().stream()
                .filter(CustomEnchantment::isEnabled)
                .filter(enchant -> !table || enchant.isTableEnabled())
                .filter(enchant -> item.getType() == Material.BOOK || enchant.canEnchantOnto(item))
                .filter(enchant -> enchant.hasPermission(enchanter))
                .filter(enchant -> enchant.computeLevel(level) >= 1)
                .collect(Collectors.toList());
    }

    public boolean hasValidEnchantments(final ItemStack item) {
        return EnchantmentAPI.getRegisteredEnchantments().stream()
                .anyMatch(enchant -> enchant.canEnchantOnto(item));
    }

    // --- Computing statistics --- //

    public int maxLevel(final int level, final int enchantability) {
        return (int)Math.max(1, (level + 1 + enchantability / 2) * 1.15f + 0.5f);
    }

    public Map<Integer, Integer> enchantabilitySpread(final int enchantability) {
        final int val = enchantability / 4 + 1;
        final Map<Integer, Integer> result = new HashMap<>(val * 2 - 1);
        for (int i = 0; i < val; i++) {
            result.put(i, i + 1);
            result.put(val * 2 - 2 - i, i + 1);
        }
        return result;
    }

    public int enchantabilityWeight(final int enchantability) {
        final int val = enchantability / 4 + 1;
        return val * val;
    }

    public double chance(final int level, final CustomEnchantment enchant, final int target) {
        final int min = (int)Math.ceil(enchant.getMinEnchantingLevel() + enchant.getEnchantLevelScaleFactor() * (target - 1));
        final int max = (int)Math.ceil(enchant.getMinEnchantingLevel() + enchant.getEnchantLevelScaleFactor() * target
                + (target == enchant.getMaxTableLevel() ? enchant.getEnchantLevelBuffer() : 0));

        if (min == max) {
            return 0;
        }

        final double minRand = Math.max(0, Math.min(1, 0.5 + (min - level) / 0.3));
        final double maxRand = Math.max(0, Math.min(1, 0.5 + (max - level) / 0.3));

        return triangularChance(maxRand) - triangularChance(minRand);
    }

    // Compute chance using symmetric triangular distribution formula
    // https://en.wikipedia.org/wiki/Triangular_distribution#Symmetric_triangular_distribution
    // 2x^2 (0 < x < 0.5), 2x^2 - (2x - 1)^2 (0.5 < x < 1)
    private double triangularChance(final double rand) {
        if (rand <= 0.5) return 2 * (rand * rand);
        final double sub = 2 * rand - 1;
        return 2 * (rand * rand) - sub * sub;
    }
}
