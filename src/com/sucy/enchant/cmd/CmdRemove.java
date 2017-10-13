package com.sucy.enchant.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.enchant.api.CustomEnchantment;
import com.sucy.enchant.api.Enchantments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;

import static com.sucy.enchant.util.Utils.isPresent;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.cmd.CmdRemove
 */
public class CmdRemove implements IFunction {

    private static final String NOT_PLAYER = "not-player";
    private static final String NO_ITEM = "no-item";
    private static final String NO_ENCHANTS = "no-enchants";
    private static final String REMOVED = "removed";

    @Override
    public void execute(
            final ConfigurableCommand command,
            final Plugin plugin,
            final CommandSender sender,
            final String[] strings) {

        if (!(sender instanceof Player)) {
            command.sendMessage(sender, NOT_PLAYER, "&4Only players can use this command");
            return;
        }

        final Player player = (Player)sender;
        final ItemStack item = player.getEquipment().getItemInMainHand();
        if (!isPresent(item)) {
            command.sendMessage(sender, NO_ITEM, "&4You don't have an item in your hand");
            return;
        }

        final Map<CustomEnchantment, Integer> enchantments = Enchantments.getAllEnchantments(item);
        if (enchantments.isEmpty()) {
            command.sendMessage(sender, NO_ENCHANTS, "&4That item doesn't have any enchantments");
            return;
        }

        Enchantments.removeAllEnchantments(item);
        command.sendMessage(sender, REMOVED, "&2 Removed all enchantments from your held item");
    }
}
