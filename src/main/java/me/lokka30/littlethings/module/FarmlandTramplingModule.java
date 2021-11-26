package me.lokka30.littlethings.module;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.io.File;

public class FarmlandTramplingModule implements LittleModule {

    public boolean isEnabled;
    String farmlandMaterial;
    private LittleThings instance;
    private YamlConfiguration moduleConfig;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return "FarmlandTrampling";
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
        loadFarmlandMaterial();
        Bukkit.getPluginManager().registerEvents(new Listeners(), LittleThings.getInstance());
    }

    private void loadFarmlandMaterial() {
        try {
            Material.valueOf("FARMLAND");
            farmlandMaterial = "FARMLAND";
            instance.debugMessage("FarmlandTrampling: Farmland Material = 'FARMLAND'");
        } catch (IllegalArgumentException exception) {
            try {
                Material.valueOf("SOIL");
                farmlandMaterial = "SOIL";
                instance.debugMessage("FarmlandTrampling: Farmland Material = 'SOIL'");
            } catch (IllegalArgumentException exception2) {
                instance.debugMessage("FarmlandTrampling: unknown farmland material?");
                instance.logger.error("LittleThings wasn't able to find the farmland material for your Minecraft version. Please report this issue, as crops will not be prevented from being trampled until this is fixed.");
                isEnabled = false;
            }
        }
    }

    @Override
    public void reloadModule() {
        loadConfig();
    }

    private class Listeners implements Listener {
        @EventHandler
        public void onTrample(final EntityChangeBlockEvent event) {
            if (!isEnabled) {
                return;
            }

            if (event.getTo() == Material.DIRT && event.getBlock().getType().toString().equals(farmlandMaterial)) {
                instance.debugMessage("FarmlandTrampling: farmland > dirt");

                if (instance.isEnabledInList(getName(), moduleConfig, event.getEntity().getWorld().getName(), "worlds")) {
                    instance.debugMessage("FarmlandTrampling: world is enabled. cancelling trample by entity.");

                    event.setCancelled(true);
                }
            }
        }
    }
}
