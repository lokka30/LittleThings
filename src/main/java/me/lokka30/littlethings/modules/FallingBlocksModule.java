package me.lokka30.littlethings.modules;

import me.lokka30.littlethings.LittleModule;
import me.lokka30.littlethings.LittleThings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Hugo5551
 * The listener class was created by Hugo5551 in the ArcanePlugins Discord Server.
 * Thank you very much to Hugo for providing this.
 */
public class FallingBlocksModule implements LittleModule {

    private final Queue<Block> blocksToFall = new LinkedList<>();
    private final HashMap<Integer, ItemStack[]> fallingContainers = new HashMap<>();
    public boolean isEnabled;
    private LittleThings instance;
    private YamlConfiguration moduleConfig;
    private BukkitTask task;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return "FallingBlocks";
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

        if (isEnabled) {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    fallBlocks();
                }
            }.runTaskTimer(instance, 0L, 1L);
        }
    }

    @Override
    public void loadModule() {
        instance = LittleThings.getInstance();
        loadConfig();
        Bukkit.getPluginManager().registerEvents(new Listeners(), LittleThings.getInstance());
    }

    @Override
    public void reloadModule() {
        if (task != null) {
            task.cancel();
        }
        blocksToFall.clear();

        loadConfig();
    }

    /**
     * lets queued blocks fall
     */
    private void fallBlocks() {
        for (int i = 0; i < moduleConfig.getInt("falling-blocks-per-tick"); i++) {
            //ignore blocks in the queue that are no longer there
            while (blocksToFall.peek() != null && blocksToFall.peek().isEmpty()) blocksToFall.poll();

            // skip if queue is empty
            if (blocksToFall.isEmpty()) {
                return;
            }

            Block block = blocksToFall.poll();

            //spawn falling block
            int fallingBlockId = block.getWorld().spawnFallingBlock(block.getLocation().add(0.5, 0, .5), block.getBlockData()).getEntityId();

            // containers need to save their items seperately
            if (block.getState() instanceof Container) {
                Inventory inventory = ((Container) block.getState()).getInventory();
                fallingContainers.put(fallingBlockId, inventory.getContents().clone());
                inventory.clear();
            }

            // set block to air
            block.setType(Material.AIR);
        }
    }

    private class Listeners implements Listener {

        /**
         * Use <code>enable</code> to enable this listener.
         * Use <code>disable</code> to disable this listener.
         * Use <code>setMaxFallingBlocksPerTick</code> to set how many falling blocks per tick should be generated.
         * Use <code>setWhitelist</code> to set the whitelist.
         * Use <code>setWhitelistIsBlacklist</code> to have the whitelist act like a blacklist.
         * Use <code>setIgnoreCaveAir</code> to let not let blocks fall when they are over caveair.
         */

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public void onBlockPhysics(BlockPhysicsEvent event) {
            if (!isEnabled) return;

            // ignore air and liquids, because liquids out of whatever reason dont work *shrug*
            if (event.getBlock().isEmpty() || event.getBlock().isLiquid()) return;

            // limit to white or blacklist
            if (!instance.isEnabledInList(getName(), moduleConfig, event.getBlock().getType().toString(), "materials"))
                return;

            // don't do chests
            if (event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == Material.TRAPPED_CHEST)
                return;

            // only make the block fall if there is air below, option to ignore cave air
            // i.e. if its not air or caveair and cave air is being ignored
            //      dont let this block fall
            final Block blockBelow = event.getBlock().getRelative(BlockFace.DOWN);
            if (!blockBelow.isEmpty() || moduleConfig.getBoolean("ignore-caves") && (blockBelow.getType().equals(Material.CAVE_AIR)))
                return;

            // queue Block for falling
            blocksToFall.add(event.getBlock());
        }

        @EventHandler(ignoreCancelled = true)
        public void onEntityBecomeBlockEvent(EntityChangeBlockEvent event) {
            if (!isEnabled) return;

            // ignore all non Falling Blocks
            if (event.getEntityType() != EntityType.FALLING_BLOCK) return;

            // I only care about saved containers
            if (!fallingContainers.containsKey(event.getEntity().getEntityId())) return;

            event.setCancelled(true);
            ItemStack[] items = fallingContainers.remove(event.getEntity().getEntityId());
            FallingBlock fallingBlock = (FallingBlock) event.getEntity();
            // set block manually to retain inventory
            event.getBlock().setBlockData(fallingBlock.getBlockData());
            Container container = (Container) event.getBlock().getState();
            container.getInventory().setContents(items);
        }
    }
}
