package com.sucy.enchant.api;

import com.sucy.enchant.TestUtils;
import org.bukkit.entity.LivingEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Clock;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.api.CooldownsTest
 */
@RunWith(MockitoJUnitRunner.class)
public class CooldownsTest {

    private static final String ENCHANT_NAME = "enchantName";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private Clock clock;
    @Mock
    private CustomEnchantment enchantment;
    @Mock
    private LivingEntity user;
    @Mock
    private Settings settings;

    @Before
    public void setUp() throws Exception {
        TestUtils.set(Cooldowns.class, "clock", clock);

        when(enchantment.getName()).thenReturn(ENCHANT_NAME);
        when(user.getUniqueId()).thenReturn(USER_ID);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void configure() throws Exception {
        Cooldowns.configure(settings, 12, 23);

        verify(settings).set("cooldown", 12, 23);
    }

    @Test
    public void secondsLeft() throws Exception {
        when(clock.millis()).thenReturn(100L, 1100L);
        when(settings.get("cooldown", 2)).thenReturn(15.0);

        Cooldowns.start(enchantment, user);
        final int result = Cooldowns.secondsLeft(enchantment, user, settings, 2);
        assertEquals(result, 14);
    }

    @Test
    public void secondsLeft_roundedUp() throws Exception {
        when(clock.millis()).thenReturn(100L, 1000L);
        when(settings.get("cooldown", 2)).thenReturn(1.0);

        Cooldowns.start(enchantment, user);
        final int result = Cooldowns.secondsLeft(enchantment, user, settings, 2);
        assertEquals(result, 1);
    }

    @Test
    public void onCooldown() throws Exception {
        when(clock.millis()).thenReturn(100L, 1100L);
        when(settings.get("cooldown", 2)).thenReturn(15.0);

        Cooldowns.start(enchantment, user);
        final boolean result = Cooldowns.onCooldown(enchantment, user, settings, 2);
        assertTrue(result);
    }

    @Test
    public void onCooldown_exact() throws Exception {
        when(clock.millis()).thenReturn(100L, 15100L);
        when(settings.get("cooldown", 2)).thenReturn(15.0);

        Cooldowns.start(enchantment, user);
        final boolean result = Cooldowns.onCooldown(enchantment, user, settings, 2);
        assertFalse(result);
    }

    @Test
    public void onCooldown_offCooldown() throws Exception {
        when(clock.millis()).thenReturn(100L, 20100L);
        when(settings.get("cooldown", 2)).thenReturn(15.0);

        Cooldowns.start(enchantment, user);
        final boolean result = Cooldowns.onCooldown(enchantment, user, settings, 2);
        assertFalse(result);
    }

    @Test
    public void reduce() throws Exception {
        when(clock.millis()).thenReturn(100L, 10100L);
        when(settings.get("cooldown", 2)).thenReturn(15.0);

        Cooldowns.start(enchantment, user);
        Cooldowns.reduce(enchantment, user, 6000);
        final boolean result = Cooldowns.onCooldown(enchantment, user, settings, 2);
        assertFalse(result);
    }

    @Test
    public void reduceNegative() throws Exception {
        when(clock.millis()).thenReturn(100L, 20100L);
        when(settings.get("cooldown", 2)).thenReturn(15.0);

        Cooldowns.start(enchantment, user);
        Cooldowns.reduce(enchantment, user, -6000);
        final boolean result = Cooldowns.onCooldown(enchantment, user, settings, 2);
        assertTrue(result);
    }
}