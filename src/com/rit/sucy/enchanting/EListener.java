package com.rit.sucy.enchanting;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.config.LanguageNode;
import com.rit.sucy.config.RootConfig;
import com.rit.sucy.config.RootNode;
import com.rit.sucy.service.ENameParser;
import com.rit.sucy.service.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Listens for events and passes them onto enchantments
 */
public class EListener implements Listener {

    /**
     * Plugin reference
     */
    EnchantmentAPI plugin;

    /**
     * Tasks for updating enchanting tables
     */
    Hashtable<String, TableTask> tasks = new Hashtable<String, TableTask>();

    /**
     * Whether or not to excuse the next player attack event
     */
    public static boolean excuse = false;

    /**
     * Basic constructor that registers this listener
     *
     * @param plugin plugin to register this listener to
     */
    public EListener(EnchantmentAPI plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    /**
     * Event for offensive enchantments
     *
     * @param event the event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {

        if (excuse) {
            excuse = false;
            return;
        }

        // Rule out cases where enchantments don't apply
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity damaged = (LivingEntity)event.getEntity();
        LivingEntity damager = event.getDamager() instanceof LivingEntity ? (LivingEntity) event.getDamager()
                : event.getDamager() instanceof Projectile ? ((Projectile)event.getDamager()).getShooter()
                : null;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                && event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) return;
        if (damager != null) {

            // Apply offensive enchantments
            for (Map.Entry<CustomEnchantment, Integer> entry : getValidEnchantments(getItems(damager)).entrySet()) {
                entry.getKey().applyEffect(damager, damaged, entry.getValue(), event);
            }
        }

        // Apply defensive enchantments
        for (Map.Entry<CustomEnchantment, Integer> entry : getValidEnchantments(getItems(damaged)).entrySet()) {
            entry.getKey().applyDefenseEffect(damaged, damager, entry.getValue(), event);
        }
    }

    /**
     * Event for defensive enchantments
     *
     * @param event the event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamaged(EntityDamageEvent event) {

        // Rule out cases where enchantments don't apply
        if (!(event.getEntity() instanceof LivingEntity)) return;

        // Apply enchantments
        LivingEntity damaged = (LivingEntity)event.getEntity();
        for (Map.Entry<CustomEnchantment, Integer> entry : getValidEnchantments(getItems(damaged)).entrySet()) {
            entry.getKey().applyDefenseEffect(damaged, null, entry.getValue(), event);
        }
    }

    /**
     * Event for defensive enchantments
     *
     * @param event the event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamaged(EntityDamageByBlockEvent event) {

        // Rule out cases where enchantments don't apply
        if (!(event.getEntity() instanceof LivingEntity)) return;

        // Apply enchantments
        LivingEntity damaged = (LivingEntity)event.getEntity();
        for (Map.Entry<CustomEnchantment, Integer> entry : getValidEnchantments(getItems(damaged)).entrySet()) {
            entry.getKey().applyDefenseEffect(damaged, null, entry.getValue(), event);
        }
    }


    /**
     * Event for tool enchantments
     *
     * @param event the event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageBlock(BlockDamageEvent event) {

        // Apply enchantments
        for (Map.Entry<CustomEnchantment, Integer> entry : getValidEnchantments(getItems(event.getPlayer())).entrySet()) {
            entry.getKey().applyToolEffect(event.getPlayer(), event.getBlock(), entry.getValue(), event);
        }
    }

    /**
     * Event for tool enchantments
     *
     * @param event the event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {

        // Apply enchantments
        for (Map.Entry<CustomEnchantment, Integer> entry : getValidEnchantments(getItems(event.getPlayer())).entrySet()) {
            entry.getKey().applyToolEffect(event.getPlayer(), event.getBlock(), entry.getValue(), event);
        }

        if (event.getBlock().getType() == Material.ENCHANTMENT_TABLE) {
            for (TableTask task : tasks.values()) {
                task.restore();
            }
        }
    }

    /**
     * Event for miscellaneous enchantments and Equip effects
     *
     * @param event the event details
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {

        // Apply enchantments
        for (Map.Entry<CustomEnchantment, Integer> entry : getValidEnchantments(getItems(event.getPlayer())).entrySet()) {
            entry.getKey().applyMiscEffect(event.getPlayer(), entry.getValue(), event);
        }

        new EEquip(event.getPlayer()).runTaskLater(plugin, 1);
    }

    /**
     * Event for entity interaction effects
     *
     * @param event the event details
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEntityEvent event) {
        for (Map.Entry<CustomEnchantment, Integer> entry : getValidEnchantments(getItems(event.getPlayer())).entrySet()) {
            entry.getKey().applyEntityEffect(event.getPlayer(), entry.getValue(), event);
        }
    }

    /**
     * Event for Equip and Unequip effects
     *
     * @param event event details
     */
    @EventHandler (priority =  EventPriority.MONITOR, ignoreCancelled = true)
    public void onEquip(InventoryClickEvent event) {
        new EEquip(plugin.getServer().getPlayer(event.getWhoClicked().getName())).runTaskLater(plugin, 1);
    }

    /**
     * Event for Equip and Unequip events
     *
     * @param event event details
     */
    @EventHandler
    public void onBreak(PlayerItemBreakEvent event) {
        new EEquip(event.getPlayer()).runTaskLater(plugin, 1);
    }

    /**
     * Equipment loading event
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConnect(PlayerJoinEvent event) {
        EEquip.loadPlayer(event.getPlayer());
    }

    /**
     * Equipment loading event
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDisconnect(PlayerQuitEvent event) {
        EEquip.clearPlayer(event.getPlayer());
        if (tasks.contains(event.getPlayer().getName())) {
            tasks.remove(event.getPlayer().getName());
        }
    }

    /**
     * Projectile launch effects for enchantments
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectile(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() == null)
            return;
        for (Map.Entry<CustomEnchantment, Integer> entry : getValidEnchantments(getItems(event.getEntity().getShooter())).entrySet()) {
            entry.getKey().applyProjectileEffect(event.getEntity().getShooter(), entry.getValue(), event);
        }
    }

    /**
     * Updates inventory for enchanting tables
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (event.getInventory().getType() == InventoryType.ENCHANTING && event.isShiftClick()) {
            if (tasks.containsKey(event.getWhoClicked().getName())) {
                tasks.get(event.getWhoClicked().getName()).restore();
            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ENCHANTING && event.getPlayer().hasPermission(PermissionNode.TABLE.getNode())) {
            tasks.put(event.getPlayer().getName(), new TableTask(plugin, plugin.getServer().getPlayer(event.getPlayer().getName())));
        }
    }

    /**
     * Restores any items when an enchanting table is closed
     *
     * @param event event details
     */
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() instanceof EnchantingInventory) {
            if (tasks.containsKey(event.getPlayer().getName())) {
                tasks.get(event.getPlayer().getName()).restore();
                tasks.get(event.getPlayer().getName()).cancel();
                tasks.remove(event.getPlayer().getName());
            }
        }
    }

    /**
     * Enchantment table integration
     *
     * @param event event details
     */
    @EventHandler (ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {

        // Make sure the player can enchant using custom mechanics
        if (!event.getEnchanter().hasPermission(PermissionNode.TABLE.getNode()) || !tasks.containsKey(event.getEnchanter().getName()))
            return;
        event.setCancelled(true);

        // Make sure the item can be enchanted
        if (EnchantmentAPI.getEnchantments(event.getItem()).size() > 0) return;
        if (event.getEnchanter().getLevel() < event.getExpLevelCost()
                && event.getEnchanter().getGameMode() != GameMode.CREATIVE) return;

        // Make sure only one item is enchanted
        ItemStack storedItem = tasks.get(event.getEnchanter().getName()).stored;
        if (storedItem.getAmount() > 1) {
            storedItem.setAmount(storedItem.getAmount() - 1);
            event.getEnchanter().getInventory().addItem(storedItem.clone());
            storedItem.setAmount(1);
        }

        // Enchant the item
        int maxEnchants = plugin.getModuleForClass(RootConfig.class).getInt(RootNode.MAX_ENCHANTS);
        EnchantResult result = EEnchantTable.enchant(event.getEnchanter(), storedItem, event.getExpLevelCost(), maxEnchants);
        ItemStack item = result.getItem();

        // Make sure a result was created
        if (item == null)
            return;

        // Clear the table
        event.getInventory().clear();
        event.getEnchantsToAdd().clear();

        // Random name generation
        boolean randomName = plugin.getModuleForClass(RootConfig.class).getBoolean(RootNode.ITEM_LORE);
        if (randomName && event.getEnchanter().hasPermission(PermissionNode.NAMES.getNode())) {
            String name = "" + ChatColor.COLOR_CHAR;
            int random = (int)(Math.random() * 14) + 49;
            if (random > 57) random += 39;
            String format = plugin.getConfig().getStringList(LanguageNode.NAME_FORMAT.getFullPath()).get(0);
            format = format.replace("{adjective}", plugin.getAdjective(result.getLevel() / 11 + 1 > 4 ? 4 : result.getLevel() / 11 + 1));
            format = format.replace("{weapon}", plugin.getWeapon(item.getType().name()));
            format = format.replace("{suffix}", plugin.getSuffix(result.getFirstEnchant()));
            name += (char)random + format;
            ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : plugin.getServer().getItemFactory().getItemMeta(item.getType());
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        // Give the item
        event.getInventory().addItem(item);

        // Level cost
        if (event.getEnchanter().getGameMode() != GameMode.CREATIVE)
            event.getEnchanter().setLevel(event.getEnchanter().getLevel() - event.getExpLevelCost());
    }

    /**
     * Doesn't show options for items with custom enchantments
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasLore()
                && event.getItem().getItemMeta().getLore().contains(tasks.get(event.getEnchanter().getName()).cantEnchant)) {
            event.setCancelled(true);
        }
    }

    /**
     * Gets a list of valid enchantments from a set of items
     *
     * @param items the list of items to check for valid enchantments
     * @return      the valid enchantments and their corresponding enchantment levels
     */
    private Map<CustomEnchantment, Integer> getValidEnchantments(ArrayList<ItemStack> items) {
        Map<CustomEnchantment, Integer> validEnchantments = new HashMap<CustomEnchantment, Integer>();
        for (ItemStack item : items) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            if (!meta.hasLore()) continue;
            for (String lore : meta.getLore()) {
                String name = ENameParser.parseName(lore);
                int level = ENameParser.parseLevel(lore);
                if (name == null) continue;
                if (level == 0) continue;
                if (EnchantmentAPI.isRegistered(name)) {
                    validEnchantments.put(EnchantmentAPI.getEnchantment(name), level);
                }
            }
        }
        return validEnchantments;
    }

    /**
     * Retrieves a list of equipment on the entity that have at least some lore
     *
     * @param entity the entity wearing the equipment
     * @return       the list of all equipment with lore
     */
    private ArrayList<ItemStack> getItems(LivingEntity entity) {
        ItemStack[] armor = entity.getEquipment().getArmorContents();
        ItemStack weapon = entity.getEquipment().getItemInHand();
        ArrayList<ItemStack> items = new ArrayList<ItemStack>(Arrays.asList(armor));
        items.add(weapon);

        return items;
    }
}
