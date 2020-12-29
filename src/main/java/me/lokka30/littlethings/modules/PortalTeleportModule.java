package me.lokka30.littlethings.modules;

import me.lokka30.littlethings.LittleModule;
import me.lokka30.littlethings.LittleThings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import java.io.File;
import java.util.Objects;

public class PortalTeleportModule implements LittleModule {

    public boolean isEnabled;
    private LittleThings instance;
    private YamlConfiguration moduleConfig;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return "PortalTeleport";
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
        public void onPortalTeleport(final PlayerPortalEvent event) {
            if (!isEnabled) {
                return;
            }

            if (!instance.isEnabledInList(getName(), moduleConfig, Objects.requireNonNull(event.getFrom().getWorld()).getName(), "worlds")) {
                return;
            }
            if (!instance.isEnabledInList(getName(), moduleConfig, event.getCause().toString(), "portals")) {
                return;
            }
            if (event.getPlayer().hasPermission("littlethings.bypass.stop-portal-teleport")) {
                return;
            }

            event.setCancelled(true);
        }
    }
}
