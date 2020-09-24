package me.lokka30.littlethings;

import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LittleThings extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Ensure config.yml is generated.
        saveDefaultConfig();

        // Register events.
        getServer().getPluginManager().registerEvents(this, this);

        // Register metrics.
        new Metrics(this, 8934);

        // Ensure license.txt is created.
        if(!(new File(getDataFolder(), "license.txt").exists())) {
            saveResource("license.txt", false);
        }
    }

    @EventHandler
    public void onEntitySpawn(final EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if(entity.getType() == EntityType.ARMOR_STAND) {
            ArmorStand armorStand = (ArmorStand) entity;
            armorStand.setArms(getConfig().getBoolean("armor-stand.arms"));
            armorStand.setBasePlate(getConfig().getBoolean("armor-stand.base-plate"));
        }

        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if(getConfig().getStringList("no-ai").contains(livingEntity.getType().toString())) {
                livingEntity.setAI(false);
            }
        }
    }

    @EventHandler
    public void onExplode(final EntityExplodeEvent event) {
        if(getConfig().getBoolean("explosions.no-block-damage")) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onExplode(final BlockExplodeEvent event) {
        if(getConfig().getBoolean("explosions.no-block-damage")) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onSpread(final BlockSpreadEvent event) {
        if(event.getBlock().getType() == Material.FIRE) {
            if(getConfig().getBoolean("fire.no-spread")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDecay(final LeavesDecayEvent event) {
        if(getConfig().getBoolean("leaves.prevent-decay")) {
            event.setCancelled(true);
        }
    }
}
