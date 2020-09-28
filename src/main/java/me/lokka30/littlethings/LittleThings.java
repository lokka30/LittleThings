package me.lokka30.littlethings;

import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class LittleThings extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Generate files if they don't exist.
        saveIfNotExists("config.yml");
        saveIfNotExists("license.txt");
        if (getConfig().getInt("file-version") != 2) {
            getLogger().warning("Your config.yml file is not the correct version (outdated?). Reset the file or merge your current file, else it is very likely that you will experience errors. Please read the update changelogs!");
        }

        // Register events.
        getServer().getPluginManager().registerEvents(this, this);

        // Register metrics.
        new Metrics(this, 8934);
    }

    private void saveIfNotExists(String fileName) {
        if (!(new File(getDataFolder(), fileName).exists())) {
            saveResource(fileName, false);
        }
    }

    private boolean isEnabledInList(String item, String configPath) {
        if (getConfig().getBoolean(configPath + ".all")) {
            return true;
        } else {
            List<String> list = getConfig().getStringList(configPath + ".list");
            String mode = Objects.requireNonNull(getConfig().getString(configPath + ".mode")).toUpperCase();
            switch (mode) {
                case "WHITELIST":
                    return list.contains(item);
                case "BLACKLIST":
                    return !list.contains(item);
                default:
                    getLogger().severe("Invalid list mode in config.yml at path='" + configPath + ".mode', must be either 'WHITELIST' or 'BLACKLIST'! This module will not work properly until this is fixed!");
                    return false;
            }
        }
    }

    private void debugMessage(String message) {
        if (getConfig().contains("debug") && getConfig().getBoolean("debug")) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    @EventHandler
    public void onEntitySpawn(final EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        // Armor Stands
        if (entity.getType() == EntityType.ARMOR_STAND && getConfig().getBoolean("modify-armor-stands.enabled") && isEnabledInList(entity.getWorld().getName(), "modify-armor-stands.worlds")) {
            ArmorStand armorStand = (ArmorStand) entity;
            armorStand.setArms(getConfig().getBoolean("modify-armor-stands.modifications.arms"));
            armorStand.setBasePlate(getConfig().getBoolean("modify-armor-stands.modifications.base-plate"));
        }

        // Mob AI
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (getConfig().getBoolean("no-mob-ai.enabled")) {
                if (isEnabledInList(livingEntity.getType().toString(), "no-mob-ai.entities") && isEnabledInList(livingEntity.getWorld().getName(), "no-mob-ai.worlds")) {
                    livingEntity.setAI(false);
                }
            }
        }
    }

    @EventHandler
    public void onExplode(final BlockExplodeEvent event) {
        if (getConfig().getBoolean("stop-explosions-block-damage.enabled") && isEnabledInList(event.getBlock().getWorld().getName(), "stop-explosions-block-damage.worlds")) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onSpread(final BlockSpreadEvent event) {
        if (event.getBlock().getType() == Material.FIRE) {
            if (getConfig().getBoolean("stop-fire-spread.enabled") && isEnabledInList(event.getBlock().getWorld().getName(), "stop-fire-spread.worlds")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDecay(final LeavesDecayEvent event) {
        if (getConfig().getBoolean("stop-leaf-decay.enabled") && isEnabledInList(event.getBlock().getWorld().getName(), "stop-leaf-decay.worlds")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCombust(final EntityCombustEvent event) {
        if (getConfig().getBoolean("stop-daylight-combustion.enabled")) {
            if (isEnabledInList(event.getEntityType().toString(), "stop-daylight-combustion.entities") && isEnabledInList(event.getEntity().getWorld().getName(), "stop-daylight-combustion.worlds")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTeleport(final EntityTeleportEvent event) {
        debugMessage("EntityTeleportEvent fired.");
        if (event.getEntity() instanceof PiglinAbstract) {
            debugMessage("EntityTeleportEvent: entity '" + event.getEntityType().toString() + "' IS instanceof PiglinAbstract.");
            if (getConfig().getBoolean("stop-piglin-overworld-zombification.enabled")) {
                debugMessage("EntityTeleportEvent: Piglin overworld zombification IS disabled in the config.");

                final PiglinAbstract piglinAbstract = (PiglinAbstract) event.getEntity();
                final boolean isImmune = isEnabledInList(Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getName(), "stop-piglin-overworld-zombification.worlds");
                piglinAbstract.setImmuneToZombification(isImmune);

                debugMessage("EntityTeleportEvent: Is immune to zombification? -> " + isImmune);
            } else {
                debugMessage("EntityTeleportEvent: Piglin overworld zombification IS NOT disabled in the config.");
            }
        } else {
            debugMessage("EntityTeleportEvent: entity '" + event.getEntityType().toString() + "' IS NOT instanceof PiglinAbstract.");
        }
    }
}
