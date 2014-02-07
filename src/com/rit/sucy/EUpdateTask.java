package com.rit.sucy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EUpdateTask extends BukkitRunnable {

    Player player;

    public EUpdateTask(Plugin plugin, Player player) {
        this.player = player;
        runTaskLater(plugin, 1);
    }

    public void run() {
        player.updateInventory();
    }
}
