package com.rit.sucy.commands;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.enchanting.VanillaEnchantment;
import com.rit.sucy.service.ENameParser;
import com.rit.sucy.service.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Provides the executor with a book containing descriptions for all enchantments
 */
public class BookCommand implements ICommand {

    /**
     * Executes the command
     *
     * @param plugin  plugin reference
     * @param sender  - Sender of the command.
     * @param command - Command used.
     * @param label   - Label.
     * @param args    - Command arguments.
     * @return        true
     */
    @Override
    public boolean execute(EnchantmentAPI plugin, CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);
        BookMeta meta = (BookMeta)book.getItemMeta();
        meta.addPage("EnchantmentAPI\nMade by Steven Sucy\n(Eniripsa96)\n\n Enchantment details \n\nCommand:\n\n/enchantapi list #");
        meta.setAuthor("Eniripsa96");
        meta.setTitle("EnchantmentAPI");
        ArrayList<CustomEnchantment> enchants = new ArrayList<CustomEnchantment>(EnchantmentAPI.getEnchantments());
        Collections.sort(enchants);
        for (CustomEnchantment enchantment : enchants) {
            if (enchantment instanceof VanillaEnchantment) continue;
            if (enchantment.getDescription() == null) continue;
            String page = enchantment.name() + " - " + enchantment.getDescription() + "\n\nItems: ";
            if (enchantment.getNaturalMaterials().length > 0) {
                for (Material item : enchantment.getNaturalMaterials()) {
                    page += ChatColor.stripColor(ENameParser.getName(item)) + ", ";
                }
                page = page.substring(0, page.length() - 2);
            }
            else page += "None";
            meta.addPage(page);
        }
        book.setItemMeta(meta);
        ((Player)sender).getInventory().addItem(book);
        return true;
    }
}
