package me.lokka30.littlethings;

import me.lokka30.microlib.MicroLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class LittleThings extends JavaPlugin implements Listener {

    public final MicroLogger logger = new MicroLogger("&b&lLittleThings: &7");

    @Override
    public void onEnable() {
        logger.info("Loading files...");
        loadFiles();

        logger.info("Registering events...");
        registerEvents();

        logger.info("Registering bStats metrics...");
        registerMetrics();

        logger.info("Plugin enabled.");
    }

    @Override
    public void onDisable() {
        logger.info("Plugin disabled.");
    }

    private void loadFiles() {
        saveIfNotExists("config.yml");
        saveIfNotExists("license.txt");
        if (getConfig().getInt("file-version") != 3) {
            getLogger().warning("Your config.yml file is not the correct version (outdated?). Reset the file or merge your current file, else it is very likely that you will experience errors. Please read the update changelogs!");
        }
    }

    private void saveIfNotExists(String fileName) {
        if (!(new File(getDataFolder(), fileName).exists())) {
            logger.info("File '&b" + fileName + "&7' didn't exist, generating it...");
            saveResource(fileName, false);
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void registerMetrics() {
        new Metrics(this, 8934);
    }

    /* Code below will be distributed into multiple other classes in future versions */

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
            logger.info("&8[DEBUG] &7" + message);
        }
    }

    @EventHandler
    public void onEntitySpawn(final EntitySpawnEvent event) {
        debugMessage("EntitySpawnEvent fired.");

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

        // Piglins
        if (entity instanceof PiglinAbstract) {
            debugMessage("EntitySpawnEvent: Entity '" + event.getEntityType().toString() + "' IS instanceof PiglinAbstract.");
            if (getConfig().getBoolean("stop-piglin-overworld-zombification.enabled")) {
                debugMessage("EntitySpawnEvent: Piglin overworld zombification IS disabled in the config.");

                final PiglinAbstract piglinAbstract = (PiglinAbstract) event.getEntity();
                final boolean isImmune = isEnabledInList(Objects.requireNonNull(entity.getWorld()).getName(), "stop-piglin-overworld-zombification.worlds");
                piglinAbstract.setImmuneToZombification(isImmune);

                debugMessage("EntitySpawnEvent: Is immune to zombification? -> " + isImmune);
            } else {
                debugMessage("EntitySpawnEvent: Piglin overworld zombification IS NOT disabled in the config.");
            }
        } else {
            debugMessage("EntitySpawnEvent: Entity '" + event.getEntityType().toString() + "' IS NOT instanceof PiglinAbstract.");
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
    public void onTrample(final EntityChangeBlockEvent event) {
        debugMessage("onTrample:EntityChangeBlockEvent -> Event called.");

        Material farmland;

        try {
            farmland = Material.valueOf("FARMLAND");
            debugMessage("onTrample:EntityChangeBlockEvent -> Farmland = 'FARMLAND'");
        } catch (IllegalArgumentException exception) {
            try {
                debugMessage("onTrample:EntityChangeBlockEvent -> Farmland = 'SOIL'.");
                farmland = Material.valueOf("SOIL");
            } catch (IllegalArgumentException exception2) {
                debugMessage("onTrample:EntityChangeBlockEvent -> Error.");
                getLogger().severe("LittleThings wasn't able to find the farmland material for your Minecraft version. Please report this issue, as crops will not be prevented from being trampled until this is fixed.");
                return;
            }
        }

        if (event.getTo() == Material.DIRT && event.getBlock().getType() == farmland) {
            debugMessage("onTrample:EntityChangeBlockEvent -> Yep, block change was farmland > dirt.");

            if (getConfig().getBoolean("stop-crop-trampling.enabled")) {
                debugMessage("onTrample:EntityChangeBlockEvent -> Yep, is enabled.");

                if (isEnabledInList(event.getEntity().getWorld().getName(), "stop-crop-trampling.worlds")) {
                    debugMessage("onTrample:EntityChangeBlockEvent -> Yep, world is enabled. Cancelling.");

                    event.setCancelled(true);
                }
            }
        }
    }
}
