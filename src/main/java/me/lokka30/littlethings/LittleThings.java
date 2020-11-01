package me.lokka30.littlethings;

import me.lokka30.littlethings.commands.LTCommand;
import me.lokka30.littlethings.listeners.*;
import me.lokka30.microlib.MicroLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class LittleThings extends JavaPlugin implements Listener {

    public final MicroLogger logger = new MicroLogger("&b&lLittleThings: &7");

    @Override
    public void onEnable() {
        final long startTime = System.currentTimeMillis();

        logger.info("Loading files...");
        loadFiles();

        logger.info("Registering listeners...");
        registerListeners();

        logger.info("Registering commands...");
        registerCommands();

        logger.info("Registering bStats metrics...");
        registerMetrics();

        final long duration = System.currentTimeMillis() - startTime;
        logger.info("&fPlugin enabled successfully. &8(&7took &b" + duration + "ms&8)&r");
    }

    @Override
    public void onDisable() {
        logger.info("Plugin disabled.");
    }

    private void loadFiles() {
        saveIfNotExists("config.yml");
        saveIfNotExists("license.txt");
        if (getConfig().getInt("file-version") != 4) {
            getLogger().warning("Your config.yml file is not the correct version (outdated?). Reset the file or merge your current file, else it is very likely that you will experience errors. Please read the top of the update changelogs next time :)");
        }
    }

    private void saveIfNotExists(String fileName) {
        if (!(new File(getDataFolder(), fileName).exists())) {
            logger.info("File '&b" + fileName + "&7' didn't exist, generating it...");
            saveResource(fileName, false);
        }
    }

    private void registerListeners() {
        final PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new MobAI(this), this);
        pluginManager.registerEvents(new ModifyArmorStands(this), this);
        pluginManager.registerEvents(new StopBlockGravity(this), this);
        pluginManager.registerEvents(new StopDaylightCombustion(this), this);
        pluginManager.registerEvents(new StopExplosionsBlockDamage(this), this);
        pluginManager.registerEvents(new StopFarmlandTrampling(this), this);
        pluginManager.registerEvents(new StopFireSpread(this), this);
        pluginManager.registerEvents(new StopLeafDecay(this), this);
        pluginManager.registerEvents(new StopPiglinOverworldZombification(this), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("littlethings")).setExecutor(new LTCommand(this));
    }

    private void registerMetrics() {
        new Metrics(this, 8934);
    }

    /* Code below will be distributed into multiple other classes in future versions */

    public boolean isEnabledInList(String item, String configPath) {
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

    public void debugMessage(String message) {
        if (getConfig().contains("debug") && getConfig().getBoolean("debug")) {
            logger.info("&8[DEBUG] &7" + message);
        }
    }
}
