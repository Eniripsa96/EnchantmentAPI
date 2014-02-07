package com.rit.sucy.enchanting;

import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.service.ENameParser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Hashtable;

/**
 * Handles keeping track of player equipment for Equip and Unequip enchantment effects
 */
public class EEquip extends BukkitRunnable {

    /**
     * Table of player data
     */
    static Hashtable<String, ItemStack[]> equipment = new Hashtable<String, ItemStack[]>();

    /**
     * Loads the equipment of the given player
     *
     * @param player player to load
     */
    public static void loadPlayer(Player player) {
        equipment.put(player.getName(), player.getEquipment().getArmorContents());
    }

    /**
     * Clears teh data for the given player
     *
     * @param player player to clear
     */
    public static void clearPlayer(Player player) {
        equipment.remove(player.getName());
    }

    /**
     * Clears all player data
     */
    public static void clear() {
        equipment.clear();
    }

    /**
     * Player reference
     */
    Player player;

    /**
     * Constructor
     *
     * @param player player to re-evaluate
     */
    public EEquip(Player player) {
        this.player = player;
    }

    /**
     * Performs checks for changes to player equipment
     */
    public void run() {
        ItemStack[] equips = player.getEquipment().getArmorContents();
        ItemStack[] previous = equipment.get(player.getName());
        try{
            for (int i = 0; i < equips.length; i++) {
                if (equips[i] == null && (previous != null && previous[i] != null))
                    doUnequip(previous[i]);
                else if (equips[i] != null && (previous == null || previous[i] == null))
                    doEquip(equips[i]);
                else if (previous == null)
                    /* do nothing */ ;
                else if (!equips[i].toString().equalsIgnoreCase(previous[i].toString())) {
                    doEquip(equips[i]);
                    doUnequip(previous[i]);
                }
            }
        }
        catch(Exception e) {
            // Weird error
        }
        equipment.put(player.getName(), equips);
    }

    /**
     * Applies equip actions to the given item
     *
     * @param item the equipment that was just equipped
     */
    private void doEquip(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasLore()) return;
        for (String lore : meta.getLore()) {
            String name = ENameParser.parseName(lore);
            int level = ENameParser.parseLevel(lore);
            if (name == null) continue;
            if (level == 0) continue;
            if (EnchantmentAPI.isRegistered(name)) {
                EnchantmentAPI.getEnchantment(name).applyEquipEffect(player, level);
            }
        }
    }

    /**
     * Applies equip actions to the given item
     *
     * @param item the equipment that was just equipped
     */
    private void doUnequip(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasLore()) return;
        for (String lore : meta.getLore()) {
            String name = ENameParser.parseName(lore);
            int level = ENameParser.parseLevel(lore);
            if (name == null) continue;
            if (level == 0) continue;
            if (EnchantmentAPI.isRegistered(name)) {
                EnchantmentAPI.getEnchantment(name).applyUnequipEffect(player, level);
            }
        }
    }
}
