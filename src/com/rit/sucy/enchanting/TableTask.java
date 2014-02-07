package com.rit.sucy.enchanting;

import com.rit.sucy.EUpdateTask;
import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.config.LanguageNode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TableTask extends BukkitRunnable {

    /**
     * Placeholder for enchantable items
     */
    final ItemStack p = new ItemStack(Material.BOOK);

    /**
     * Placeholder for unenchantable items
     */
    final ItemStack p2 = new ItemStack(Material.BOOK);

    /**
     * Lore for unenchantable items
     */
    final String cantEnchant;

    Player player;
    Plugin plugin;
    ItemStack placeholder;
    ItemStack stored;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param player player reference
     */
    public TableTask(Plugin plugin, Player player) {
        this.player = player;
        this.plugin = plugin;

        List<String> enchantable = plugin.getConfig().getStringList(LanguageNode.TABLE_ENCHANTABLE.getFullPath());
        List<String> unenchantable = plugin.getConfig().getStringList(LanguageNode.TABLE_UNENCHANTABLE.getFullPath());
        cantEnchant = unenchantable.get(1).replace('&', ChatColor.COLOR_CHAR);

        ItemMeta meta = p.getItemMeta();
        meta.setDisplayName(enchantable.get(0).replace('&', ChatColor.COLOR_CHAR));
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(enchantable.get(1).replace('&', ChatColor.COLOR_CHAR));
        meta.setLore(lore);
        p.setItemMeta(meta);
        lore.clear();
        lore.add(cantEnchant);
        meta.setLore(lore);
        meta.setDisplayName(unenchantable.get(0).replace('&', ChatColor.COLOR_CHAR));
        p2.setItemMeta(meta);

        runTaskTimer(plugin, 0, 1);
    }

    /**
     * Restores the item from the placeholder
     */
    public void restore() {
        if (placeholder != null) {
            placeholder.setType(stored.getType());
            placeholder.setItemMeta(stored.getItemMeta());
            placeholder.setAmount(stored.getAmount());
            placeholder = null;
        }
    }

    /**
     * Runs the task, updating the enchantment table
     */
    public void run() {
        InventoryView view = player.getOpenInventory();
        if (view == null) {
            cancel();
            return;
        }

        EnchantingInventory inv = (EnchantingInventory)view.getTopInventory();
        if (placeholder != null) {
            if (inv.getItem() == null || !inv.getItem().hasItemMeta() || !inv.getItem().getItemMeta().hasDisplayName()
                    || !inv.getItem().getItemMeta().getDisplayName().equals(placeholder.getItemMeta().getDisplayName())) {
                restore();
                new EUpdateTask(plugin, player);
            }
        }
        if (inv.getItem() != null && inv.getItem().getType() != Material.AIR && placeholder == null) {
            stored = inv.getItem().clone();
            placeholder = createPlaceholder(inv.getItem(), stored);
            inv.setItem(placeholder);
            placeholder = inv.getItem();
        }
    }

    /**
     * Creates a placeholder for the privded item
     *
     * @param item       item to modify
     * @param storedItem original item
     */
    private ItemStack createPlaceholder(ItemStack item, ItemStack storedItem) {
        if (canEnchant(item)) {
            item.setType(p.getType());
            item.setItemMeta(p.getItemMeta());
        }
        else {
            item.setType(p2.getType());
            item.setItemMeta(p2.getItemMeta());
        }
        item.setAmount(1);
        List<String> lore = item.getItemMeta().getLore();
        if (storedItem.hasItemMeta() && storedItem.getItemMeta().hasDisplayName())
            lore.add(storedItem.getItemMeta().getDisplayName());
        else
            lore.add(ChatColor.GRAY + storedItem.getType().name().toLowerCase().replace("_", " "));
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Checks if an item can be enchanted
     *
     * @param item item to check
     * @return     true if can enchant, false otherwise
     */
    private boolean canEnchant(ItemStack item) {
        if (EnchantmentAPI.getAllValidEnchants(item, player).size() == 0) return false;
        else if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) return false;
        else if (EnchantmentAPI.getEnchantments(item).size() > 0) return false;
        return true;
    }
}
