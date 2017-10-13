package com.sucy.enchant;

import com.sucy.enchant.api.CustomEnchantment;
import com.sucy.enchant.vanilla.VanillaEnchantment;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.TestUtils
 */
public class TestUtils {

    public static final EnchantmentAPI API = wrapAPI();

    public static Enchantment createVanillaEnchantment(final String name) {
        final VanillaEnchantment enchant = mock(VanillaEnchantment.class);
        when(enchant.getName()).thenReturn(name);
        API.register(enchant);

        final Enchantment enchantment = mock(Enchantment.class);
        when(enchantment.getName()).thenReturn(name);
        return enchantment;
    }

    public static CustomEnchantment createEnchantment(final String name) {
        final CustomEnchantment enchant = mock(CustomEnchantment.class);
        when(enchant.getName()).thenReturn(name);
        API.register(enchant);
        return enchant;
    }

    public static void tearDown() throws Exception {
        getRegistry().clear();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, CustomEnchantment> getRegistry() throws Exception {
        return ((Map<String, CustomEnchantment>)getField(EnchantmentAPI.class, "ENCHANTMENTS").get(null));
    }

    public static void register(final CustomEnchantment enchantment) throws Exception {
        getRegistry().put(enchantment.getName(), enchantment);
    }

    public static void set(final Class<?> clazz, final String fieldName, final Object value) throws Exception {
        getField(clazz, fieldName).set(null, value);
    }

    public static <T> Field getField(final Class<T> clazz, final String name) throws Exception {
        final Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);

        final Field modifier = Field.class.getDeclaredField("modifiers");
        modifier.setAccessible(true);
        modifier.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        return field;
    }

    private static EnchantmentAPI wrapAPI() {
        final EnchantmentAPI api = mock(EnchantmentAPI.class);
        doCallRealMethod().when(api).register(anyVararg());
        return api;
    }
}
