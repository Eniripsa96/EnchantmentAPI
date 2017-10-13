package com.sucy.enchant.cmd;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.SenderType;
import com.sucy.enchant.EnchantmentAPI;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.cmd.Commands
 */
public class Commands {

    public static void init(final EnchantmentAPI plugin) {
        final ConfigurableCommand root = new ConfigurableCommand(plugin, "enchants", SenderType.ANYONE);
        root.addSubCommands(
                new ConfigurableCommand(
                        plugin,
                        "add",
                        SenderType.PLAYER_ONLY,
                        new CmdAdd(),
                        "enchants held item",
                        "<enchant> <level>",
                        "EnchantmentAPI.admin"),
                new ConfigurableCommand(
                        plugin,
                        "reload",
                        SenderType.ANYONE,
                        new CmdReload(),
                        "reloads the plugin",
                        "",
                        "EnchantmentAPI.admin"),
                new ConfigurableCommand(
                        plugin,
                        "graph",
                        SenderType.ANYONE,
                        new CmdGraph(),
                        "graphs probabilities",
                        "<mat> <enchant>",
                        "EnchantmentAPI.admin"),
                new ConfigurableCommand(
                        plugin,
                        "book",
                        SenderType.PLAYER_ONLY,
                        new CmdBook(),
                        "detailed book",
                        "",
                        "EnchantmentAPI.admin")
        );
        CommandManager.registerCommand(root);
    }
}
