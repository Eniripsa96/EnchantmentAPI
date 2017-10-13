package com.sucy.enchant.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.enchant.EnchantmentAPI;
import com.sucy.enchant.api.CustomEnchantment;
import com.sucy.enchant.data.ConfigKey;
import com.sucy.enchant.data.Configuration;
import com.sucy.enchant.data.Enchantability;
import com.sucy.enchant.mechanics.EnchantingMechanics;
import com.sucy.enchant.vanilla.Vanilla;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.cmd.CmdGraph
 */
public class CmdGraph implements IFunction {

    private static final String INVALID_MATERIAL = "invalid-material";
    private static final String INVALID_ENCHANTMENT = "invalid-enchantment";
    private static final String INCOMPATIBLE_ENCHANTMENT = "incompatible-enchantment";

    private static final DecimalFormat FORMAT = new DecimalFormat("##0.0");

    private final EnchantingMechanics MECHANICS = new EnchantingMechanics();

    private final int maxEnchantsSetting = Configuration.amount(ConfigKey.MAX_ENCHANTMENTS);

    @Override
    public void execute(
            final ConfigurableCommand command,
            final Plugin plugin,
            final CommandSender sender,
            final String[] args) {

        if (args.length >= 2) {
            try {

                // Parse the item
                final Material mat = Material.getMaterial(args[0].toUpperCase());
                if (mat == null) {
                    command.sendMessage(sender, INVALID_MATERIAL, "That is not a valid material");
                    return;
                }

                final ItemStack item = new ItemStack(mat);

                // Make sure the enchantment can work on the item
                String name = args[1];
                for (int i = 2; i < args.length; i++) name += " " + args[i];
                final CustomEnchantment enchant = Vanilla.getEnchantment(name).orElse(EnchantmentAPI.getEnchantment(name));
                if (enchant == null) {
                    command.sendMessage(sender, INVALID_ENCHANTMENT, "&4That is not a valid enchantment");
                    return;
                }
                else if (!enchant.canEnchantOnto(item)) {
                    command.sendMessage(sender, INCOMPATIBLE_ENCHANTMENT, "&4That enchantment doesn't work on that item");
                    return;
                }

                // Run the computation task (usually takes 5-10ms)
                plugin.getServer()
                        .getScheduler()
                        .runTaskAsynchronously(plugin, () -> compute(enchant, sender, item));
            }
            catch (final Exception e) {
                // Do nothing
            }
        }
    }

    private int next;
    private int getNext() {
        return next++;
    }

    private double total(final Map<String, List<CustomEnchantment>> grouped, final String key, final Material material) {
        return grouped.get(key).stream().mapToDouble(e -> e.getWeight(material)).sum();
    }

    void compute(final CustomEnchantment enchant, final CommandSender sender, final ItemStack item) {
        final Player enchanter = (sender instanceof Player) ? (Player)sender : null;
        final int enchantability = Enchantability.determine(item.getType());

        // Part 1 - Selecting n weighted random enchantments
        final int maxModified = MECHANICS.maxLevel(30, enchantability);
        final List<Double> enchantWeights = new ArrayList<>();
        for (int i = 2; i <= maxModified; i++) {

            final List<CustomEnchantment> enchants = MECHANICS.getAllValidEnchants(item, enchanter, i, true);
            next = 0;
            final Map<String, List<CustomEnchantment>> grouped = enchants.stream().collect(
                    Collectors.groupingBy(e -> e.getGroup().equals(CustomEnchantment.DEFAULT_GROUP) ? "d" + getNext() : e.getGroup()));
            final List<String> groups = grouped.keySet().stream()
                    .filter(key -> !grouped.get(key).contains(enchant))
                    .collect(Collectors.toList());
            final List<Double> groupWeights = groups.stream()
                    .map(key -> total(grouped, key, item.getType()))
                    .collect(Collectors.toList());

            final double totalWeight = enchants.stream().mapToDouble(e -> e.getWeight(item.getType())).sum();
            final double targetGroupWeight = enchant.getGroup().equals(CustomEnchantment.DEFAULT_GROUP)
                    ? enchant.getWeight(item.getType())
                    : total(grouped, enchant.getGroup(), item.getType());
            final double enchantGroupWeight = enchant.getWeight(item.getType()) / targetGroupWeight;

            // Number of enchantment odds
            final int maxEnchants = Math.min(maxEnchantsSetting, grouped.size());
            final double[] numWeights = new double[maxEnchants];
            double remaining = 1.0;
            int lvl = i;
            for (int numEnchants = 0; numEnchants < maxEnchants; numEnchants++) {
                final double chance = 1 - (lvl + 1) * 0.02;
                numWeights[numEnchants] = remaining * (numEnchants < maxEnchants - 1 ? chance : 1);
                remaining *= 1 - chance;
                lvl /= 2;
            }

            // Selecting the enchantment odds
            double current = targetGroupWeight / totalWeight;
            double total = current * numWeights[0];
            final Set<Integer> set = new HashSet<>();
            for (int numEnchants = 1; numEnchants < maxEnchants; numEnchants++) {
                current += compute(groupWeights, totalWeight, targetGroupWeight, numEnchants, set);
                total += current * numWeights[numEnchants];
            }
            enchantWeights.add(total * enchantGroupWeight);
        }

        // Part 2 - Enchantability distribution
        final Map<Integer, Integer> enchantabilitySpread = MECHANICS.enchantabilitySpread(enchantability);
        final int enchantabilityWeight = MECHANICS.enchantabilityWeight(enchantability);
        final List<Double> modifierWeights = enchantabilitySpread.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(e -> (double)e.getValue() / enchantabilityWeight)
                .collect(Collectors.toList());

        // Part 3 - Uniform triangular distributed multiplier
        final List<double[]> result = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            final double[] weights = new double[enchant.getMaxTableLevel()];
            for (int k = 0; k < modifierWeights.size(); k++) {
                final double joined = enchantWeights.get(i + k - 1) * modifierWeights.get(k);
                for (int j = 1; j < weights.length; j++) {
                    weights[j - 1] += MECHANICS.chance(i + 1 + k, enchant, j) * joined;
                }
            }
            result.add(weights);
        }

        // Results - render graph
        render(sender, enchant, result);
    }

    double compute(final List<Double> weights, final double totalWeight, final double targetWeight, final int enchants, final Set<Integer> indices) {
        if (enchants == 0) return targetWeight / totalWeight;

        double total = 0;
        for (int i = 0; i < weights.size(); i++) {
            if (!indices.contains(i)) {
                indices.add(i);
                total += weights.get(i) * compute(weights, totalWeight - weights.get(i), targetWeight, enchants - 1, indices) / totalWeight;
                indices.remove(i);
            }
        }
        return total;
    }

    void render(final CommandSender sender, final CustomEnchantment enchant, final List<double[]> probabilities) {

        // Get the maximum probability
        double max = 0;
        for (final double[] data : probabilities) {
            for (final double prob : data) {
                max = Math.max(prob, max);
            }
        }
        max = Math.ceil(max * 1000) / 1000;

        // Construct the graph
        for (int i = 8; i >= 0; i--) {

            final ChatColor lc = i == 0 ? ChatColor.GRAY : ChatColor.DARK_GRAY;

            // Label the intervals
            final StringBuilder line = new StringBuilder(99)
                    .append(ChatColor.GOLD).append(FORMAT.format(i * max * 100 / 9))
                    .append("-").append(FORMAT.format((i + 1) * max * 100 / 9))
                    .append("%").append(lc).append("_");
            while (line.length() < 16) line.append("_");
            line.append(ChatColor.GRAY).append("|");

            // Print out the points
            for (final double[] data : probabilities) {

                // Search if any of the levels fell into the interval for the given level
                String piece = lc + "_";
                for (int k = 0; k < data.length; k++) {
                    if (data[k] > i * max / 9 && data[k] <= (i + 1) * max / 9) {
                        piece = ChatColor.getByChar((char)(49 + k % 6)) + "X";
                    }
                }
                line.append(piece);
            }

            // Send the line of the graph to the sender
            sender.sendMessage(line.toString());
        }

        // Output the level scale at the bottom
        sender.sendMessage(ChatColor.DARK_GRAY + "||__________" + ChatColor.GRAY + "|" + ChatColor.DARK_GRAY
                + "____" + ChatColor.GRAY + "5" + ChatColor.DARK_GRAY + "____" + ChatColor.GRAY + "10"
                + ChatColor.DARK_GRAY + "___" + ChatColor.GRAY + "15" + ChatColor.DARK_GRAY + "___"
                + ChatColor.GRAY + "20" + ChatColor.DARK_GRAY + "___" + ChatColor.GRAY + "25" + ChatColor.DARK_GRAY
                + "___" + ChatColor.GRAY + "30");
    }
}
