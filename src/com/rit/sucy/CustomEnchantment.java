package com.rit.sucy;

import com.rit.sucy.service.ENameParser;
import com.rit.sucy.service.ERomanNumeral;
import com.rit.sucy.service.MaterialClass;
import com.rit.sucy.service.MaterialsParser;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for custom enchantments
 */
public abstract class CustomEnchantment implements Comparable<CustomEnchantment>{

    /**
     * Default group that doesn't conflict with any other enchantments
     */
    public static final String DEFAULT_GROUP = "Default";

    /**
     * List of suffix groups this enchantment takes from
     * Takes from all lists if no groups are added
     */
    protected List<String> suffixGroups = new ArrayList<String>();

    /**
     * Name of the enchantment
     */
    protected final String enchantName;

    /**
     * Description for the enchantment
     */
    protected String description;

    /**
     * Names of all the items that can receive this enchantment at an enchanting table
     */
    protected Material [] naturalItems;

    /**
     * Weight of the enchantment
     */
    protected Map<MaterialClass, Integer> weight;

    /**
     * Whether or not the enchantment is enabled
     */
    protected boolean isEnabled;

    /**
     * Whether or not the enchantment can be obtained from the enchantment table
     */
    protected boolean isTableEnabled;

    /**
     * The conflict group of this enchantment
     */
    protected String group;

    /**
     * The interval value for calculating enchantment level
     */
    protected double interval;

    /**
     * The base value for calculating enchantment level
     */
    protected double base;

    /**
     * The maximum level of this enchantment
     */
    protected int max;

    /**
     * @param name enchantment name
     */
    public CustomEnchantment(String name) {
        this(name, null, new Material[0], DEFAULT_GROUP, 5);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param naturalItems the names of items that can normally have this enchantment
     * @deprecated use the constructor with Material[] instead
     */
    public CustomEnchantment(String name, String[] naturalItems) {
        this(name, null, MaterialsParser.toMaterial(naturalItems), DEFAULT_GROUP, 5);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param naturalItems the names of items that can normally have this enchantment
     */
    public CustomEnchantment(String name, Material[] naturalItems) {
        this(name, null, naturalItems, DEFAULT_GROUP, 5);
    }

    /**
     * @param name        the unique name of the enchantment
     * @param description the brief description for the enchantment
     */
    public CustomEnchantment(String name, String description) {
        this(name, description, new Material[0], DEFAULT_GROUP, 5);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param naturalItems the names of items that can normally have this enchantment
     * @param weight       the weight of the enchantment
     * @deprecated use constructor with Material[] instead instead of String[]
     */
    public CustomEnchantment(String name, String[] naturalItems, int weight) {
        this(name, null, MaterialsParser.toMaterial(naturalItems), DEFAULT_GROUP, weight);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param naturalItems the items that can normally have this enchantment
     * @param weight       the weight of the enchantment
     */
    public CustomEnchantment(String name, Material [] naturalItems, int weight) {
        this(name, null, naturalItems, DEFAULT_GROUP, weight);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param naturalItems the items that can normally have this enchantment
     * @param group        the conflict group for this enchantment
     */
    public CustomEnchantment(String name, Material[] naturalItems, String group) {
        this(name, null, naturalItems, group, 5);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param description  a brief description for the enchantment
     * @param naturalItems the items that can normally have this enchantment
     */
    public CustomEnchantment(String name, String description, Material[] naturalItems) {
        this(name, description, naturalItems, DEFAULT_GROUP, 5);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param description  a brief description for the enchantment
     * @param group        the group that this enchantment conflicts with
     */
    public CustomEnchantment(String name,  String description, String group) {
        this (name, description, new Material[0], group, 5);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param description  a brief description for the enchantment
     * @param weight       the weight of this enchantment
     */
    public CustomEnchantment(String name, String description, int weight) {
        this (name, description, new Material[0], DEFAULT_GROUP, 5);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param description  a brief description for the enchantment
     * @param naturalItems the items that can normally have this enchantment
     * @param group        the conflict group of the enchantment
     */
    public CustomEnchantment(String name, String description, Material[] naturalItems, String group) {
        this (name, description, naturalItems, group, 5);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param description  a brief description for the enchantment
     * @param naturalItems the items that can normally have this enchantment
     * @param weight       the weight of this enchantment
     */
    public CustomEnchantment(String name, String description, Material[] naturalItems, int weight) {
        this(name, description, naturalItems, DEFAULT_GROUP, 5);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param description  a brief description for the enchantment
     * @param group        the group that this enchantment conflicts with
     * @param weight       the weight of this enchantment
     */
    public CustomEnchantment(String name, String description, String group, int weight) {
        this(name, description, new Material[0], group, weight);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param naturalItems the items that can normally have this enchantment
     * @param group        the group that this enchantment conflicts with
     * @param weight       the weight of this enchantment
     */
    public CustomEnchantment(String name, Material[] naturalItems, String group, int weight) {
        this(name, null, naturalItems, group, weight);
    }

    /**
     * @param name         the unique name of the enchantment
     * @param description  a brief description for the enchantment
     * @param naturalItems the items that can normally have this enchantment
     * @param group        the group that this enchantment conflicts with
     * @param weight       the weight of this enchantment
     */
    public CustomEnchantment(String name, String description, Material[] naturalItems, String group, int weight) {
        Validate.notEmpty(name, "Your Enchantment needs a name!");
        Validate.notNull(naturalItems, "Input an empty array instead of \"null\"!");
        Validate.isTrue(weight >= 0, "Weight can't be negative!");

        this.enchantName = name;
        this.description = description;
        this.naturalItems = naturalItems;
        this.isEnabled = true;
        this.group = group;
        this.max = 1;
        this.base = 1;
        this.interval = 10;
        this.isTableEnabled = true;

        this.weight = new HashMap<MaterialClass, Integer>();
        this.weight.put(MaterialClass.DEFAULT, weight);
    }

    /**
     * Retrieves the name of the enchantment
     *
     * @return Enchantment name
     */
    public String name() {
        return enchantName;
    }

    /**
     * @return enchantment description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set if this enchantment is enabled or not
     *
     * @param enabled enabled state of the enchantment
     */
    public void setEnabled (boolean enabled)
    {
        this.isEnabled = enabled;
    }

    /**
     * Retrieves whether or not the enchantment is enabled
     *
     * @return enabled state
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Set if the enchantmnet can be obtained in the table
     *
     * @param value enabled state of the enchantment
     */
    public void setTableEnabled(boolean value) {
        isTableEnabled = value;
    }

    /**
     * @return true if can be obtained in the table, false otherwise
     */
    public boolean isTableEnabled() {
        return isTableEnabled;
    }

    /**
     * Gets the enchantment level of an enchantment
     *
     * @param expLevel modified exp level
     * @return         enchantment level
     */
    public int getEnchantLevel(int expLevel) {
        for (int i = max; i >= 1; i--) {
            if (expLevel >= base + interval * (i - 1)) return i;
        }
        return 0;
    }

    /**
     * @return maximum level for this enchantment
     */
    public int getMaxLevel() {
        return max;
    }

    /**
     * Sets the maximum level for this enchantment
     *
     * @param value maximum level
     */
    public void setMaxLevel(int value) {
        max = value;
    }

    /**
     * @return minimum modified level to get this enchantment
     */
    public double getBase() {
        return base;
    }

    /**
     * Sets the minimum modified level to get this enchantment
     *
     * @param value minimum modified level
     */
    public void setBase(double value) {
        base = value;
    }

    /**
     * @return number of modified levels required to get the next enchantment level
     */
    public double getInterval() {
        return interval;
    }

    /**
     * Sets the number of modified levels required to get the next enchantment level
     *
     * @param value required modified levels
     */
    public void setInterval(double value) {
        interval = value;
    }

    /**
     * @return suffix groups this enchantment pulls from
     */
    public List<String> getSuffixGroups() {
        return suffixGroups;
    }

    /**
     * Gets the cost per level in the anvil when this enchantment is present
     *
     * @param withBook whether or not a book was used
     * @return         cost per level
     */
    public int getCostPerLevel(boolean withBook) {
        int costIndex = weight.get(MaterialClass.DEFAULT) * max;
        int divisor = withBook ? 2 : 1;
        return
            weight.get(MaterialClass.DEFAULT) == 1 ? 8 / divisor
            : costIndex < 10 ? 4 / divisor
            : costIndex < 30 ? 2 / divisor
            : 1;
    }

    /**
     * Set the items on which this enchantment can be found
     *
     * @param materials list of valid Materials for this enchantment
     */
    public void setNaturalMaterials(Material[] materials)
    {
        this.naturalItems = materials;
    }

    /**
     * Get the items on which this enchantment can naturally be found on
     *
     * @return      the names of the items
     * @deprecated use getNaturalMaterials instead
     */
    public String[] getNaturalItems(){
        String[] natItems = new String [naturalItems.length];
        for (int i = 0; i < naturalItems.length; i++)
            natItems[i] = naturalItems[i].name();
        return natItems;
    }

    /**
     * @return list of materials this item applies to normally
     */
    public Material[] getNaturalMaterials()
    {
        return naturalItems;
    }

    /**
     * Set weight for default MaterialClass
     *
     * @param weight enchantment weight
     */
    public void setWeight(int weight)
    {
        this.weight.put(MaterialClass.DEFAULT, weight);
    }

    /**
     * @return the default weight of this item
     */
    public int getWeight (){
        return weight.get(MaterialClass.DEFAULT);
    }

    /**
     * Get the weight of an Enchantment for a specific MaterialClass
     *
     * @param material  Material to get the weight for
     * @return          of the Enchantment or the DefaultWeight if not found
     */
    public int getWeight (MaterialClass material){
        return weight.containsKey(material) ? weight.get(material) : weight.get(MaterialClass.DEFAULT);
    }

    /**
     * Checks if this enchantment can be normally applied to the item.
     *
     * @param  item the item to check for
     * @return      true if the enchantment can be normally applied, false otherwise
     */
    public boolean canEnchantOnto(ItemStack item) {
        if (naturalItems == null || item == null) return false;
        for (Material validItem : naturalItems) {
            if (item.getType() == validItem) return true;
        }
        return item.getType() == Material.BOOK || item.getType() == Material.ENCHANTED_BOOK;
    }

    /**
     * Set the conflict group for this enchantment
     *
     * @param group the new group for this enchantment
     */
    public void setGroup (String group){
        this.group = group;
    }

    /**
     * @return conflict group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Check if this CustomEnchantment conflicts with another Enchantment
     *
     * @param enchantment   to check
     * @return              true if conflicts and false if Enchantment can be applied
     */
    public boolean conflictsWith (CustomEnchantment enchantment){
        Validate.notNull(enchantment);

        return !group.equals(DEFAULT_GROUP) && group.equalsIgnoreCase(enchantment.group);
    }

    /**
     * Check for the given List of Items if they conflict with this Enchantment
     *
     * @param enchantmentsToCheck   All Enchantments to check
     *
     * @return                      if this enchant conflicts with one (or more) enchantments
     */
    public boolean conflictsWith (List<CustomEnchantment> enchantmentsToCheck){
        Validate.notNull(enchantmentsToCheck);
        for (CustomEnchantment enchantment : enchantmentsToCheck)
            if (conflictsWith(enchantment))
                return true;
        return false;
    }

    /**
     * Check for the given List of Items if they conflict with this Enchantment
     *
     * @param enchantmentsToCheck   All Enchantments to check
     *
     * @return                      if this enchant conflicts with one (or more) enchantments
     */
    public boolean conflictsWith (CustomEnchantment ... enchantmentsToCheck){
        Validate.notNull(enchantmentsToCheck);
        for (CustomEnchantment enchantment : enchantmentsToCheck)
            if (conflictsWith(enchantment))
                return true;
        return false;
    }

    /**
     * Adds this enchantment onto the given item with the enchantment level provided
     *
     * @param  item         the item being enchanted
     * @param  enchantLevel the level of enchantment
     * @return              the enchanted item
     */
    public ItemStack addToItem(ItemStack item, int enchantLevel) {
        Validate.notNull(item);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) meta = Bukkit.getServer().getItemFactory().getItemMeta(item.getType());
        List<String> metaLore = meta.getLore() == null ? new ArrayList<String>() : meta.getLore();

        // Make sure the enchantment doesn't already exist on the item
        for (Map.Entry<CustomEnchantment, Integer> entry : EnchantmentAPI.getEnchantments(item).entrySet()) {
            if (entry.getKey().name().equals(name())) {
                if (entry.getValue() < enchantLevel) {
                    metaLore.remove(ChatColor.GRAY + name() + " " + ERomanNumeral.numeralOf(entry.getValue()));
                }
                else {
                    return item;
                }
            }
        }

        // Add the enchantment
        metaLore.add(0, ChatColor.GRAY + enchantName + " " + ERomanNumeral.numeralOf(enchantLevel));
        meta.setLore(metaLore);
        String name = ENameParser.getName(item);
        if (name != null) meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Removes this enchantment from the item if it exists
     *
     * @param item item to remove this enchantment from
     * @return     the item without this enchantment
     */
    public ItemStack removeFromItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (!meta.hasLore()) return item;
        List<String> metaLore = meta.getLore();

        // Make sure the enchantment doesn't already exist on the item
        for (Map.Entry<CustomEnchantment, Integer> entry : EnchantmentAPI.getEnchantments(item).entrySet()) {
            if (entry.getKey().name().equals(name())) {
                metaLore.remove(ChatColor.GRAY + name() + " " + ERomanNumeral.numeralOf(entry.getValue()));
            }
        }
        return item;
    }

    /**
     * Compare the name of the enchantment
     *
     * @param obj   Object to compare
     * @return      if Objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CustomEnchantment)
            return this.name().equalsIgnoreCase(((CustomEnchantment) obj).name());
        return false;
    }

    /**
     * Make CustomEnchantments sortable by List.sort().
     * We just look at the name for comparison.
     *
     * @param customEnchantment to compare to
     * @return -1 if less than, 0 if equal, 1 if greater
     */
    @Override
    public int compareTo(CustomEnchantment customEnchantment) {
        return this.name().compareTo(customEnchantment.name());
    }

    /**
     * Applies the enchantment affect when attacking someone
     *
     * @param user         the entity that has the enchantment
     * @param target       the entity that was struck by the enchantment
     * @param enchantLevel the level of the used enchantment
     * @param event        the event details
     */
    public void applyEffect(LivingEntity user, LivingEntity target, int enchantLevel, EntityDamageByEntityEvent event) { }

    /**
     * Applies the enchantment defensively (when taking damage)
     *
     * @param user         the entity hat has the enchantment
     * @param target       the entity that attacked the enchantment, can be null
     * @param enchantLevel the level of the used enchantment
     * @param event        the event details (EntityDamageByEntityEvent, EntityDamageByBlockEvent, or just EntityDamageEvent)
     */
    public void applyDefenseEffect(LivingEntity user, LivingEntity target,
            int enchantLevel, EntityDamageEvent event) {}

    /**
     * Applies effects while breaking blocks (for tool effects)
     *
     * @param player the player with the enchantment
     * @param block  the block being broken
     * @param event  the event details (either BlockBreakEvent or BlockDamageEvent)
     */
    public void applyToolEffect(Player player, Block block, int enchantLevel, BlockEvent event) {}

    /**
     * Applies effects when the player left or right clicks (For other kinds of enchantments like spells)
     *
     * @param player the player with the enchantment
     * @param event  the event details
     */
    public void applyMiscEffect(Player player, int enchantLevel, PlayerInteractEvent event) {}

    /**
     * Applies effects when the item is equipped
     *
     * @param player       the player that equipped it
     * @param enchantLevel the level of enchantment
     */
    public void applyEquipEffect(Player player, int enchantLevel) {}

    /**
     * Applies effects when the item is unequipped
     *
     * @param player       the player that unequipped it
     * @param enchantLevel the level of enchantment
     */
    public void applyUnequipEffect(Player player, int enchantLevel) {}

    /**
     * Applies effects when the player interacts with an entity
     *
     * @param player       player with the enchantment
     * @param enchantLevel enchantment level
     * @param event        the event details
     */
    public void applyEntityEffect(Player player, int enchantLevel, PlayerInteractEntityEvent event) {}

    /**
     * Applies effects when firing a projectile
     *
     * @param user         entity firing the projectile
     * @param enchantLevel enchantment level
     * @param event        the event details
     */
    public void applyProjectileEffect(LivingEntity user, int enchantLevel, ProjectileLaunchEvent event) { }
}
