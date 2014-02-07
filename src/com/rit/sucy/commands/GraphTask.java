package com.rit.sucy.commands;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.config.RootConfig;
import com.rit.sucy.config.RootNode;
import com.rit.sucy.enchanting.EEnchantTable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Map;

/**
 * Displays a graph for the probabilities of an enchantment on a weapon
 */
public class GraphTask extends BukkitRunnable {

    private static final DecimalFormat format = new DecimalFormat("##0.0");

    private final CommandSender sender;
    private final ItemStack item;
    private final CustomEnchantment enchant;
    private final int maxEnchants;

    /**
     * Constructor
     *
     * @param sender  command sender
     * @param item    target item
     * @param enchant target enchantment
     */
    public GraphTask(CommandSender sender, ItemStack item, CustomEnchantment enchant) {
        this.sender = sender;
        this.item = item;
        this.enchant = enchant;
        this.maxEnchants = ((EnchantmentAPI) Bukkit.getPluginManager().getPlugin("EnchantmentAPI")).getModuleForClass(RootConfig.class).getInt(RootNode.MAX_ENCHANTS);
    }

    /**
     * Runs the task
     */
    public void run() {

        Hashtable<String, int[]> points = new Hashtable<String, int[]>();
        for (int i = 1; i <= 30; i++)
            points.put(enchant.name() + i, new int[enchant.getMaxLevel()]);
        // Run 3,000,000 samples over 30 levels
        for (int j = 1; j <= 30; j++) {

            // Runs 100,000 samples for each level
            for (int i = 0; i < 100000; i++) {

                Map<CustomEnchantment, Integer> list = EEnchantTable.enchant(null, item, j, maxEnchants, false).getAddedEnchants();
                for (Map.Entry<CustomEnchantment, Integer> entry : list.entrySet()) {
                    if (entry.getKey().equals(enchant)) {
                        int[] values = points.get(enchant.name() + j);
                        values[entry.getValue() - 1]++;
                    }
                }
            }
        }

        // Get the maximum probability
        int max = 0;
        for (int j = 1; j <= 30; j++) {
            int[] data = points.get(enchant.name() + j);
            if (data == null) continue;

            for (int k = 0; k < enchant.getMaxLevel(); k++) {
                if (data[k] > max)
                    max = data[k];
            }
        }
        max = (max + 999) / 1000;

        // Construct the graph
        for (int i = 9; i >= 0; i--) {

            ChatColor lc = i == 0 ? ChatColor.GRAY : ChatColor.DARK_GRAY;

            // Label the intervals
            String line = ChatColor.GOLD + ""
                    + format.format(i * max / 10.0) + "-"
                    + format.format((i + 1) * max / 10.0)
                    + "%" + lc + "_";
            while (line.length() < 16) line += "_";
            line += ChatColor.GRAY + "|";

            // Print out the points
            for (int j = 1; j <= 30; j++) {
                int[] data = points.get(enchant.name() + j);

                // Search if any of the levels fell into the interval for the given level
                String piece = lc + "_";
                for (int k = 0; k < enchant.getMaxLevel(); k++) {
                    if (data[k] > i * max * 100 && data[k] <= (i + 1) * max * 100) {
                        piece = ChatColor.getByChar((char)(49 + k % 6)) + "X";
                    }
                }
                line += piece;
            }

            // Send the line of the graph to the sender
            sender.sendMessage(line);
        }

        // Output the level scale at the bottom
        sender.sendMessage(ChatColor.DARK_GRAY + "||__________" + ChatColor.GRAY + "|" + ChatColor.DARK_GRAY
                + "____" + ChatColor.GRAY + "5" + ChatColor.DARK_GRAY + "____" + ChatColor.GRAY + "10"
                + ChatColor.DARK_GRAY + "___" + ChatColor.GRAY + "15" + ChatColor.DARK_GRAY + "___"
                + ChatColor.GRAY + "20" + ChatColor.DARK_GRAY + "___" + ChatColor.GRAY + "25" + ChatColor.DARK_GRAY
                + "___" + ChatColor.GRAY + "30");
    }
}
