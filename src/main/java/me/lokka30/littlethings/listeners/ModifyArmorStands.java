package me.lokka30.littlethings.listeners;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class ModifyArmorStands implements Listener {

    private final LittleThings instance;

    public ModifyArmorStands(final LittleThings instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onEntitySpawn(final EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.ARMOR_STAND && instance.getConfig().getBoolean("modify-armor-stands.enabled") && instance.isEnabledInList(entity.getWorld().getName(), "modify-armor-stands.worlds")) {
            ArmorStand armorStand = (ArmorStand) entity;
            armorStand.setArms(instance.getConfig().getBoolean("modify-armor-stands.modifications.arms"));
            armorStand.setBasePlate(instance.getConfig().getBoolean("modify-armor-stands.modifications.base-plate"));
        }
    }
}
