package com.rit.sucy.commands;

import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.service.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author Diemex
 */
public class ReloadCommand implements ICommand{
    @Override
    public boolean execute(EnchantmentAPI plugin, CommandSender sender, Command command, String label, String[] args) {
        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Enchantment API has been reloaded.");
        return true;
    }
}
