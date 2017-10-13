package com.sucy.enchant.api;

import com.google.common.collect.ImmutableList;
import com.sucy.enchant.TestUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.api.CustomEnchantmentTest
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomEnchantmentTest {

    @Mock
    private ItemMeta meta;
    @Mock
    private ItemStack item;

    private List<String> lore;

    private CustomEnchantment subject;

    @Before
    public void setUp() throws Exception {
        subject = new TestEnchant();
        TestUtils.register(subject);

        lore = new ArrayList<>();
        when(item.getType()).thenReturn(Material.DIAMOND_SWORD);
        when(item.getItemMeta()).thenReturn(meta);
        when(meta.getLore()).thenReturn(lore);
        when(meta.hasLore()).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void computeLevel() throws Exception {
        // Use data from ARROW_DAMAGE to have a solid baseline to compare against
        subject.setMaxLevel(10, 5);
        subject.setMinEnchantingLevel(1);
        subject.setEnchantLevelScaleFactor(10);
        subject.setEnchantLevelBuffer(5);

        assertEquals(0, subject.computeLevel(0));
        assertEquals(1, subject.computeLevel(1));
        assertEquals(1, subject.computeLevel(10));
        assertEquals(2, subject.computeLevel(11));
        assertEquals(2, subject.computeLevel(20));
        assertEquals(3, subject.computeLevel(21));
        assertEquals(3, subject.computeLevel(30));
        assertEquals(4, subject.computeLevel(31));
        assertEquals(4, subject.computeLevel(40));
        assertEquals(5, subject.computeLevel(41));
        assertEquals(5, subject.computeLevel(50));
        assertEquals(5, subject.computeLevel(56));
        assertEquals(0, subject.computeLevel(57));
    }

    @Test
    public void canEnchantOnto() throws Exception {
        subject.addNaturalItems(ItemSet.AXES.getItems());

        assertTrue(subject.canEnchantOnto(new ItemStack(Material.DIAMOND_AXE)));
        assertTrue(subject.canEnchantOnto(new ItemStack(Material.STONE_AXE)));
        assertTrue(subject.canEnchantOnto(new ItemStack(Material.BOOK)));
        assertTrue(subject.canEnchantOnto(new ItemStack(Material.ENCHANTED_BOOK)));
        assertFalse(subject.canEnchantOnto(new ItemStack(Material.DIAMOND_SWORD)));
        assertFalse(subject.canEnchantOnto(new ItemStack(Material.LAPIS_BLOCK)));
        assertFalse(subject.canEnchantOnto(new ItemStack(Material.BOW)));
        assertFalse(subject.canEnchantOnto(new ItemStack(Material.STONE_PICKAXE)));
    }

    @Test
    public void conflictsWith_default() throws Exception {
        final TestEnchant other = new TestEnchant();
        assertFalse(subject.conflictsWith(other, true));
        assertFalse(subject.conflictsWith(other, false));
    }

    @Test
    public void conflictsWith_same() throws Exception {
        assertTrue(subject.conflictsWith(subject, true));
        assertFalse(subject.conflictsWith(subject, false));
    }

    @Test
    public void conflictsWith_differentGroups() throws Exception {
        final TestEnchant other = new TestEnchant();
        subject.setGroup("subject");
        other.setGroup("other");

        assertFalse(subject.conflictsWith(other, true));
        assertFalse(subject.conflictsWith(other, false));
    }

    @Test
    public void conflictsWith_sameGroups() throws Exception {
        final TestEnchant other = new TestEnchant();
        subject.setGroup("same");
        other.setGroup("same");

        assertTrue(subject.conflictsWith(other, true));
        assertTrue(subject.conflictsWith(other, false));
    }

    @Test
    public void conflictsWith_list() throws Exception {
        assertTrue(subject.conflictsWith(ImmutableList.of(subject), true));
        assertFalse(subject.conflictsWith(ImmutableList.of(), true));
    }

    @Test
    public void conflictsWith_array() throws Exception {
        assertTrue(subject.conflictsWith(true, subject));
        assertFalse(subject.conflictsWith(true));
    }

    @Test
    public void addToItem_happyCase() throws Exception {
        subject.addToItem(item, 2);
        assertEquals(1, lore.size());
        assertEquals(ChatColor.GRAY + "test II", lore.get(0));
        verify(meta).setLore(lore);
        verify(item).setItemMeta(meta);
    }

    @Test
    public void addToItem_toTop() throws Exception {
        lore.add("Line");
        lore.add("Another Line");
        subject.addToItem(item, 2);
        assertEquals(3, lore.size());
        assertEquals(ChatColor.GRAY + "test II", lore.get(0));
        verify(meta).setLore(lore);
        verify(item).setItemMeta(meta);
    }

    @Test
    public void addToItem_alreadyExists() throws Exception {
        lore.add(ChatColor.GRAY + "test IV");
        lore.add("junk");
        lore.add("Another Line");
        subject.addToItem(item, 2);
        assertEquals(3, lore.size());
        assertEquals(ChatColor.GRAY + "test II", lore.get(0));
        assertEquals("junk", lore.get(1));
        assertEquals("Another Line", lore.get(2));
        verify(meta).setLore(lore);
        verify(item).setItemMeta(meta);
    }

    @Test
    public void addToItem_noLore() throws Exception {
        when(meta.hasLore()).thenReturn(false);
        lore.add(ChatColor.GRAY + "test IV");
        lore.add("junk");
        lore.add("Another Line");
        subject.addToItem(item, 2);
        verify(meta).setLore(ImmutableList.of(ChatColor.GRAY + "test II"));
        verify(item).setItemMeta(meta);
    }

    @Test
    public void addToEnchantment() throws Exception {
    }

    @Test
    public void removeFromItem() throws Exception {
    }

    @Test
    public void hasPermission() throws Exception {
    }

    @Test
    public void getPermission() throws Exception {
    }

    @Test
    public void applyOnHit() throws Exception {
    }

    @Test
    public void applyDefense() throws Exception {
    }

    @Test
    public void applyBreak() throws Exception {
    }

    @Test
    public void applyEquip() throws Exception {
    }

    @Test
    public void applyUnequip() throws Exception {
    }

    @Test
    public void applyInteractBlock() throws Exception {
    }

    @Test
    public void applyInteractEntity() throws Exception {
    }

    @Test
    public void applyProjectile() throws Exception {
    }

    @Test
    public void equals() throws Exception {
    }

    @Test
    public void compareTo() throws Exception {
    }

    @Test
    public void getSaveFolder() throws Exception {
    }

    @Test
    public void save() throws Exception {
    }

    @Test
    public void load() throws Exception {
    }

    private static class TestEnchant extends CustomEnchantment {
        TestEnchant() { super("test", "description"); }
    }
}