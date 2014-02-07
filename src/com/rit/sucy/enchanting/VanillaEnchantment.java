package com.rit.sucy.enchanting;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.service.MaterialClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Diemex
 */
public class VanillaEnchantment extends CustomEnchantment
{
    /**
     * The Enchantment which this represents
     */
    Enchantment vanilla;
    /**
     * Allow this enchantment to be more prevalent on certain Material types
     */
    Map<MaterialClass, Integer> enchantability;

    public VanillaEnchantment(Enchantment vanilla, Material[] items, String group, String[] suffixGroup, int weight, double base, double interval, int max, String name) {
        super(name, items, group, weight); //we override the method
        this.base = base;
        this.interval = interval;
        this.max = max;
        this.vanilla = vanilla;

        this.weight = new HashMap<MaterialClass, Integer>();
        this.weight.put(MaterialClass.DEFAULT, weight);

        this.enchantability = new HashMap<MaterialClass, Integer>();

        for (String sGroup : suffixGroup)
            suffixGroups.add(sGroup);
    }

    public Enchantment getVanillaEnchant() {
        return vanilla;
    }

    @Override
    public String name() {
        return vanilla.getName();
    }

    @Override
    public ItemStack addToItem(ItemStack item, int level) {
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
            if (meta.hasStoredEnchant(vanilla)) {
                if (meta.getEnchantLevel(vanilla) < level) {
                    meta.removeStoredEnchant(vanilla);
                    item.setItemMeta(meta);
                }
                else return item;
            }
        }

        item.addUnsafeEnchantment(vanilla, level);
        return item;
    }

    @Override
    public ItemStack removeFromItem(ItemStack item) {
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
            if (meta.hasStoredEnchant(vanilla)) {
                meta.removeStoredEnchant(vanilla);
                item.setItemMeta(meta);
                return item;
            }
        }
        item.removeEnchantment(vanilla);
        return item;
    }
}
