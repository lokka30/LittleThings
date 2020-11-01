package me.lokka30.littlethings.listeners;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

public class StopLeafDecay implements Listener {

    private final LittleThings instance;

    public StopLeafDecay(final LittleThings instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onDecay(final LeavesDecayEvent event) {
        if (instance.getConfig().getBoolean("stop-leaf-decay.enabled")
                && instance.isEnabledInList(event.getBlock().getWorld().getName(), "stop-leaf-decay.worlds")) {
            event.setCancelled(true);
        }
    }
}
