package me.lokka30.littlethings.modules;

import me.lokka30.littlethings.LittleModule;
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
        Bukkit.getPluginManager().registerEvents(new Listeners(), LittleThings.getInstance());
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

            Material farmland;

            try {
                farmland = Material.valueOf("FARMLAND");
                instance.debugMessage("FarmlandTrampling: Farmland='FARMLAND'");
            } catch (IllegalArgumentException exception) {
                try {
                    farmland = Material.valueOf("SOIL");
                    instance.debugMessage("FarmlandTrampling: Farmland='SOIL'");
                } catch (IllegalArgumentException exception2) {
                    instance.debugMessage("FarmlandTrampling: unknown farmland material?");
                    instance.logger.error("LittleThings wasn't able to find the farmland material for your Minecraft version. Please report this issue, as crops will not be prevented from being trampled until this is fixed.");
                    return;
                }
            }

            if (event.getTo() == Material.DIRT && event.getBlock().getType() == farmland) {
                instance.debugMessage("FarmlandTrampling: Yep, block change was farmland > dirt.");

                if (instance.isEnabledInList(getName(), moduleConfig, event.getEntity().getWorld().getName(), "worlds")) {
                    instance.debugMessage("FarmlandTrampling: Yep, world is enabled. Cancelling trample by entity.");

                    event.setCancelled(true);
                }
            }
        }
    }
}
