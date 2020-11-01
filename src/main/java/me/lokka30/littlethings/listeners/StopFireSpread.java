package me.lokka30.littlethings.listeners;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public class StopFireSpread implements Listener {

    private final LittleThings instance;

    public StopFireSpread(final LittleThings instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onSpread(final BlockSpreadEvent event) {
        if (event.getBlock().getType() != Material.FIRE) {
            return;
        }
        if (!instance.isModuleEnabled("stop-fire-spread")) {
            return;
        }
        if (!instance.isEnabledInList(event.getBlock().getWorld().getName(), "stop-fire-spread.worlds")) {
            return;
        }
        event.setCancelled(true);
    }
}
