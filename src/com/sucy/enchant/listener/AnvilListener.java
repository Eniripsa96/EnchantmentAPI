package com.sucy.enchant.listener;

import com.rit.sucy.text.TextFormatter;
import com.sucy.enchant.data.ConfigKey;
import com.sucy.enchant.data.Configuration;
import com.sucy.enchant.mechanics.EnchantmentMerger;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.sucy.enchant.util.Utils.isPresent;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.listener.AnvilListener
 */
public class AnvilListener extends BaseListener {

    private final boolean colored = Configuration.using(ConfigKey.COLORED_NAMES_IN_ANVILS);

    @EventHandler
    public void onCombine(final PrepareAnvilEvent event) {
        final ItemStack first = event.getInventory().getItem(0);
        final ItemStack second = event.getInventory().getItem(1);
        if (isSingle(first) && isSingle(second)) {
            final ItemStack result = select(first, second, true).clone();
            final EnchantmentMerger merger = getMerger(event.getInventory());
            if (merger.getCustomCost() != 0 || merger.getVanillaCost() != 0) {
                event.setResult(merger.apply(result));
                event.getInventory().setRepairCost(merger.getCost());
            }
        }

        if (isPresent(event.getResult())) {
            String text = event.getInventory().getRenameText();

            final ItemStack primary = select(first, second, true);
            if (primary.hasItemMeta()) {
                final ItemMeta primaryMeta = primary.getItemMeta();
                if (primaryMeta.hasDisplayName()) {
                    final String withoutColorChar = primaryMeta.getDisplayName().replace("" + ChatColor.COLOR_CHAR, "");
                    if (withoutColorChar.equals(event.getInventory().getRenameText())) {
                        text = primaryMeta.getDisplayName();
                    }
                }
            }


            final ItemMeta meta = event.getResult().getItemMeta();
            meta.setDisplayName(colored ? TextFormatter.colorString(text) : text);
            event.getResult().setItemMeta(meta);
        }
    }

    private EnchantmentMerger getMerger(final AnvilInventory anvil) {
        final ItemStack result = select(anvil.getItem(0), anvil.getItem(1), true).clone();
        final ItemStack supplement = select(anvil.getItem(0), anvil.getItem(1), false);
        return new EnchantmentMerger()
                .merge(result, false)
                .merge(supplement, true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL && event.getRawSlot() == 2) {
            final AnvilInventory anvil = (AnvilInventory) event.getInventory();
            if (anvil.getRepairCost() == 0
                    && isPresent(anvil.getItem(2))
                    && !isPresent(event.getWhoClicked().getItemOnCursor()) || !event.getAction().name().startsWith("PICKUP")) {
                final Player player = (Player)event.getWhoClicked();
                if (player.getGameMode() == GameMode.CREATIVE || checkLevels(player, anvil)) {
                    player.setItemOnCursor(anvil.getItem(2));
                    anvil.clear();
                }
            }
        }
    }

    private boolean checkLevels(final Player player, final AnvilInventory anvil) {
        final EnchantmentMerger merger = getMerger(anvil);
        if (player.getLevel() >= merger.getCost()) {
            player.setLevel(player.getLevel() - merger.getCost());
            return true;
        }
        return false;
    }

    private ItemStack select(final ItemStack first, final ItemStack second, final boolean result) {
        return (!isBook(first) || isBook(second)) == result ? first : second;
    }

    private boolean isBook(final ItemStack item) {
        return !isPresent(item) || item.getType() == Material.BOOK || item.getType() == Material.ENCHANTED_BOOK;
    }

    private boolean isSingle(final ItemStack item) {
        return isPresent(item) && item.getAmount() == 1;
    }
}
