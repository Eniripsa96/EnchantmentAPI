package com.rit.sucy;

import com.rit.sucy.Anvil.AnvilListener;
import com.rit.sucy.commands.Commander;
import com.rit.sucy.config.RootConfig;
import com.rit.sucy.enchanting.*;
import com.rit.sucy.lore.LoreConfig;
import com.rit.sucy.service.ENameParser;
import com.rit.sucy.service.ERomanNumeral;
import com.rit.sucy.service.IModule;
import com.rit.sucy.service.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Contains methods to register and access custom enchantments
 *
 * @author  Steven Sucy
 * @version 4.3
 */
public class EnchantmentAPI extends JavaPlugin {

    /**
     * A table of the custom enchantments that are registered
     */
    private static Hashtable<String, CustomEnchantment> enchantments = new Hashtable<String, CustomEnchantment>();

    /**
     * Adjectives for item lores
     */
    private Hashtable<String, List<String>> adjectives;

    /**
     * Weapon names for item lores
     */
    private Hashtable<String, List<String>> weapons;

    /**
     * Suffixes for item lores
     */
    private Hashtable<String, List<String>> suffixes;

    /**
    * Registered modules.
    */
    private final Map<Class<? extends IModule>, IModule> modules = new HashMap<Class<? extends IModule>, IModule>();

    /**
     * Prefix for all messages send to the Player/Console
     */
    private static String TAG = "[EnchantAPI]"; //just to make it a bit shorter

    /**
     * Enables the plugin and calls for all custom enchantments from any plugins
     * that extend the EnchantPlugin class
     */
    @Override
    public void onEnable(){
        //When adding new commands register them in Commander and if you want to change the root command you have to change it in Commander as well
        getCommand("enchantapi").setExecutor(new Commander(this));
        registerModule(RootConfig.class, new RootConfig(this));
        reload();
    }

    /**
     * Disables the plugin and clears all custom enchantments
     */
    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        enchantments.clear();
        EEquip.clear();
    }

    /**
     * Reloads all custom and vanilla Enchantments.
     * Shouldn't be called by other plugins.
     */
    public void reload()
    {
        adjectives = new LoreConfig(this, "adjectives").getLists();
        weapons = new LoreConfig(this, "weapons").getLists();
        suffixes = new LoreConfig(this, "suffixes").getLists();
        HandlerList.unregisterAll(this);
        EEquip.clear();
        enchantments.clear();

        //Load provided vanilla enchantments
        loadVanillaEnchantments();

        // Get custom enchantments from other plugins
        for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof EnchantPlugin) {
                try {
                    ((EnchantPlugin) plugin).registerEnchantments();
                    getLogger().info("Loaded enchantments from plugin: " + plugin.getName());
                }
                catch (Exception e) {
                    getLogger().info("Failed to load enchantments from " + plugin.getName() + ": perhaps it is outdated?");
                }
            }
        }

        for (Player player : getServer().getOnlinePlayers()) {
            EEquip.loadPlayer(player);
        }

        //Important that the enchantments are loaded before the configuration is loaded
        getModuleForClass(RootConfig.class).reload();

        // Listeners
        new EListener(this);
        new AnvilListener(this);
    }

    /**
     * Get all enchantments which can be applied to the given ItemStack
     *
     * @param item  item to check for applicable enchantments
     * @return      List of all applicable enchantments
     */
    public static List<CustomEnchantment> getAllValidEnchants(ItemStack item){
        return getAllValidEnchants(item, 45);
    }

    /**
     * Get all enchantments which can be applied to the given ItemStack
     *
     * @param item  item to check for applicable enchantments
     * @param level modified enchantment level
     * @return      List of all applicable enchantments
     */
    public static List<CustomEnchantment> getAllValidEnchants(ItemStack item, int level) {
        List<CustomEnchantment> validEnchantments = new ArrayList<CustomEnchantment>();

        boolean book = item.getType() == Material.BOOK;
        for (CustomEnchantment enchantment : getEnchantments()){
            if ((enchantment.canEnchantOnto(item) || book) && enchantment.isEnabled() && enchantment.getEnchantLevel(level) >= 1 && enchantment.isTableEnabled()){
                validEnchantments.add(enchantment);
            }
        }
        return validEnchantments;
    }

    /**
     * Get all enchantments which can be applied to the given ItemStack
     *
     * @param item  item to check for applicable enchantments
     * @return      List of all applicable enchantments
     */
    public static List<CustomEnchantment> getAllValidEnchants(ItemStack item, Permissible enchanter){
        return getAllValidEnchants(item, enchanter, 45);
    }

    /**
     * Get all enchantments which can be applied to the given ItemStack
     *
     * @param item  item to check for applicable enchantments
     * @param level modified enchantment level
     * @return      List of all applicable enchantments
     */
    public static List<CustomEnchantment> getAllValidEnchants(ItemStack item, Permissible enchanter, int level) {
        List<CustomEnchantment> validEnchantments = new ArrayList<CustomEnchantment>();

        boolean book = item.getType() == Material.BOOK;
        for (CustomEnchantment enchantment : getEnchantments()){
            if ((enchantment.canEnchantOnto(item) || book) && enchantment.isEnabled()
                    && enchantment.getEnchantLevel(level) >= 1 && enchantment.isTableEnabled()
                    && (enchanter.hasPermission(PermissionNode.ENCHANT.getNode())
                    || (enchanter.hasPermission(PermissionNode.ENCHANT_VANILLA.getNode()) && enchantment instanceof VanillaEnchantment)
                    || enchanter.hasPermission(PermissionNode.ENCHANT.getNode() + "." + enchantment.name().replace(" ", "").toLowerCase()))){
                validEnchantments.add(enchantment);
            }
        }
        return validEnchantments;
    }

    /**
     * Get the adjectives for a given tier level
     *
     * @param level tier level
     * @return      adjective list
     */
    public String getAdjective(int level) {
        if (adjectives.containsKey("tier" + level)) {
            List<String> list = adjectives.get("tier" + level);
            return list.get((int)(Math.random() * list.size()));
        }
        else {
            ArrayList<String> all = new ArrayList<String>();
            for (List<String> list : adjectives.values())
                for (String adjective : list)
                    all.add(adjective);
            return all.get((int)(Math.random() * all.size()));
        }
    }

    /**
     * Get the names for a given item
     *
     * @param item item name
     * @return     alternate name list
     */
    public String getWeapon(String item) {
        String[] pieces = item.split("_");
        String type = pieces[pieces.length - 1].toLowerCase();
        if (weapons.containsKey(type)) {
            List<String> list = weapons.get(type);
            return list.get((int)(Math.random() * list.size()));
        }
        else return type.substring(0, 1).toUpperCase() + type.substring(1);
    }

    /**
     * Gets the suffixes for an enchantment
     *
     * @param enchant enchantment
     * @return        suffixes for the enchantment
     */
    public String getSuffix(CustomEnchantment enchant) {
        ArrayList<String> options = new ArrayList<String>();
        if (enchant.getSuffixGroups().size() == 0) {
            for (List<String> list : suffixes.values())
                for (String suffix : list)
                    if (!options.contains(suffix))
                        options.add(suffix);
        }
        else {
            for (String group : enchant.getSuffixGroups())
                for (String suffix : suffixes.get(group))
                    if (!options.contains(suffix))
                        options.add(suffix);
        }
        return options.get((int)(Math.random() * options.size()));
    }

    /**
     * Will load Vanilla Enchantments as CustomEnchantments
     * Idea: Plugins modifying the probability of vanilla enchants
     */
    private void loadVanillaEnchantments(){
        for (VanillaData defaults : VanillaData.values())
        {
            VanillaEnchantment vanilla = new VanillaEnchantment(defaults.getEnchantment(), defaults.getItems(), defaults.getGroup(), defaults.getSuffixGroup(), defaults.getEnchantWeight(), defaults.getBase(), defaults.getInterval(), defaults.getMaxLevel(), defaults.name());
            registerCustomEnchantment(vanilla);
        }
    }

    /**
     * Checks if the enchantment with the given name is currently registered
     *
     * @param enchantmentName name of the enchantment
     * @return true if it is registered, false otherwise
     */
    public static boolean isRegistered(String enchantmentName) {
        return enchantments.containsKey(enchantmentName.toUpperCase());
    }

    /**
     * Retrieves the custom enchantment with the given name
     *
     * @param  name the name of the enchantment
     * @return      the enchantment with the given name, null if not found
     */
    public static CustomEnchantment getEnchantment(String name) {
        return enchantments.get(name.toUpperCase());
    }

    /**
     * Retrieves the names of all enchantments that have been registered
     *
     * @return set of custom enchantment names
     */
    public static Set<String> getEnchantmentNames() {
        return enchantments.keySet();
    }

    /**
     * Retrieves all custom enchantments
     *
     * @return the list of all custom enchantments
     */
    public static Collection<CustomEnchantment> getEnchantments() {
        return enchantments.values();
    }

    /**
     * Registers the given custom enchantment for the plugin
     *
     * @param  enchantment the enchantment to register
     * @return             true if it was registered, false otherwise
     */
    public static boolean registerCustomEnchantment(CustomEnchantment enchantment) {
        if (enchantments.containsKey(enchantment.name().toUpperCase())) return false;
        if (!enchantment.isEnabled()) return false;
        enchantments.put(enchantment.name().toUpperCase(), enchantment);
        return true;
    }

    /**
     * Registers all of the provided enchantments
     *
     * @param enchantments enchantments to register
     */
    public static void registerCustomEnchantments(CustomEnchantment ... enchantments) {
        for (CustomEnchantment enchantment : enchantments)
            registerCustomEnchantment(enchantment);
    }

    /**
     * Unregisters the enchantment with the given name
     *
     * @param enchantmentName name of the enchantment to unregister
     * @return                true if it was removed, false if it didn't exist
     */
    public static boolean unregisterCustomEnchantment(String enchantmentName) {
        if (enchantments.containsKey(enchantmentName.toUpperCase())) {
            enchantments.remove(enchantmentName.toUpperCase());
            return true;
        }
        else return false;
    }

    /**
     * Returns the list of custom enchantments applied to the item
     *
     * @param item the item that's being checked for enchantments
     * @return     the list of attached enchantments
     */
    public static Map<CustomEnchantment, Integer> getEnchantments(ItemStack item) {
        HashMap<CustomEnchantment, Integer> list = new HashMap<CustomEnchantment, Integer>();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return list;
        if (!meta.hasLore()) return list;
        for (String lore : meta.getLore()) {
            String name = ENameParser.parseName(lore);
            int level = ENameParser.parseLevel(lore);
            if (name == null) continue;
            if (level == 0) continue;
            if (EnchantmentAPI.isRegistered(name)) {
                list.put(EnchantmentAPI.getEnchantment(name), level);
            }
        }
        return list;
    }

    /**
     * Gets every enchantment on an item, vanilla and custom
     * @param item item to retrieve the enchantments of
     * @return     all enchantments on the item
     */
    public static Map<CustomEnchantment, Integer> getAllEnchantments(ItemStack item) {
        Map<CustomEnchantment, Integer> map = getEnchantments(item);
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                map.put(getEnchantment(entry.getKey().getName()), entry.getValue());
            }
        }
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
            for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
                map.put(getEnchantment(entry.getKey().getName()), entry.getValue());
            }
        }
        return map;
    }

    /**
     * Checks if the given item has an enchantment with the given name
     *
     * @param item            item to check
     * @param enchantmentName name of enchantment
     * @return                true if it has the enchantment, false otherwise
     */
    public static boolean itemHasEnchantment(ItemStack item, String enchantmentName) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        if (!meta.hasLore()) return false;
        for (String lore : meta.getLore()) {
            if (lore.contains(enchantmentName) && ENameParser.parseLevel(lore) > 0)
                return true;
        }
        return false;
    }

    /**
     * Removes all enchantments from the given item
     *
     * @param item item to clear enchantments from
     * @return     the item without enchantments
     */
    public static ItemStack removeEnchantments(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (!meta.hasLore()) return item;
        List<String> lore = meta.getLore();
        for (Map.Entry<CustomEnchantment, Integer> entry : getEnchantments(item).entrySet()) {
            lore.remove(ChatColor.GRAY + entry.getKey().name() + " " + ERomanNumeral.numeralOf(entry.getValue()));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Gets the Prefix to be used for messages
     *
     * @return prefix
     */
    public String getTag(){
        return TAG;
    }

    // Does nothing when run as .jar
    public static void main(String[] args) {}

    /**
     * Register a module.
     *
     * @param clazz  - Class of the instance.
     * @param module - Module instance.
     * @throws IllegalArgumentException - Thrown if an argument is null.
     */
    <T extends IModule> void registerModule(Class<T> clazz, T module)
    {
        // Check arguments.
        if (clazz == null)
        {
            throw new IllegalArgumentException("Class cannot be null");
        }
        else if (module == null)
        {
            throw new IllegalArgumentException("Module cannot be null");
        }
        // Add module.
        modules.put(clazz, module);
        // Tell module to start.
        module.starting();
    }

    /**
     * Deregister a module.
     *
     * @param clazz - Class of the instance.
     * @return Module that was removed. Returns null if no instance of the module
     *         is registered.
     */
    public <T extends IModule> T deregisterModuleForClass(Class<T> clazz)
    {
        // Check arguments.
        if (clazz == null)
        {
            throw new IllegalArgumentException("Class cannot be null");
        }
        // Grab module and tell it its closing.
        T module = clazz.cast(modules.get(clazz));
        if (module != null)
        {
            module.closing();
        }
        return module;
    }

    /**
     * Retrieve a registered module.
     *
     * @param clazz - Class identifier.
     * @return Module instance. Returns null is an instance of the given class
     *         has not been registered with the API.
     */
    public <T extends IModule> T getModuleForClass(Class<T> clazz)
    {
        return clazz.cast(modules.get(clazz));
    }
}
