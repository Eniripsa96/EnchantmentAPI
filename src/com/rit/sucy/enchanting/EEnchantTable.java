package com.rit.sucy.enchanting;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.service.MaterialClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Handles selecting enchantments when enchanting items
 */
public class EEnchantTable {

    /**
     * Maximum tries before the enchantment stops adding enchantments
     */
    static final int MAX_TRIES = 10;

    /**
     * Enchants an item
     *
     * @param enchanter    player enchanting the item
     * @param item         item being enchanted
     * @param enchantLevel level of the enchantment
     * @param maxEnchants  maximum enchantments allowed
     * @return             enchantment results
     */
    public static EnchantResult enchant(Player enchanter, ItemStack item, int enchantLevel, int maxEnchants) {
        return enchant(enchanter, item, enchantLevel, maxEnchants, true);
    }

    /**
     * Enchants an item
     *
     * @param enchanter    player enchanting the item
     * @param item         item being enchanted
     * @param enchantLevel level of the enchantment
     * @param maxEnchants  maximum enchantments allowed
     * @param apply        whether or not to apply the enchantments
     * @return             enchantment results
     */
    public static EnchantResult enchant(Player enchanter, ItemStack item, int enchantLevel, int maxEnchants, boolean apply) {

        EnchantResult result = new EnchantResult();
        boolean chooseEnchantment = true;

        //enchants added to the item
        Map<CustomEnchantment, Integer> chosenEnchantWithCost = new HashMap<CustomEnchantment, Integer>();

        //Build a Map where the number of keys for a certain Enchantment corresponds to the weight
        List<CustomEnchantment> validEnchants = enchanter != null ?
                EnchantmentAPI.getAllValidEnchants(item, enchanter)
                : EnchantmentAPI.getAllValidEnchants(item);
        int totalWeight = weightOfAllEnchants(validEnchants);

        int level;
        int max = 0;

        // Keep choosing enchantments as long as needed
        while (chooseEnchantment && validEnchants.size() > 0) {

            chooseEnchantment = false;

            // Modify the enchantment level
            enchantLevel = modifiedLevel(enchantLevel, MaterialClass.getEnchantabilityFor(item.getType()));
            result.setLevel(enchantLevel);

            // Try to add an Enchantment, stop adding enchantments if the enchantment would conflict
            CustomEnchantment enchant;
            int tries = 0;
            max = enchantLevel > max ? enchantLevel : max;
            do {
                enchant = weightedRandom(validEnchants, totalWeight);
                level = enchant.getEnchantLevel(max);
                if (level < 1) {
                    if (result.getFirstEnchant() != null) break;
                    else continue;
                }

                // Add the enchantment to the list
                chosenEnchantWithCost.put(enchant, level);
                totalWeight -= enchant.getWeight();
                validEnchants.remove(enchant);
                result.setFirstEnchant(enchant);
                break;
            } while(tries++ < MAX_TRIES || result.getFirstEnchant() == null);

            // Reduce the chance of getting another one along with the power of the next one
            if (Math.random() < (enchantLevel + 1) / 50.0) chooseEnchantment = true;
            enchantLevel /= 2;

            // Books can only have two enchantments
            if (item.getType() == Material.BOOK && chosenEnchantWithCost.size() == 2) chooseEnchantment = false;

            // Max allowed enchantments
            if (chosenEnchantWithCost.size() == maxEnchants) chooseEnchantment = false;

            // Clear up enchants that no longer can be added
            if (chooseEnchantment) {
                int prevWeight = totalWeight;
                for (int i = 0; i < validEnchants.size(); i++) {
                    if (enchant.conflictsWith(validEnchants.get(i))) {
                        totalWeight -= validEnchants.get(i).getWeight();
                        validEnchants.remove(i--);
                    }
                }
                if (validEnchants.size() == 0)
                    chooseEnchantment = false;
            }
        }

        result.setAddedEnchants(chosenEnchantWithCost);

        // Apply the enchantments
        boolean success = false;
        if (apply) {
            for (Map.Entry<CustomEnchantment, Integer> enchantCostEntry : chosenEnchantWithCost.entrySet()) {
                CustomEnchantment selectedEnchant = enchantCostEntry.getKey();
                int levelCost = enchantCostEntry.getValue();

                if (selectedEnchant == null) {
                    result.setItem(item);
                    return result; //And cancel event
                }

                selectedEnchant.addToItem(item, levelCost);
                success = true;
            }
        }

        if (success)
            result.setItem(item);
        return result;
    }

    /**
     * Calculates a modified experience level
     *
     * @param expLevel       chosen exp level
     * @param enchantability the enchantibility of the item
     * @return               modified exp level
     */
    static int modifiedLevel(int expLevel, int enchantability) {
        expLevel = expLevel + random(enchantability / 4 * 2) + 1;
        double bonus = random(0.3) + 0.85;
        return (int)(expLevel * bonus + 0.5);
    }

    /**
     * Chooses a random integer with triangular distribution
     *
     * @param max maximum value
     * @return    random integer
     */
    static int random(int max) {
        return (int)(Math.random() * max / 2 + Math.random() * max / 2);
    }

    /**
     * Chooses a random double with triangular distribution
     *
     * @param max maximum value
     * @return    random double
     */
    static double random(double max) {
        return Math.random() * max / 2 + Math.random() * max / 2;
    }

    /**
     * Get an Enchantment considering the weight (probability) of each Enchantment
     *
     * @param enchantments  possible valid enchantments
     *
     * @return              One possible CustomEnchantment
     */
    static CustomEnchantment weightedRandom (Collection<CustomEnchantment> enchantments, int totalWeight){

        //select a random value between 0 and our total
        int random = new Random().nextInt(totalWeight);

        Iterator<CustomEnchantment> iter = enchantments.iterator();
        CustomEnchantment enchantment = null;

        //loop through our weightings until we arrive at the correct one
        int current = 0;
        while (iter.hasNext()){
            enchantment = iter.next();
            current += enchantment.getWeight();
            if (random < current)
                return enchantment;
        }

        return enchantment; //or null
    }

    /**
     * Gets the total weight of all custom enchantments applicable to the item
     *
     * @return      total custom enchantment weight
     */
    static int weightOfAllEnchants(Collection<CustomEnchantment> validEnchants) {
        int count = 0;
        for (CustomEnchantment enchantment : validEnchants) {
            count += enchantment.getWeight();
        }
        return count;
    }
}
