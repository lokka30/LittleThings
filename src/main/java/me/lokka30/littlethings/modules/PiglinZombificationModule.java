package me.lokka30.littlethings.modules;

import me.lokka30.littlethings.LittleModule;
import me.lokka30.littlethings.LittleThings;
import me.lokka30.microlib.other.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.io.File;
import java.util.Objects;

public class PiglinZombificationModule implements LittleModule {

    public boolean isEnabled;
    private LittleThings instance;
    private YamlConfiguration moduleConfig;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return "PiglinZombification";
    }

    @Override
    public int getInstalledConfigVersion() {
        return moduleConfig.getInt("file-version");
    }

    @Override
    public int getLatestConfigVersion() {
        return 1;
    }

    @Override
    public void loadConfig() {
        File moduleConfigFile = instance.getModuleConfigFile(getName());

        //Check if the file exists, create it if necessary.
        if (!moduleConfigFile.exists()) {
            instance.saveModuleConfigFile(getName());
        }

        moduleConfig = YamlConfiguration.loadConfiguration(moduleConfigFile);
        moduleConfig.options().copyDefaults(true);

        isEnabled = moduleConfig.getBoolean("enabled");
    }

    @Override
    public void loadModule() {
        instance = LittleThings.getInstance();
        loadConfig();
        if (isEnabled) {
            if (VersionUtils.isOneSixteen()) {
                Bukkit.getPluginManager().registerEvents(new Listeners(), LittleThings.getInstance());
            } else {
                instance.logger.error("The &bPiglinZombification&7 module is enabled but your server is not &bMC 1.16+&7. Please disable the module as it will have no effect on your server.");
            }
        }
    }

    @Override
    public void reloadModule() {
        loadConfig();
    }

    private class Listeners implements Listener {
        @EventHandler
        public void onEntitySpawn(final EntitySpawnEvent event) {

            if (!isEnabled) {
                return;
            }

            Entity entity = event.getEntity();

            if (entity instanceof PiglinAbstract) {
                instance.debugMessage("PiglinZombification: Entity '" + event.getEntityType() + "' instanceof PiglinAbstract");

                final PiglinAbstract piglinAbstract = (PiglinAbstract) event.getEntity();
                final boolean shouldBeImmune = instance.isEnabledInList(getName(), moduleConfig, Objects.requireNonNull(entity.getWorld()).getName(), "worlds");

                if (shouldBeImmune) {
                    piglinAbstract.setImmuneToZombification(true);
                }

                instance.debugMessage("PiglinZombification: Is immune to zombification: " + shouldBeImmune);
            }
        }
    }
}
