package com.sucy.enchant.cmd;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.enchant.EnchantmentAPI;
import com.sucy.enchant.api.CustomEnchantment;
import com.sucy.enchant.api.GlowEffects;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static com.sucy.enchant.util.Utils.isPresent;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.cmd.CmdAdd
 */
public class CmdAdd implements IFunction {

    private static final String PLAYER_ONLY = "player-only";
    private static final String NOT_ENCHANTMENT = "not-enchantment";
    private static final String NOT_LEVEL = "not-level";
    private static final String NO_ITEM = "no-item";
    private static final String SUCCESS = "success";

    @Override
    public void execute(
            final ConfigurableCommand command,
            final Plugin plugin,
            final CommandSender sender,
            final String[] args) {

        if (!(sender instanceof Player)) {
            command.sendMessage(sender, PLAYER_ONLY, ChatColor.DARK_RED + "Only players can use this command");
            return;
        }

        if (args.length < 2) {
            CommandManager.displayUsage(command, sender);
            return;
        }

        final Player player = (Player)sender;
        if (!isPresent(player.getEquipment().getItemInMainHand())) {
            command.sendMessage(sender, NO_ITEM, ChatColor.DARK_RED + "You are not holding an item to enchant");
        }

        final StringBuilder builder = new StringBuilder(args[0]);
        for (int i = 1; i < args.length - 1; i++) {
            builder.append(' ');
            builder.append(args[i]);
        }

        final CustomEnchantment enchantment = EnchantmentAPI.getEnchantment(builder.toString());
        if (enchantment == null) {
            command.sendMessage(sender, NOT_ENCHANTMENT, ChatColor.GOLD + builder.toString() + ChatColor.DARK_RED + " is not an enchantment");
            return;
        }

        final int level;
        try {
            level = Integer.parseInt(args[args.length - 1]);
        } catch (final Exception ex) {
            command.sendMessage(sender, NOT_LEVEL, ChatColor.GOLD + args[args.length - 1] + ChatColor.DARK_RED + " is not a number");
            return;
        }

        enchantment.addToItem(player.getInventory().getItemInMainHand(), level);
        GlowEffects.finalize(player.getInventory().getItemInMainHand());
        command.sendMessage(sender, SUCCESS, ChatColor.DARK_GREEN + "Added the enchantment successfully");
    }
}
