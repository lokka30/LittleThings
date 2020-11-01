package me.lokka30.littlethings.listeners;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

public class StopExplosionsBlockDamage implements Listener {

    private final LittleThings instance;

    public StopExplosionsBlockDamage(final LittleThings instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onExplode(final BlockExplodeEvent event) {
        if (!instance.isModuleEnabled("stop-explosions-block-damage")) {
            return;
        }
        if (!instance.isEnabledInList(event.getBlock().getWorld().getName(), "stop-explosions-block-damage.worlds")) {
            return;
        }

        event.blockList().clear();
    }
}
