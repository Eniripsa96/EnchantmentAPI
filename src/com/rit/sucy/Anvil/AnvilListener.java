package com.rit.sucy.Anvil;

import com.rit.sucy.Anvil.v1_8_3.MainAnvil;
import com.rit.sucy.EUpdateTask;
import com.rit.sucy.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnvilListener implements Listener {

    private final Plugin plugin;

    private final Hashtable<String, AnvilTask> tasks = new Hashtable<String, AnvilTask>();

    private boolean custom = false;

    public AnvilListener(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Opens the custom inventory instead of the default anvil inventory
     *
     * @param event event details
     */
    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL) {
            Player player = plugin.getServer().getPlayer(event.getPlayer().getName());

            AnvilView anvil;

            // See if the server is supported
            String pack = Version.getPackage();
            if (pack.equals("v1_9_R1"))
                anvil = new com.rit.sucy.Anvil.v1_9_0.MainAnvil(plugin, event.getInventory(), player);
            else if (pack.equals("v1_8_R3"))
                anvil = new com.rit.sucy.Anvil.v1_8_6.MainAnvil(plugin, event.getInventory(), player);
            else if (pack.equals("v1_8_R2"))
                anvil = new MainAnvil(plugin, event.getInventory(), player);
            else if (pack.equals("v1_8_R1"))
                anvil = new com.rit.sucy.Anvil.v1_8.MainAnvil(plugin, event.getInventory(), player);
            else {
                event.setCancelled(true);
                anvil = new CustomAnvil(plugin, player);
                custom = true;
            }
            tasks.put(player.getName(), new AnvilTask(plugin, anvil));
        }
    }

    /**
     * Gives back any items when the inventory is closed
     *
     * @param event event details
     */
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (tasks.containsKey(event.getPlayer().getName())) {
            tasks.get(event.getPlayer().getName()).getView().close();
            tasks.get(event.getPlayer().getName()).cancel();
            tasks.remove(event.getPlayer().getName());
        }
    }

    /**
     * Handles anvil transactions
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = plugin.getServer().getPlayer(event.getWhoClicked().getName());

        // Make sure the inventory is the custom inventory
        if (tasks.containsKey(player.getName()) && custom) {
            if (tasks.get(player.getName()).getView().getInventory().getName().equals(event.getInventory().getName())) {
                AnvilView view = tasks.get(player.getName()).getView();
                ItemStack[] inputs = view.getInputSlots();
                boolean top = event.getRawSlot() < view.getInventory().getSize();
                if (event.getSlot() == -999) return;

                if (event.isShiftClick()) {

                    if (event.getRawSlot() == view.getResultSlotID() && isFilled(view.getResultSlot())) {
                        if (player.getGameMode() != GameMode.CREATIVE && (view.getRepairCost() > player.getLevel() || view.getRepairCost() >= 40)) {
                            event.setCancelled(true);
                        }
                        else {
                            view.clearInputs();
                            if (player.getGameMode() != GameMode.CREATIVE)
                                player.setLevel(player.getLevel() - view.getRepairCost());
                        }
                    }

                    // Don't allow clicking in other slots in the anvil
                    else if (top && !view.isInputSlot(event.getSlot())) {
                        event.setCancelled(true);
                    }

                    // Don't allow shift clicking into the product slot
                    else if (!top && areFilled(inputs[0], inputs[1])) {
                        event.setCancelled(true);
                    }
                }
                else if (event.isLeftClick()) {

                    // Same as shift-clicking out the product
                    if (event.getRawSlot() == view.getResultSlotID() && !isFilled(event.getCursor()) && isFilled(view.getResultSlot())) {
                        if (player.getGameMode() != GameMode.CREATIVE && (view.getRepairCost() > player.getLevel() || view.getRepairCost() >= 40)) {
                            event.setCancelled(true);
                        }
                        else {
                            view.clearInputs();
                            if (player.getGameMode() != GameMode.CREATIVE)
                                player.setLevel(player.getLevel() - view.getRepairCost());
                        }
                    }

                    // Don't allow clicks in other slots of the anvil
                    else if (top && !view.isInputSlot(event.getSlot())) {
                        event.setCancelled(true);
                    }
                }
                else if (event.isRightClick()) {
                    if (top) event.setCancelled(true);
                }

                // Update the inventory manually after the click has happened
                new EUpdateTask(plugin, player);
            }
        }
    }

    private boolean isFilled(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    private boolean areFilled(ItemStack item1, ItemStack item2) {
        return isFilled(item1) && isFilled(item2);
    }
}
