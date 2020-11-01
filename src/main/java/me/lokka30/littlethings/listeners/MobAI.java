package me.lokka30.littlethings.listeners;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobAI implements Listener {

    private final LittleThings instance;

    public MobAI(final LittleThings instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onEntitySpawn(final EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (instance.getConfig().getBoolean("no-mob-ai.enabled")) {
                if (instance.isEnabledInList(livingEntity.getType().toString(), "no-mob-ai.entities") && instance.isEnabledInList(livingEntity.getWorld().getName(), "no-mob-ai.worlds")) {
                    livingEntity.setAI(false);
                }
            }
        }
    }
}
