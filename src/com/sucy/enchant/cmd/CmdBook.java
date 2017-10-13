package com.sucy.enchant.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.items.ItemManager;
import com.sucy.enchant.EnchantmentAPI;
import com.sucy.enchant.api.CustomEnchantment;
import com.sucy.enchant.vanilla.VanillaEnchantment;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.cmd.CmdBook
 */
public class CmdBook implements IFunction {

    private static final String NOT_PLAYER = "not-player";
    private static final String SUCCESS = "success";

    @Override
    public void execute(
            final ConfigurableCommand command,
            final Plugin plugin,
            final CommandSender sender,
            final String[] args) {

        if (!(sender instanceof Player)) {
            command.sendMessage(sender, NOT_PLAYER, "&4You must be a player to use this command");
            return;
        }

        final ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);
        final BookMeta meta = (BookMeta)book.getItemMeta();
        meta.addPage("EnchantmentAPI\nBy Eniripsa96\n\n Enchantment details");
        meta.setAuthor("Eniripsa96");
        meta.setTitle("EnchantmentAPI");

        final ArrayList<CustomEnchantment> enchants = new ArrayList<>(EnchantmentAPI.getRegisteredEnchantments());
        Collections.sort(enchants);

        for (final CustomEnchantment enchantment : enchants) {
            if (enchantment instanceof VanillaEnchantment) continue;
            if (enchantment.getDescription() == null) continue;
            StringBuilder page = new StringBuilder();
            page.append(enchantment.getName()).append(" - ").append(enchantment.getDescription()).append("\n\nItems: ");

            boolean first = true;
            for (Material item : enchantment.getNaturalItems()) {
                if (first) first = false;
                else page.append(", ");
                page.append(ItemManager.getVanillaName(item));
            }
            if (first) page.append("None");
            meta.addPage(page.toString());
        }
        book.setItemMeta(meta);
        ((Player)sender).getInventory().addItem(book);
        command.sendMessage(sender, SUCCESS, "&2You have received a book with all enchantment details");
    }
}
