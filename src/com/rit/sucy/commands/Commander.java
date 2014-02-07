package com.rit.sucy.commands;

import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.service.CommandHandler;
import com.rit.sucy.service.ICommand;
import com.rit.sucy.service.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Commander extends CommandHandler
{

    public Commander(EnchantmentAPI plugin)
    {
        super(plugin, "EnchantAPI");
        HelpCommand help = new HelpCommand();
        registerCommand("help", help);
        registerCommand("?", help);
        registerCommand("reload", new ReloadCommand());
        registerCommand("list", new EnchantListCommand());
        registerCommand("add", new AddEnchantCommand());
        registerCommand("book", new BookCommand());
        registerCommand("stats", new StatCommand());
        registerCommand("graph", new GraphCommand());
    }

    @Override
    public boolean noArgs(CommandSender sender, Command command, String label)
    {
        sender.sendMessage(ChatColor.GRAY + "========= " + ChatColor.RED + plugin.getName() + ChatColor.GRAY + " =========");
        sender.sendMessage    (" /enchantapi");
        sender.sendMessage    ("    help         " + ChatColor.YELLOW + "- Show the help menu");
        if (sender.hasPermission(PermissionNode.LIST.getNode())) {
            sender.sendMessage("    list          " + ChatColor.YELLOW + "- List all enchantments");
            sender.sendMessage("    list <page> " + ChatColor.YELLOW + "- List a page of enchantments with descriptions");
        }
        if (sender.hasPermission(PermissionNode.BOOK.getNode())) {
            sender.sendMessage("    book        " + ChatColor.YELLOW + "- Gives a book with enchantment descriptions");
        }
        if (sender.hasPermission(PermissionNode.ADMIN.getNode()))
        {
            sender.sendMessage("    reload      " + ChatColor.YELLOW + "- Reload the plugin");
            sender.sendMessage("    add          " + ChatColor.YELLOW + "- Add an enchantment to an item");
            sender.sendMessage("    stats <item> <level>      " + ChatColor.YELLOW + "- Displays enchantment stats");
            sender.sendMessage("    graph <item> <enchant> " + ChatColor.YELLOW + "- Displays a graph of stats");
        }
        return true;
    }

    @Override
    public boolean unknownCommand(CommandSender sender, Command command, String label, String[] args)
    {
        sender.sendMessage(ChatColor.YELLOW + plugin.getTag() + " Unknown command: " + ChatColor.WHITE + args[0]);
        return true;
    }

    private class HelpCommand implements ICommand
    {

        @Override
        public boolean execute(EnchantmentAPI plugin, CommandSender sender, Command command, String label, String[] args)
        {
            return noArgs(sender, command, label);
        }

    }
}
