package me.lokka30.littlethings.modules;

import me.lokka30.littlethings.LittleModule;
import me.lokka30.littlethings.LittleThings;
import me.lokka30.microlib.other.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.io.File;

/**
 * @author ProfliX, lokka30
 */
public class MobSilentModule implements LittleModule {
    public boolean isEnabled;
    private LittleThings instance;
    private YamlConfiguration moduleConfig;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return "MobSilent";
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
            if (VersionUtils.isOneNine()) {
                Bukkit.getPluginManager().registerEvents(new Listeners(), LittleThings.getInstance());
            } else {
                instance.logger.error("The &bMobSilent&7 module is enabled but your server is not &bMC 1.9+&7. Please disable the module as it will have no effect on your server.");
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

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                if (!instance.isEnabledInList(getName(), moduleConfig, livingEntity.getType().toString(), "entities")) {
                    instance.debugMessage("MobSilent: entity silenced");
                    return;
                }

                if (!instance.isEnabledInList(getName(), moduleConfig, livingEntity.getWorld().getName(), "worlds")) {
                    instance.debugMessage("MobSilent: world disabled");
                    return;
                }

                instance.debugMessage("MobSilent: removing entity's sound");
                livingEntity.setSilent(true);
            }
        }
    }
}
