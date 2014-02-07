package com.rit.sucy.commands;

import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.service.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

/**
 * Displays enchantment probability statistics for an item
 */
public class StatCommand implements ICommand {

    /**
     * Executes the command
     *
     * @param plugin  plugin reference
     * @param sender  - Sender of the command.
     * @param command - Command used.
     * @param label   - Label.
     * @param args    - Command arguments.
     * @return        true if valid item and level, false otherwise
     */
    @Override
    public boolean execute(EnchantmentAPI plugin, CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            try {

                // Parse the item
                Material mat = Material.getMaterial(args[0].toUpperCase());
                if (mat == null)
                    mat = Material.getMaterial(Integer.parseInt(args[0]));
                if (mat == null)
                    return false;


                // Make sure it has valid enchantments
                ItemStack item = new ItemStack(mat);
                if (EnchantmentAPI.getAllValidEnchants(item).size() == 0){
                    sender.sendMessage(ChatColor.DARK_RED + "That item has no natural enchantments");
                    return true;
                }

                // Parse the level
                int level = Integer.parseInt(args[1]);

                // Display stats
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new StatTask(sender, item, level));
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
