package com.sucy.enchant.listener;

import com.sucy.enchant.EnchantmentAPI;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.listener.BaseListener
 */
public abstract class BaseListener implements Listener {
    public void init(final EnchantmentAPI plugin) { }
    public void cleanUp(final EnchantmentAPI plugin) { }

    /**
     * Retrieves a damager from an entity damage event which will get the
     * shooter of projectiles if it was a projectile hitting them or
     * converts the Entity damager to a LivingEntity if applicable.
     *
     * @param event event to grab the damager from
     *
     * @return LivingEntity damager of the event or null if not found
     */
    LivingEntity getDamager(final EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof LivingEntity)
        {
            return (LivingEntity) event.getDamager();
        }
        else if (event.getDamager() instanceof Projectile)
        {
            final Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof LivingEntity)
            {
                return (LivingEntity) projectile.getShooter();
            }
        }
        return null;
    }
}
