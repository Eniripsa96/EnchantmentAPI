package com.sucy.enchant.listener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.rit.sucy.config.CommentedConfig;
import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.items.ItemManager;
import com.sucy.enchant.EnchantmentAPI;
import com.sucy.enchant.api.Enchantments;
import com.sucy.enchant.api.Tasks;
import com.sucy.enchant.api.ItemSet;
import com.sucy.enchant.data.ConfigKey;
import com.sucy.enchant.data.Configuration;
import com.sucy.enchant.mechanics.EnchantResult;
import com.sucy.enchant.mechanics.EnchantingMechanics;
import com.sucy.enchant.api.GlowEffects;
import com.sucy.enchant.data.Path;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static com.sucy.enchant.util.Utils.isPresent;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.listener.EnchantListener
 */
public class EnchantListener extends BaseListener {

    private static final String DATA_FILE = "seeds";

    private final Map<UUID, Long> enchantSeeds = new HashMap<>();
    private final Map<UUID, ItemStack> placeholders = new HashMap<>();
    private final Map<UUID, int[]> offers = new HashMap<>();

    private final EnchantingMechanics mechanics = new EnchantingMechanics();

    private final Random random = new Random();

    private final boolean nonEnchantables = Configuration.using(ConfigKey.NON_ENCHANTABLES);

    @Override
    public void init(final EnchantmentAPI plugin) {
        final DataSection data = new CommentedConfig(plugin, Path.DATA_FOLDER + DATA_FILE).getConfig();
        data.keys().forEach(key -> enchantSeeds.put(UUID.fromString(key), Long.parseLong(data.getString(key))));
    }

    @Override
    public void cleanUp(final EnchantmentAPI plugin) {
        final CommentedConfig config = new CommentedConfig(plugin, Path.DATA_FOLDER + DATA_FILE);
        final DataSection data = config.getConfig();
        data.clear();

        enchantSeeds.forEach((uuid, seed) -> data.set(uuid.toString(), Long.toString(seed)));
        config.save();
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        enchantSeeds.computeIfAbsent(event.getPlayer().getUniqueId(), uuid -> random.nextLong());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrepareEnchant(final PrepareItemEnchantEvent event) {
        final ItemStack item = getItem(event.getItem(), event.getEnchanter());
        final long seed = enchantSeeds.get(event.getEnchanter().getUniqueId());
        offers.put(event.getEnchanter().getUniqueId(), new int[] {
                event.getOffers()[0].getCost(), 1,
                event.getOffers()[1].getCost(), 2,
                event.getOffers()[2].getCost(), 3
        });
        for (final EnchantmentOffer offer : event.getOffers()) {
            final EnchantResult result = mechanics.generateEnchantments(
                    event.getEnchanter(), item, offer.getCost(), true, seed);
            result.getFirstVanillaEnchantment().ifPresent(enchant -> {
                offer.setEnchantment(enchant.getEnchantment());
                offer.setEnchantmentLevel(result.getFirstVanillaLevel());
            });
        }
    }

    private ItemStack getItem(final ItemStack fromEvent, final Player enchanter) {
        return placeholders.getOrDefault(enchanter.getUniqueId(), fromEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnchant(final EnchantItemEvent event) {
        final ItemStack item = getItem(event.getItem(), event.getEnchanter());
        final long seed = enchantSeeds.get(event.getEnchanter().getUniqueId());
        final EnchantResult result = mechanics.generateEnchantments(
                event.getEnchanter(), item, event.getExpLevelCost(), true, seed);

        placeholders.remove(event.getEnchanter().getUniqueId());
        event.getEnchantsToAdd().clear();
        result.getEnchantments().forEach((enchant, level) -> enchant.addToItem(item, level));
        GlowEffects.finalize(item);
        enchantSeeds.put(event.getEnchanter().getUniqueId(), random.nextLong());
        event.getInventory().setItem(0, item);
        event.setCancelled(true);

        if (event.getEnchanter().getGameMode() != GameMode.CREATIVE) {
            int cost = 0;
            final int[] tiers = offers.get(event.getEnchanter().getUniqueId());
            for (int i = 0; i < 6; i += 2) {
                if (tiers[i] == event.getExpLevelCost()) cost = tiers[i + 1];
            }
            event.getEnchanter().setLevel(event.getEnchanter().getLevel() - cost);
            event.getInventory().removeItem(new ItemStack(Material.INK_SACK, cost, (short) 4));
        }
    }

    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (!nonEnchantables) {
            return;
        }

        if (event.getInventory().getType() == InventoryType.ENCHANTING) {
            if (event.getRawSlot() == 0 && placeholders.containsKey(event.getWhoClicked().getUniqueId())) {
                event.getInventory().setItem(0, placeholders.remove(event.getWhoClicked().getUniqueId()));
            }

            Tasks.schedule(() -> {
                final ItemStack item = event.getInventory().getItem(0);
                if (isPresent(item) && !ENCHANTABLES.contains(item.getType())
                        && Enchantments.getAllEnchantments(item).isEmpty()
                        && mechanics.hasValidEnchantments(item)
                        && !placeholders.containsKey(event.getWhoClicked().getUniqueId())) {
                    placeholders.put(event.getWhoClicked().getUniqueId(), item);
                    event.getInventory().setItem(0, wrap(item));
                }
            });
        }
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (placeholders.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().getInventory().addItem(placeholders.get(event.getPlayer().getUniqueId()));
            event.getInventory().clear();
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        offers.remove(event.getPlayer().getUniqueId());
        if (placeholders.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().closeInventory();
        }
    }

    private ItemStack wrap(final ItemStack item) {
        final ItemStack wrapper = new ItemStack(Material.BOOK);
        final ItemMeta meta = wrapper.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Enchanting");
        meta.setLore(ImmutableList.of(ItemManager.getVanillaName(item)));
        wrapper.setItemMeta(meta);
        return wrapper;
    }

    private static final Set<Material> ENCHANTABLES = ImmutableSet.<Material>builder()
            .add(ItemSet.ALL.getItems())
            .add(Material.BOOK)
            .add(Material.ENCHANTED_BOOK)
            .build();
}
