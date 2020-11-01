package me.lokka30.littlethings.listeners;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Objects;

public class StopPiglinOverworldZombification implements Listener {

    private final LittleThings instance;

    public StopPiglinOverworldZombification(final LittleThings instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onEntitySpawn(final EntitySpawnEvent event) {

        Entity entity = event.getEntity();

        if (entity instanceof PiglinAbstract) {
            instance.debugMessage("EntitySpawnEvent: Entity '" + event.getEntityType().toString() + "' is instanceof PiglinAbstract");
            if (instance.getConfig().getBoolean("stop-piglin-overworld-zombification.enabled")) {
                instance.debugMessage("EntitySpawnEvent: Piglin overworld zombification IS disabled in the config.");

                final PiglinAbstract piglinAbstract = (PiglinAbstract) event.getEntity();
                final boolean isImmune = instance.isEnabledInList(Objects.requireNonNull(entity.getWorld()).getName(), "stop-piglin-overworld-zombification.worlds");
                piglinAbstract.setImmuneToZombification(isImmune);

                instance.debugMessage("EntitySpawnEvent: Is immune to zombification? -> " + isImmune);
            } else {
                instance.debugMessage("EntitySpawnEvent: Piglin overworld zombification IS NOT disabled in the config.");
            }
        } else {
            instance.debugMessage("EntitySpawnEvent: Entity '" + event.getEntityType().toString() + "' IS NOT instanceof PiglinAbstract.");
        }
    }
}
