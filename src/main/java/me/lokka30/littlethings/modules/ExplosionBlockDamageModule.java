package me.lokka30.littlethings.modules;

import me.lokka30.littlethings.LittleModule;
import me.lokka30.littlethings.LittleThings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

import java.io.File;

public class ExplosionBlockDamageModule implements LittleModule {

    public boolean isEnabled;
    private LittleThings instance;
    private YamlConfiguration moduleConfig;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return "ExplosionBlockDamage";
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
        public void onExplode(final BlockExplodeEvent event) {
            instance.debugMessage("ExplosionBlockDamage: event called");

            if (!isEnabled) {
                instance.debugMessage("ExplosionBlockDamage: not enabled");
                return;
            }

            if (!instance.isEnabledInList(getName(), moduleConfig, event.getBlock().getWorld().getName(), "worlds")) {
                instance.debugMessage("ExplosionBlockDamage: world not enabled");
                return;
            }

            instance.debugMessage("ExplosionBlockDamage: clearing block list");
            event.blockList().clear();
        }
    }
}
