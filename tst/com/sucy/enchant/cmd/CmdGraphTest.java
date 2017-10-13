package com.sucy.enchant.cmd;

import com.google.common.collect.ImmutableList;
import com.rit.sucy.config.parse.DataSection;
import com.sucy.enchant.TestUtils;
import com.sucy.enchant.data.ConfigKey;
import com.sucy.enchant.data.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.cmd.CmdGraphTest
 */
public class CmdGraphTest {

    private static final List<Double> WEIGHTS = ImmutableList.of(2.0, 2.0, 4.0, 4.0, 8.0);
    private static final double TOTAL = WEIGHTS.stream().mapToDouble(Double::doubleValue).sum() + 4;

    private CmdGraph    subject;
    private DataSection data;

    @Before
    public void setUp() throws Exception {
        data = mock(DataSection.class);
        when(data.getInt(ConfigKey.MAX_ENCHANTMENTS.getKey())).thenReturn(3);
        TestUtils.set(Configuration.class, "data", data);
        subject = new CmdGraph();
    }

    @Test
    public void compute() {
        double one = 4.0 / TOTAL;
        double two = 1.0/33 + 1.0/15 + 1.0/12;

        assertEquals(one, subject.compute(WEIGHTS, TOTAL, 4, 0, new HashSet<>()), 0.00000001);
        assertEquals(two, subject.compute(WEIGHTS, TOTAL, 4, 1, new HashSet<>()), 0.00000001);

        double total = 0;
        for (int i = 0; i <= WEIGHTS.size(); i++) {
            total += subject.compute(WEIGHTS, TOTAL, 4, i, new HashSet<>());
        }
        assertEquals(1, total, 0.0000000001);
    }
}