package com.rit.sucy.Anvil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * An Anvil view can be anything which is able to display the following properties
 */
public interface AnvilView
{
    /**
     * Get the Items currently in the inputSlots
     *
     * @return array of 2 input slots
     */
    public ItemStack[] getInputSlots ();

    /**
     * Get the items that will be in the inputSlots when updated
     *
     * @param slot    slot being changed
     * @param newItem item being set to the slot
     * @return        updated input slots
     */
    public ItemStack[] getInputSlots(int slot, ItemStack newItem);

    /**
     * Gets the slot ID for the input
     *
     * @param input input number (1 or 2)
     * @return      slot ID
     */
    public int getInputSlotID(int input);

    /**
     * Set the item in the resultSlot
     *
     * @param result the resulting item
     */
    public void setResultSlot (ItemStack result);

    /**
     * Get the item in the resultSlot
     *
     * @return item
     */
    public ItemStack getResultSlot ();

    /**
     * Gets the Player looking at the Anvil
     * Pass Player in the constructor, that's why there is no setPlayer();
     */
    public Player getPlayer();

    /**
     * Set the cost for repairing this item
     *
     * @param repairCost repaircost in levels
     */
    public void setRepairCost (int repairCost);

    /**
     * Get the cost for repairing this item
     *
     * @return repaircost in levels
     */
    public int getRepairCost ();

    /**
     * Checks if a slot is an input slot
     *
     * @param slot slot ID
     * @return     true if an input slot, false otherwise
     */
    public boolean isInputSlot(int slot);

    /**
     * Get the slot ID for the result
     *
     * @return result slot ID
     */
    public int getResultSlotID();

    /**
     * Clears the inputs of the anvil when the result is taken
     */
    public void clearInputs();

    /**
     * Performs actions on closing
     */
    public void close();

    /**
     * Retrieves the inventory of this view
     *
     * @return anvil inventory
     */
    public Inventory getInventory();
}