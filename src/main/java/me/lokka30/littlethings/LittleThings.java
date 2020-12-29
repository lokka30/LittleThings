package me.lokka30.littlethings;

import me.lokka30.littlethings.commands.LTCommand;
import me.lokka30.littlethings.modules.*;
import me.lokka30.microlib.MicroLogger;
import me.lokka30.microlib.QuickTimer;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LittleThings extends JavaPlugin {

    // I apologise for the mess in this class. Might clean it up later :)

    private static LittleThings instance;
    public final MicroLogger logger = new MicroLogger("&b&lLittleThings: &7");
    private final List<LittleModule> modules = Arrays.asList(new ArmorStandsModule(), new BlockGravityModule(), new DaylightCombustionModule(), new ExplosionBlockDamageModule(), new FarmlandTramplingModule(), new FireSpreadModule(), new LeafDecayModule(), new MobAIModule(), new PiglinZombificationModule(), new PortalTeleportModule());

    public static LittleThings getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        final QuickTimer timer = new QuickTimer();
        timer.start();
        logger.info("&f~ Plugin starting up ~");

        loadFiles();
        loadModules();
        registerCommands();

        logger.info("Running misc methods...");
        startMetrics();
        saveLicense();

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
            if (module.getInstalledConfigVersion() != module.getLatestConfigVersion()) {
                instance.logger.error("Module &b" + getName() + "&7's config has a mismatched version (outdated?). Please replace it as soon as possible else errors are highly likely to occur.");
            }
            logger.info("Loaded module &b" + module + "&7 with status &b" + (module.isEnabled() ? "enabled" : "disabled") + "&7.");
        });
    }

    public void reloadModules() {
        logger.info("Reloading modules...");
        modules.forEach(module -> {
            module.reloadModule();
            if (module.getInstalledConfigVersion() != module.getLatestConfigVersion()) {
                instance.logger.error("Module &b" + getName() + "&7's config has a mismatched version (outdated?). Please replace it as soon as possible else errors are highly likely to occur.");
            }
            logger.info("Reloaded module &b" + module + "&7 with status &b" + (module.isEnabled() ? "enabled" : "disabled") + "&7.");
        });
    }

    public String getModulesFolderPath() {
        return getDataFolder() + File.separator + "modules" + File.separator;
    }

    public File getModuleConfigFile(String moduleName) {
        return new File(getModulesFolderPath() + moduleName + ".yml");
    }

    public void saveModuleConfigFile(String moduleName) {
        saveOuterResource(new File(getDataFolder().getPath() + File.separator + "modules"), "modules" + File.separator + moduleName + ".yml", false);
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

    public boolean isOneSixteen() {
        try {
            Class.forName("org.bukkit.entity.PiglinAbstract");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public void debugMessage(String message) {
        if (getConfig().contains("debug") && getConfig().getBoolean("debug")) {
            logger.info("&8[DEBUG] &7" + message);
        }
    }

    private void loadFiles() {
        logger.info("Loading files...");
        saveDefaultConfig();
        if (getConfig().getInt("file-version") != 7) {
            logger.error("File version mismatch for config.yml, reset / update the config file as soon as possible.");
        }
    }

    private void registerCommands() {
        logger.info("Registering commands...");
        Objects.requireNonNull(getCommand("littlethings")).setExecutor(new LTCommand(this));
    }

    private void startMetrics() {
        new Metrics(this, 8934);
    }

    private void saveLicense() {
        saveResource("license.txt", true);
    }

    //adapted version of Bukkit's saveResource method, allowing files outside of the data folder
    public void saveOuterResource(File directory, String resourcePath, boolean replace) {
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = this.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
            } else {
                File outFile = new File(directory, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(directory, resourcePath.substring(0, Math.max(lastIndex, 0)));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (replace || !outFile.exists()) {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException exception) {
                    logger.error("Could not save " + outFile.getName() + " to " + outFile);
                    exception.printStackTrace();
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }
}
