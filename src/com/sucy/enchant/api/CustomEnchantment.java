package com.sucy.enchant.api;

import com.google.common.collect.ImmutableList;
import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.text.TextFormatter;
import com.sucy.enchant.EnchantmentAPI;
import com.sucy.enchant.data.Permission;
import com.sucy.enchant.util.LoreReader;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sucy.enchant.util.Utils.isPresent;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.api.CustomEnchantment
 */
public abstract class CustomEnchantment implements Comparable<CustomEnchantment> {

    /**
     * Default conflict group for enchantments. Using this group causes the
     * enchantment not to conflict with any other enchantments.
     */
    public static final String DEFAULT_GROUP = "Default";

    private final Map<Material, Double> materialWeights = new HashMap<>();

    private final Set<Material> naturalItems = new HashSet<>();

    private String key;
    private String name;
    private String description;

    private String  group;
    private boolean enabled;
    private boolean tableEnabled;
    private boolean stacks;
    private double  enchantLevelScaleFactor;
    private double  minEnchantingLevel;
    private double  enchantLevelBuffer;
    private double  weight;
    private int     maxLevel;
    private int     maxTableLevel;
    private int     combineCostPerLevel;

    private boolean setFactors = false;

    protected Settings settings = new Settings();

    protected CustomEnchantment(final String name, final String description) {
        Validate.notEmpty(name, "The name must be present and not empty");
        Validate.notEmpty(description, "The description must be present and not empty");

        this.key = name.trim();
        this.name = name.trim();
        this.description = description.trim();

        enabled = true;
        tableEnabled = true;
        group = DEFAULT_GROUP;
        maxLevel = 1;
        maxTableLevel = 1;
        minEnchantingLevel = 1;
        enchantLevelScaleFactor = 60;
        enchantLevelBuffer = 0;
        stacks = false;
        combineCostPerLevel = 1;

        weight = 5.0;
    }

    // ---- Getters/Setters ---- //

    /**
     * @return name of the enchantment that shows up in the lore
     */
    public String getName() {
        return name;
    }

    /**
     * @return details for the enchantment to show in the details book
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return whether or not having the enchantment on multiple items stacks their effects
     */
    public boolean canStack() {
        return stacks;
    }

    /** @see CustomEnchantment#canStack() */
    public void setCanStack(final boolean stacks) {
        this.stacks = stacks;
    }

    /**
     * @return whether or not the enchantment is obtainable without commands
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return whether or not the enchantment is obtainable
     */
    public boolean isTableEnabled() {
        return enabled && tableEnabled;
    }

    /** @see CustomEnchantment#isTableEnabled() */
    public void setTableEnabled(final boolean tableEnabled) {
        this.tableEnabled = tableEnabled;
    }

    /**
     * @return the max level the enchantment can normally reach via combining enchantments
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * @return the max level the enchantment can be from an enchanting table
     */
    public int getMaxTableLevel() {
        return maxTableLevel;
    }

    /**
     * Sets both the max table level and combine level to the given amount.
     *
     * @param maxLevel max normally obtainable level
     */
    public void setMaxLevel(final int maxLevel) {
        setMaxLevel(maxLevel, maxLevel);
        if (!setFactors) {
            enchantLevelScaleFactor = 60 / maxLevel;
        }
    }

    /**
     * @see CustomEnchantment#getMaxLevel()
     * @see CustomEnchantment#getMaxTableLevel()
     */
    public void setMaxLevel(final int maxLevel, final int maxTableLevel) {
        Validate.isTrue(maxTableLevel > 0, "Max table level must be at least 1");
        Validate.isTrue(maxLevel >= maxTableLevel, "Max level must be at least 1");
        this.maxLevel = maxLevel;
        this.maxTableLevel = maxTableLevel;
    }

    /**
     * @return minimum modified enchantment level needed to receive this enchantment
     * @apiNote modified enchantment level is normally between 2 and 48
     */
    public double getMinEnchantingLevel() {
        return minEnchantingLevel;
    }

    public void setMinEnchantingLevel(final double minEnchantingLevel) {
        setFactors = true;
        this.minEnchantingLevel = minEnchantingLevel;
    }

    public double getEnchantLevelScaleFactor() {
        return enchantLevelScaleFactor;
    }

    public void setEnchantLevelScaleFactor(final double enchantLevelScaleFactor) {
        Validate.isTrue(enchantLevelScaleFactor > 0, "Scale factor must be a positive number");
        setFactors = true;
        this.enchantLevelScaleFactor = enchantLevelScaleFactor;
    }

    public double getEnchantLevelBuffer() {
        return enchantLevelBuffer;
    }

    public void setEnchantLevelBuffer(final double enchantLevelBuffer) {
        Validate.isTrue(enchantLevelBuffer >= 0, "Buffer cannot be negative");
        setFactors = true;
        this.enchantLevelBuffer = enchantLevelBuffer;
    }

    public int getCombineCostPerLevel() {
        return combineCostPerLevel;
    }

    public void setCombineCostPerLevel(final int combineCostPerLevel) {
        Validate.isTrue(combineCostPerLevel >= 0, "Combine cost cannot be negative");
        this.combineCostPerLevel = combineCostPerLevel;
    }

    public Set<Material> getNaturalItems() {
        return naturalItems;
    }

    public void addNaturalItems(final Material... materials) {
        for (Material material : materials) {
            Objects.requireNonNull(material, "Cannot add a null natural material");
            naturalItems.add(material);
        }
    }

    public double getWeight(final Material material) {
        return materialWeights.getOrDefault(material, weight);
    }

    public void setWeight(final double weight) {
        this.weight = weight;
    }

    public void setWeight(final Material material, final double weight) {
        Validate.isTrue(weight > 0, "Weight must be a positive number");
        this.materialWeights.put(material, weight);
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(final String group) {
        Validate.notEmpty(group, "Group cannot be empty or missing");
        this.group = group;
    }

    // --- Functional Methods --- //

    /**
     * @param expLevel enchanting level (from enchanting table)
     * @return enchantment level
     */
    public int computeLevel(final int expLevel) {
        final int level = Math.min(1 + (int)Math.floor((expLevel - minEnchantingLevel) / enchantLevelScaleFactor), this.maxTableLevel);
        final double cap = minEnchantingLevel + level * enchantLevelScaleFactor + enchantLevelBuffer;
        return expLevel <= cap ? Math.max(0, level) : 0;
    }

    /**
     * @param item item to check
     * @return true if can go onto the item, not including conflicts with other enchantments
     */
    public boolean canEnchantOnto(final ItemStack item) {
        if (!isPresent(item)) {
            return false;
        }

        final Material material = item.getType();
        return material == Material.BOOK || material == Material.ENCHANTED_BOOK || naturalItems.contains(material);
    }

    /**
     * Checks whether or not this enchantment works with the given enchantment
     *
     * @param other enchantment to check against
     * @param same whether or not to allow the same enchantment (in case of merging)
     * @return true if there is a conflict, false otherwise
     */
    public boolean conflictsWith(final CustomEnchantment other, final boolean same) {
        Objects.requireNonNull(other, "Cannot check against a null item");
        if (other == this) {
            return same;
        }
        return !group.equals(DEFAULT_GROUP) && group.equals(other.getGroup());
    }

    /**
     * Checks whether or not this enchantment works with all of the given enchantments
     *
     * @param enchantments enchantments to check against
     * @param same whether or not to allow the same enchantment (in case of merging)
     * @return true if there is a conflict, false otherwise
     */
    public boolean conflictsWith(final List<CustomEnchantment> enchantments, final boolean same) {
        Objects.requireNonNull(enchantments, "Cannot check a null enchantment list");
        return enchantments.stream().anyMatch(enchant -> conflictsWith(this, same));
    }

    /**
     * Checks whether or not this enchantment works with all of the given enchantments
     *
     * @param same whether or not to allow the same enchantment (in case of merging)
     * @param enchantments enchantments to check against
     * @return true if there is a conflict, false otherwise
     */
    public boolean conflictsWith(final boolean same, final CustomEnchantment... enchantments) {
        return Arrays.stream(enchantments).anyMatch(enchant -> conflictsWith(this, same));
    }

    /**
     * @param item item to add to
     * @param level enchantment level
     * @return item with the enchantment
     */
    public ItemStack addToItem(final ItemStack item, final int level) {
        Objects.requireNonNull(item, "Item cannot be null");
        Validate.isTrue(level > 0, "Level must be at least 1");

        if (item.getType() == Material.BOOK) {
            item.setType(Material.ENCHANTED_BOOK);
        }

        final ItemMeta meta = item.getItemMeta();
        final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        final int lvl = Enchantments.getCustomEnchantments(item).getOrDefault(this, 0);
        if (lvl > 0) {
            lore.remove(LoreReader.formatEnchantment(name, lvl));
        }

        lore.add(0, LoreReader.formatEnchantment(name, level));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * @param item item to remove from
     * @return item without this enchantment
     */
    public ItemStack removeFromItem(final ItemStack item) {
        Objects.requireNonNull(item, "Item cannot be null");

        final int lvl = Enchantments.getCustomEnchantments(item).getOrDefault(this, 0);
        if (lvl > 0) {
            final ItemMeta meta = item.getItemMeta();
            final List<String> lore = meta.getLore();
            lore.remove(LoreReader.formatEnchantment(name, lvl));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public void addToEnchantment(final Map<Enchantment, Integer> enchantments, final ItemStack result, final int level) {
        addToItem(result, level);
    }

    /**
     * @param permissible person to receive the enchantment
     * @return true if they have permission, false otherwise
     */
    public boolean hasPermission(final Permissible permissible) {
        return permissible == null
                || permissible.hasPermission(Permission.ENCHANT)
                || permissible.hasPermission(getPermission());
    }

    /**
     * @return permission used by the enchantment
     */
    public String getPermission() {
        return Permission.ENCHANT + "." + getName().replace(" ", "");
    }

    // ---- API for effects ---- //

    /**
     * Applies the enchantment affect when attacking someone
     *
     * @param user   the entity that has the enchantment
     * @param target the entity that was struck by the enchantment
     * @param level  the level of the used enchantment
     * @param event  the event details
     */
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent event) { }

    /**
     * Applies the enchantment defensively (when taking damage)
     *
     * @param user   the entity hat has the enchantment
     * @param target the entity that attacked the enchantment, can be null
     * @param level  the level of the used enchantment
     * @param event  the event details (EntityDamageByEntityEvent, EntityDamageByBlockEvent, or just EntityDamageEvent)
     */
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent event) { }

    /**
     * Applies effects while breaking blocks (for tool effects)
     *
     * @param user  the player with the enchantment
     * @param block the block being broken
     * @param event the event details (either BlockBreakEvent or BlockDamageEvent)
     */
    public void applyBreak(final LivingEntity user, final Block block, final int level, final BlockEvent event) { }

    /**
     * Applies effects when the item is equipped
     *
     * @param user  the player that equipped it
     * @param level the level of enchantment
     */
    public void applyEquip(final LivingEntity user, final int level) { }

    /**
     * Applies effects when the item is unequipped
     *
     * @param user  the player that unequipped it
     * @param level the level of enchantment
     */
    public void applyUnequip(final LivingEntity user, final int level) { }

    /**
     * Applies effects when the player left or right clicks (For other kinds of enchantments like spells)
     *
     * @param user  the player with the enchantment
     * @param event the event details
     */
    public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent event) { }

    /**
     * Applies effects when the player interacts with an entity
     *
     * @param user  player with the enchantment
     * @param level enchantment level
     * @param event the event details
     */
    public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent event) { }

    /**
     * Applies effects when firing a projectile
     *
     * @param user  entity firing the projectile
     * @param level enchantment level
     * @param event the event details
     */
    public void applyProjectile(final LivingEntity user, final int level, final ProjectileLaunchEvent event) { }

    // ---- Object operations ---- //

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof CustomEnchantment && ((CustomEnchantment) other).name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(final CustomEnchantment other) {
        return name.compareTo(other.name);
    }

    // ---- IO ---- /

    private static final String SAVE_FOLDER = "enchant/custom/";

    private static final String
            COMBINE_COST_PER_LEVEL = "combine-cost-per-level",
            DESCRIPTION = "description",
            ENABLED = "enabled",
            ENCHANT_LEVEL_SCALE_FACTOR = "enchant-level-scale-factor",
            ENCHANT_LEVEL_BUFFER = "enchant-level-buffer",
            GROUP = "group",
            MAX_LEVEL = "max-level",
            MAX_TABLE_LEVEL = "max-table-level",
            MIN_ENCHANTING_LEVEL = "min-enchanting-level",
            NAME = "name",
            NATURAL_ITEMS = "natural-items",
            STACKS = "stacks",
            TABLE_ENABLED = "table-enabled",
            WEIGHT = "weight",
            MATERIAL = "material-weights",
            EFFECT = "effect";

    private static final List<String>
            BUFFER_COMMENT = ImmutableList.of("", " How many enchantment levels beyond the max can still yield the enchantment"),
            COMBINE_COMMENT = ImmutableList.of("", " Level cost per level of the enchantment"),
            LEVEL_SCALE_COMMENT = ImmutableList.of("", " Higher numbers result in requiring higher enchanting levels", " to get higher ranks"),
            GROUP_COMMENT = ImmutableList.of("", " When something other than default, prevents multiple enchantments", " in the same group being on the same item"),
            MAX_COMMENT = ImmutableList.of("", " Max attainable level from anvils"),
            MAX_TABLE_COMMENT = ImmutableList.of("", " Max attainable level from enchanting"),
            MIN_COMMENT = ImmutableList.of("", " Minimum enchanting level to receive the enchantment.", " Negatives make it easier to get higher ranks"),
            ITEM_COMMENT = ImmutableList.of("", " Items that can receive the enchantment from enchanting or anvils"),
            STACK_COMMENT = ImmutableList.of("", " Whether or not the same enchantment stacks if on multiple items.", " When false, the highest level is applied"),
            TABLE_COMMENT = ImmutableList.of("", " Whether or not this enchantment can be achieved from enchanting"),
            WEIGHT_COMMENT = ImmutableList.of("", " How common the enchantment is. Higher numbers are more common."),
            MATERIAL_COMMENT = ImmutableList.of("", " Weights for specific materials"),
            EFFECT_COMMENT = ImmutableList.of("", " Extra settings specific to the enchantment");

    protected String getSaveFolder() {
        return SAVE_FOLDER;
    }

    public void save(final EnchantmentAPI plugin) {
        final CommentedConfig config = new CommentedConfig(plugin, getSaveFolder() + key);
        final DataSection data = config.getConfig();
        data.clear();

        data.set(NAME, name);
        data.set(DESCRIPTION, description);
        data.set(ENABLED, enabled);

        data.set(MAX_LEVEL, maxLevel);
        data.setComments(MAX_LEVEL, MAX_COMMENT);

        data.set(MAX_TABLE_LEVEL, maxTableLevel);
        data.setComments(MAX_TABLE_LEVEL, MAX_TABLE_COMMENT);

        data.set(GROUP, group);
        data.setComments(GROUP, GROUP_COMMENT);

        data.set(NATURAL_ITEMS, naturalItems.stream().map(Material::name).collect(Collectors.toList()));
        data.setComments(NATURAL_ITEMS, ITEM_COMMENT);

        data.set(WEIGHT, weight);
        data.setComments(WEIGHT, WEIGHT_COMMENT);

        final DataSection weightData = data.createSection(MATERIAL);
        materialWeights.forEach((material, weight) -> weightData.set(material.name(), weight));
        data.setComments(MATERIAL, MATERIAL_COMMENT);

        data.set(MIN_ENCHANTING_LEVEL, minEnchantingLevel);
        data.setComments(MIN_ENCHANTING_LEVEL, MIN_COMMENT);

        data.set(ENCHANT_LEVEL_SCALE_FACTOR, enchantLevelScaleFactor);
        data.setComments(ENCHANT_LEVEL_SCALE_FACTOR, LEVEL_SCALE_COMMENT);

        data.set(ENCHANT_LEVEL_BUFFER, enchantLevelBuffer);
        data.setComments(ENCHANT_LEVEL_BUFFER, BUFFER_COMMENT);

        data.set(COMBINE_COST_PER_LEVEL, combineCostPerLevel);
        data.setComments(COMBINE_COST_PER_LEVEL, COMBINE_COMMENT);

        data.set(STACKS, stacks);
        data.setComments(STACKS, STACK_COMMENT);

        data.set(TABLE_ENABLED, tableEnabled);
        data.setComments(TABLE_ENABLED, TABLE_COMMENT);

        data.set(EFFECT, settings);
        data.setComments(EFFECT, EFFECT_COMMENT);

        config.save();
    }

    public void load(final EnchantmentAPI plugin) {
        final CommentedConfig config = new CommentedConfig(plugin, getSaveFolder() + key);
        if (!config.getConfigFile().exists()) {
            return;
        }

        final DataSection data = config.getConfig();
        name = TextFormatter.colorString(data.getString(NAME, name));
        description = TextFormatter.colorString(data.getString(DESCRIPTION, description));
        enabled = data.getBoolean(ENABLED, enabled);
        setMaxLevel(data.getInt(MAX_LEVEL, maxLevel), data.getInt(MAX_TABLE_LEVEL, maxTableLevel));
        setGroup(data.getString(GROUP, group));
        setMinEnchantingLevel(data.getDouble(MIN_ENCHANTING_LEVEL, minEnchantingLevel));
        setEnchantLevelScaleFactor(data.getDouble(ENCHANT_LEVEL_SCALE_FACTOR, enchantLevelScaleFactor));
        setEnchantLevelBuffer(data.getDouble(ENCHANT_LEVEL_BUFFER, enchantLevelBuffer));
        setCombineCostPerLevel(data.getInt(COMBINE_COST_PER_LEVEL, combineCostPerLevel));
        setCanStack(data.getBoolean(STACKS, stacks));
        setTableEnabled(data.getBoolean(TABLE_ENABLED, tableEnabled));
        setWeight(data.getDouble(WEIGHT, weight));

        if (data.has(NATURAL_ITEMS)) {
            naturalItems.clear();
            addNaturalItems();
            naturalItems.addAll(data.getList(NATURAL_ITEMS).stream()
                    .map(line -> line.toUpperCase().replace(' ', '_'))
                    .map(Material::matchMaterial)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        if (data.isSection(MATERIAL)) {
            materialWeights.clear();
            final DataSection weightData = data.getSection(MATERIAL);
            weightData.keys().forEach(key -> {
                final Material material = Material.matchMaterial(key);
                if (material == null) {
                    plugin.getLogger().warning(key + " is not a valid material (in material weights for " + name);
                } else {
                    setWeight(material, weightData.getDouble(key));
                }
            });
        }

        if (data.isSection(EFFECT)) {
            final Settings settings = new Settings();
            final DataSection file = data.getSection(EFFECT);
            file.keys().forEach(key -> settings.set(key, file.get(key)));
            this.settings.keys().forEach(key -> settings.checkDefault(key, this.settings.get(key)));
            this.settings = settings;
        }
    }
}
