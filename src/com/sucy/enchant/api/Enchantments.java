package com.sucy.enchant.api;

import com.sucy.enchant.EnchantmentAPI;
import com.sucy.enchant.data.PlayerEquips;
import com.sucy.enchant.util.LoreReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.sucy.enchant.util.Utils.isPresent;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.api.Enchantments
 */
public class Enchantments {

    private static final Map<UUID, PlayerEquips> EQUIPMENT = new HashMap<>();

    /**
     * @param player player to get enchantments for
     * @return combined enchantments on all active equipment the player has
     */
    public static Map<CustomEnchantment, Integer> getEnchantments(final Player player) {
        return getEquipmentData(player).getEnchantments();
    }

    /**
     * @param player player to get equipment data for
     * @return the equipment data tracking the player's enchantments
     */
    public static PlayerEquips getEquipmentData(final Player player) {
        return EQUIPMENT.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerEquips(player));
    }

    /**
     * @param player player to clear equipment data for (will refresh next access)
     */
    public static void clearEquipmentData(final Player player) {
        EQUIPMENT.remove(player.getUniqueId()).clear(player);
    }

    /**
     * Clears equipment data for all players, forcing all equipment to refresh
     */
    public static void clearAllEquipmentData() {
        EQUIPMENT.forEach((id, data) -> data.clear(Bukkit.getPlayer(id)));
        EQUIPMENT.clear();
    }

    /**
     * @param item item to grab the enchantments from
     * @return     list of custom enchantments (does not include vanilla enchantments)
     */
    public static Map<CustomEnchantment, Integer> getCustomEnchantments(final ItemStack item) {

        final HashMap<CustomEnchantment, Integer> list = new HashMap<CustomEnchantment, Integer>();
        if (!isPresent(item)) return list;

        final ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return list;

        final List<String> lore = meta.getLore();
        for (final String line : lore) {
            final String name = LoreReader.parseEnchantmentName(line);
            if (EnchantmentAPI.isRegistered(name)) {
                final CustomEnchantment enchant = EnchantmentAPI.getEnchantment(name);
                final int level = LoreReader.parseEnchantmentLevel(line);
                if (level > 0) {
                    list.put(enchant, level);
                }
            }

            // Short-circuit if we aren't finding valid formatted enchantments
            // since all enchantments should be added at the top.
            else if (name.isEmpty()) {
                return list;
            }
        }
        return list;
    }

    /**
     * Gets all enchantments on an item, including vanilla enchantments. This wraps
     * vanilla enchantments in the CustomEnchantment class for more visibility on
     * their settings.
     *
     * @param item item to get enchantments for.
     * @return all enchantments on the item and their levels
     */
    public static Map<CustomEnchantment, Integer> getAllEnchantments(final ItemStack item) {
        final Map<CustomEnchantment, Integer> result = getCustomEnchantments(item);
        if (item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();
            if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
                merge(meta.getEnchants(), result);
                if (item.getType() == Material.ENCHANTED_BOOK) {
                    merge(((EnchantmentStorageMeta) meta).getStoredEnchants(), result);
                }
            }
        }
        return result;
    }

    /**
     * Checks whether or not the item has the enchantment on it
     *
     * @param item item to check
     * @param enchantmentName name of the enchantment to check for
     * @return true if it has the enchantment, false otherwise
     */
    public static boolean hasCustomEnchantment(final ItemStack item, final String enchantmentName) {
        if (!item.hasItemMeta()) return false;
        final ItemMeta meta = item.getItemMeta();
        return meta.hasLore() && meta.getLore().stream().anyMatch(line -> LoreReader.isEnchantment(line, enchantmentName));
    }

    /**
     * Removes all enchantments from the item, including vanilla enchantments
     *
     * @param item item to remove all enchantments from
     * @return item with all enchantments removed
     */
    public static ItemStack removeAllEnchantments(final ItemStack item) {
        item.getEnchantments().forEach((enchant, level) -> item.removeEnchantment(enchant));
        return removeCustomEnchantments(item);
    }

    /**
     * Removes all custom enchantments from an item
     *
     * @param item item to remove enchantments from
     * @return item without custom enchantments
     */
    public static ItemStack removeCustomEnchantments(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta.hasLore()) {
            meta.setLore(meta.getLore().stream()
                    .filter(line -> LoreReader.isEnchantment(line, LoreReader.parseEnchantmentName(line)))
                    .collect(Collectors.toList()));
        }
        return item;
    }

    private static void merge(final Map<Enchantment, Integer> source, final Map<CustomEnchantment, Integer> result) {
        source.forEach((enchant, level) -> result.put(EnchantmentAPI.getEnchantment(enchant.getName()), level));
    }
}
