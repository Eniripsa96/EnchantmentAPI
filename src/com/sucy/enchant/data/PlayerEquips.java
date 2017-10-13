package com.sucy.enchant.data;

import com.google.common.collect.ImmutableSet;
import com.sucy.enchant.api.CustomEnchantment;
import com.sucy.enchant.api.Enchantments;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.sucy.enchant.util.Utils.isPresent;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.data.PlayerEquips
 */
public class PlayerEquips {

    private static final ItemData EMPTY = new ItemData();
    private static ItemData[] CLEAR = new ItemData[] { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY };

    private ItemData[] equipped = CLEAR;
    private int[] slots = new int[] { 36, 37, 38, 39, 40 };

    private Map<CustomEnchantment, Integer> enchantments = new HashMap<>();

    public PlayerEquips(final Player player) {
        update(player);
    }

    public Map<CustomEnchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public void clear(final Player player) {
        swap(player, CLEAR);
    }

    public void update(final Player player) {
        final PlayerInventory inv = player.getInventory();
        final ItemData[] updated = new ItemData[equipped.length];
        for (int i = 0; i < updated.length; i++) {
            final ItemStack item = i < slots.length ? inv.getItem(slots[i]) : inv.getItemInMainHand();
            updated[i] = ItemData.from(item);
        }
        swap(player, updated);
    }

    public void clearWeapon(final Player player) {
        final ItemData[] copy = new ItemData[equipped.length];
        System.arraycopy(equipped, 0, copy, 0, equipped.length - 1);
        copy[copy.length - 1] = EMPTY;
        swap(player, copy);
    }

    public void updateWeapon(final PlayerInventory inventory) {
        final ItemData[] copy = new ItemData[equipped.length];
        System.arraycopy(equipped, 0, copy, 0, equipped.length - 1);
        copy[copy.length - 1] = ItemData.from(inventory.getItemInMainHand());
        swap((Player)inventory.getHolder(), copy);
    }

    private void swap(final Player player, final ItemData[] updatedEquips) {
        final Map<CustomEnchantment, Integer> newEnchants = new HashMap<>();
        for (final ItemData updatedEquip : updatedEquips) {
            mergeEnchantments(updatedEquip, newEnchants);
        }

        enchantments.forEach((enchant, level) -> {
            if (!newEnchants.containsKey(enchant)) {
                enchant.applyUnequip(player, level);
            }
        });
        newEnchants.forEach((enchant, level) -> {
            if (!enchantments.containsKey(enchant)) {
                enchant.applyEquip(player, level);
            } else {
                final int previous = enchantments.get(enchant);
                if (previous != level) {
                    enchant.applyUnequip(player, previous);
                    enchant.applyEquip(player, level);
                }
            }
        });

        equipped = updatedEquips;
        enchantments = newEnchants;
    }

    private void mergeEnchantments(final ItemData itemData, final Map<CustomEnchantment, Integer> enchantments) {
        itemData.enchantments.forEach((enchant, level) -> {
            final int previous = enchantments.getOrDefault(enchant, 0);
            final int merged = enchant.canStack() ? previous + level : Math.max(previous, level);
            enchantments.put(enchant, merged);
        });
    }

    private static class ItemData {
        private Map<CustomEnchantment, Integer> enchantments;

        private static ItemData from(final ItemStack item) {
            return !isPresent(item) || item.getType() == Material.ENCHANTED_BOOK ? EMPTY : new ItemData(item);
        }

        private ItemData() {
            enchantments = new HashMap<>();
        }

        private ItemData(final ItemStack item) {
            enchantments = Enchantments.getCustomEnchantments(item);
        }
    }

    public static final Set<Material> ARMOR_TYPES = ImmutableSet.<Material>builder()
            .add(Material.LEATHER_BOOTS)
            .add(Material.LEATHER_CHESTPLATE)
            .add(Material.LEATHER_HELMET)
            .add(Material.LEATHER_LEGGINGS)
            .add(Material.IRON_BOOTS)
            .add(Material.IRON_CHESTPLATE)
            .add(Material.IRON_HELMET)
            .add(Material.IRON_LEGGINGS)
            .add(Material.GOLD_BOOTS)
            .add(Material.GOLD_CHESTPLATE)
            .add(Material.GOLD_HELMET)
            .add(Material.GOLD_LEGGINGS)
            .add(Material.DIAMOND_BOOTS)
            .add(Material.DIAMOND_CHESTPLATE)
            .add(Material.DIAMOND_HELMET)
            .add(Material.DIAMOND_LEGGINGS)
            .add(Material.CHAINMAIL_BOOTS)
            .add(Material.CHAINMAIL_CHESTPLATE)
            .add(Material.CHAINMAIL_HELMET)
            .add(Material.CHAINMAIL_LEGGINGS)
            .build();
}
