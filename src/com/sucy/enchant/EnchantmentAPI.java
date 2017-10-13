package com.sucy.enchant;

import com.rit.sucy.commands.CommandManager;
import com.sucy.enchant.api.CustomEnchantment;
import com.sucy.enchant.api.EnchantPlugin;
import com.sucy.enchant.api.EnchantmentRegistry;
import com.sucy.enchant.api.Enchantments;
import com.sucy.enchant.cmd.Commands;
import com.sucy.enchant.data.ConfigKey;
import com.sucy.enchant.data.Configuration;
import com.sucy.enchant.data.Enchantability;
import com.sucy.enchant.listener.AnvilListener;
import com.sucy.enchant.listener.BaseListener;
import com.sucy.enchant.listener.EnchantListener;
import com.sucy.enchant.listener.FishingListener;
import com.sucy.enchant.listener.ItemListener;
import com.sucy.enchant.skillapi.SkillAPIHook;
import com.sucy.enchant.vanilla.VanillaData;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.EnchantmentAPI
 */
public class EnchantmentAPI extends JavaPlugin implements EnchantmentRegistry {

    private static final Map<String, CustomEnchantment> ENCHANTMENTS = new HashMap<>();

    private final List<BaseListener> listeners = new ArrayList<>();

    private static EnchantmentAPI enabled;

    @Override
    public void onEnable() {
        if (enabled != null) throw new IllegalStateException("Cannot enable multiple times!");
        enabled = this;

        Configuration.reload(this);
        Enchantability.init(this);

        registerEnchantments();
        register(new ItemListener(), true);
        register(new EnchantListener(), true);
        register(new AnvilListener(), true);
        register(new FishingListener(), Configuration.using(ConfigKey.CUSTOM_FISHING));
        Commands.init(this);
    }

    @Override
    public void onDisable() {
        if (enabled == null) throw new IllegalStateException("Plugin not enabled!");
        enabled = null;

        CommandManager.unregisterCommands(this);
        ENCHANTMENTS.clear();
        listeners.forEach(listener -> listener.cleanUp(this));
        listeners.clear();
        HandlerList.unregisterAll(this);
        Enchantments.clearAllEquipmentData();
    }

    private void register(final BaseListener listener, final boolean condition) {
        if (condition) {
            listeners.add(listener);
            listener.init(this);
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * @param name enchantment name
     * @return true if the enchantment is registered successfully, false otherwise
     */
    public static boolean isRegistered(final String name) {
        return ENCHANTMENTS.containsKey(name.toLowerCase());
    }

    /**
     * @param name name of the enchantment (not case-sensitive)
     * @return enchantment with the provided name
     */
    public static CustomEnchantment getEnchantment(final String name) {
        return ENCHANTMENTS.get(name.toLowerCase());
    }

    /**
     * @return collection of all registered enchantments including vanilla enchantments
     */
    public static Collection<CustomEnchantment> getRegisteredEnchantments() {
        return ENCHANTMENTS.values();
    }

    /**
     * Registers enchantments with the API
     *
     * @param enchantments enchantments to register
     */
    @Override
    public void register(final CustomEnchantment... enchantments) {
        for (final CustomEnchantment enchantment : enchantments) {
            final String key = enchantment.getName().toLowerCase();
            if (ENCHANTMENTS.containsKey(key)) {
                getLogger().warning("Duplicate enchantment name \"" + enchantment.getName() + "\" was found");
                continue;
            }

            Objects.requireNonNull(enchantment, "Cannot register a null enchantment");
            enchantment.load(this);
            enchantment.save(this);
            ENCHANTMENTS.put(key, enchantment);
        }
    }

    private void registerEnchantments() {
        for (final VanillaData vanillaData : VanillaData.values()) {
            register(vanillaData.getEnchantment());
        }

        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin instanceof EnchantPlugin) {
                try {
                    ((EnchantPlugin) plugin).registerEnchantments(this);
                } catch (final Exception ex) {
                    getLogger().warning(plugin.getName() + " failed to register enchantments. Send the error to the author.");
                    ex.printStackTrace();
                }
            }
        }

        SkillAPIHook.getEnchantments(this).forEach(this::register);
    }
}
