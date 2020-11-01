package me.lokka30.littlethings.listeners;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class StopBlockGravity implements Listener {

    private final LittleThings instance;

    public StopBlockGravity(final LittleThings instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onChangeBlock(final EntityChangeBlockEvent event) {
        final Entity entity = event.getEntity();

        if (entity instanceof FallingBlock) {
            if (!instance.isModuleEnabled("stop-block-gravity")) {
                return;
            }
            if (!instance.isEnabledInList(event.getBlock().getType().toString(), "stop-block-gravity.materials")) {
                return;
            }
            if (!instance.isEnabledInList(entity.getWorld().getName(), "stop-block-gravity.worlds")) {
                return;
            }

            event.setCancelled(true);
        }
    }
}
