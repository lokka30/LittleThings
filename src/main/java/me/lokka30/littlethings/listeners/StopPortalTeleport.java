package me.lokka30.littlethings.listeners;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.Objects;

public class StopPortalTeleport implements Listener {

    private final LittleThings instance;

    public StopPortalTeleport(final LittleThings instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPortalTeleport(final PlayerPortalEvent event) {
        if (!instance.isModuleEnabled("stop-portal-teleport")) {
            return;
        }
        if (!instance.isEnabledInList(Objects.requireNonNull(event.getFrom().getWorld()).getName(), "stop-portal-teleport.worlds")) {
            return;
        }
        if (!instance.isEnabledInList(event.getCause().toString(), "stop-portal-teleport.portals")) {
            return;
        }

        event.setCancelled(true);
    }
}
