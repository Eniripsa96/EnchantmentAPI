package com.rit.sucy.Anvil;

import com.rit.sucy.config.LanguageNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class CustomAnvil implements AnvilView, Listener {

    /**
     * Name prefix for the inventory
     */
    private static final String NAME = "Anvil (CraftBukkit broke!)";

    /**
     * Prefix for the cost lore
     */
    private static final String COST = ChatColor.DARK_RED + "Cost - ";

    /**
     * Description item for the left side
     */
    private static final ItemStack COMPONENT = makeComponentIndicator();

    /**
     * Description item for the right side
     */
    private static final ItemStack RESULT = makeResultIndicator();

    /**
     * Description item for the center
     */
    private static final ItemStack MIDDLE = makeMiddleIndicator();

    /**
     * Initial contents of a custom anvil inventory
     */
    private static final ItemStack[] CONTENTS = new ItemStack[] { COMPONENT, null, null, COMPONENT, MIDDLE, MIDDLE, RESULT, null, RESULT };

    final Plugin plugin;
    final Inventory anvil;
    final Player player;

    int repairCost;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param player player viewing the inventory
     */
    public CustomAnvil(Plugin plugin, Player player) {
        anvil = plugin.getServer().createInventory(null, 9, NAME);
        anvil.setContents(CONTENTS);
        this.plugin = plugin;
        this.player = player;
        player.openInventory(anvil);
    }

    /**
     * Retrieves the current items in the two input slots
     *
     * @return input items
     */
    @Override
    public ItemStack[] getInputSlots() {
        return new ItemStack[] { anvil.getItem(1), anvil.getItem(2) };
    }

    /**
     * Retrieves the items that will be in the input slots when updated
     *
     * @param slot    slot being changed
     * @param newItem item being set to the slot
     * @return        input items
     */
    @Override
    public ItemStack[] getInputSlots(int slot, ItemStack newItem) {
        if (slot == 1) return new ItemStack[] { newItem, anvil.getItem(2) };
        else if (slot == 2) return new ItemStack[] { anvil.getItem(1), newItem };
        else throw new IllegalArgumentException(slot + " is not an input slot!");
    }

    /**
     * Retrieves the slot id for the input number
     *
     * @param input input number (1 or 2)
     * @return      input slot
     */
    @Override
    public int getInputSlotID(int input) {
        if (input == 1 || input == 2) return input;
        else throw new IllegalArgumentException("Invalid input number: " + input);
    }

    /**
     * Sets the result slot to the target item
     *
     * @param item result
     */
    @Override
    public void setResultSlot(ItemStack item) {

        if (item == null) {
            anvil.clear(7);
            return;
        }

        item = item.clone();
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : plugin.getServer().getItemFactory().getItemMeta(item.getType());
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
        lore.add(COST + repairCost);
        meta.setLore(lore);
        item.setItemMeta(meta);

        anvil.setItem(7, item);
    }

    /**
     * Retrieves the item in the result slot
     *
     * @return result
     */
    @Override
    public ItemStack getResultSlot() {

        if (anvil.getItem(7) == null)
            return null;

        ItemStack result = anvil.getItem(7).clone();
        ItemMeta meta = result.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(lore.size() - 1);
        meta.setLore(lore);
        result.setItemMeta(meta);
        return result;
    }

    /**
     * Gets the player using this anvil
     *
     * @return player
     */
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the repair cost for the anvil
     *
     * @param repairCost repaircost in levels
     */
    @Override
    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
        setResultSlot(getResultSlot());
    }

    /**
     * Gets the repair cost for the anvil
     *
     * @return repaircost in levels
     */
    @Override
    public int getRepairCost() {
        return this.repairCost;
    }

    /**
     * Checks if the given slot ID is an input slot
     *
     * @param slot slot ID
     * @return     true if input, false otherwise
     */
    @Override
    public boolean isInputSlot(int slot) {
        return slot == 1 || slot == 2;
    }

    /**
     * Gets the slot ID for the result
     *
     * @return result slot ID
     */
    @Override
    public int getResultSlotID() {
        return 7;
    }

    /**
     * Clears the inputs for the anvil when taking the result
     */
    @Override
    public void clearInputs() {
        anvil.clear(1);
        anvil.clear(2);
        anvil.setItem(7, getResultSlot());
    }

    /**
     * Closes the anvil, giving items back to the player
     */
    @Override
    public void close() {
        if (anvil.getItem(1) != null)
            player.getInventory().addItem(anvil.getItem(1));
        if (anvil.getItem(2) != null)
            player.getInventory().addItem(anvil.getItem(2));
    }

    /**
     * Gets the anvil inventory
     *
     * @return anvil inventory
     */
    @Override
    public Inventory getInventory() {
        return anvil;
    }

    /**
     * Creates the component indicator
     *
     * @return component indiactor
     */
    static ItemStack makeComponentIndicator() {

        List<String> component = getText(LanguageNode.ANVIL_COMPONENT);
        return makeIndicator(component);
    }

    /**
     * Creates the separator between components and results
     *
     * @return separator
     */
    static ItemStack makeMiddleIndicator() {

        List<String> separator = getText(LanguageNode.ANVIL_SEPARATOR);
        return makeIndicator(separator);
    }

    /**
     * Creates the result indicator
     *
     * @return result indicator
     */
    static ItemStack makeResultIndicator() {

        List<String> result = getText(LanguageNode.ANVIL_RESULT);
        return makeIndicator(result);
    }

    /**
     * Creates an indicator item stack
     *
     * @param lines text lines
     * @return      indicator item
     */
    static ItemStack makeIndicator(List<String> lines) {

        ItemStack item = new ItemStack(Material.BOOK);

        ArrayList<String> lore = new ArrayList<String>();
        for (int i = 1; i < lines.size(); i++)
            lore.add(lines.get(i));

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(lines.get(0));
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Gets text from the config
     *
     * @param node node to load from
     * @return     text list
     */
    static List<String> getText(LanguageNode node) {
        List<String> list = Bukkit.getPluginManager().getPlugin("EnchantmentAPI").getConfig().getStringList(node.getFullPath());
        List<String> coloredList = new ArrayList<String>();
        for (String line : list)
            coloredList.add(line.replace('&', ChatColor.COLOR_CHAR));
        return coloredList;
    }
}
