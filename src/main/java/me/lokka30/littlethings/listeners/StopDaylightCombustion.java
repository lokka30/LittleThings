package me.lokka30.littlethings.listeners;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

public class StopDaylightCombustion implements Listener {

    private final LittleThings instance;

    public StopDaylightCombustion(final LittleThings instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onCombust(final EntityCombustEvent event) {
        if (!instance.isModuleEnabled("stop-daylight-combustion")) {
            return;
        }
        if (!instance.isEnabledInList(event.getEntityType().toString(), "stop-daylight-combustion.entities")) {
            return;
        }
        if (!instance.isEnabledInList(event.getEntity().getWorld().getName(), "stop-daylight-combustion.worlds")) {
            return;
        }

        event.setCancelled(true);
    }
}
