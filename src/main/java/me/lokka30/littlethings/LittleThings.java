package me.lokka30.littlethings;

import me.lokka30.littlethings.command.LittleThingsCommand;
import me.lokka30.littlethings.module.*;
import me.lokka30.microlib.exceptions.OutdatedServerVersionException;
import me.lokka30.microlib.maths.QuickTimer;
import me.lokka30.microlib.messaging.MicroLogger;
import me.lokka30.microlib.other.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LittleThings extends JavaPlugin {

    // I apologise for the mess in this class. Might clean it up later :)

    private static LittleThings instance;
    public final MicroLogger logger = new MicroLogger("&b&lLittleThings: &7");

    private final List<LittleModule> modules = Arrays.asList(
            new ArmorStandsModule(),
            new BlockGravityModule(),
            new DaylightCombustionModule(),
            new DragonEggBreakModule(),
            new ExplosionBlockDamageModule(),
            new FallingBlocksModule(),
            new FarmlandTramplingModule(),
            new IronGolemZombieVillagerModule(),
            new LeafDecayModule(),
            new MobAIModule(),
            new MobSilentModule(),
            new PiglinZombificationModule(),
            new PlantGrowthModule(),
            new PortalTeleportModule()
    );

    public static LittleThings getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        final QuickTimer timer = new QuickTimer();
        timer.start();

        loadFiles();
        loadModules();
        registerCommands();

        logger.info("Running misc methods...");
        startMetrics();
        checkForUpdates();

        logger.info("&f~ Plugin enabled successfully, took &b" + timer.getTimer() + "ms&f ~");
    }

    @Override
    public void onDisable() {
        logger.info("&f~ Plugin shut down successfully ~");
    }

    private void loadModules() {
        logger.info("Loading modules...");
        modules.forEach(module -> {

            module.loadModule();

            // run module version check
            if (module.isEnabled() && (module.getInstalledConfigVersion() != module.getLatestConfigVersion())) {
                instance.logger.error("Module &b" + module.getName() + "&7's config has a mismatched version (outdated?). Please replace it as soon as possible, or else, errors are highly likely to occur.");
            }

            logger.info("Loaded module &b" + module.getName() + "&7 with status &b" + (module.isEnabled() ? "enabled" : "disabled") + "&7.");
        });
    }

    public void reloadModules() {
        logger.info("Re-loading modules...");
        modules.forEach(module -> {
            module.reloadModule();
            if (module.getInstalledConfigVersion() != module.getLatestConfigVersion()) {
                logger.error("Module &b" + module.getName() + "&7's config has a mismatched version (outdated?). Please replace it as soon as possible, or else, errors are highly likely to occur.");
                debugMessage("MismatchedVersion: (Installed) v" + module.getInstalledConfigVersion() + " != (Latest) v" + module.getLatestConfigVersion() + ".");
            }

            logger.info("Re-loaded module &b" + module.getName() + "&7 with status &b" + (module.isEnabled() ? "enabled" : "disabled") + "&7.");
        });
    }

    @NotNull
    public String getModulesFolderPath() {
        return getDataFolder() + File.separator + "modules" + File.separator;
    }

    @NotNull
    public File getModuleConfigFile(@NotNull final String moduleName) {
        return new File(getModulesFolderPath() + moduleName + ".yml");
    }

    public void saveModuleConfigFile(@NotNull final String moduleName) {
        saveResource("modules" + File.separator + moduleName + ".yml", false);
    }

    public boolean isEnabledInList(String moduleName, YamlConfiguration config, String item, String configPath) {
        if (config.getBoolean(configPath + ".all")) {
            return true;
        } else {
            List<String> list = config.getStringList(configPath + ".list");
            String mode = Objects.requireNonNull(config.getString(configPath + ".mode")).toUpperCase();
            switch (mode) {
                case "WHITELIST":
                    return list.contains(item);
                case "BLACKLIST":
                    return !list.contains(item);
                default:
                    logger.error("Invalid list mode in module &b" + moduleName + "&7's config at path='" + configPath + ".mode', must be either 'WHITELIST' or 'BLACKLIST'! This module will not work properly until this is fixed!");
                    return false;
            }
        }
    }

    public void debugMessage(String message) {
        if (!getConfig().getBoolean("debug", false)) { return; }

        logger.info("&8[DEBUG] &7" + message);
    }

    private void loadFiles() {
        logger.info("Loading files...");

        saveDefaultConfig();

        if (getConfig().getInt("file-version") != 7) {
            logger.error("File version mismatch for config.yml, reset / update the config file as soon as possible.");
        }

        saveResource("license.txt", true);
    }

    private void registerCommands() {
        logger.info("Registering commands...");
        Objects.requireNonNull(getCommand("littlethings")).setExecutor(new LittleThingsCommand(this));
    }

    private void startMetrics() {
        new Metrics(this, 8934);
    }

    private void checkForUpdates() {
        if (!getConfig().getBoolean("check-for-updates")) {
            return;
        }

        UpdateChecker updateChecker = new UpdateChecker(this, 84163);
        String currentVersion = updateChecker.getCurrentVersion();

        try {
            updateChecker.getLatestVersion(latestVersion -> {
                if (!currentVersion.equals(latestVersion)) {
                    logger.warning("An update is available on the SpigotMC resource page. You are running &bv" + currentVersion + "&7, new version is &bv" + latestVersion + "&7.");
                }
            });
        } catch(OutdatedServerVersionException ex) {
            ex.printStackTrace();
        }
    }
}
