package com.rit.sucy.commands;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.config.RootConfig;
import com.rit.sucy.config.RootNode;
import com.rit.sucy.enchanting.EEnchantTable;
import com.rit.sucy.enchanting.VanillaEnchantment;
import com.rit.sucy.service.ENameParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Handles calculating item enchantment probability statistics
 */
public class StatTask extends BukkitRunnable {

    private static final DecimalFormat format = new DecimalFormat("##0.0#");

    private final ItemStack item;
    private final CommandSender sender;
    private final int level;
    private final int maxEnchants;

    /**
     * Constructor
     *
     * @param sender command sender
     * @param item   item to get the stats of
     * @param level  enchantment level
     */
    public StatTask(CommandSender sender, ItemStack item, int level) {
        this.sender = sender;
        this.level = level;
        this.item = item;
        this.maxEnchants = ((EnchantmentAPI) Bukkit.getPluginManager().getPlugin("EnchantmentAPI")).getModuleForClass(RootConfig.class).getInt(RootNode.MAX_ENCHANTS);
    }

    /**
     * Calculates statistics for an item using 100,000 samples
     */
    public void run() {
        List<CustomEnchantment> validEnchants = EnchantmentAPI.getAllValidEnchants(item);

        // Run 100,000 samples
        Hashtable<String, int[]> data = new Hashtable<String, int[]>();
        for (int i = 0; i < 100000; i++) {

            Map<CustomEnchantment, Integer> list = EEnchantTable.enchant(null, item, level, maxEnchants, false).getAddedEnchants();
            for (Map.Entry<CustomEnchantment, Integer> entry : list.entrySet()) {
                String name = entry.getKey().name();
                if (!data.containsKey(name)) data.put(name, new int[entry.getKey().getMaxLevel()]);
                int[] values = data.get(name);
                values[entry.getValue() - 1]++;
            }
        }

        // Display the results
        sender.sendMessage(ChatColor.GOLD + item.getType().name() + ChatColor.DARK_GREEN + " - Enchantment Stats (Lv " + level + ")");
        for (CustomEnchantment enchant : validEnchants) {
            if (enchant.getMaxLevel() == 0) continue;
            String message = enchant.name() + " (";

            // Convert vanilla enchantments to proper names
            if (enchant instanceof VanillaEnchantment)
                message = ENameParser.getVanillaName(((VanillaEnchantment) enchant).getVanillaEnchant()) + " (";

            // Retrieve stats if the enchantment occurred at all
            if (data.containsKey(enchant.name())) {
                int[] values = data.get(enchant.name());
                int index = 0;

                // List out the stats for each level
                for (int i = 0; i < enchant.getMaxLevel(); i++) {
                    message += ChatColor.GOLD + format.format(values[i] / 1000.0) + "%" + ChatColor.DARK_GREEN + ", ";
                }
            }

            // Otherwise just display 0's
            else {
                for (int i = 1; i <= enchant.getMaxLevel(); i++) {
                    message += ChatColor.GOLD + "0.0%" + ChatColor.DARK_GREEN + ", ";
                }
            }

            // Send the message
            sender.sendMessage(ChatColor.DARK_GREEN + message.substring(0, message.length() - 2) + ")");
        }
    }
}
