package me.lokka30.littlethings.listeners;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class StopFarmlandTrampling implements Listener {

    private final LittleThings instance;

    public StopFarmlandTrampling(final LittleThings instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onTrample(final EntityChangeBlockEvent event) {
        if (!instance.isModuleEnabled("stop-crop-trampling")) {
            return;
        }

        Material farmland;

        try {
            farmland = Material.valueOf("FARMLAND");
            instance.debugMessage("onTrample:EntityChangeBlockEvent -> Farmland = 'FARMLAND'");
        } catch (IllegalArgumentException exception) {
            try {
                instance.debugMessage("onTrample:EntityChangeBlockEvent -> Farmland = 'SOIL'.");
                farmland = Material.valueOf("SOIL");
            } catch (IllegalArgumentException exception2) {
                instance.debugMessage("onTrample:EntityChangeBlockEvent -> Error.");
                instance.logger.error("LittleThings wasn't able to find the farmland material for your Minecraft version. Please report this issue, as crops will not be prevented from being trampled until this is fixed.");
                return;
            }
        }

        if (event.getTo() == Material.DIRT && event.getBlock().getType() == farmland) {
            instance.debugMessage("onTrample:EntityChangeBlockEvent -> Yep, block change was farmland > dirt.");

            if (instance.isEnabledInList(event.getEntity().getWorld().getName(), "stop-crop-trampling.worlds")) {
                instance.debugMessage("onTrample:EntityChangeBlockEvent -> Yep, world is enabled. Cancelling.");

                event.setCancelled(true);
            }
        }
    }
}
