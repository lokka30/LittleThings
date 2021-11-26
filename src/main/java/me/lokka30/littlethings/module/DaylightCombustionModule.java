package me.lokka30.littlethings.module;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.inventory.EntityEquipment;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DaylightCombustionModule implements LittleModule {

    public boolean isEnabled;
    private LittleThings instance;
    private YamlConfiguration moduleConfig;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return "DaylightCombustion";
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
        public void onCombust(final EntityCombustEvent event) {
            if (!isEnabled) {
                return;
            }
            if (!instance.isEnabledInList(getName(), moduleConfig, event.getEntityType().toString(), "entities")) {
                return;
            }
            if (!instance.isEnabledInList(getName(), moduleConfig, event.getEntity().getWorld().getName(), "worlds")) {
                return;
            }

            // Credit to SpigotMC users Gadse and StealingDaPanda: https://www.spigotmc.org/threads/stop-mobs-from-burning-in-daylight.481610/#post-4044537
            if (event instanceof EntityCombustByBlockEvent || event instanceof EntityCombustByEntityEvent) {
                instance.debugMessage("DaylightCombustion: Event instanceof ECBBE or ECBEE (not daylight combustion), skipping");
                return;
            }

            // Make sure that the entity at hand can burn in the sunlight
            List<String> entityTypesCanBurnInSunlight = Arrays.asList("SKELETON", "ZOMBIE", "ZOMBIE_VILLAGER", "STRAY", "DROWNED", "PHANTOM");
            if (!entityTypesCanBurnInSunlight.contains(event.getEntity().getType().toString())) {
                instance.debugMessage("DaylightCombustion: Mob is not burnable by sunlight, skipping");
                return;
            }

            if (event.getEntity() instanceof LivingEntity) {
                EntityEquipment equipment = ((LivingEntity) event.getEntity()).getEquipment();

                if (equipment != null && equipment.getHelmet() != null && equipment.getHelmet().getType() != Material.AIR) {
                    instance.debugMessage("DaylightCombus ion: Mob has a helmet (which prevents them from burning from daylight), skipping");
                    return;
                }
            }

            instance.debugMessage("DaylightCombustion: Cancelling");
            event.setCancelled(true);
        }
    }
}
