package com.rit.sucy.commands;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.service.ENameParser;
import com.rit.sucy.service.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

/**
 * Displays a graph to the sender for the probability statistics for an item's enchantment
 */
public class GraphCommand implements ICommand {

    private long timer = 0l;

    /**
     * Executes the command
     *
     * @param plugin  plugin reference
     * @param sender  - Sender of the command.
     * @param command - Command used.
     * @param label   - Label.
     * @param args    - Command arguments.
     * @return        true if valid item and enchantment, false otherwise
     */
    @Override
    public boolean execute(EnchantmentAPI plugin, CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            try {

                // Parse the item
                Material mat = Material.getMaterial(args[0].toUpperCase());
                if (mat == null)
                    mat = Material.getMaterial(Integer.parseInt(args[0]));
                if (mat == null)
                    return false;

                ItemStack item = new ItemStack(mat);

                // Make sure the enchantment can work on the item
                String name = args[1];
                for (int i = 2; i < args.length; i++) name += " " + args[i];
                CustomEnchantment enchant = EnchantmentAPI.getEnchantment(ENameParser.getBukkitName(name));
                if (!enchant.canEnchantOnto(item)) {
                    sender.sendMessage(ChatColor.DARK_RED + "That enchantment doesn't work on that item");
                    return true;
                }

                // Make sure the command isn't being used too much (to avoid lag)
                if (System.currentTimeMillis() - timer < 10000) {
                    sender.sendMessage(ChatColor.DARK_RED + "Please give the server a quick break!");
                    sender.sendMessage(ChatColor.DARK_RED + "The command should only be used every 10 seconds!");
                    return true;
                }

                // Display stats
                timer = System.currentTimeMillis();
                sender.sendMessage(ChatColor.DARK_GREEN + "Calculating probabilities...");
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new GraphTask(sender, item, enchant));
                return true;
            }
            catch (Exception e) {
                // Do nothing
            }
            return false;
        }
        else return false;
    }
}
