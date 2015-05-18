package com.rit.sucy.Anvil;

import net.minecraft.server.v1_8_R1.ContainerAnvil;
import net.minecraft.server.v1_8_R1.ContainerAnvilInventory;
import net.minecraft.server.v1_8_R1.IInventory;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

public class MainAnvil implements AnvilView {

    final Player player;
    final Plugin plugin;
    CraftInventoryAnvil inv;
    ContainerAnvil      anvil;
    int                 repairCost;

    public MainAnvil(Plugin plugin, Inventory anvil, Player player)
    {
        this.player = player;
        this.plugin = plugin;

        inv = (CraftInventoryAnvil) anvil;
        try
        {
            Field container = ContainerAnvilInventory.class.getDeclaredField("a");
            container.setAccessible(true);
            this.anvil = (ContainerAnvil) container.get(inv.getInventory());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // -.-
        }
    }

    /**
     * Retrieves the text from the anvil name field
     *
     * @return name field text
     */
    public String getNameText() {
        try {
            // Make sure the item doesn't have a unique name
            for (net.minecraft.server.v1_8_R1.ItemStack item : inv.getIngredientsInventory().getContents()) {
                ItemStack i = CraftItemStack.asBukkitCopy(item);
                if (i.hasItemMeta() && i.getItemMeta().hasDisplayName() && !i.getItemMeta().getDisplayName().equals(ChatColor.stripColor(i.getItemMeta().getDisplayName()))) {
                    return null;
                }
            }

            // Gross (Reflection to obtain the field text)
            Field textField = ContainerAnvil.class.getDeclaredField("l");
            textField.setAccessible(true);
            String name = (String)textField.get(anvil);
            if (name == null)
                return null;

            // More gross (Reflection to obtain the first item)
            Field g = ContainerAnvil.class.getDeclaredField("h");
            g.setAccessible(true);
            net.minecraft.server.v1_8_R1.ItemStack item = ((IInventory)g.get(anvil)).getItem(0);
            if (item == null)
                return null;

            // Much better
            if (name.equals(item.getName()))
                return null;

            // Finally, we're done T_T
            return name;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ItemStack[] getInputSlots() {
        ItemStack[] input = new ItemStack[2];
        input[0] = CraftItemStack.asCraftMirror(inv.getIngredientsInventory().getItem(0));
        input[1] = CraftItemStack.asCraftMirror(inv.getIngredientsInventory().getItem(1));
        return input;
    }

    @Override
    public ItemStack[] getInputSlots(int slot, ItemStack newItem) {
        ItemStack[] input = new ItemStack[2];
        input[0] = slot == 0 ? newItem : CraftItemStack.asCraftMirror(inv.getIngredientsInventory().getItem(0));
        input[1] = slot == 1 ? newItem : CraftItemStack.asCraftMirror(inv.getIngredientsInventory().getItem(1));
        return input;
    }

    @Override
    public int getInputSlotID(int input) {
        return input - 1;
    }

    @Override
    public void setResultSlot(final ItemStack result) {
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (result == null)
                    inv.getResultInventory().setItem(0, null);
                else
                    inv.getResultInventory().setItem(0, CraftItemStack.asNMSCopy(result));
                ((CraftPlayer) player).getHandle().setContainerData(anvil, 0, anvil.a);
            }
        });
    }

    @Override
    public ItemStack getResultSlot() {
        return CraftItemStack.asCraftMirror(inv.getResultInventory().getItem(0));
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setRepairCost(final int repairCost) {
        this.repairCost = repairCost;
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    anvil.a = repairCost;
                    ((CraftPlayer) player).getHandle().setContainerData(anvil, 0, anvil.a);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getRepairCost() {
        return repairCost;
    }

    @Override
    public boolean isInputSlot(int slot) {
        return slot == 0 || slot == 1;
    }

    @Override
    public int getResultSlotID() {
        return 2;
    }

    @Override
    public void clearInputs() {
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                inv.getIngredientsInventory().setItem(0, null);
                inv.getIngredientsInventory().setItem(1, null);
                ((CraftPlayer) player).getHandle().setContainerData(anvil, 0, anvil.a);
            }
        });
    }

    @Override
    public void close() {
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
