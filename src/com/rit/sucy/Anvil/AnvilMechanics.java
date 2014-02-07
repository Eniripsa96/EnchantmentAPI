package com.rit.sucy.Anvil;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Custom implementation of anvil mechanics to accommodate for custom enchantments
 */
public class AnvilMechanics {

    /**
     * Matching item prefixes to material types used for repairing
     */
    private static final HashMap<String, Material> REPAIR_MATS = new HashMap<String, Material>() {{
        put("WOOD_", Material.WOOD);
        put("STONE_", Material.COBBLESTONE);
        put("CHAINMAIL_", Material.IRON_INGOT);
        put("IRON_", Material.IRON_INGOT);
        put("GOLD_", Material.GOLD_INGOT);
        put("DIAMOND_", Material.DIAMOND);
    }};

    public static void updateResult(AnvilView view, ItemStack[] input, String name) {
        if (input.length < 2) {
            view.setResultSlot(null);
            return;
        }

        ItemStack first = input[0];
        ItemStack second = input[1];

        // Result if one is missing and the text differs
        if (isFilled(first) && !isFilled(second) && name != null) {
            ItemStack result = first.clone();
            ItemMeta meta = result.getItemMeta() == null ? Bukkit.getItemFactory().getItemMeta(result.getType()) : result.getItemMeta();
            meta.setDisplayName(name);
            result.setItemMeta(meta);
            view.setResultSlot(result);
            int cost = getBaseCost(first, false) + (first.getType().getMaxDurability() != 0 ? 7 : 5 * first.getAmount());
            view.setRepairCost(cost > 39 ? 39 : cost);
        }
        else if (!isFilled(first) && isFilled(second) && name != null) {
            ItemStack result = second.clone();
            ItemMeta meta = result.getItemMeta() == null ? Bukkit.getItemFactory().getItemMeta(result.getType()) : result.getItemMeta();
            meta.setDisplayName(name);
            result.setItemMeta(meta);
            view.setResultSlot(result);
            int cost = getBaseCost(first, false) + (first.getType().getMaxDurability() != 0 ? 7 : 5 * first.getAmount());
            view.setRepairCost(cost > 39 ? 39 : cost);
        }

        // No result if one is missing
        else if (!isFilled(first) || !isFilled(second))
            view.setResultSlot(null);

        // If they are different items, only special cases work
        else if (first.getType() != second.getType()) {

            // Books adding to other items
            if (isBook(first))
                validateBook(view, first, second);
            else if (isBook(second)) {
                validateBook(view, second, first);
            }

            // Materials repairing tools
            else {
                for (Map.Entry<String, Material> entry : REPAIR_MATS.entrySet()) {
                    if (first.getType().name().contains(entry.getKey())
                            && second.getType() == entry.getValue()) {
                        int cost = getMaterialRepairCost(first, second) + getBaseCost(first, false);
                        view.setResultSlot(makeItem(first, second.getAmount()));
                        view.setRepairCost(cost);
                    }
                    else if (second.getType().name().contains(entry.getKey())
                            && first.getType() == entry.getValue()) {
                        int cost = getMaterialRepairCost(second, first) + getBaseCost(second, false);
                        view.setResultSlot(makeItem(second, first.getAmount()));
                        view.setRepairCost(cost);
                    }
                }
            }
        }

        // If there's more than one item, don't allow it to be used
        else if (first.getAmount() > 1)
            view.setResultSlot(null);
        else if (second.getAmount() > 1)
            view.setResultSlot(null);

            // If they both have at least one enchantment, make the output combine them
        else if (EnchantmentAPI.getAllEnchantments(first).size() * EnchantmentAPI.getAllEnchantments(second).size() > 0) {
            view.setResultSlot(makeItem(first, second));
            view.setRepairCost(getCombineCost(first, second));
        }

        // If they are missing durability, make the output fix them
        else if (first.getDurability() > 0) {
            view.setResultSlot(makeItem(first, second));
            view.setRepairCost(getCombineCost(first, second));
        }

        // No result needed
        else view.setResultSlot(null);
    }

    /**
     * Updates the result slot of the anvil when the inputs are changed
     *
     * @param view  anvil inventory
     * @param input input items
     */
    public static void updateResult(AnvilView view, ItemStack[] input) {
        updateResult(view, input, null);
    }

    /**
     * Gets the base cost of an item
     *
     * @param item     item
     * @param withBook if using a book with the item
     * @return         base cost
     */
    private static int getBaseCost(ItemStack item, boolean withBook) {

        int cost = 0;
        int count = 0;

        // Add up the costs of each enchantment (costPerLevel * enchantLevel)
        for (Map.Entry<CustomEnchantment, Integer> entry : EnchantmentAPI.getAllEnchantments(item).entrySet()) {
            cost += entry.getKey().getCostPerLevel(withBook) * entry.getValue();
            count++;
        }

        // Add in the number of enchants cost and return the result
        return cost + (int)((count + 1) * (count / 2.0) + 0.5);
    }

    /**
     * Gets the cost of repairing an item with materials
     *
     * @param item     repaired item
     * @param material material used
     * @return         repair cost
     */
    private static int getMaterialRepairCost(ItemStack item, ItemStack material) {

        int cost;

        // Diamonds cost 3 times as much as other materials
        int m = material.getType() == Material.DIAMOND ? 3 : 1;

        // Calculate the cost depending on how damaged the item is
        cost = 4 * m * item.getDurability() / item.getType().getMaxDurability() + 1;
        if (cost > m * material.getAmount())
            cost = m * material.getAmount();

        return cost;
    }

    /**
     * Gets the cost of combining to items
     *
     * @param first  primary item
     * @param second secondary item
     * @return       combine cost
     */
    private static int getCombineCost(ItemStack first, ItemStack second) {

        // Books halve the cost of several factors
        boolean book = isBook(second);
        double m = book ? 0.5 : 1;

        // The 'extra' costs include the base cost of the primary item
        int cost = 0;
        int extra = getBaseCost(first, book);

        // Combining repair cost
        if (first.getDurability() > 0 && first.getType() == second.getType()) {
            int extraCost = (int)((durability(second) + 0.12 * first.getType().getMaxDurability()) / 100);
            cost += extraCost > 0 ? extraCost : 1;
        }

        // Find costs for any enchantments that would merge
        Set<Map.Entry<CustomEnchantment, Integer>> enchants = EnchantmentAPI.getAllEnchantments(first).entrySet();
        int newEnchants = 0;
        for (Map.Entry<CustomEnchantment, Integer> entry : EnchantmentAPI.getAllEnchantments(second).entrySet()) {
            boolean conflict = false;
            boolean needsCost = false;
            if (entry.getKey().canEnchantOnto(first)) {
                for (Map.Entry<CustomEnchantment, Integer> e : enchants) {
                    if (e.getKey().name().equals(entry.getKey().name())) {

                        // If the enchantment is being raised, add cost for the gained levels
                        if (e.getValue() < entry.getValue()) {
                            cost += (entry.getValue() - e.getValue()) * entry.getKey().getCostPerLevel(book);
                            extra += (entry.getValue() - e.getValue()) * entry.getKey().getCostPerLevel(book);
                        }
                        else if (e.getValue().equals(entry.getValue())) {
                            cost += entry.getKey().getCostPerLevel(false);
                            extra += entry.getKey().getCostPerLevel(false);
                        }
                        else needsCost = true;
                        conflict = true;
                    }
                    else if (e.getKey().conflictsWith(entry.getKey())) {
                        conflict = true;
                        needsCost = true;
                    }
                }
            }
            else conflict = needsCost = true;

            // Add the full cost of the enchantment if it would be added
            if (!conflict) {
                newEnchants++;
                cost += entry.getValue() * entry.getKey().getCostPerLevel(book);
                extra += entry.getValue() * entry.getKey().getCostPerLevel(book);
            }

            // If the enchantment was not compatible, add cost equal to its level
            if (needsCost) cost += entry.getValue();
        }

        // Add additional cost for the enchantments added
        if (newEnchants > 0)
            extra += newEnchants * (enchants.size() + newEnchants - 1) + 1;

        // Return the total cost, halving the extra cost if a book was used
        return cost + (int)(m * extra);
    }

    /**
     * Makes sure the book in the anvil can benefit the target item
     *
     * @param inv    anvil inventory
     * @param book   book component
     * @param target target item
     */
    static void validateBook(AnvilView inv, ItemStack book, ItemStack target) {
        Map<CustomEnchantment, Integer> enchants = EnchantmentAPI.getAllEnchantments(book);

        // Book must have at least one enchantment and they must be able to go on the target item
        boolean valid = false;
        if (target.getType() != Material.BOOK && target.getType() != Material.ENCHANTED_BOOK) {
            for (CustomEnchantment e : enchants.keySet())
                if (e.canEnchantOnto(target))
                    valid = true;
        }

        // If the book passed the test, create a new result
        if (valid && (enchants.size() > 0 && book.getAmount() == 1 && target.getAmount() == 1)) {
            inv.setResultSlot(makeItem(target, book));
            inv.setRepairCost(getCombineCost(target, book));
        }

            // Otherwise leave no result
        else inv.setResultSlot(null);
    }

    /**
     * Creates a result item from a component and a material
     *
     * @param item   item to repair
     * @param amount amount of materials
     * @return       result item
     */
    static ItemStack makeItem(ItemStack item, int amount) {
        ItemStack newItem = item.clone();

        // Repair the item
        if (item.getDurability() - item.getType().getMaxDurability() * 0.25 * amount < 0)
            newItem.setDurability((short)0);
        else newItem.setDurability((short)(item.getDurability() - item.getType().getMaxDurability() * amount * 0.25));

        // Return the item
        return newItem;
    }

    /**
     * Makes a result item from two components
     *
     * @param primary   target item
     * @param secondary supplement item
     * @return          resulting item
     */
    static ItemStack makeItem(ItemStack primary, ItemStack secondary) {

        // Take the type of the primary item
        ItemStack item = new ItemStack(primary.getType());
        if (primary.hasItemMeta()) item.setItemMeta(primary.getItemMeta());

        // Set the durability if applicable and add the corresponding cost
        if (primary.getDurability() > 0 && primary.getType() == secondary.getType()) {
            if (durability(item) + durability(secondary) < 0.88 * primary.getType().getMaxDurability())
                setDurability(item, (short)(durability(primary) + durability(secondary) + 0.12 * primary.getType().getMaxDurability()));
        }
        else item.setDurability(primary.getDurability());

        // Merge the enchantments
        Set<Map.Entry<CustomEnchantment, Integer>> enchants = EnchantmentAPI.getAllEnchantments(primary).entrySet();
        for (Map.Entry<CustomEnchantment, Integer> entry : EnchantmentAPI.getAllEnchantments(secondary).entrySet()) {
            boolean conflict = false;
            if (entry.getKey().canEnchantOnto(item)) {
                for (Map.Entry<CustomEnchantment, Integer> e : enchants) {

                    // If they share the same enchantment, use the higher one
                    // If the levels are the same, raise it by one if it can go that high
                    if (e.getKey().name().equals(entry.getKey().name())) {
                        e.getKey().removeFromItem(item);

                        // Get the new level
                        int level = e.getValue();
                        if (entry.getValue() > level) level = entry.getValue();
                        else if (entry.getValue() == level) level = Math.min(level + 1, e.getKey().getMaxLevel());

                        // Add the enchantment
                        e.getKey().addToItem(item, level);
                        conflict = true;
                    }

                    // If the enchantment can't be added onto the target, don't add it
                    else if (e.getKey().conflictsWith(entry.getKey())) {
                        conflict = true;
                    }
                }
            }
            else conflict = true;

            // Add the enchant if there were no problems
            if (!conflict)
                entry.getKey().addToItem(item, entry.getValue());
        }

        // Return the item
        return item;
    }

    /**
     * Checks if the item is not an empty slot
     *
     * @param item item to check
     * @return     true if not an empty slot, false otherwise
     */
    static boolean isFilled(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    /**
     * Checks if an item is a book
     *
     * @param item item to check
     * @return     true if book, false otherwise
     */
    static boolean isBook(ItemStack item) {
        return item.getType() == Material.BOOK || item.getType() == Material.ENCHANTED_BOOK;
    }

    /**
     * Gets the durability of an item
     *
     * @param item item
     * @return     durability
     */
    static short durability(ItemStack item) {
        return (short)(item.getType().getMaxDurability() - item.getDurability());
    }

    /**
     * Sets the durability of an item
     *
     * @param item  item to set the durability of
     * @param value durability remaining
     * @return      the item
     */
    static ItemStack setDurability(ItemStack item, short value) {
        item.setDurability((short)(item.getType().getMaxDurability() - value));
        return item;
    }
}
