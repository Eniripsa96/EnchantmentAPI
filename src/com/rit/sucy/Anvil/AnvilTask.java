package com.rit.sucy.Anvil;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Timer task to handle updating anvil results
 * - You should not use this class -
 */
public class AnvilTask extends BukkitRunnable {

    private ItemStack[] contents;
    private AnvilView anvil;

    /**
     * @param plugin plugin reference
     * @param view   anvil view
     */
    public AnvilTask(Plugin plugin, AnvilView view) {
        this.anvil = view;
        contents = view.getInputSlots();
        runTaskTimer(plugin, 2, 2);
    }

    /**
     * Gets the view that the timer is handling
     *
     * @return anvil view
     */
    public AnvilView getView() {
        return anvil;
    }

    /**
     * Updates the anvil output
     */
    public void run() {
        ItemStack[] input = anvil.getInputSlots();

        if (input[0] != contents[0] || input[1] != contents[1]) {
            if (anvil instanceof MainAnvil)
                AnvilMechanics.updateResult(anvil, input, ((MainAnvil) anvil).getNameText());
            else
                AnvilMechanics.updateResult(anvil, input);
            contents = input;
        }
    }
}
