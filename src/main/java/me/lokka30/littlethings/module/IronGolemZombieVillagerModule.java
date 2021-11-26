package me.lokka30.littlethings.module;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.io.File;

public class IronGolemZombieVillagerModule implements LittleModule {

    public boolean isEnabled;
    private LittleThings instance;
    private YamlConfiguration moduleConfig;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return "IronGolemZombieVillager";
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
        Bukkit.getPluginManager().registerEvents(new Listeners(), LittleThings.getInstance());
    }

    @Override
    public void reloadModule() {
        loadConfig();
    }

    private class Listeners implements Listener {
        @EventHandler
        public void onDamage(final EntityDamageByEntityEvent event) {
            if (!isEnabled) {
                return;
            }

            // Check if defender is a zombie villager.
            if (event.getEntity().getType() != EntityType.ZOMBIE_VILLAGER) {
                return;
            }

            // Check if attacker is an iron golem.
            if (event.getDamager().getType() != EntityType.IRON_GOLEM) {
                return;
            }

            // Make sure that the world is enabled in the module config.
            if (!instance.isEnabledInList(getName(), moduleConfig, event.getEntity().getWorld().getName(), "worlds")) {
                instance.debugMessage("IronGolemZombieVillager: world disabled");
                return;
            }

            ZombieVillager zombieVillager = (ZombieVillager) event.getEntity();

            if (moduleConfig.getBoolean("mustBeConverting")) {
                if (zombieVillager.isConverting()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
