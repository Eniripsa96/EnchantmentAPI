package com.sucy.enchant.listener;

import com.sucy.enchant.api.Enchantments;
import com.sucy.enchant.api.Tasks;
import com.sucy.enchant.data.PlayerEquips;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static com.sucy.enchant.util.Utils.isPresent;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.listener.ItemListener
 */
public class ItemListener extends BaseListener {

    private static final HashMap<UUID, Long> LAST_INTERACT_BLOCK  = new HashMap<>();
    private static final HashMap<UUID, Long> LAST_INTERACT_ENTITY = new HashMap<>();

    private static final int INTERACT_DELAY_MILLIS = 250;

    // ---- Tracking enchantments ---- //

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Enchantments.getEquipmentData(event.getPlayer());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        Enchantments.clearEquipmentData(event.getPlayer());
        LAST_INTERACT_BLOCK.remove(event.getPlayer().getUniqueId());
        LAST_INTERACT_ENTITY.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        Enchantments.getEquipmentData(event.getPlayer()).clearWeapon(event.getPlayer());
    }

    @EventHandler
    public void onBreak(final PlayerItemBreakEvent event) {
        final PlayerEquips equips = Enchantments.getEquipmentData(event.getPlayer());
        Tasks.schedule(() -> equips.updateWeapon(event.getPlayer().getInventory()));
    }

    @EventHandler
    public void onPickup(final EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            final PlayerEquips equips = Enchantments.getEquipmentData((Player) event.getEntity());
            Tasks.schedule(() -> equips.updateWeapon(((Player) event.getEntity()).getInventory()));
        }
    }

    @EventHandler
    public void onHeld(final PlayerItemHeldEvent event) {
        final PlayerEquips equips = Enchantments.getEquipmentData(event.getPlayer());
        Tasks.schedule(() -> equips.updateWeapon(event.getPlayer().getInventory()));
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        final PlayerEquips equips = Enchantments.getEquipmentData((Player) event.getPlayer());
        Tasks.schedule(() -> equips.update((Player) event.getPlayer()));
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && isPresent(item) && PlayerEquips.ARMOR_TYPES.contains(item.getType())) {
            final PlayerEquips equips = Enchantments.getEquipmentData(event.getPlayer());
            Tasks.schedule(() -> equips.update(event.getPlayer()));
        }
    }

    // ---- API Methods ---- //

    private boolean running = false;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHit(final EntityDamageByEntityEvent event) {
        if (running || event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            return;
        }

        running = true;
        try {
            final LivingEntity damager = getDamager(event);
            if (damager instanceof Player && event.getEntity() instanceof LivingEntity) {
                Enchantments.getEnchantments((Player) damager).forEach(
                        (enchant, level) -> enchant.applyOnHit(
                                damager,
                                (LivingEntity) event.getEntity(),
                                level,
                                event));
            }

            if (event.getEntity() instanceof Player && damager != null) {
                Enchantments.getEnchantments((Player) event.getEntity()).forEach(
                        (enchant, level) -> enchant.applyDefense((Player) event.getEntity(), damager, level, event));
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        running = false;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onShoot(final ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        final Player shooter = (Player) event.getEntity().getShooter();
        Enchantments.getEnchantments(shooter)
                .forEach((enchant, level) -> enchant.applyProjectile(shooter, level, event));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(final BlockBreakEvent event) {
        Enchantments.getEnchantments(event.getPlayer()).forEach(
                (enchant, level) -> enchant.applyBreak(event.getPlayer(), event.getBlock(), level, event));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteractBlock(final PlayerInteractEvent event) {
        if (LAST_INTERACT_BLOCK.getOrDefault(event.getPlayer().getUniqueId(), 0L) > System.currentTimeMillis()) {
            return;
        }

        LAST_INTERACT_BLOCK.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + INTERACT_DELAY_MILLIS);
        Enchantments.getEnchantments(event.getPlayer()).forEach(
                (enchant, level) -> enchant.applyInteractBlock(event.getPlayer(), level, event));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteractEntity(final PlayerInteractEntityEvent event) {
        if (LAST_INTERACT_ENTITY.getOrDefault(event.getPlayer().getUniqueId(), 0L) > System.currentTimeMillis()) {
            return;
        }

        LAST_INTERACT_ENTITY.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + INTERACT_DELAY_MILLIS);
        Enchantments.getEnchantments(event.getPlayer()).forEach(
                (enchant, level) -> enchant.applyInteractEntity(event.getPlayer(), level, event));
    }
}
