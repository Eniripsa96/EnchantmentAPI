package com.sucy.enchant.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.enchant.EnchantmentAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.cmd.CmdReload
 */
public class CmdReload implements IFunction {

    @Override
    public void execute(
            final ConfigurableCommand configurableCommand,
            final Plugin plugin,
            final CommandSender commandSender,
            final String[] strings) {

        final EnchantmentAPI enchantmentAPI = JavaPlugin.getPlugin(EnchantmentAPI.class);
        enchantmentAPI.onDisable();
        enchantmentAPI.onEnable();
    }
}
